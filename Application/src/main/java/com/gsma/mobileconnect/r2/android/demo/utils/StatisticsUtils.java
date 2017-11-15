package com.gsma.mobileconnect.r2.android.demo.utils;
import android.app.DownloadManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.gsma.mobileconnect.r2.json.Response;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import static org.apache.http.HttpHeaders.USER_AGENT;


public class StatisticsUtils {


    private static final String TAG = StatisticsUtils.class.getSimpleName();


    public static void sendDiscoveryElapsedTime(Context context, final long discoveryStartTime, final long discoveryEndTime) {
        String countryCode = LocationUtils.getCountyCodeUsingNetwork(context);
        Log.d(TAG, "Country code: " + countryCode);
        String encCountryCode = JWTKUtils.encrypt(countryCode);
        Log.d(TAG, "Encrypted country code: " + encCountryCode);
        long discoveryElapsedTime = discoveryEndTime - discoveryStartTime;

        Log.d(TAG, "Discovery elapsed time: " + discoveryElapsedTime);
        String encryptedDiscoveryElapsedTime= JWTKUtils.encrypt(String.valueOf(discoveryElapsedTime));
        Log.d(TAG, "Encrypted discovery elapsed code: " + encryptedDiscoveryElapsedTime);


        String requestDate = DateUtils.getDate(discoveryStartTime, "MM/dd/yyyy H:mm:ss");
        Log.d(TAG, "Discovery request date: " + discoveryStartTime);
        String encryptedRequestDate = JWTKUtils.encrypt(requestDate);
        Log.d(TAG, "Encrypted discovery request date: " + encryptedRequestDate);

        post("http://localhost:7071/transfer/send");

        CallAPI callAPI = new CallAPI();
        callAPI.doInBackground();

//        try {
//            sendGet();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        String geckoURL = "https://api.geckoboard.com/datasets/mobileconnect.android.sdk.discovery/data";
//        String jsonUpdate = String.format("\"data\": [\n" +
//                "    {\n" +
//                "      \"date\": %s\n" +
//                "      \"discoverytime\": %s\n" +
//                "    }\n" +
//                "  ]", discoveryElapsedTime);
//        HttpURLConnection connection;
//        try {
//            URL url = new URL(geckoURL);
//            connection = (HttpURLConnection)url.openConnection();
//            connection.setRequestMethod("PUT");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    private static void post(String url) {
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
        try {
            Log.d(TAG, "Prepraping request");
            JSONObject jsonobj = new JSONObject();

            jsonobj.put("locale", "eyJhbGciOiJIUzI1NiIsInppcCI6IkRFRiJ9.eNqqViouTVKyUkqqVKoFAAAA__8.u6gvA_18-H9OqhyR9t1vp52rE4S53pRpHMWC4Ib7tOI");
            jsonobj.put("requestTime", "eyJhbGciOiJIUzI1NiIsInppcCI6IkRFRiJ9.eNqqViouTVKyUjI0Mje0UKoFAAAA__8.DBxs7d9fKMeQkOtXi-dynMDDrtfut1WzUdntvz-wAw8");
            jsonobj.put("requestDate", "eyJhbGciOiJIUzI1NiIsInppcCI6IkRFRiJ9.eNqqViouTVKyUjI01Dc01TcyMDRXMLSwMjGwMjJWqgUAAAD__w.Ip-9pkd-286DLqUBewS7nkFvS10T9M61Bga5NIbs6bM");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("req", jsonobj.toString()));

            Log.e("mainToPost", "mainToPost" + nameValuePairs.toString());
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = client.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public static class CallAPI extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(final String... params) {
            new Thread(new Runnable() {
                public void run() {
                    post("http://localhost:7071/transfer/send");
                }
            });
            return null;
        }
    }



}
