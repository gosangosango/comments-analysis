package org.kakaowork;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import org.kakaowork.util.CustomLogger;
import org.kakaowork.util.DBConnection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentsAnalysisTest {

    @Test
    public void 정규식패턴매치1_findSchoolPattern(){
        //given
        CommentsAnalysis ca = new CommentsAnalysis();

        String eachComment = "수원, 창현고 짜장면 먹고싶어요\n" +
                "사진은 제가 먹다남은 짬뽕이네요\n" +
                "재학중은 아니지만 모교학생분들의 배를 불러드리게 하고자 이 이벤트에 참여하는 바입니다\n" +
                "(팔로워님들 좋아요 한번씩 부탁합니다..)";
        //when

        //학교명 정상입력케이스
        //Pattern passPattern1 = Pattern.compile("[가-힣]+(학교)");
        //학교명 약어 사용 케이스
        Pattern passPattern2 = Pattern.compile("[가-힣]+(초|중|고|대)");


        Matcher matcher2 = passPattern2.matcher(eachComment);

        while (matcher2.find()) {
            String matchString2 = matcher2.group();
            System.out.println(matchString2);
        }

        //assertEquals(1, resultSet.size());
    }

    @Test
    public void 정규식패턴매치_findSchoolPattern(){
        //given
        CommentsAnalysis ca = new CommentsAnalysis();

        String eachComment = "저희 급식 수준인데...주세요...짜장면..❣️\n" +
                "경북 영천/영천여고등학교 \n" +
                "짜장면이 고파요\n" +
                "저희를 위해 하나의 짜장면이 필요합니다..\n" +
                "저희를 보아주세요, 저희를 도와주세요, 저희를 구출해주세요.. 부탁드립니다..\n" +
                "김예림김예진김수민윤유정김명주신연홍강한아정다현장다영박다인";
        //when
        Set<String> resultSet = ca.findSchoolPattern(eachComment);

        //then
        assertEquals(1, resultSet.size());
    }

    @Test
    public void 학교이름찾기_findSchoolFromDB(){
        //given

        MongoClient mongoClient = DBConnection.getConnection();
        Set<String> matchSchoolName = new HashSet<>();
        MongoCollection<Document> coll =  mongoClient.getDatabase("kakaobank").getCollection("school");
        Set<String> findNames = new HashSet<>();

        //유효학교
        findNames.add("한동대학교");
        findNames.add("부평고등학교");
        findNames.add("부광중학교");
        //미존재
        findNames.add("아바타초등학교");

        //when
        for(String findName : findNames){
            if(coll.countDocuments(Filters.in("schlNm", findName)) > 0){
                matchSchoolName.add(findName);
            }
        }
        //then
        assertEquals(3, matchSchoolName.size());
    }

    @Test
    public void 결과값세팅_addSchoolCntMap(){
        //given
        CommentsAnalysis ca = new CommentsAnalysis();
        ca.schoolCntMap = new HashMap<>();
        Set<String> findNames = new HashSet<>();
        Set<String> findNames2 = new HashSet<>();
        Set<String> findNames3 = new HashSet<>();

        findNames.add("한동대학교");
        findNames.add("부평고등학교");
        findNames.add("부광중학교");

        findNames2.add("한동대학교");
        findNames2.add("부평고등학교");

        findNames3.add("한동대학교");


        //when
        ca.addSchoolCntMap(findNames);
        ca.addSchoolCntMap(findNames2);
        ca.addSchoolCntMap(findNames3);
        //then
        assertEquals(3, ca.schoolCntMap.get("한동대학교"));
        assertEquals(2, ca.schoolCntMap.get("부평고등학교"));
        assertEquals(1, ca.schoolCntMap.get("부광중학교"));
    }

}
