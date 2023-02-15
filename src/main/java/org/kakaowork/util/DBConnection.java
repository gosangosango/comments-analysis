package org.kakaowork.util;

import com.mongodb.MongoClient;

public class DBConnection {

    private static MongoClient mongoClient;

    private static final String LOCAL_IP = "127.0.0.1";
    private static final int LOCAL_PORT = 27017;

    public static MongoClient getConnection(String ip, int port){
        CustomLogger cl = CustomLogger.getLogger();
        cl.info("[MongoDB CREATE CONNECTION] IP = " + ip);
        cl.info("[MongoDB CREATE CONNECTION] PORT = " + port);
        if(mongoClient == null) {
            mongoClient = new MongoClient(ip, port);
        }
        return mongoClient;
    }

    public static MongoClient getConnection(){
        CustomLogger cl = CustomLogger.getLogger();
        cl.info("[MongoDB CREATE CONNECTION] IP = " + LOCAL_IP);
        cl.info("[MongoDB CREATE CONNECTION] PORT = " + LOCAL_PORT);
        if(mongoClient == null) {
            mongoClient = new MongoClient(LOCAL_IP, LOCAL_PORT);
        }
        return mongoClient;
    }

}
