import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

public class Main {
    // modified from: https://stackoverflow.com/a/4886666
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

    static void sleep(int n) {
        try {
            Thread.sleep(100 * n);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
