package org.kakaowork;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.kakaowork.util.CustomLogger;
import org.kakaowork.util.DBConnection;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentsAnalysis {

    private static final String INPUT_FILE_PATH = "C:\\Kakaobank-comment-analysis\\comments.csv";
    private static final String RESULT_FILE_PATH = "C:\\Kakaobank-comment-analysis\\result.txt";
    public static Map<String, Integer> schoolCntMap;
    public void doAnalyisis() {
        CustomLogger cl = CustomLogger.getLogger();
        schoolCntMap = new HashMap<String, Integer>();

        cl.info("[INPUT FILE READ] FILE = " + INPUT_FILE_PATH);
        cl.info("[INPUT FILE READ] START");
        int cnt = 0; //댓글 수
        String eachComment = "";  //댓글 문자열
        String line = "";

        try (
            FileInputStream input = new FileInputStream(INPUT_FILE_PATH);
            InputStreamReader reader = new InputStreamReader(input,"UTF-8");
            BufferedReader bufReader = new BufferedReader(reader);
            MongoClient mongoClient = DBConnection.getConnection();
        ) {
            MongoCollection<Document> coll =  mongoClient.getDatabase("kakaobank").getCollection("school");
            while ((line = bufReader.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                } else if (line.charAt(line.length() - 1) == '"') { //댓글단위 분석 수행 및 변수 초기화
                    cnt++;
                    eachComment += " " + line;
                    //댓글 내 학교명 패턴검색 (중복제거)
                    Set<String> findNames = findSchoolPattern(eachComment);

                    //DB내 유효 학교 검색
                    findSchoolFromDB(findNames, coll);
                    eachComment = "";
                } else {
                    eachComment += " " + line;
                }

            }
        }catch (IOException e) {
            e.printStackTrace();
            cl.info("[FILE WRITE EXCEPTION] " + e.getMessage());
        }
        cl.info("[INPUT FILE READ] END");
        cl.info("[ANALYZED INFO] 댓글 수 = " + cnt);
        cl.info("[ANALYZED INFO] 언급 된 유효 학교 갯수 = " + schoolCntMap.keySet().size());

        //댓글 분석 값 파일 출력
        resultFileWrite();
    }

    //패턴종류 : 초/중/고/대/초등학교/중학교/고등학교/대학교/(여자)/(체육)
    public Set<String> findSchoolPattern(String eachComment){
        Set<String> findResult = new HashSet<>();
        //학교명 정상입력케이스
        Pattern passPattern1 = Pattern.compile("[가-힣]+(초등학교|중학교|고등학교|대학교|학교)");
        //학교명 약어 사용 케이스
        Pattern passPattern2 = Pattern.compile("[가-힣]+(초|중|고|대)");

        Matcher matcher1 = passPattern1.matcher(eachComment);
        Matcher matcher2 = passPattern2.matcher(eachComment);

        while (matcher1.find()) {
            String matchString1 = matcher1.group();
            findResult.add(matchString1);
        }

        while (matcher2.find()) {
            String matchString2 = matcher2.group();
            String transString = "";
            char preNameChar = matchString2.charAt(matchString2.length() - 2);
            char sufNameChar = matchString2.charAt(matchString2.length() - 1);

            //여중,여고,여대,체고,체대 약어 케이스 변환
            switch(preNameChar){
                case '여' :
                    transString = matchString2.substring(0, matchString2.length() - 2) + "여자";
                    break;
                case '체' :
                    transString = matchString2.substring(0, matchString2.length() - 2) + "체육";
                    break;
                default :
                    transString = matchString2.substring(0, matchString2.length() - 1);
                    break;
            }

            switch(sufNameChar){
                case '초' :
                    transString = transString + "초등학교";
                    break;
                case '중' :
                    transString = transString + "중학교";
                    break;
                case '고' :
                    transString = transString + "고등학교";
                    break;
                case '대' :
                    transString = transString + "대학교";
                    break;
                default :
                    break;
            }
            findResult.add(transString);
        }
        return findResult;
    }

    public void findSchoolFromDB(Set<String> findNames, MongoCollection<Document> coll){
        CustomLogger cl = CustomLogger.getLogger();
        Set<String> matchSchoolName = new HashSet<String>();
        for(String findName : findNames){
            if(coll.countDocuments(Filters.in("schlNm", findName)) > 0){
                matchSchoolName.add(findName);
            }
        }
        addSchoolCntMap(matchSchoolName);
    }

    public void addSchoolCntMap(Set<String> matchStrings){
        CustomLogger cl = CustomLogger.getLogger();
        for(String matchString : matchStrings){
            schoolCntMap.put(matchString, schoolCntMap.getOrDefault(matchString, 0) + 1);
        }
    }

    public void resultFileWrite(){
        CustomLogger cl = CustomLogger.getLogger();
        String line = "";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESULT_FILE_PATH))) {
            for(String key : schoolCntMap.keySet()){
                line = key + "\t" + schoolCntMap.get(key) + "\n";
                writer.write(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            cl.info("[FILE WRITE EXCEPTION] " + e.getMessage());
        }
    }

}
