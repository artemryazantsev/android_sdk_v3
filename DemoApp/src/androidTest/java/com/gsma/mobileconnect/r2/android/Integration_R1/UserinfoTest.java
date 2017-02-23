package com.gsma.mobileconnect.r2.android.Integration_R1;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentActivity;

import com.gsma.mobileconnect.r2.MobileConnect;
import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.Asserts;
import com.gsma.mobileconnect.r2.android.GsmaDataReader;
import com.gsma.mobileconnect.r2.android.MobileConnectConfigCreator;
import com.gsma.mobileconnect.r2.android.activity.MainActivity;
import com.gsma.mobileconnect.r2.android.compatibility.AndroidMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.android.main.IMobileConnectContract;
import com.gsma.mobileconnect.r2.android.main.MobileConnectAndroidView;
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponse;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;


/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class UserinfoTest extends ActivityTestRule<MainActivity> {
    public UserinfoTest() {
        super(MainActivity.class);
    }

    public static DiscoveryResponse discoveryResponseData = null;
    public static RequestTokenResponse requestTokenResponse = null;
    public static String authError = null;
    public static String authErrorDecription = null;
    public static String idToken = null;
    MobileConnectStatus result;

    GsmaDataReader dataReader;
    long timeout = 7000;
    long manualTimeout = 4;
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

    @After
    public void tearDown(){
        result = null;
    }

    @Test
    public void test_userinfo_open_id() throws FileNotFoundException, InterruptedException, JSONException {
        test_userinfo_scope(GsmaDataReader.UserInfoDataPrefixes.userinfo_openid);
    }

    @Test
    public void test_userinfo_openid_profile() throws FileNotFoundException, InterruptedException, JSONException {
        test_userinfo_scope(GsmaDataReader.UserInfoDataPrefixes.userinfo_openid_profile);
    }

    @Test
    public void test_userinfo_openid_email() throws FileNotFoundException, InterruptedException, JSONException {
        test_userinfo_scope(GsmaDataReader.UserInfoDataPrefixes.userinfo_openid_email);
    }

    @Test
    public void test_userinfo_openid_address() throws FileNotFoundException, InterruptedException, JSONException {
        test_userinfo_scope(GsmaDataReader.UserInfoDataPrefixes.userinfo_openid_address);
    }

    @Test
    public void test_userinfo_openid_phone() throws FileNotFoundException, InterruptedException, JSONException {
        test_userinfo_scope(GsmaDataReader.UserInfoDataPrefixes.userinfo_openid_phone);
    }

    @Test
    public void test_userinfo_openid_offline_access() throws FileNotFoundException, InterruptedException, JSONException {
        test_userinfo_scope(GsmaDataReader.UserInfoDataPrefixes.userinfo_openid_profile);
    }


    @Test
    public void test_userinfo_invalid_token() throws FileNotFoundException, InterruptedException, JSONException {
        String token = getToken(GsmaDataReader.UserInfoDataPrefixes.userinfo_openid.getScope(), "login", "2");
        mobileConnectAndroidInterface.requestUserInfo("invalid", new IMobileConnectContract.IMobileConnectCallback(){
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus) {
                result = mobileConnectStatus;
            }
        });
        Thread.sleep(timeout);
        assertEquals(result.getIdentityResponse().getResponseCode(),401);
        assertNull(result.getIdentityResponse().getResponseJson());
    }

    private void test_userinfo_scope(GsmaDataReader.UserInfoDataPrefixes scope) throws FileNotFoundException, InterruptedException, JSONException {
        String token = getToken(scope.getScope(), "login", "2");
        mobileConnectAndroidInterface.requestUserInfo(token, new IMobileConnectContract.IMobileConnectCallback() {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus) {
                result = mobileConnectStatus;
            }
        });
        Thread.sleep(timeout);
        System.out.println(result.getIdentityResponse().getResponseJson());
        if(!scope.equals(GsmaDataReader.UserInfoDataPrefixes.userinfo_openid_address))
            Asserts.assertIdentityResponse(dataReader.getUserinfo(scope.getXmlTag()), new JSONObject(result.getIdentityResponse().getResponseJson()));
        else
            Asserts.assertUserinfoAddress(dataReader.getUserinfo(scope.getXmlTag()), new JSONObject(result.getIdentityResponse().getResponseJson()));
    }


    private String getToken(String scope) throws InterruptedException, FileNotFoundException {
        return getToken(scope, "mobile","2");
    }

    private String getToken(String scope, String prompt, String acrValue) throws InterruptedException, FileNotFoundException {
        authorize(scope, prompt, acrValue);
        while(AuthorizationTest.requestTokenResponse==null){
            Thread.sleep(timeout/8);
        }
        String token = AuthorizationTest.requestTokenResponse.getResponseData().getAccessToken();
        AuthorizationTest.requestTokenResponse = null;
        return token;
    }

    private void authorize(String scope, String prompt, String acrValue, String state, String nonce) throws FileNotFoundException, InterruptedException {
        MobileConnectStatus mobileConnectStatus = getDiscoveryResult();
        AuthenticationOptions.Builder authenticationOptionsBuilder = new AuthenticationOptions.Builder().withPrompt(prompt)
                .withClientName(mobileConnectStatus.getDiscoveryResponse().getClientName())
                .withContext(
                        "context").withBindingMessage("binding").withAcrValues(acrValue);

        final MobileConnectRequestOptions.Builder mobileConnectRequestOptionsBuilder = new
                MobileConnectRequestOptions.Builder();


        authenticationOptionsBuilder.withScope(scope);

        final MobileConnectRequestOptions mobileConnectRequestOptions = mobileConnectRequestOptionsBuilder
                .withAuthenticationOptions(
                        authenticationOptionsBuilder.build()).build();
        mobileConnectAndroidInterface.startAuthentication(mobileConnectStatus.getDiscoveryResponse().
                getResponseData().getSubscriberId(), state, nonce, mobileConnectRequestOptions,  new IMobileConnectContract.IMobileConnectCallback()
        {
            @Override
            public void onComplete(MobileConnectStatus
                                           mobileConnectStatus)
            {
                mobileConnectAndroidInterface.attemptAuthenticationWithWebView((FragmentActivity)mActivity,
                        new TestAuthListener(),
                        mobileConnectStatus.getUrl(),
                        "state", "nonce",
                        null);
            }
        });
    }

    private void authorize(String scope, String prompt, String acrValue) throws FileNotFoundException, InterruptedException {
        authorize(scope, prompt, acrValue, "state", "nonce");
    }

    private void authorize(String scope) throws FileNotFoundException, InterruptedException {
        authorize(scope, "mobile");
    }

    private void authorize(String scope, String prompt) throws FileNotFoundException, InterruptedException {
        authorize(scope, prompt, "2");
    }

    private MobileConnectStatus getDiscoveryResult() throws FileNotFoundException, InterruptedException {

        MobileConnectRequestOptions requestOptions = createOptions();
        final MobileConnectStatus[] actResp = new MobileConnectStatus[1];
        mobileConnectAndroidInterface.attemptDiscovery(dataReader.getMsisdn(),
                null,
                null,
                requestOptions,
                new IMobileConnectContract.IMobileConnectCallback()
                {
                    @Override
                    public void onComplete(MobileConnectStatus
                                                   mobileConnectStatus)
                    {
                        gotResponse = true;
                        actResp[0] = mobileConnectStatus;
                    }
                });

        Thread.sleep(timeout);
        return actResp[0];

    }

    private MobileConnectRequestOptions createMobileConnectOptions() throws FileNotFoundException {
        dataReader = new GsmaDataReader(mActivity.getApplicationContext());
        return null;
    }


    private MobileConnectRequestOptions createOptions() throws FileNotFoundException {
        dataReader = new GsmaDataReader(mActivity.getApplicationContext());

        MobileConnectConfig mobileConnectConfig = new MobileConnectConfigCreator(mActivity.getApplicationContext()).createConfig();

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