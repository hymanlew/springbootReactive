package com.hyman.springboot.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 对 zookeeper 的操作（如 create，delete，cd，get 等操作），所使用的路径都必须是从 zookeeper 自身的根路径进行操作的（如 cd /xxx），
 * 因为它判定相对路径是无效的，会报错。
 *
 * ZooKeeper是一个开放源码的分布式服务管理及协调框架，它包含一个简单的原语集。它的主要作用有（即等于文件系统 + 通知机制）：
 * 实现分布式应用程序，分布式消息同步和协调机制，数据一致性的同步服务，统一的配置维护管理，服务端的命名服务（域名负载调用），分布式锁，
 * 服务器节点动态上下线，集群管理（服务节点的管理，主从管理）等。
 *
 * 1，数据发布与订阅（即集中式配置中心，推 + 拉模式）（适用于配置信息的多设备共享，并动态变化更新）：
 * 应用启动时主动到 zookeeper 上获取配置信息，并注册 watcher 监听。
 * 配置管理员变更 zookeeper 配置节点的内容之后。
 * zookeeper 就推送变更到所有监听的应用，触发 watcher 回调函数。
 *
 * 2，软负载均衡：
 * Register 负责域名的注册，服务启动后将域名信息通过 Register 注册到 zookeeper 相对应的域名服务下。
 * Dispatcher 负责域名的解析，可以实现软负载均衡。
 * Scanner 通过定时监测服务状态，动态更新节点地址信息。
 * Monitor 负责收集服务信息与状态监控。
 * Controller 提供后台 Console，提供配置管理功能。后台管理。
 *
 * 3，集群管理（也可以配置成 zookeeper 集群来管理应用集群）：
 * zookeeper 集群的每个 zookeeper 服务器都管理一个应用程序集群。每个应用程序也都把自己的状态信息注册到自己对应的 zookeeper 服务器上，
 * 然后其他服务就可以到 zookeeper 集群服务器中查询到某个应用的状态（在线，离线等）。
 * 并且 zookeeper 集群服务器之间可以配置成主从读写分离架构，由一个应用进行写操作到 zookeeper 之后，在其他 zookeeper 服务器中也能查询
 * 到相应的信息。
 *
 * 4，实现分布式锁，因为它在同一个目录下是不能有同样的节点的。所以可以创建一个 lock 节点，其他人就无法再创建，也就实现的锁的功能。即节
 * 点抢占，并利用它的有序节点 + watch 功能（避免惊群效应），实现锁等待及释放后锁节点的抢占。这就是 zookeeper 分布式锁的原理。
 *
 * 5，实现数据的监听，watch。
 *
 * 6，服务注册（服务地址的维护，服务动态发现，负载均衡），首先有一个根节点（/registry），然后在它下面生成各个服务的持久化节点（每个服
 * 务对应一个节点，持久化后 session 结束或服务重启后节点也不会丢失）。并且每个持久化服务节点下还配置有 url 服务地址临时节点（用于服务
 * 地址维护，及被发现），可使用 getChildrens 来获取到服务节点下所有的 url 配置子节点，并可以让客户端使用 watch 监听地址节点的动态变化
 * （服务动态发现，并且在服务异常或重启时，能获知到地址的动态变化或更新）。
 *
 * 应用根据逻辑，主动获取新的配置信息，并更改自身的逻辑。之所以一直说是获取配置信息，是因为 zookeeper 只能存储少量的数据，不能存储大量
 * 的数据，节点也不支持。所以 zookeeper 存储的全部都是配置类的信息。
 *
 * 惊群效应：
 * 例如有一个节点抢占到并成为锁节点，而另外有三个节点同时 watch 监听了这一个锁节点。而当锁节点被释放后，其他的三个节点也只会有一个节点
 * 会抢占到锁，但锁节点发生变化后，却会触发其他所有的节点。这就是惊群效应。解决这个问题，就可以使用到顺序（有序）节点。我们可以在锁节点
 * 后，创建出多个顺序子节点，并且每一个节点都会监听到上一节点。那么当锁节点被释放后，它就会传递到下一个节点，而不会触发所有的节点。
 *
 */
@Component
public class ZkClient {

    private static final Logger logger = LoggerFactory.getLogger(ZkClient.class);

    @Autowired
    private ZooKeeper zkClient;

