package com.hyman.springboot.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Redis 测试
 *
 * 不可否认，Jedis 是一个优秀的基于 Java 语言的 Redis 客户端，但是，其不足也很明显：Jedis 在实现上是直接连接 Redis-Server，
 * 在多个线程间共享一个 Jedis 实例时是线程不安全的，如果想要在多线程场景下使用 Jedis，需要使用连接池，每个线程都使用自己的
 * Jedis 实例，当连接数量增多时，会消耗较多的物理资源。
 *
 * 与 Jedis 相比，Lettuce 则完全克服了其线程不安全的缺点：Lettuce 是一个可伸缩的线程安全的 Redis 客户端，支持同步、异步和响
 * 应式模式。多个线程可以共享一个连接实例，而不必担心多线程并发问题。它基于优秀 Netty NIO 框架构建，支持 Redis 的高级功能，如
 * Sentinel，集群，流水线，自动重新连接和 Redis 数据模型。
 *
 * 本场 Chat 将介绍以下内容：
 * Lettuce 重要接口介绍；
 * Redis单机模式下，Lettuce 的使用；
 * Redis集群模式下，Lettuce 的使用；
 * 使用 Lettuce 创建 Redis 集群；
 * 使用 Lettuce 监控 Redis；
 * Lettuce 使用过程中的“坑”：堆内存溢出和堆外内存溢出。
 *
 * @author huaimin
 */
public class RedisTest {

    @Test
    public void simple(){
        // 创建 RedisClient
        RedisClient redisClient = RedisClient.create("redis://127.0.0.1");
        StatefulRedisConnection<String, String> connect = redisClient.connect();
        connect.sync().set("key1", "value2");
        String value1 = connect.sync().get("key1");
        //测试
        Assert.assertEquals(value1,"value1");
        connect.close();
        redisClient.shutdown();
    }

    /**
     * 场景一： 位操作 签到
     */
    @Test
    public void signIn(){
        // 创建 RedisClient
        RedisClient redisClient = RedisClient.create("redis://127.0.0.1");
        //同步连接 (耗时 28秒)
        StatefulRedisConnection<String, String> connect = redisClient.connect();
        for(int i = 0; i<  1000000; i++) {
            connect.sync().setbit("signIn_20190221",i,1);
        }

        //关闭连接
        connect.close();
        //停止
        redisClient.shutdown();
    }

    /**
     * 场景一： 位操作 签到
     */
    @Test
    public void signInAsync(){
        // 创建 RedisClient
        RedisClient redisClient = RedisClient.create("redis://127.0.0.1");
        //同步连接
        // 统计 (100万 耗时 4秒 内存空间占用 122.07)
        StatefulRedisConnection<String, String> connect = redisClient.connect();
        for(int i = 0; i<  1000000; i++) {
            connect.async().setbit("signIn",i,1);
        }
        //关闭连接
        connect.close();
        //停止
        redisClient.shutdown();
    }

    /**
     * 使用批处理之后 加速  8s 表数据量 32M 按天
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @Test
    public void signInMySql() throws ClassNotFoundException, SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/redis?rewriteBatchedStatements=true";
        Class.forName("com.mysql.jdbc.Driver"); // 加载驱动
        Connection mysqlConnection = DriverManager.getConnection(url, "root", "123456"); // 打开一个数据库连接

        PreparedStatement ps = mysqlConnection.prepareStatement("insert into new_table values(?,?)");
        for(int i = 0; i<  1000000; i++) {
            ps.setInt(1, i);
            ps.setInt(2, i);
            ps.addBatch();

            if (i > 0 && i % 500 == 0) {
                ps.executeBatch();
            }
        }
        ps.executeBatch();
        ps.close();// 关闭Statement
        mysqlConnection.close(); // 关闭数据库连接
    }
}
