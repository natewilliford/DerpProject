package com.herp.derp;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class BitstampApi {

    public static boolean auth(List<NameValuePair> authParams) {
        try {
            final JSONObject balanceValues = BitstampApi.balance(authParams);

            if (balanceValues == null) {
                Log.e(MainActivity.LOG_TAG, "Balance returned null");
                return false;
            } else {
                if (balanceValues.has("error")) {
                    Log.e(MainActivity.LOG_TAG, "Balance has error");
                    try {
                        Log.e(MainActivity.LOG_TAG,  balanceValues.getString("error"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                } else {
                    Log.i(MainActivity.LOG_TAG, "Successfully set up account");
                    Log.d(MainActivity.LOG_TAG, balanceValues.toString());
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(MainActivity.LOG_TAG, "Exception building balance request");
            return false;
        }
    }

    public static JSONObject balance(List<NameValuePair> authParams) throws Exception {
        return new JSONObject(simpleJSONPostRequest("https://www.bitstamp.net/api/balance/", authParams));
    }

    public static JSONObject ticker() throws Exception {
        return new JSONObject(simpleJSONGetRequest("https://www.bitstamp.net/api/ticker/"));
    }

//    public JSONArray transactions() {
//        try {
//            return new JSONArray(simpleJSONPostRequest("https://www.bitstamp.net/api/user_transactions/", buildDefaultAuthParmas()));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public static JSONObject ticker() {
//        try {
//            return new JSONObject(simpleJSONGetRequest("https://www.bitstamp.net/api/ticker/"));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static List<NameValuePair> buildDefaultAuthParmas(String clientId, String apiKey, String apiSecret) throws Exception {
        String nonce = Long.toString(System.currentTimeMillis());
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        String message = nonce + clientId + apiKey;
        String signature = Util.encodeSignature(apiSecret, message);

        nameValuePairs.add(new BasicNameValuePair("key", apiKey));
        nameValuePairs.add(new BasicNameValuePair("signature", signature));
        nameValuePairs.add(new BasicNameValuePair("nonce", nonce));

        return nameValuePairs;
    }

    private static String simpleJSONGetRequest(String uri) {
        try {
            URI endpoint = new URI(uri);

            HttpGet request = new HttpGet();
            request.setURI(endpoint);

            return stringFromResponse(makeRequest(request));
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static String simpleJSONPostRequest(String uri, List<NameValuePair> params) {
        try {
            URI endpoint = new URI(uri);

            HttpPost request = new HttpPost();
            request.setEntity(new UrlEncodedFormEntity(params));
            request.setURI(endpoint);

            return stringFromResponse(makeRequest(request));
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static HttpResponse makeRequest(HttpRequestBase request) throws IOException {
        HttpClient client = new DefaultHttpClient();
        return client.execute(request);
    }

    private static String stringFromResponse(HttpResponse response) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity()
                .getContent()));
        StringBuffer sb = new StringBuffer("");
        String l = "";
        String nl = System.getProperty("line.separator");
        while ((l = in.readLine()) != null) {
            sb.append(l + nl);
        }
        in.close();
        return sb.toString();
    }

}
