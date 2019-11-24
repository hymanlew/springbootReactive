package com.hyman.springboot.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static com.mongodb.client.model.Filters.eq;

/**
 * http://mongodb.github.io/mongo-java-driver/3.10/driver/getting-started/quick-start/
 *
 * @author niaoshuai
 * @date 2019年02月28日15:02:01
 */
public class SyncMongoTest {

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
                .append("count",1)
                .append("versions", Arrays.asList("\"v3.2\", \"v3.0\", \"v2.6\""))
                .append("info",new Document("x", 203).append("y", 102));

        collection.insertOne(document);
    }

    @Test
    public  void find(){
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("d1");
        MongoCollection<Document> collection = database.getCollection("c1");
        Document first = collection.find(eq("name", "MongoDB")).first();
        Assert.assertNotNull(first);
    }
}
