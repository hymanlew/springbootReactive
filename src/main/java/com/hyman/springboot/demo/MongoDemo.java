package com.hyman.springboot.demo;

import com.hyman.springboot.entity.Customer;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import io.micrometer.core.instrument.util.JsonUtils;
import org.bson.Document;

/**
 在 mongodb 中基本的概念是文档、集合、数据库（多个文档组成集合，多个集合组成数据库）：
 SQL术语/概念	MongoDB术语/概念	        解释/说明
 database		database				数据库，一个服务器中可以有多个数据库。用于保存集合。
 table			collection				数据库表/集合，集合类似于数组，在集合中可以存放文档。一个数据库中可以有多个集合。
 row			document				数据记录行/文档，文档是 MongoDB 中的最小单位，我们存储和操作的内容都是文档。一个集合中可以有多个文档。文档可以是各种各样的，没有格式要求。
 column			field					数据字段/域
 index			index				    索引
 join 			joins	 		        表连接，MongoDB 不支持
 primary key	primary key				主键，MongoDB 自动将 _id 字段设置为主键


 在 mongoDB 中，数据库和集合都不需要创建。基本的指令：
 show dbs， 			- 显示所有的数据库
 use 数据库名，		- 进入到指定的数据库中，即使没有该库也可以进。因为数据库和集合会在第一次插入文档时，自动创建。
 db，				- 显示当前数据库
 show collections，	- 显示当前数据库的所有集合

 db.<collection>.insert(doc(s))			- 可以用于向集合中添加一个或多个文档，可以传递一个对象，或一个数组。插入的文档对象会默认添加_id属性，这个属性对应一个唯一的id，是文档的唯一标识。例如，db.users.insert({username:"hy",age:18,gender:"male"})。当然，也可以自定义 id 属性的值。
 db.<collection>.insertOne()			- 向集合中添加一个文档，并且只能传递一个对象。
 db.<collection>.insertMany()			- 向集合中添加多个文档，并且只能传递一个数组作为参数。
 db.<collection>.find()					- 查询当前集合中的所有文档，返回的是一个数组。findOne() 只能查询到第一个，返回的是一个对象。
 db.<collection>.count()				- 统计集合中文档的数量。
 db.collection.update()					- 替换和修改文档，可以传递两个参数，一个是查询文档，一个是新的文档，这样符和条件的文档将会被新文档所替换。update()的第三个参数，用来指定是否使用 upsert（没有就新增），默认为 false。第四个参数用来指定是否同时修改多个文档，默认为 false。
 db.collection.remove()					- 可以用来移除指定文档对象。方法接收一个查询文档作为参数，只有符合条件的文档才会被删除。删除数据是永久的，不能撤销。组合了 deleteOne，deleteMany 两个方法。
 db.collection.drop()					- 删除集合，如果数据库中只有一个集合，则会连带数据库一块被删除。
 db.dropDatabase()						- 删除数据库。

 MongoDB 语法（使用的是 JS 语言基本语法）与现有关系型数据库，SQL 语法比较：
 MongoDB 语法            													MySql 语法
 db.test.insert({'name':'foobar','age':25})    	<==>        insert into test ('name','age') values('foobar',25)
 db.test.find()                                	<==>        select *from test
 db.test.find()[0].name             			<==>        select name from test  limit 1
 db.test.find({'name':'foobar'})             	<==>        select * from test where name like 'foobar'，find 操作执行的是，等于或数组中包含该值的查询。
 db.test.find({'ID':10}).count()             	<==>        select count(*) from test where ID=10

 db.test.find({ }).limit(10)          			<==>        select * from test limit 10，查询集合中前 10 条数据。用于分页查询。limit（每页条数）.skip（每页条数 * （页码 - 1））。
 db.test.find().skip(10).limit(10)          	<==>        select * from test limit 10,20。查询集合中第二个 10 条数据，即从 11 到 20。并且 skip 和 limit 方法的位置可以互换，因为在 mongoDB 中，它会自动调整这两个方法的位置。

 db.test.find({'ID':{$in:[25,35,45]}})     		<==>        select * from test where ID in (25,35,45)

 db.test.find().sort({'ID':-1})                 <==>        select * from test order by ID desc

 db.test.find('this.ID<20',{name:1})    		<==>        select name from test whereID<20

 db.test.distinct('name',{'ID':{$lt:20}}) 		<==>        select distinct(name) from test where ID < 20

 db.test.remove({})                             <==>       delete * from test
 db.test.remove({'age':20})                     <==>       delete from test where age=20，该操作执行的是，等于或数组中包含该值的查询。
 db.test.remove({'age':{$lt:20}})               <==>       delete from test where age<20，{$lte:20} 为 age<=20，{$gt:20} 为 age>20，{$gte:20} 为 age>=20，{$ne:20} 为  age!=20，{$eq:20} 为  age=20。

 db.test.update({'name':'foobar'},{$set:{'age':36}})	<==> 		update test set age=36 where name like 'foobar'
 db.test.update({'name':'foobar'},{$inc:{'age':3}})	    <==> 		update test set age=age+3 where name like 'foobar'

 db.test.group({key:{'name':true}, cond:{'name':'foo'},
 reduce:function(obj,prev){prev.msum+=obj.marks;},
 initial:{msum:0}})     					            <==>     	select name,sum(marks) from test group by name

 db.test.find({$or:[{'age':{$gt:40}},{'age':{$lt:20}}]}) <==>      查询大于 40，或小于 20 的数。

 模糊查询（$regex）：db.test.find({"name":{$regex:"aaa"}})

 分组个数过滤：db.getCollection('id_mapper').aggregate([{$group:{ _id :"$contract_id",count:{$sum:1}}},{$match:{count:{$gt:1}}}])

 判断是否为空：db.getCollection('id_mapper').find({"sinocardid":{$in:[null]}})

 批量插入数据，使用的是 JS 的语法，但是以下这种方法很慢：
 for(var i=1; i<100; i++){
 db.test.insert({num:i});
 }

 优化之后为：
 var arr = [];
 for(var i=1; i<100; i++){
 arr.push({num:i});
 }
 db.test.insert(arr);

 修改器（全部以 $ 开头）：
 使用 update 会将整个文档替换，但是大部分情况下是不需要这么做的。如果只需要对文档中的一部分进行更新时，可以使用更新修改器来进行。

 例如，$set（set）、$unset（delete）、$inc（数字累加）、$push（向内嵌文档的数组属性中增加值，并且可以重复添加）、$addToSet（向
 内嵌文档的数 组属性中增加值，但是只添加不存在的元素，如果存在则不执行任何操作）。
 因为在 mongoDB 中，文档的属性值也可以是一个文档，所以可以使用 update 命令更新一个属性值为文档。该属性值文档被称为内嵌文档。

 $set 用来指定一个字段的值，如果这个字段不存在，则创建它。语法为：db.test.update(查询对象, {$set:更新对象});
 $unset 可以用来删除文档中一个不需要的字段，用法和set类似。
 $inc 用来增加已有键的值，或者该键不存在那就创建一个。并且它只能用于 Number 类型的值。
 如果需要查询内嵌文档中的属性，则需要通过 . 的方式来查询（即属性 . 内嵌文档属性），并且属性名必须要加引号，否则会查询报错。

 */
