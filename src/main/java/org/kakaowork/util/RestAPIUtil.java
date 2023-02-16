package org.kakaowork.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RestAPIUtil {

    public String callAPI(URL url,CustomLogger cl){


        cl.info("[REST API CALL] URL = " + url.toString());

        HttpURLConnection conn = null;
        BufferedReader rd = null;
        int responseCode = 200;
        StringBuilder sbInput = new StringBuilder();
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            responseCode = conn.getResponseCode();

            if(responseCode >= 200 && responseCode <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line;
            while ((line = rd.readLine()) != null) {
                sbInput.append(line);
            }
            rd.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            conn.disconnect();
        }

        cl.info("[REST API CALL] RESPONSE CODE = " + responseCode);
        return sbInput.toString();
    }

    public URL SetUrl(String url, String serviceKey, int pageNo, int numOfRows, String type) throws Exception{
        StringBuilder urlBuilder = null;
        urlBuilder = new StringBuilder(url);

        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + URLEncoder.encode(serviceKey, "UTF-8"));/*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(pageNo), "UTF-8")); /*페이지 번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(numOfRows), "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*XML/JSON 여부*/

        return new URL(urlBuilder.toString());
    }
}
