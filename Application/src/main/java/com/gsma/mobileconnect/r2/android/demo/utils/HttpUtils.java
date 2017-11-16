package com.gsma.mobileconnect.r2.android.demo.utils;


import android.os.AsyncTask;
import android.util.Log;

import com.gsma.mobileconnect.r2.android.demo.constants.Constants;
import com.gsma.mobileconnect.r2.android.demo.constants.Headers;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpUtils {


    private static String post(String url, String locale, String requestTime, String requestDate) {
        InputStream inputStream = null;
        String result = "";
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(Headers.CONTENT_TYPE, Headers.CONTENT_TYPE_VALUE);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.LOCALE, locale);
            jsonObject.put(Constants.REQUEST_TIME, requestTime);
            jsonObject.put(Constants.REQUEST_DATE, requestDate);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            httpPost.setEntity(stringEntity);
            HttpResponse httpResponse = client.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
            }
            else {
                result = Constants.API_REQUEST_FAIL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }


    public static class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String locale = params[1];
            String requestTime = params[2];
            String requestDate = params[3];
            return post(url, locale, requestTime, requestDate);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("HTTP", "Finished");
        }
    }
}