    /**
     * 测试方法  初始化
     */
    @PostConstruct
    public  void init() throws Exception {
        String path = "/zk-watcher-2";
        logger.info("【执行初始化测试方法。。。。。。。。。。。。】");

        createNode(path,"测试");

        String value = getData(path, new WatcherApi());
        logger.info("【执行初始化测试方法getData返回值。。。。。。。。。。。。】={}",value);

        // 删除节点出发 监听事件
        deleteNode(path);

        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * 创建持久化节点
     * @param path
     * @param data
     */
    public boolean createNode(String path, String data){

        try {
            // 数据的增删改查
            // 参数1：要创建的节点的路径； 参数2：节点数据 ； 参数3：节点权限（开放的非安全的） ；参数4：节点的类型（持久，临时，带序号等等）
            String nodeCreated = zkClient.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            logger.info("【创建持久化节点】{},{},{}", path, data);
            return true;

        } catch (Exception e) {
            logger.error("【创建持久化节点异常】{},{},{}",path,data,e);
            return false;
        }
    }

    /**
     * 获取当前节点的子节点(不包含孙子节点)
     * @param path 父节点path
     */
    public List<String> getChildren(String path) throws KeeperException, InterruptedException{

        // watch 参数表示是否需要监听
        List<String> children = zkClient.getChildren(path, false);
        for (String child : children) {
            System.out.println(child);
        }

        return children;
    }

    /**
     * 判断指定节点是否存在，并获取到节点的详情信息
     * @param path
     * @param needWatch  指定是否复用 zookeeper 中默认的 Watcher（即初始化 zkClient 时指定的 Watcher 监听器）
     * @return
     */
    public Stat exists(String path, boolean needWatch){

        /**
         * 因为 zookeeper watch 是一次性触发，即只能监听到一次更新操作。如果要实现能够连续不断的监听到节点的更新操作，则当前设置监听
         * 操作后程序就不能停止，要一直保持在运行状态。
         * 并且监听到的信息，是回调 watcher 接口的 process 方法进行执行和相关操作的。所以要连接监听此节点，还要在 process 方法中，重
         * 新设置监听。
         */
        try {
            //System.out.println(stat == null ? "not exist" : "exist");
            Stat stat = zkClient.exists(path, needWatch);

            Thread.sleep(Long.MAX_VALUE);
            return stat;
        } catch (Exception e) {
            logger.error("【断指定节点是否存在异常】{},{}",path,e);
            return null;
        }
    }

    /**
     * 检测结点是否存在，并设置监听事件
     * 三种监听类型： 创建，删除，更新
     *
     * @param path
     * @param watcher  传入指定的监听类
     * @return
     */
    public Stat exists(String path, Watcher watcher ){

        try {
            return zkClient.exists(path, watcher);

        } catch (Exception e) {
            logger.error("【断指定节点是否存在异常】{},{}",path,e);
            return null;
        }
    }


    /**
     * 修改持久化节点
     * @param path
     * @param data
     */
    public boolean updateNode(String path, String data){

        try {
            //zk的数据版本是从0开始计数的。如果客户端传入的是-1，则表示zk服务器需要基于最新的数据进行更新。如果对zk的数据节点的更新操作没有原子性要求则可以使用-1.
            //version参数指定要更新的数据的版本, 如果version和真实的版本不同, 更新操作将失败. 指定version为-1则忽略版本检查
            zkClient.setData(path,data.getBytes(),-1);
            return true;
        } catch (Exception e) {
            logger.error("【修改持久化节点异常】{},{},{}",path,data,e);
            return false;
        }
    }

    /**
     * 删除持久化节点
     * @param path
     */
    public boolean deleteNode(String path){

        try {
            //version参数指定要更新的数据的版本, 如果version和真实的版本不同, 更新操作将失败. 指定version为-1则忽略版本检查
            zkClient.delete(path,-1);
            return true;
        } catch (Exception e) {
            logger.error("【删除持久化节点异常】{},{}",path,e);
            return false;
        }
    }

    /**
     * 获取指定节点的值
     * @param path
     * @return
     */
    public  String getData(String path,Watcher watcher){

        try {
            // Stat 用于装载节点的详情信息
            Stat stat = new Stat();
            byte[] bytes = zkClient.getData(path,watcher,stat);
            return  new String(bytes);

        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

}