public class MongoDemo {

    public MongoCollection<Document> method0(){

        // 连接 mongodb 数据库。并且其默认值就是 "localhost",27017，所以可以不写。
        //MongoClient client = new MongoClient("localhost",27017);
        //MongoClient client = new MongoClient("127.10.54.101",27017);
        MongoClient client = new MongoClient();

        // 连接到指定的数据库
        MongoDatabase testDB = client.getDatabase("test");

        // 获取到指定的集合对象
        MongoCollection<Document> usersCollection = testDB.getCollection("users");

        // 创建一个文档并插入到集合中
        Document document = new Document("name", "hyman");
        document.append("age", 18);
        document.append("say", "hello");

        usersCollection.insertOne(document);
        return usersCollection;
    }

    public void method1(){

        MongoCollection<Document> collection = method0();

        // 创建一个实体类文档并插入到集合中
        Customer customer = new Customer();
        String customerJson = customer.toString();
        Document document = Document.parse(customerJson);

        collection.insertOne(document);
    }

    public void method2(){

        MongoCollection<Document> collection = method0();

        // 获取集合中所有的文档
        FindIterable<Document> documents = collection.find();
        for(Document document : documents){
            System.out.println(document.toJson());
        }

        // 获取集合中，第二个 10 条的文档，即分页功能
        documents = collection.find().limit(10).skip(10);

        // 获取集合中第一个文档，并将该文档转换为实体类
        Document first = collection.find().first();
        String customerJson = first.toJson();

        // 在 java 中 mongoDB 的查询操作符是通过 Filters 的方法来实现的
        documents = collection.find(Filters.eq("name","hyman"));
        first = documents.first();
    }

    public void method3(){

        MongoCollection<Document> collection = method0();

        collection.deleteOne(Filters.eq("name","hyman"));

        collection.updateOne(
                Filters.eq("name","hyman"),
                new Document("$set", new Document("age", 20)));
    }
}
