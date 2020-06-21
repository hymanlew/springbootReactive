package com.hyman.springboot.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class ZkClient {

    private static final Logger logger = LoggerFactory.getLogger(ZkClient.class);

    @Autowired
    private ZooKeeper zkClient;

    /**
     * 测试方法  初始化
     */
    @PostConstruct
    public  void init(){
        String path = "/zk-watcher-2";
        logger.info("【执行初始化测试方法。。。。。。。。。。。。】");

        createNode(path,"测试");

        String value = getData(path, new WatcherApi());
        logger.info("【执行初始化测试方法getData返回值。。。。。。。。。。。。】={}",value);

        // 删除节点出发 监听事件
        deleteNode(path);
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
     * 判断指定节点是否存在
     * @param path
     * @param needWatch  指定是否复用 zookeeper 中默认的 Watcher（即初始化 zkClient 时指定的 Watcher 监听器）
     * @return
     */
    public Stat exists(String path, boolean needWatch){

        try {
            //System.out.println(stat == null ? "not exist" : "exist");
            return zkClient.exists(path, needWatch);

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
            Stat stat=new Stat();
            byte[] bytes=zkClient.getData(path,watcher,stat);
            return  new String(bytes);
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

}
