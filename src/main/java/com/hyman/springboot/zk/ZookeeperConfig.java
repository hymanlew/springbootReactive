package com.hyman.springboot.zk;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;

@Configuration
public class ZookeeperConfig {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperConfig.class);

    @Value("${zookeeper.address}")
    private    String connectString;

    @Value("${zookeeper.timeout}")
    private  int sessionTimeout;

    private  ZooKeeper zooKeeper = null;

    @Bean(name = "zkClient")
    public ZooKeeper zkClient(){

        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);

            /**
             * 连接成功后，会回调 watcher 监听，此连接操作是异步的，执行完 new 语句后，直接调用后续代码。
             * 可指定多台服务地址 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
             */
            zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    // 收到事件通知后的回调函数（用户的业务逻辑）
                    System.out.println(event.getType() + "--" + event.getPath());

                    // 如果收到了服务端的响应事件，连接成功
                    if(Event.KeeperState.SyncConnected == event.getState()){
                        countDownLatch.countDown();
                    }

                    try {
                        zooKeeper.exists(event.getPath(), true);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            countDownLatch.await();
            logger.info("【初始化ZooKeeper连接状态....】={}",zooKeeper.getState());

        }catch (Exception e){
            logger.error("初始化ZooKeeper连接异常....】={}",e);
        }

        return  zooKeeper;
    }

}
