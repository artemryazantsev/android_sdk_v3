package com.gsma.mobileconnect.r2.android.exampleapps;

import android.support.v7.app.AppCompatActivity;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.main.MobileConnectAndroidView;

/**
 * Created by usmaan.dad on 17/10/2016.
 */

public class BaseActivity extends AppCompatActivity
{
    public static MobileConnectAndroidView mobileConnectAndroidView;
    public static MobileConnectStatus mobileConnectStatus;

    @Override
    protected void onStart()
    {
        super.onStart();
        mobileConnectAndroidView.initialise();
    }

    @Override
    protected void onStop()
    {
        mobileConnectAndroidView.cleanUp();
        super.onStop();
    }
}
