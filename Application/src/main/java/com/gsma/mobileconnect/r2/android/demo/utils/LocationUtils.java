package com.gsma.mobileconnect.r2.android.demo.utils;


import android.content.Context;
import android.telephony.TelephonyManager;

public class LocationUtils {

    public static String getCountyCodeUsingNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkCountryIso();
    }
}
