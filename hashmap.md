## Gotchas: Concurrent Execution and Maps in Java

I was once tasked with building a streaming [Dataflow](https://cloud.google.com/dataflow/) pipeline that looked something like this:

{:.center} ![conc-pipeline]({{ site.url }}/blog/assets/conc-pipeline.png)

Data producers published messages onto a Pub/Sub topic. Each message contained a `schema_id`, which would be resolved through a request to a Schema Registry (SR), and a `payload` to be decoded using the corresponding schema. After decoding the data we could write transform it to a BigQuery row.

### Cache Me If You Can

The set of `schema_ids` / schemas is finite while the amount of messages we could receive is theoretically limitless. I decided to implement a read-only cache at the worker level to minimize the number of calls to the SR. The idea was simple: query the SR any time a new `schema_id` was encountered and memoize the response. We could just use a `HashMap` to do this, right?

There's an issue with this approach. This only works under a single-threaded execution mode. During multi-threaded execution keys are updated and read **concurrently**. In other words `HashMap` is not thread-safe out of the box. (For a primer on concurrency read [this](https://sookocheff.com/post/concurrency/concurrency-a-primer/)).

We could utilize the `synchronized` keyword to acquire the [intrinsic lock](https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html) associated with each Java object, granting  exclusive access to the caller (thread). In a multi-threaded environment, the first thread would acquire the lock and subsequent threads would have their execution blocked until the first thread completed their work and released the lock. In essence, by using `synchronized` in this manner we're forcing ourselves into a single-threaded execution mode in order to gain thread-safety.

{:.center}![conc-1]({{ site.url }}/blog/assets/conc-1.png)

<sub>source: https://www.youtube.com/watch?v=ddceop8tAm4</sub>

This adds undesirable latency overhead in the pipeline. Envision the following scenario:

- Dataflow's autoscaler allocates a new worker with a new cache.
- The worker needs to process requests while simultaneously populating the cache from nothing, a cold-start.
- In the worst case scenario incoming messages would be blocked on the cache warming up, with an upper bound of the total number of potential cache entries. This would be a one time cost, and in the worst case the additional latency would be: `number of potential cache keys * avg response time`.

<sub>It's feasible to block incoming requests until the cache is adequately warmed, but let's roll with this for now.</sub>

While in this particular scenario, the latency from a cold-start wasn't necessarily a show stopper I saw an opportunity to improve upon it.

### java.util.concurrent to the Rescue!

Lucky for us Java's `ConcurrentHashMap` allows for multiple concurrent reads/updates by utilizing a technique known as [lock-striping](https://netjs.blogspot.com/2016/05/lock-striping-in-java-concurrency.html). The `synchronized` keyword locks the _entire_ `HashMap` (and thus all keys), while lock-striping provides a more granular approach by using multiple locks, making it possible to modify multiple keys at once thus mitigating contention.

{:.center}![conc-2]({{ site.url }}/blog/assets/conc-2.png)

<sub>source: https://www.youtube.com/watch?v=ddceop8tAm4</sub>

This is exactly what we're looking for, thread-safety without significantly compromising performance.

### Measure Twice, Cut Once

After using `ConcurrentHashMap`, everything looked fine except for one small detail.

The Java `Map` interface has two methods for providing a default value when a key is missing: `putIfAbsent` and `computeIfAbsent`. The difference between these two is subtle, but significant to distinguish if you're concerned about re-computation. In our case, this is making multiple requests with the same `schema_id`.

Let's take a look at the `ConcurrentHashMap`'s Java docs for a little more insight.

##### putIfAbsent documentation

````java
If the specified key is not already associated with a value, associate it with the given value. This is equivalent to

 if (!map.containsKey(key))
   return map.put(key, value);
 else
   return map.get(key);

except that the action is performed atomically.
````

Atomicity is good, right? Atomicity typically means "**all or nothing**", so either the key update happens or it doesn't. That sounds like what we want, now what about `computeIfAbsent`?

##### computeIfAbsent documentation

```
If the specified key is not already associated with a value, attempts to compute its value using the given mapping function and enters it into this map unless null. The entire method invocation is performed atomically, so the function is applied at most once per key.
```

This is also an atomic operation, however there's an additional detail: `the function is applied at most once per key`. Contrast this with the former's documentation: `the action is performed atomically.` What is the action is this scenario? Let's look at a code sample.

```java
    public static void main(String[] args) throws InterruptedException {
        Map<String, Integer> concurrentMap = new ConcurrentHashMap();
        String key = "key";

        ExecutorService exec = Executors.newFixedThreadPool(10);
        ExecutorCompletionService compService = new ExecutorCompletionService(exec);

        for (int i = 15; i > 0; i--) {
            final int counter = i;
            compService.submit(() -> {
                if (args.length == 1 && args[0].equals("putTest")) {
                    concurrentMap.putIfAbsent(key, getValue(counter));
                } else {
                    System.out.println("executing...");
                    concurrentMap.computeIfAbsent(key, getObjectIntegerFunction(counter));
                }
                return null;
            });
        }

        exec.shutdown();
        exec.awaitTermination(10L, TimeUnit.SECONDS);

        System.out.println(concurrentMap.get(key));
    }

    private static Function<Object, Integer> getObjectIntegerFunction(int finalI) {
        return (Object x) -> {
            getValue(finalI);
            return finalI;
        };
    }

    private static Integer getValue(int finalI) {
        System.out.println("starting " + finalI);
        sleep(finalI);
        System.out.println("completing " + finalI);
        return finalI;
    }
```

<sub> Full code here: https://github.com/nguyent/blog/blob/master/assets/code/concurrentTest.java </sub>

The code is fairly straightforward, I've added a runtime parameter check to showcase the difference between `putIfAbsent` and `computeIfAbsent`. In this sample we're spawning an `Executor` with 10 threads, submitting 15 `Callables` to the `ExecutorService`. The `Callables` are attempting to update the `ConcurrentHashMap` with one of the two methods. Let's take a look at `putIfAbsent` first.

```
starting 14
...
starting 7
starting 6
completing 6
starting 5
completing 7
starting 4
completing 8
starting 3
...
6
```

<sub> I'm truncating some output here.</sub>

Here we can see calling `putIfAbsent` multiple times results in multiple evaluations of `getValue`. Another important observation is that with multiple `putIfAbsent` calls, the initial call is not necessarily the value that will be populated in the map. The first `value` returned will be what's put in the map. So in the output above, we're counting backwards from 15, sleeping for 15 * 200 ms, before returning. We can see the first call `14` was initiated, however `6` completed before and gets put into the map (the last line is the value associated with the key).

This means any in-flight `putIfAbsent` calls to the same key will not update the map. This is what the Java docs mean by **atomicity** with `putIfAbsent`. The `value` portion of the `putIfAbsent(key, value)` call is eagerly evaluated each time. This works fine if computing the `value` is relatively cheap, but when the computation is expensive, like a call to an external service, we want lazy blocking evaluation. This is exactly what `computeIfAbsent` provides.

Let's take a look at the `computeIfAbsent` output:

```
executing...
executing...
...
starting 15
executing...
completing 15
...
executing...
executing...
15
```

<sub>Truncating output again. </sub>

Here we can see `executing…` printed multiple times, which means we're executing the `Callable` with the  `computeIfAbsent` call, however we only see `starting` and `completing` once. This confirms we're actually only ever computing the value once, and also blocking subsequent calls until the initial computation has completed. Great success!

### Closing Notes

This was a lot, and if you made it this far I commend you. While this approach worked for me, if your needs are slightly more complex you may want to look into using Google's Guava [Cache](https://github.com/google/guava/wiki/CachesExplained) which behaves in a similar manner to `ConcurrentHashMap` while also allowing you to set time expiration policies, limit the size of the cache, removal listeners and more. Why bother re-inventing the wheel? Learn from my mistakes: read the documentation carefully and research to see if there's a turn key solution available to your problem.

<sub>- TN</sub>