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
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponse;
import com.gsma.mobileconnect.r2.constants.Scopes;
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
public class IdentityTest extends ActivityTestRule<MainActivity> {
    public IdentityTest() {
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
    public void test_identity_open_id() throws FileNotFoundException, InterruptedException, JSONException {
        String token = getToken(Scopes.MOBILECONNECT, "login", "2");
        mobileConnectAndroidInterface.requestIdentity(token, new IMobileConnectContract.IMobileConnectCallback() {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus) {
                result = mobileConnectStatus;
            }
        });
        Thread.sleep(timeout);
        Asserts.assertIdentityResponse(dataReader.getIdentityDefaultResponse(), new JSONObject(result.getIdentityResponse().getResponseJson()));
        //{"sub": "c20b3624-9168-11e6-b298-0242ac110003"}
    }

    @Test
    public void test_identity_mc_authz() throws FileNotFoundException, InterruptedException, JSONException {
        String token = getToken(Scopes.MOBILECONNECTAUTHORIZATION, "login", "2");
        mobileConnectAndroidInterface.requestIdentity(token, new IMobileConnectContract.IMobileConnectCallback() {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus) {
                result = mobileConnectStatus;
            }
        });
        Thread.sleep(timeout);
        Asserts.assertIdentityResponse(dataReader.getIdentityDefaultResponse(), new JSONObject(result.getIdentityResponse().getResponseJson()));
        //{"sub": "c20b3624-9168-11e6-b298-0242ac110003"}
    }

    @Test
    public void test_identity_mc_authz_plus() throws FileNotFoundException, InterruptedException, JSONException {
        String token = getToken(Scopes.MOBILECONNECTAUTHORIZATION, "login", "3");
        mobileConnectAndroidInterface.requestIdentity(token, new IMobileConnectContract.IMobileConnectCallback() {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus) {
                result = mobileConnectStatus;
            }
        });
        Thread.sleep(timeout);
        Asserts.assertIdentityResponse(dataReader.getIdentityDefaultResponse(), new JSONObject(result.getIdentityResponse().getResponseJson()));
        //{"sub": "c20b3624-9168-11e6-b298-0242ac110003"}
    }

    @Test
    public void test_identity_mc_authn() throws FileNotFoundException, InterruptedException, JSONException {
        String token = getToken(Scopes.MOBILECONNECTAUTHORIZATION, "login", "2");
        mobileConnectAndroidInterface.requestIdentity(token, new IMobileConnectContract.IMobileConnectCallback() {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus) {
                result = mobileConnectStatus;
            }
        });
        Thread.sleep(timeout);
        Asserts.assertIdentityResponse(dataReader.getIdentityDefaultResponse(), new JSONObject(result.getIdentityResponse().getResponseJson()));
        //{"sub": "c20b3624-9168-11e6-b298-0242ac110003"}
    }

    @Test
    public void test_identity_mc_authn_plus() throws FileNotFoundException, InterruptedException, JSONException {
        String token = getToken(Scopes.MOBILECONNECTAUTHORIZATION, "login", "3");
        mobileConnectAndroidInterface.requestIdentity(token, new IMobileConnectContract.IMobileConnectCallback() {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus) {
                result = mobileConnectStatus;
            }
        });
        Thread.sleep(timeout);
        Asserts.assertIdentityResponse(dataReader.getIdentityDefaultResponse(), new JSONObject(result.getIdentityResponse().getResponseJson()));
        //{"sub": "c20b3624-9168-11e6-b298-0242ac110003"}
    }

    @Test
    public void test_identity_mc_identity_phonenumber() throws FileNotFoundException, InterruptedException, JSONException {
        String token = getToken(Scopes.MOBILECONNECTIDENTITYPHONE, "login", "2");
        mobileConnectAndroidInterface.requestIdentity(token, new IMobileConnectContract.IMobileConnectCallback() {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus) {
                result = mobileConnectStatus;
            }
        });
        Thread.sleep(timeout);
        Asserts.assertIdentityResponse(dataReader.getIdentityPhoneResponse(), new JSONObject(result.getIdentityResponse().getResponseJson()));
        //{"sub": "c20b3624-9168-11e6-b298-0242ac110003", "phone_number": "+447700900461", "updated_at": "2016-10-16T12:28:53.986637"}
    }

    @Test
    public void test_identity_mc_identity_signup() throws FileNotFoundException, InterruptedException, JSONException {
        String token = getToken(Scopes.MOBILECONNECTIDENTITYSIGNUP, "login", "2");
        mobileConnectAndroidInterface.requestIdentity(token, new IMobileConnectContract.IMobileConnectCallback() {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus) {
                result = mobileConnectStatus;
            }
        });
        Thread.sleep(timeout);
        Asserts.assertIdentityResponse(dataReader.getIdentitySignupResponse(), new JSONObject(result.getIdentityResponse().getResponseJson()));
        //{"sub": "c20b3624-9168-11e6-b298-0242ac110003", "email": "stesterovich@mail.ru", "postal_code": "EC4N 8AF", "state": "London", "given_name": "Jane", "city": "London", "family_name": "Doe", "street_address": "GSMA, 2nd Floor, The Wallbrook Building, 25 Wallbrook", "country": "UK", "preferred_username": "stesterovich@mail.ru", "email_verified": true, "phone_number_alternate": "+4413242346544", "updated_at": "2016-10-16T12:30:05.958820"}
    }

    @Test
    public void test_identity_mc_identity_signup_plus() throws FileNotFoundException, InterruptedException, JSONException {
        String token = getToken(Scopes.MOBILECONNECTIDENTITYSIGNUP, "login", "3");
        mobileConnectAndroidInterface.requestIdentity(token, new IMobileConnectContract.IMobileConnectCallback() {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus) {
                result = mobileConnectStatus;
            }
        });
        Thread.sleep(timeout);
        Asserts.assertIdentityResponse(dataReader.getIdentitySignupResponse(), new JSONObject(result.getIdentityResponse().getResponseJson()));
        //{"sub": "c20b3624-9168-11e6-b298-0242ac110003", "email": "stesterovich@mail.ru", "postal_code": "EC4N 8AF", "state": "London", "given_name": "Jane", "city": "London", "family_name": "Doe", "street_address": "GSMA, 2nd Floor, The Wallbrook Building, 25 Wallbrook", "country": "UK", "preferred_username": "stesterovich@mail.ru", "email_verified": true, "phone_number_alternate": "+4413242346544", "updated_at": "2016-10-16T12:30:05.958820"}
    }

    @Test
    public void test_identity_mc_identity_nationalid() throws FileNotFoundException, InterruptedException, JSONException {
        String token = getToken(Scopes.MOBILECONNECTIDENTITYNATIONALID, "login", "2");
        mobileConnectAndroidInterface.requestIdentity(token, new IMobileConnectContract.IMobileConnectCallback() {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus) {
                result = mobileConnectStatus;
            }
        });
        Thread.sleep(timeout);
        Asserts.assertIdentityResponse(dataReader.getIdentityNationalIdResponse(), new JSONObject(result.getIdentityResponse().getResponseJson()));
        //{"sub": "c20b3624-9168-11e6-b298-0242ac110003", "given_name": "Jane", "city": "London", "family_name": "Doe", "street_address": "GSMA, 2nd Floor, The Wallbrook Building, 25 Wallbrook", "country": "UK", "birthdate": "1970-01-01", "updated_at": "2016-10-16T12:30:59.940258", "postal_code": "EC4N 8AF", "national_identifier": "OM/uuid/fabcad3f11442896070a804f5a6fa6c929d6f60bf17aba0c218f75dfb94c8bee", "state": "London"}
    }


    @Test
    public void test_identity_invalid_token() throws FileNotFoundException, InterruptedException, JSONException {
        String token = getToken(Scopes.MOBILECONNECTAUTHORIZATION, "login", "2");
        mobileConnectAndroidInterface.requestIdentity("invalid", new IMobileConnectContract.IMobileConnectCallback() {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus) {
                result = mobileConnectStatus;
            }
        });
        Thread.sleep(timeout);
        assertEquals(result.getIdentityResponse().getResponseCode(),401);
        assertNull(result.getIdentityResponse().getResponseJson());
    }


    private String getToken(String scope) throws InterruptedException, FileNotFoundException {
        return getToken(scope, "mobile","2");
    }

    private String getToken(String scope, String prompt, String acrValue) throws InterruptedException, FileNotFoundException {
        authorize(scope, prompt, acrValue);
        Thread.sleep(timeout*2);

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