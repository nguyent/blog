# Hiatus

Hi, my name is Thang Nguyen and I'm 28 years old. I have been on hiatus since September from my position as a Data Engineer at The New York Times. This has naturally been a hot topic of discussion, for both friends and potential employers, so I decided to write a formal post on my reasoning for anyone interested in my [thought process](https://zenpencils.com/comic/128-bill-watterson-a-cartoonists-advice/). 

### Why did I leave the Times and decide to go on hiatus? 

I'm going to return to this question, but for the sake of my narrative I'm going to answer a different question.

### Why did I decide to join the Times? 

This is a bit of a long story, so bear with me. 

Before I joined the Times, I was working in Boston at Zipcar as a full stack engineer. It was my first job out of college and like some young engineers with an inferiority complex I felt like I had something to prove. I wanted to build a legitimately useful product, but my lack of experience meant I was limited in what I could deliver.

Back then, one of Zipcar's main applications was a Rails API deployed on a distributed cluster. We lacked aggregated logging infrastructure, meaning debugging production issues involved: 

- SSHing into multiple nodes simultaneously
- Grepping each nodes application logs
- Manually scanning for an error

This got old very quickly. I saw an opportunity to ease the burden for the engineers on-call. 

Enter the ELK (Elasticsearch, Logstash, Kibana) stack, an open source solution to the aggregated logging problem. Elasticsearch (ES) is a distributed document store, Logstash is a daemon providing the transport mechanism from a running application to an output target (ES in this case), and Kibana is a visualization tool which allows for dashboard creation and ad-hoc querying of ES. 

I took it upon myself to test, build, and deploy the entire ELK infrastructure on my own. It was a significant undertaking, fraught with issues (many of them puppet related), and took much longer than expected. I contemplated giving up a few times. I distinctly remember during a standup telling my team I was trying different combinations of configurations to get something working when a teammate sarcastically quipped, "Have you tried giving up?"

I refused to give up, partially because of a sunk cost fallacy, but also because I wanted to be perceived as valuable by providing a service to co-workers I respected. Despite the issues I pushed through the pain and eventually had the ELK stack successfully running end-to-end. 

![histo]({{ site.url }}/blog/assets/histo.png)

The first Kibana dashboard I made was a pretty stacked histogram showcasing user actions. This was put on display in our area of the office and naturally people walking by started asking what it was. I was extremely proud to show co-workers what I had done and eager for them to use it. I even led a department wide lunch and learn that was well received. I felt gratification once I started seeing Kibana used around the company without explicitly introducing it to those users. People were empowered to do their own ad-hoc analysis and create their own dashboards. 

I had accomplished my goal of providing value to the company. Now what? 

### Path to the Times 

The genesis of my foray into Data Engineering (DE) was borne from my work on the ELK stack and also my observation that DE work is capable of providing genuine tangible value to any organization. Data are irrefutable, the foundation for pivotal decisions, and I wanted to be the one to deliver it. I applied to and was accepted to the [Insight Data Engineering program](https://www.insightdataengineering.com/), built a [project]([http://thangnguyen.us/insight](http://thangnguyen.us/insight)) in 6 weeks, presented it to the Times, was subsequently invited to an onsite, and received an offer shortly thereafter. 

When I received my offer I had to decide if I wanted to leave the state I spent my entire life in, where my family lived, break my lease, and move to a completely new city where I only knew a handful of people. I knew it would be hard, but I felt like I needed to do it. I opted to make the move, partially out of existential fear, but also because I wanted to be a part of an organization that I truly believed would make the world a better place. I actually had a higher competing offer, but turned it down to be at the Times, because the mission was more important to me than money.

### On Motivation and Moving on

![autonomy-mastery-purpose]({{ site.url }}/blog/assets/autonomy-mastery-purpose.jpg)

The above is a visualization of an [excellent Ted Talk](https://www.youtube.com/watch?v=rrkrvAUbU9Y) by Daniel Pink. The crux of the talk is that motivation is based on three concepts: Autonomy, Mastery, & Purpose. When all three are satisfied, motivation is plentiful. When any one of those is lacking, motivation wanes. I have adopted this system as a tool for introspection whenever I feel like I'm getting into a slump with anything I may be pursuing, not strictly just work.

I left Zipcar because I felt like the Times was an objectively more impactful place to work, thus satisfying my desire for purpose, while also subverting whatever purpose I felt at Zipcar. Autonomy and mastery were implied, as it was a new environment to be in and I had much to learn. Now back to the original question...

### Why did I leave the Times? 

My reasoning for joining the Times would ironically be the reason I left. I wanted to be somewhere I felt like my work mattered and made an impact. Something I didn't realize at the Times going in is the division between the Newsroom and the Business™. The Newsroom is naturally where the hard hitting, front-facing journalism work happens. The Business on the other hand was just that: the business side of things. I worked on the business side.

![separation-of-church-and-state]({{ site.url }}/blog/assets/separation-of-church-and-state.jpg)

My work was primarily providing data to analysts, engineers, or data scientists who would use the data in a novel way to tell a story that would produce business value. This included recommendations, personalizations, understanding user behavior, subscription starts & stops, etc. I often didn't even see how the data were being used by others, but I was certainly one of the first to know when they stopped receiving it. Our team was essentially a data conduit, which made sense given how our team was structured horizontally across the organization, but also meant that we weren't the ones making an impact with the data. 

Once I came to this realization, I increasingly felt more and more divorced from the value the Times was providing to the world through journalism. Sure, one could argue my work played a part in generating more revenue for the company, which in turn would allow it to continue to function and grow. I even told myself that for a while, but it wasn't enough to keep me going. 

I became disillusioned and lost my motivation to work. I realized that I was undergoing an [identity crisis](https://www.huffpost.com/entry/self-identity_b_1128731) of sorts, my identity was tied to my career, which I had conflated with the value the Times was producing. With my connection lost, I began to feel myself getting burnt out. I was undergoing an existential crisis. I needed a change of pace and some time away from industry to clear my head. I took a hiatus to pursue my main hobby, Brazilian Jiu-Jitsu, full-time. 

### Time after Times

![bjj]({{ site.url }}/blog/assets/bjj.jpg)

People often ask what I've been up to, and are pretty surprised when I say [nothing](https://www.nytimes.com/2019/04/29/smarter-living/the-case-for-doing-nothing.html). Nothing career related, but I was keeping busy: I volunteered at an animal shelter, trained several times a week, competed in some tournaments, read some books, fostered a dog, improved my culinary skills, but most importantly did a lot of self-reflection to understand what my personal needs are and what I want for my future. I realized that I need to be able to contribute observable value to others in order to feel connected to the company mission and ultimately my work. For me, the knowledge gained from this introspective journey is almost more important than what I actually did during my break from industry.

I loved my time at the Times, the people I met, and the experience I gained, but when I was no longer emotionally invested in my work I knew I needed a break. Though I wasn't honing my technical skills, I feel like I gained significant personal insight into who I am as a person and what motivates me. I am also confident this newly found wisdom will grant me long term satisfaction that I can take with me anywhere, and that alone has made my hiatus worthwhile.

<sub>- TN</sub> 
