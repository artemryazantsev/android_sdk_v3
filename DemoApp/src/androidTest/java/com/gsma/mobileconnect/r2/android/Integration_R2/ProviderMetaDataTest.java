package com.gsma.mobileconnect.r2.android.Integration_R2;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
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
import com.gsma.mobileconnect.r2.discovery.ProviderMetadata;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ProviderMetaDataTest extends ActivityTestRule<MainActivity> {
    public ProviderMetaDataTest() {
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
        gotResponse = false;
        mActivity = mActivityRule.getActivity();
    }

    @Test
    public void test_provider_metadata_valid() throws FileNotFoundException, InterruptedException {

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
                        String actProvMD = (new Gson()).toJson(mobileConnectStatus.getDiscoveryResponse().getProviderMetadata(), ProviderMetadata.class);
                        System.out.println(actProvMD);
                        String expProvMD = null;
                        try {
                            expProvMD = dataReader.getDiscoveryProviderMetadataResponse();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        assertEquals(expProvMD, actProvMD);
                    }
                });

        Thread.sleep(timeout);
        if (!gotResponse){
            assertTrue("Didn't get response in "+timeout/1000+"seconds", false);
        }
    }


    /**
     * This should be checked manually: check that second call to AttemptDiscovery doesn't sent provider metadata request (GET: https://operator-g.integration.sandbox.mobileconnect.io/.well-known/openid-configuration)
     */
    @Test
    public void test_provider_metadata_caching() throws FileNotFoundException, InterruptedException {

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
                    }
                });
        Thread.sleep(timeout);
        gotResponse = false;
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
                        String actProvMD = (new Gson()).toJson(mobileConnectStatus.getDiscoveryResponse().getProviderMetadata(), ProviderMetadata.class);
                        System.out.println(actProvMD);
                        String expProvMD = null;
                        try {
                            expProvMD = dataReader.getDiscoveryProviderMetadataResponse();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        assertEquals(expProvMD, actProvMD);
                    }
                });
        while(!gotResponse) {
            Thread.sleep(timeout/5);
        }
    }


    private MobileConnectRequestOptions createOptions() throws FileNotFoundException {
        dataReader = new GsmaDataReader(mActivity.getApplicationContext());

        MobileConnectConfig mobileConnectConfig = new MobileConnectConfigCreator(mActivity.getApplicationContext()).createConfig(true);

        final MobileConnect mobileConnect = MobileConnect.build(mobileConnectConfig,
                new AndroidMobileConnectEncodeDecoder());

        MobileConnectInterface mobileConnectInterface = mobileConnect.getMobileConnectInterface();

        mobileConnectAndroidInterface = new MobileConnectAndroidView(
                mobileConnectInterface);

        DiscoveryOptions.Builder discoveryOptionsBuilder = new DiscoveryOptions.Builder();


        return new MobileConnectRequestOptions.Builder().withDiscoveryOptions(
                discoveryOptionsBuilder.withMsisdn(null).build()).build();
    }

}