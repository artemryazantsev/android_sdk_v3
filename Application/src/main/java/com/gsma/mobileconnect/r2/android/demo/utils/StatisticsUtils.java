package com.gsma.mobileconnect.r2.android.demo.utils;
import android.content.Context;

import com.gsma.mobileconnect.r2.android.demo.constants.Constants;


public class StatisticsUtils {

    public static void sendDiscoveryStatistic(Context context, final long discoveryStartTime, final long discoveryEndTime) {
        String countryCode = LocationUtils.getCountyCodeUsingNetwork(context);
        String encCountryCode = JWTKUtils.encrypt(countryCode);
        long discoveryElapsedTime = discoveryEndTime - discoveryStartTime;
        String encryptedDiscoveryElapsedTime= JWTKUtils.encrypt(String.valueOf(discoveryElapsedTime));
        String requestDate = DateUtils.getDate(discoveryStartTime);
        String encryptedRequestDate = JWTKUtils.encrypt(requestDate);
        HttpUtils.HttpAsyncTask httpAsyncTask = new HttpUtils.HttpAsyncTask();
        httpAsyncTask.execute(Constants.API_URL, encCountryCode, encryptedDiscoveryElapsedTime, encryptedRequestDate);
    }
}
