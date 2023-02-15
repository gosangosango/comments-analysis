package org.kakaowork.util;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class DBConnectionTest {

    @Test
    public void 몽고DB테스트_DML_AWS(){
        //given
        String AWSMongoIp = "34.198.137.161";
        int AWSMongoPort = 27017;
        MongoClient mongoClient = DBConnection.getConnection(AWSMongoIp, AWSMongoPort);
        MongoCollection<Document> coll =  mongoClient.getDatabase("kakaobank").getCollection("schoolTest");
        Document doc = new Document();

        doc.put("schlNm","부평중학교");
        coll.insertOne(doc);

        //when
        FindIterable<Document> docs = coll.find(Filters.in("schlNm", "부평중학교"));

        //then
        assertEquals("부평중학교", docs.iterator().next().get("schlNm"));
    }

    @Test
    public void 몽고DB테스트_DML(){
        //given
        MongoClient mongoClient = DBConnection.getConnection();
        MongoCollection<Document> coll =  mongoClient.getDatabase("kakaobank").getCollection("schoolTest");
        Document doc = new Document();

        doc.put("schlNm","부평중학교");
        coll.insertOne(doc);

        //when
        FindIterable<Document> docs = coll.find(Filters.in("schlNm", "부평중학교"));

        //then
        assertEquals("부평중학교", docs.iterator().next().get("schlNm"));
    }

    @Test
    public void 몽고DB테스트_DML_Like검색(){
        //given
        MongoClient mongoClient = DBConnection.getConnection();
        MongoCollection<Document> coll =  mongoClient.getDatabase("kakaobank").getCollection("schoolTest");
        Document doc = new Document();

        doc.put("schlNm","부평중학교");
        coll.insertOne(doc);

        //when
        FindIterable<Document> docs = coll.find(Filters.regex("schlNm", "^인천부평중학교*"));

        //then
        assertEquals("부평중학교", docs.iterator().next().get("schlNm"));
    }

}
