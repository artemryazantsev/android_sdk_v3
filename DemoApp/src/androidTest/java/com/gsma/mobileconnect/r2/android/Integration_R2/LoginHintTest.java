package com.gsma.mobileconnect.r2.android.Integration_R2;

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
import com.gsma.mobileconnect.r2.android.utils.LoginHintUtilities;
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponse;
import com.gsma.mobileconnect.r2.constants.Scopes;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;


/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class LoginHintTest extends ActivityTestRule<MainActivity> {
    public LoginHintTest() {
        super(MainActivity.class);
    }

    public static DiscoveryResponse discoveryResponseData = null;
    public static RequestTokenResponse requestTokenResponse = null;
    public static String authError = null;
    public static String authErrorDecription = null;
    public static String idToken = null;

    GsmaDataReader dataReader;
    long timeout = 14000;
    long manualTimeout = 4;
    MobileConnectAndroidView mobileConnectAndroidInterface;
    boolean gotResponse = false;
    private Activity mActivity;
    MobileConnectStatus result = null;

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
    public void test_authorization_valid_generated_encr_msisdn() throws FileNotFoundException, InterruptedException, JSONException {
        dataReader = new GsmaDataReader(mActivity.getApplicationContext());
        authorize(LoginHintUtilities.generateForEncryptedMsisdn(dataReader.getEncrMsisdn()));
        Thread.sleep(timeout);

        Assert.assertNotNull(AuthorizationTest.requestTokenResponse.getResponseData().getAccessToken());
        JSONObject expObject = dataReader.getDecodedIdToken();
        JSONObject actObject = new JSONObject(AuthorizationTest.requestTokenResponse.getDecodedIdTokenPayload());
        Asserts.assertRequestTokenResponse(expObject, actObject);
        AuthorizationTest.requestTokenResponse = null;
    }

    @Test
    public void test_authorization_valid_generated_msisdn() throws FileNotFoundException, InterruptedException, JSONException {
        dataReader = new GsmaDataReader(mActivity.getApplicationContext());
        authorize(LoginHintUtilities.generateForMsisdn(dataReader.getMsisdn()));
        Thread.sleep(timeout);

        Assert.assertNotNull(AuthorizationTest.requestTokenResponse.getResponseData().getAccessToken());
        JSONObject expObject = dataReader.getDecodedIdToken();
        JSONObject actObject = new JSONObject(AuthorizationTest.requestTokenResponse.getDecodedIdTokenPayload());
        Asserts.assertRequestTokenResponse(expObject, actObject);
        AuthorizationTest.requestTokenResponse = null;
    }

    @Test
    public void test_authorization_valid_generated_pcr() throws FileNotFoundException, InterruptedException, JSONException {
        dataReader = new GsmaDataReader(mActivity.getApplicationContext());
        authorize(LoginHintUtilities.generateForEncryptedMsisdn(dataReader.getEncrMsisdn()),"login");
        Thread.sleep(timeout);

        String token = AuthorizationTest.requestTokenResponse.getResponseData().getAccessToken();
        AuthorizationTest.requestTokenResponse = null;
        mobileConnectAndroidInterface.requestIdentity(token, new IMobileConnectContract.IMobileConnectCallback() {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus) {
                result = mobileConnectStatus;
            }
        });
        Thread.sleep(timeout);
        String pcr = new JSONObject(result.getIdentityResponse().getResponseJson()).getString("sub");
        authorize(LoginHintUtilities.generateForPcr(pcr),"login");
        Thread.sleep(timeout);

        Assert.assertNotNull(AuthorizationTest.requestTokenResponse.getResponseData().getAccessToken());
        JSONObject expObject = dataReader.getDecodedIdToken();
        JSONObject actObject = new JSONObject(AuthorizationTest.requestTokenResponse.getDecodedIdTokenPayload());
        Asserts.assertRequestTokenResponse(expObject, actObject);
        AuthorizationTest.requestTokenResponse = null;
    }

    private void authorize(String loginHint) throws FileNotFoundException, InterruptedException {
       authorize(loginHint, "login");
    }

    private void authorize(String loginHint, String prompt) throws FileNotFoundException, InterruptedException {
        MobileConnectStatus mobileConnectStatus = getDiscoveryResult();
        AuthenticationOptions.Builder authenticationOptionsBuilder = new AuthenticationOptions.Builder().withPrompt(prompt)
                .withClientName(mobileConnectStatus.getDiscoveryResponse().getClientName())
                .withContext(
                        "context").withBindingMessage("binding").withAcrValues("2").withLoginHint(loginHint);

        final MobileConnectRequestOptions.Builder mobileConnectRequestOptionsBuilder = new
                MobileConnectRequestOptions.Builder();


        authenticationOptionsBuilder.withScope(Scopes.MOBILECONNECTAUTHORIZATION);

        final MobileConnectRequestOptions mobileConnectRequestOptions = mobileConnectRequestOptionsBuilder
                .withAuthenticationOptions(
                        authenticationOptionsBuilder.build()).build();
        mobileConnectAndroidInterface.startAuthentication(null,"state", "nonce", mobileConnectRequestOptions,  new IMobileConnectContract.IMobileConnectCallback()
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

    private void authorize() throws FileNotFoundException, InterruptedException {
        MobileConnectStatus mobileConnectStatus = getDiscoveryResult();
        AuthenticationOptions.Builder authenticationOptionsBuilder = new AuthenticationOptions.Builder().withPrompt("login")
                .withClientName(mobileConnectStatus.getDiscoveryResponse().getClientName())
                .withContext(
                        "context").withBindingMessage("binding").withAcrValues("2");

        final MobileConnectRequestOptions.Builder mobileConnectRequestOptionsBuilder = new
                MobileConnectRequestOptions.Builder();


        authenticationOptionsBuilder.withScope(Scopes.MOBILECONNECTAUTHORIZATION);

        final MobileConnectRequestOptions mobileConnectRequestOptions = mobileConnectRequestOptionsBuilder
                .withAuthenticationOptions(
                        authenticationOptionsBuilder.build()).build();
        mobileConnectAndroidInterface.startAuthentication(mobileConnectStatus.getDiscoveryResponse().
                getResponseData().getSubscriberId(), "state", "nonce", mobileConnectRequestOptions,  new IMobileConnectContract.IMobileConnectCallback()
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

    private MobileConnectRequestOptions createOptions(MobileConnectConfig mobileConnectConfig){

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