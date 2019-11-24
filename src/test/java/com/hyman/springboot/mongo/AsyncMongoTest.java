package com.hyman.springboot.mongo;

import com.hyman.springboot.config.OperationSubscriber;
import com.mongodb.reactivestreams.client.*;
import org.bson.Document;
import org.junit.Test;

import java.util.Arrays;

/**
 * boot 教程及项目：
 * https://github.com/spring-projects/spring-boot/tree/2.1.x/spring-boot-samples/spring-boot-sample-data-mongodb
 * https://hub.docker.com/_/mongo
 *
 * http://mongodb.github.io/mongo-java-driver-reactivestreams/1.11/getting-started/quick-tour/
 *
 * @author huaimin
 * @date 2019年02月28日15:02:01
 */
public class AsyncMongoTest {

    @Test
    public void hello(){

//        MongoClient mongoClient = MongoClients.create();
//        MongoClient mongoClient = MongoClients.create("mongodb://hostOne:27017,hostTwo:27018");

        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("d1");
        MongoCollection<Document> collection = database.getCollection("c1");

        // 定义数据
//        {
//            "name" : "MongoDB",
//            "type" : "database",
//            "count" : 1,
//            "versions": [ "v3.2", "v3.0", "v2.6" ],
//            "info" : { x : 203, y : 102 }
//        }
        Document document = new Document();
        document.append("name","MongoDB")
                .append("type", "database")
                .append("count",2)
                .append("versions", Arrays.asList("\"v3.2\", \"v3.0\", \"v2.6\""))
                .append("info",new Document("x", 203).append("y", 102));

        //方式1
        collection.insertOne(document).subscribe(new OperationSubscriber<Success>());

        //方式2
//        Publisher<Success> publisher = collection.insertOne(document);
//        publisher.subscribe(new Subscriber<Success>() {
//            public void onSubscribe(Subscription s) {
//                System.out.println("s = " + s);
//            }
//
//            public void onNext(Success success) {
//                System.out.println("success = " + success);
//            }
//
//            public void onError(Throwable t) {
//                System.out.println("t = " + t);
//            }
//
//            public void onComplete() {
//                System.out.println(" complete ");
//            }
//        });
    }

}
