package org.kakaowork.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.net.URLDecoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RestAPIUtilTest {

    @Test
    public void REST호출테스트_공공API() throws Exception{
        //given
        CustomLogger cl = CustomLogger.getLogger();
        String SERVICE_KEY = "OWU0bFwlp0xvMK26VmtnmAkrrOmqNBhKkF6oIRTY9gMuiYOf%2B9EKmt%2F5CoTlesDB%2FGTZOAj6BMjelTofL2SIOw%3D%3D";
        String serviceKeyDecoded = URLDecoder.decode(SERVICE_KEY, "UTF-8");

        RestAPIUtil restApiUtil = new RestAPIUtil();
        int pageNo = 1;
        int numOfRows = 1000;

        String urlStr = "http://api.data.go.kr/openapi/tn_pubr_public_univ_info_api";

        //when
        String response = restApiUtil.callAPI(restApiUtil.SetUrl(urlStr, serviceKeyDecoded, pageNo, numOfRows, "json"), cl);

        //JSON 파싱
        JsonObject objData = new Gson().fromJson(response, JsonObject.class);
        pageNo = objData.get("response").getAsJsonObject().get("body").getAsJsonObject().get("pageNo").getAsInt();
        JsonArray items = objData.get("response").getAsJsonObject().get("body").getAsJsonObject().get("items").getAsJsonArray();

        //then
        assertEquals(1000, items.size());
        assertEquals(1, pageNo);
        assertNotNull(response);
    }

}
