package com.hyman.springboot.demo;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * 监听服务器节点动态上下线案例：
 * 1）需求：某分布式系统中，主节点可以有多台，可以动态上下线，任意一台客户端都能实时感知到主节点服务器的上下线。
 * 2）需求分析：注册服务器节点，获取到存活的服务器节点，并监听这些节点，当监听到服务器变动时处理相关业务。
 *
 * 1，当应用服务器集群启动时，就到 zookeeper 集群中注册自己的信息（全部创建临时节点，因为是动态上下线）
 * 2，每个应用服务器当前的客户端连接数，都注册到对应的 zookeeper 集群节点中（通过连接数，来实现负载的调度）
 * 3，从 zookeeper 集群中获取到存活的服务器节点，并监听这些节点。
 * 4，当某些服务器挂掉或新增时，程序就能从 zookeeper 集群中获取到监听信息。
 * 5，通过 watcher 监听器对监听到的服务器节点，进行更新操作。
 */
public class ZkServerDemo {

    @Autowired
    private ZooKeeper zkClient;

    @Value("${app.servers}")
    private String servers;

    private String parentNode = "/servers";

    // 创建主节点（根节点）
    public void creatMain() throws Exception {

        String create = zkClient.create(parentNode, "main".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("/servers is created "+ create);
    }

    // 注册应用服务器集群
    public void registServer(String hostname) throws Exception{

        String create = zkClient.create(parentNode + "/server-" + hostname, hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostname +" is noline "+ create);
    }

    // 业务功能
    public void business(String hostname) throws Exception{
        System.out.println(hostname+" is working ...");

        Thread.sleep(Long.MAX_VALUE);
    }

    public void mainMehod(String[] args) throws Exception {

        creatMain();

        String[] serverIps = servers.split(",");
        for(String s : serverIps){

            // 利用 zk 连接注册服务器信息
            registServer(s);

            // 启动业务功能
            business(s);
        }


    }
}
