package com.gsma.mobileconnect.r2.android.Integration_R1;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.gsma.mobileconnect.r2.MobileConnect;
import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.GsmaDataReader;
import com.gsma.mobileconnect.r2.android.MobileConnectConfigCreator;
import com.gsma.mobileconnect.r2.android.activity.MainActivity;
import com.gsma.mobileconnect.r2.android.compatibility.AndroidMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.android.main.IMobileConnectContract;
import com.gsma.mobileconnect.r2.android.main.MobileConnectAndroidView;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.json.DiscoveryResponseData;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;
import java.io.IOException;

import static com.gsma.mobileconnect.r2.android.Asserts.AssertResponse;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class DiscoveryTest extends ActivityTestRule<MainActivity> {
    public DiscoveryTest() {
        super(MainActivity.class);
    }

    public static DiscoveryResponse discoveryResponseData = null;

    GsmaDataReader dataReader;
    long timeout = 5000;
    long manualTimeout = 10;
    MobileConnectAndroidView mobileConnectAndroidInterface;
    boolean gotResponse = false;
    private Activity mActivity;

    @Rule
    public TestName name = new TestName();

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        mActivity = mActivityRule.getActivity();
    }

    @Test
    public void test_discovery_valid() throws FileNotFoundException, InterruptedException {

        MobileConnectRequestOptions requestOptions = createOptions();
        mobileConnectAndroidInterface.attemptDiscovery(null,
                dataReader.getMcc(),
                dataReader.getMnc(),
                requestOptions,
                new IMobileConnectContract.IMobileConnectCallback()
                {
                    @Override
                    public void onComplete(MobileConnectStatus
                                                   mobileConnectStatus)
                    {
                        gotResponse = true;
                        DiscoveryResponseData actResp = mobileConnectStatus.getDiscoveryResponse().getResponseData();
                        DiscoveryResponseData expResp = null;
                        try {
                            expResp = dataReader.getDiscoveryResponseDataWithExpectedParameters(GsmaDataReader.DiscoveryItems.Spain, actResp.getResponse().getClientId(),
                                    actResp.getResponse().getClientSecret(),
                                    actResp.getSubscriberId(), actResp.getTtl()).getResponseData();
                        } catch (IOException | JsonDeserializationException e) {
                            e.printStackTrace();
                        }
                        AssertResponse(expResp, actResp);
                    }
                });

        Thread.sleep(timeout);
        if (!gotResponse){
            assertTrue("Didn't get response in "+timeout/1000+"seconds", false);
        }
    }

    @Test
    public void test_discovery_invalid_mcc_mnc() throws FileNotFoundException, InterruptedException {

        //Invalid MCC
        MobileConnectRequestOptions requestOptions = createOptions();
        mobileConnectAndroidInterface.attemptDiscovery(null,
                "invalid",
                dataReader.getMnc(),
                requestOptions,
                new IMobileConnectContract.IMobileConnectCallback() {
                    @Override
                    public void onComplete(MobileConnectStatus
                                                   mobileConnectStatus) {
                        try {
                            Log.d("error_code", mobileConnectStatus.getErrorCode());
                            assertEquals(dataReader.getErrorInvalidMncMcc().getError(), mobileConnectStatus.getErrorCode());
                            assertEquals(dataReader.getErrorInvalidMncMcc().getErrorDescription(), mobileConnectStatus.getErrorMessage());
                        } catch (IOException | JsonDeserializationException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Thread.sleep(timeout);

        //Invalid MNC
        mobileConnectAndroidInterface.attemptDiscovery(null,
                dataReader.getMcc(),
                "invalid",
                requestOptions,
                new IMobileConnectContract.IMobileConnectCallback() {
                    @Override
                    public void onComplete(MobileConnectStatus
                                                   mobileConnectStatus) {
                        try {
                            assertEquals(dataReader.getErrorInvalidMncMcc().getError(), mobileConnectStatus.getErrorCode());
                            assertEquals(dataReader.getErrorInvalidMncMcc().getErrorDescription(), mobileConnectStatus.getErrorMessage());
                        } catch (IOException | JsonDeserializationException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Thread.sleep(timeout);
    }

    @Test
    public void test_discovery_invalid_client_id() throws FileNotFoundException, InterruptedException {
        dataReader = new GsmaDataReader(mActivity.getApplicationContext(), GsmaDataReader.Environment.SandboxR1);

        MobileConnectConfig mobileConnectConfig = new MobileConnectConfigCreator(mActivity.getApplicationContext(), GsmaDataReader.Environment.SandboxR1).createConfigClientId("Invalid");
        MobileConnectRequestOptions requestOptions = createOptions(mobileConnectConfig);
        mobileConnectAndroidInterface.attemptDiscovery(null,
                dataReader.getMcc(),
                dataReader.getMnc(),
                requestOptions,
                new IMobileConnectContract.IMobileConnectCallback() {
                    @Override
                    public void onComplete(MobileConnectStatus
                                                   mobileConnectStatus) {
                        try {
                            assertEquals(dataReader.getErrorJsonObject().getErrorResponse().getError(), mobileConnectStatus.getErrorCode());
                            assertEquals(dataReader.getErrorJsonObject().getErrorResponse().getErrorDescription(), mobileConnectStatus.getErrorMessage());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Thread.sleep(timeout);

    }

    @Test
    public void test_discovery_invalid_client_secret() throws FileNotFoundException, InterruptedException {
        dataReader = new GsmaDataReader(mActivity.getApplicationContext(), GsmaDataReader.Environment.SandboxR1);

        MobileConnectConfig mobileConnectConfig = new MobileConnectConfigCreator(mActivity.getApplicationContext(), GsmaDataReader.Environment.SandboxR1).createConfigClientSecret("Invalid");
        MobileConnectRequestOptions requestOptions = createOptions(mobileConnectConfig);
        mobileConnectAndroidInterface.attemptDiscovery(null,
                dataReader.getMcc(),
                dataReader.getMnc(),
                requestOptions,
                new IMobileConnectContract.IMobileConnectCallback() {
                    @Override
                    public void onComplete(MobileConnectStatus
                                                   mobileConnectStatus) {
                        try {
                            assertEquals(dataReader.getErrorJsonObject().getErrorResponse().getError(), mobileConnectStatus.getErrorCode());
                            assertEquals(dataReader.getErrorJsonObject().getErrorResponse().getErrorDescription(), mobileConnectStatus.getErrorMessage());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Thread.sleep(timeout);

    }

    @Test
    public void test_discovery_UOI_flow() throws FileNotFoundException, InterruptedException {

        final MobileConnectRequestOptions requestOptions = createOptions();
        mobileConnectAndroidInterface.attemptDiscovery(null,
                null,
                null,
                requestOptions,
                new IMobileConnectContract.IMobileConnectCallback()
                {
                    @Override
                    public void onComplete(final MobileConnectStatus
                                                   mobileConnectStatus)
                    {
                        gotResponse = true;
                        final TestDiscoveryListener listener = new TestDiscoveryListener();
                        try {
                            //Manual part - you need to select MCC and MNC 902 - 02
                            mobileConnectAndroidInterface.attemptDiscoveryWithWebView((FragmentActivity)mActivity, listener, mobileConnectStatus.getUrl(), dataReader.getRedirectUri(), requestOptions);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        try {
                            Thread.sleep(timeout);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
        int timeSpend = 0;
        while(TestDiscoveryListener.response == null&&timeSpend<manualTimeout) {
            Thread.sleep(timeout);
            timeSpend++;
        }
        DiscoveryResponseData actResp = TestDiscoveryListener.response.getDiscoveryResponse().getResponseData();
        DiscoveryResponseData expResp = null;
        try {
            expResp = dataReader.getDiscoveryResponseDataWithExpectedParameters(GsmaDataReader.DiscoveryItems.Spain, actResp.getResponse().getClientId(),
                        actResp.getResponse().getClientSecret(),
                        actResp.getSubscriberId(), actResp.getTtl()).getResponseData();
        } catch (IOException | JsonDeserializationException e) {
                e.printStackTrace();
        }
        AssertResponse(expResp, actResp);
    }


    @Test
    public void test_discovery_null_redirect() throws FileNotFoundException, InterruptedException {
        dataReader = new GsmaDataReader(mActivity.getApplicationContext(), GsmaDataReader.Environment.SandboxR1);
        MobileConnectConfig mobileConnectConfig = new MobileConnectConfigCreator(mActivity.getApplicationContext(), GsmaDataReader.Environment.SandboxR1).createConfigRedirectUri("");
        MobileConnectRequestOptions requestOptions = createOptions(mobileConnectConfig);
        mobileConnectAndroidInterface.attemptDiscovery(null,
                dataReader.getMcc(),
                dataReader.getMnc(),
                requestOptions,
                new IMobileConnectContract.IMobileConnectCallback()
                {
                    @Override
                    public void onComplete(MobileConnectStatus
                                                   mobileConnectStatus)
                    {
                        gotResponse = true;
                        try {
                            assertEquals(dataReader.getErrorNullRedirectUri().getError(), mobileConnectStatus.getErrorCode());
                            assertEquals(dataReader.getErrorNullRedirectUri().getErrorDescription(), mobileConnectStatus.getErrorMessage());
                        } catch (IOException | JsonDeserializationException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Thread.sleep(timeout);
        if (!gotResponse){
            assertTrue("Didn't get response in "+timeout/1000+"seconds", false);
        }
    }


    private MobileConnectRequestOptions createOptions() throws FileNotFoundException {
        dataReader = new GsmaDataReader(mActivity.getApplicationContext(), GsmaDataReader.Environment.SandboxR1);

        MobileConnectConfig mobileConnectConfig = new MobileConnectConfigCreator(mActivity.getApplicationContext(), GsmaDataReader.Environment.SandboxR1).createConfig();

        final MobileConnect mobileConnect = MobileConnect.build(mobileConnectConfig,
                new AndroidMobileConnectEncodeDecoder());

        MobileConnectInterface mobileConnectInterface = mobileConnect.getMobileConnectInterface();

        mobileConnectAndroidInterface = new MobileConnectAndroidView(
                mobileConnectInterface);
        mobileConnectAndroidInterface.initialise();

        DiscoveryOptions.Builder discoveryOptionsBuilder = new DiscoveryOptions.Builder();


        return new MobileConnectRequestOptions.Builder().withDiscoveryOptions(
                discoveryOptionsBuilder.withMsisdn(null).build()).build();
    }

    private MobileConnectRequestOptions createOptions(MobileConnectConfig mobileConnectConfig){

        final MobileConnect mobileConnect = MobileConnect.build(mobileConnectConfig,
                new AndroidMobileConnectEncodeDecoder());

        MobileConnectInterface mobileConnectInterface = mobileConnect.getMobileConnectInterface();

        mobileConnectAndroidInterface = new MobileConnectAndroidView(
                mobileConnectInterface);
        mobileConnectAndroidInterface.initialise();

        DiscoveryOptions.Builder discoveryOptionsBuilder = new DiscoveryOptions.Builder();


        return new MobileConnectRequestOptions.Builder().withDiscoveryOptions(
                discoveryOptionsBuilder.withMsisdn(null).build()).build();
    }

}