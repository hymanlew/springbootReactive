package com.hyman.instuc;

/**
 * SpringBoot2.0新特性：
 * 编程语言Java8+，和当前火爆的Kotlin，底层框架Spring Framwork 5.0.x，全新特性Web Flux。
 * <p>
 * Kotlin 是一个用于现代多平台应用的静态编程语言，由 JetBrains 开发。Kotlin可以编译成Java字节码，也可以编译成JavaScript，方便在没有JVM的设备上运行。已正式成为Android官方支持开发语言。
 * <p>
 * SpringBoot1.0 是仅支持 Servlet Containers -> Servlet API，属于传统方式。
 * SpringBoot2.0 在支持1.0的特性上，同时添加了一个新特性就是WebFlux，可以使用Netty及Servlet3.1作为容器，基于 Java9 Reactive Streams 流处理。
 * <p>
 * Java9 Reactive Streams 允许我们实现非阻塞异步流处理。这是将响应式编程模型应用于核心java编程的重要一步。
 * Reactive Streams 是关于流的异步处理，因此应该有一个发布者（Publisher）和一个订阅者（Subscriber）。发布者发布数据流，订阅者使用数据。有时我们必须在Publisher和Subscriber之间转换数据。
 * 处理器（Processor）是位于最终发布者和订阅者之间的实体，用于转换从发布者接收的数据，以便订阅者能理解它。我们可以拥有一系列（chain ）处理器。Processor既可以作为订阅者也可以作为发布者。
 * <p>
 * Reactive Streams 与 Java8 Stream 最大的不同在于，前者是 Push 模式，后者是 Pull 模式。
 * 常见的操作集合类的方式就是 Pull 模式。因为以下是客户端主动向服务端请求数据，然后在操作数据。好比客户端主动从服务端拉取数据，故称为拉模式。
 * List<User> users = userRpcClient.findAllUsers();
 * for (String user : users) {
 * // do something
 * }
 * <p>
 * 而以下 Callback 风格的代码通常都是 Push 模式的。
 * userRpcPublisher.subscribe(new UserSubscriber() {
 * public void onNext(User user) {
 * // do something
 * }
 * });
 * <p>
 * Pull 模式的代码的问题在于，如果 userRpcClient.findAllUsers() 表示的操作是一个很耗时的操作，那 Pull-based 的代码的并发能力将很成问题。这就是 Push 模式出现的原因。
 * <p>
 * 当然，上面两个例子只是一个直观的介绍。并不是说传统形式的代码就一定是 Pull 模式，Callback 风格的代码就一定是 Pull 模式。
 * 换言之，代码形式并不是 Pull 与 Push 的本质。从更深的层面说，Pull 模式对应的是同步的、命令式的程序，Push 模式对应的是异步的、非阻塞的、反应式的程序。
 * 因此，虽然在代码形式上说 Java8 Stream 和 Reactive Streams 的代码有些像，但从本质上来说，同步、阻塞的 Java8 Stream 与异步、非阻塞的 Reactive Streams 有着很大的差别。
 * 因此 Reactive Streams 不仅在形的层面，以接口定义的形式对反应式编程做出了规范，更在实的层面定义了 TCK，用来保证相关实现确实满足了异步、非阻塞等等的要求。
 * <p>
 * Java 9 Flow API实现了Reactive Streams规范。Flow API是Iterator和Observer模式的组合。Iterator在pull模型上工作，用于应用程序从源中拉取项目；而Observer在push模型上工作，并在item从源推
 * 送到应用程序时作出反应。
 * Java 9 Flow API订阅者可以在订阅发布者时请求N个项目。然后将项目从发布者推送到订阅者，直到推送玩所有项目或遇到某些错误。
 * <p>
 * Servlet3.0之前和3.0的区别？
 * 3.0 之前Servlet 线程会一直阻塞，只有当业务处理完成并返回后时结束 Servlet线程。
 * 3.0 规范其中一个新特性是异步处理支持,即是在接收到请求之后，Servlet 线程可以将耗时的操作委派给另一个线程来完成，在不生成响应的情况下返回至容器。即反应式的编程。
 * <p>
 * SpringMVC 是通过 @Controller和 @RequestMapping来定义路由。
 * WebFlux 是基于 Functional 函数式的路由 RouterFunctions。
 * <p>
 * <p>
 * 什么是反应式编程(Reactive)？
 * 就是基于事件驱动(事件模式或者说订阅者模式)，类似于 Netty异步事件的编程模型，对不同的事件做不同的处理。所有信息都通过一个编程模型处理，就像水在管道里面
 * 运动一样（这里把事件比作水流），所有的信息都封装成一个Channel，这个channel就像在管道中流动一样,被管道中的这些处理器所处理。
 * 比如大名鼎鼎的 React 前端框架配合redux 流模型，将服务器返回的信息包装成 action数据流，然后根据 action去映射到页面上，页面随着 action的改变而改变。页面
 * 和数据就相当于这管道中的东西，被一层一层的梳理，展示。
 * <p>
 * 反应式的编程模型的好处是什么？
 * Servlet 3.0之前线程会一直阻塞，只有当业务处理完成并返回后时，才会结束 Servlet线程。3.0规范其中一个新特性是异步处理支持，即是在接收到请求之后，Servlet线
 * 程可以将耗时的操作委派给另一个线程来完成，在不生成响应的情况下返回至容器。
 * <p>
 * 我们假设，设置tomcat最大线程为200，遇到200个非常耗时的请求。那么当有200个线程同时并发在处理，当有 201个请求的时候，就已经处理不了，因为所有的线程都阻塞了。
 * 这是3.0之前的处理情况。
 * 而3.0之后异步处理，类似于Netty一样就一个boss线程池和work线程池，boss线程只负责接收请求，work线程只负责处理逻辑。那么servlet3.0规范中，这200个线程只负责
 * 接收请求，然后每个线程将收到的请求，转发到work线程去处理。因为这200个线程只负责接收请求，并不负责处理逻辑，故不会被阻塞，而影响通信，就算处理非常耗时，
 * 也只是对work线程形成阻塞，所以当再来请求，同样可以处理。其主要应用场景是针对业务处理较耗时的情况可以减少服务器资源的占用，并且提高并发处理速度。
 * <p>
 * Mone和Flux都是数据反应式编程的核心组件，开发人员就是多利用其编写出高效率的代码
 * <p>
 * Reactor是JVM的完全非阻塞反应式编程基础，具有高效的需求管理（以管理“背压”的形式）。它直接与Java 8功能的API，特别是整合CompletableFuture，Stream和 Duration。它提供了可组合的异步序列API Flux（用于[N]元素）和Mono（用于[0 | 1]元素），广泛地实现了Reactive Extensions规范。这段的重点是和Java8结合利用lambda表达式简洁的优点。
 * <p>
 * Flux 相当于一个 RxJava Observable 观察者
 * <p>
 * 观察者可以把生产者的消息Publisher,推送给消费者subscribe
 * <p>
 * 我们可以把Mono理解为一个结果它对应的数据是 1，其实可以理解为对结果的一个包装
 * <p>
 * Flux 和 Mono 都是一个数据的载体，不同的是 Flux 是一种集合(0,n)，Mono 是一个实体包装(0,1)。
 */
public class InstrucMain {
}
