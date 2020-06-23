package com.hyman.springboot.zk;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 监听器是一个接口，在代码中可以实现 Wather 接口，并实现其中的 process 方法，方法中实现我们自己的业务逻辑。
 *
 * 监听器的注册是在获取数据的操作中实现：
 * getData(path,watch?) 监听的事件是：节点数据变化事件
 * getChildren(path,watch?) 监听的事件是：节点下的子节点增减变化事件
 *
 *
 * ZooKeeper Watch：
 * 它是一种监听通知机制。Zookeeper 所有的读操作 getData(),  getChildren() 和 exists() 都可以设置监视 (watch)，这是 zookeeper API
 * 设置监听的三种方式。对应的是 get，exists，getChildren 三个命令。监视事件可以理解为一次性的触发器，即只能触发一次。如果本次触发失
 * 败（网络或其他原因），则也意味着其代表的事件也丢失了。官方定义如下：
 * a watch event is one-time trigger, sent to the client that set the watch, whichoccurs when the data for which the watch was set changes。
 *
 * Watch的三个关键点：
 * 1，One-time trigger（一次性触发），当设置监视的数据发生改变时，该监视事件会被发送到客户端，例如客户端调用了getData("/znode1", true)，
 * 并且稍后 /znode1 节点上的数据发生了改变或者被删除了，客户端将会获取到 /znode1 发生变化的监视事件，而如果 /znode1 再一次发生了变化，
 * 除非客户端再次对 /znode1 设置监视，否则客户端不会收到事件通知。
 *
 * 2，Sent to the client（发送至客户端），Zookeeper客户端和服务端是通过 socket 进行通信的，由于网络存在故障，所以监视事件很有可能不
 * 会成功地到达客户端，监视事件是异步发送至监视者的，Zookeeper 本身提供了顺序保证(ordering guarantee)：即客户端只有首先看到了监视事
 * 件后，才会感知到它所设置监视的znode发生了变化。网络延迟或者其他因素可能导致不同的客户端在不同的时刻感知某一监视事件，但是不同的客
 * 户端所看到的一切都具有一致的顺序。
 *
 * 3，The data for which the watch was set（被设置 watch 的数据），这意味着 znode 节点本身具有不同的改变方式。可以想象为  Zookeeper
 * 维护了两条监视链表：数据监视和子节点监视。getData() 和 exists()设置数据监视，getChildren()设置子节点监视。也可以想象 Zookeeper 设
 * 置的不同监视返回不同的数据，getData() 和 exists() 返回 znode 节点的相关信息，而 getChildren() 返回子节点列表。setData() 会触发设
 * 置在某一节点上所设置的数据监视（假定数据设置成功），而一次成功的 create() 操作则会发出当前节点上所设置的数据监视以及父节点的子节点监
 * 视。一次成功的 delete 操作将会触发当前节点的数据监视和子节点监视事件，同时也会触发该节点父节点的 child watch。
 *
 *
 * Zookeeper 中的监视是轻量级的，因此容易设置、维护和分发。当客户端与 Zookeeper 服务器失去联系时，客户端并不会收到监视事件的通知，只有
 * 当客户端重新连接后，若在必要的情况下，以前注册的监视会重新被注册并触发，对于开发人员来说这通常是透明的。只有一种情况会导致监视事件的
 * 丢失，即：通过 exists()设置了某个znode节点的监视，但是如果某个客户端在此 znode 节点被创建和删除的时间间隔内与 zookeeper 服务器失去
 * 了联系，该客户端即使稍后重新连接 zookeeper 服务器后也得不到事件通知。
 *
 */
public class WatcherApi implements Watcher {

    private static final Logger logger = LoggerFactory.getLogger(WatcherApi.class);

    @Autowired
    private ZooKeeper zkClient;

    @Override
    public void process(WatchedEvent event) {

        /**
         * 三种监听类型： 创建，删除，更新
         */
        logger.info("【Watcher监听事件】={}",event.getState());
        logger.info("【监听路径为】={}",event.getPath());
        logger.info("【监听的类型为】={}",event.getType());

        /**
         * 因为 zookeeper watch 是一次性触发，即只能监听到一次更新操作。如果要实现能够连续不断的监听到节点的更新操作，则当前设置监听
         * 操作后程序就不能停止，要一直保持在运行状态。
         * 并且监听到的信息，是回调 watcher 接口的 process 方法进行执行和相关操作的。所以要连接监听此节点，还要在 process 方法中，重
         * 新设置监听。
         */
        try {
            byte[] bytes=zkClient.getData(event.getPath(),true, new Stat());

            List<String> servers = zkClient.getChildren("/servers",true);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

