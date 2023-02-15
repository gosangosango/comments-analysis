package org.kakaowork;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.kakaowork.util.CustomLogger;
import org.kakaowork.util.DBConnection;

public class Main {

    public static final String AWS_MONGODB_IP = "34.198.137.161";
    public static final int AWS_MONGO_PORT = 27017;
    public static void main(String[] args) throws Exception{

        //인스턴스 생성
        CustomLogger cl = CustomLogger.getLogger();
        SchoolService sc = new SchoolService();
        CommentsAnalysis ca = new CommentsAnalysis();

        //로컬 DB Connection
        MongoClient mongoClient = DBConnection.getConnection();
        MongoCollection<Document> coll =  mongoClient.getDatabase("kakaobank").getCollection("school");

        //MongoDB 컬렉션 초기화
        cl.info("[DB DML] Collection drop");
        coll.drop();
        coll.createIndex(Indexes.ascending("schlNm"));
        //학교정보 공공 REST API 호출 + MongoDB에 저장
        sc.getSchoolInfo("SCHOOL", cl);
        sc.getSchoolInfo("UNIV", cl);

        //Comment 파일 Read 및 분석
        ca.doAnalyisis();
    }
}