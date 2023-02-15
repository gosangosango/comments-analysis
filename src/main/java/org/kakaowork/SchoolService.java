package org.kakaowork;

import com.google.gson.*;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import org.bson.Document;
import org.kakaowork.util.CustomLogger;
import org.kakaowork.util.DBConnection;
import org.kakaowork.util.RestAPIUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class SchoolService {

    private static final String SERVICE_KEY = "OWU0bFwlp0xvMK26VmtnmAkrrOmqNBhKkF6oIRTY9gMuiYOf%2B9EKmt%2F5CoTlesDB%2FGTZOAj6BMjelTofL2SIOw%3D%3D";
    RestAPIUtil restApiUtil;
    MongoClient mongoClient;

    public void getSchoolInfo (String kind, CustomLogger cl) throws UnsupportedEncodingException {

        String serviceKeyDecoded = URLDecoder.decode(SERVICE_KEY, "UTF-8");

        restApiUtil = new RestAPIUtil();
        mongoClient = DBConnection.getConnection();
        int totalCnt = Integer.MAX_VALUE;
        int curCnt = 0;
        int pageNo = 0;
        int numOfRows = 1000;

        String urlStr = "";
        if("SCHOOL".equals(kind)){
            urlStr = "http://api.data.go.kr/openapi/tn_pubr_public_elesch_info_api"; /*URL*/
        }else if("UNIV".equals(kind)){
            urlStr = "http://api.data.go.kr/openapi/tn_pubr_public_univ_info_api"; /*URL*/
        }

        //Page별 1000개씩 나누어 처리
        while(curCnt + 1000 < totalCnt){
            pageNo++;
            String response = null;
            try {
                response = restApiUtil.callAPI(restApiUtil.SetUrl(urlStr, serviceKeyDecoded, pageNo, numOfRows, "json"), cl);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            //JSON 파싱
            JsonObject objData = new Gson().fromJson(response, JsonObject.class);
            totalCnt = objData.get("response").getAsJsonObject().get("body").getAsJsonObject().get("totalCount").getAsInt();
            pageNo = objData.get("response").getAsJsonObject().get("body").getAsJsonObject().get("pageNo").getAsInt();
            curCnt =  (pageNo - 1) * numOfRows;
            JsonArray items = objData.get("response").getAsJsonObject().get("body").getAsJsonObject().get("items").getAsJsonArray();

            //배열 순회 및 데이터 처리
            JsonObject tmp;
            String tmpSchlNm = "";
            List<Document> documentList = new ArrayList<Document>();
            StringBuilder sb= new StringBuilder();
            int seq = curCnt;
            for(JsonElement item: items){
                seq++;
                tmp = (JsonObject) item.getAsJsonObject();
                tmpSchlNm = tmp.get("schlNm").getAsString();
                documentList.add(new Document("schlNm", tmpSchlNm).append("id", seq));
            }
            MongoCollection<Document> coll =  mongoClient.getDatabase("kakaobank").getCollection("school");
            InsertManyOptions options = new InsertManyOptions();
            coll.insertMany(documentList, options.ordered(options.isOrdered()));
        }
    }
}
