package com.hyman.springboot.demo;

import com.hyman.springboot.zk.WatcherApi;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

public class ZkClientDemo {

    @Autowired
    private ZooKeeper zkClient;

    @Value("${app.servers}")
    private String servers;

    private String parentNode = "/servers";

    private volatile ArrayList<String> serversList = new ArrayList<>();

    public void getServerList() throws Exception {

        // 获取服务器子节点信息，并且对父节点进行监听
        List<String> children = zkClient.getChildren(parentNode, new WatcherApi());
        ArrayList<String> servers = new ArrayList<>();

        for (String child : children) {
            byte[] data = zkClient.getData(parentNode + "/" + child, false, null);
            servers.add(new String(data));
        }

        // 把 servers 赋值给成员 serverList，已提供给各业务线程使用
        serversList = servers;
        System.out.println(serversList);
    }

    public void business() throws Exception {
        System.out.println("client is working ...");

        Thread.sleep(Long.MAX_VALUE);
    }

    public void mainMethod(String[] args) throws Exception {

        // 获取servers的子节点信息，从中获取服务器信息列表
        getServerList();

        // 业务进程启动
        business();
    }
}
