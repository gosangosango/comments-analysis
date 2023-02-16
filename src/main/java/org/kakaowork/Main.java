package org.kakaowork;

import org.kakaowork.util.CustomLogger;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static Map<String, Integer> schoolNmMap;
    public static void main(String[] args) throws Exception{

        //인스턴스 생성
        CustomLogger cl = CustomLogger.getLogger();
        SchoolService sc = new SchoolService();
        CommentsAnalysis ca = new CommentsAnalysis();

        //학교 공공데이터 저장 Map
        schoolNmMap = new HashMap<String, Integer>();
        sc.getSchoolInfo("SCHOOL", schoolNmMap, cl);
        sc.getSchoolInfo("UNIV", schoolNmMap, cl);
        cl.info("[schoolNmMap SIZE] = " + schoolNmMap.size());

        //Comment 파일 Read 및 분석
        ca.doAnalyisis(schoolNmMap);
    }
}