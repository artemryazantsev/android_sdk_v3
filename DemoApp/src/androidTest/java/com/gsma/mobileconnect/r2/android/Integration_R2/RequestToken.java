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
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;


@RunWith(AndroidJUnit4.class)
public class RequestToken extends ActivityTestRule<MainActivity> {
    public RequestToken() {
        super(MainActivity.class);
    }

    public static DiscoveryResponse discoveryResponseData = null;
    public static RequestTokenResponse requestTokenResponse = null;
    public static String authError = null;
    public static String authErrorDecription = null;
    public static String idToken = null;

    GsmaDataReader dataReader;
    long timeout = 7000*2;
    MobileConnectStatus result;
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
    public void test_request_token_invalid_code() throws IOException, InterruptedException, JSONException, URISyntaxException, JsonDeserializationException {
        MobileConnectRequestOptions options = authorize(Scopes.MOBILECONNECTAUTHORIZATION);
        Thread.sleep(timeout);
        URI uri = new URI(dataReader.getRedirectUri()+"?code=invalid&state=state&nonce=nonce");
        mobileConnectAndroidInterface.requestToken(uri, "state", "nonce", options, new IMobileConnectContract.IMobileConnectCallback() {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus) {
                result = mobileConnectStatus;
            }
        });
        Thread.sleep(timeout);
        assertEquals(dataReader.getError(GsmaDataReader.ErrorList.invalidCodeError).getError(), result.getErrorCode());
        assertEquals(dataReader.getError(GsmaDataReader.ErrorList.invalidCodeError).getErrorDescription(), result.getErrorMessage());
    }

    @Test
    public void test_request_token_valid() throws IOException, InterruptedException, JSONException, URISyntaxException, JsonDeserializationException {
        MobileConnectRequestOptions options = authorize(Scopes.MOBILECONNECTAUTHORIZATION);
        Thread.sleep(timeout);
        URI uri = new URI(dataReader.getRedirectUri()+"?code=invalid&state=state&nonce=nonce");
        mobileConnectAndroidInterface.requestToken(uri, "state", "nonce", options, new IMobileConnectContract.IMobileConnectCallback() {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus) {
                result = mobileConnectStatus;
            }
        });
        Thread.sleep(timeout);
        assertNull(result.getErrorCode());
        assertNull(result.getErrorMessage());

    }

    @Test
    public void test_request_token_invalid_state() throws IOException, InterruptedException, JSONException, URISyntaxException, JsonDeserializationException {
        MobileConnectRequestOptions options = authorize(Scopes.MOBILECONNECTAUTHORIZATION);
        Thread.sleep(timeout);
        URI uri = new URI(dataReader.getRedirectUri()+"?code=invalid&state=state&nonce=nonce");
        mobileConnectAndroidInterface.requestToken(uri, "invalid", "nonce", options, new IMobileConnectContract.IMobileConnectCallback() {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus) {
                result = mobileConnectStatus;
            }
        });
        Thread.sleep(timeout);
        assertEquals(dataReader.getError(GsmaDataReader.ErrorList.invalidExpectedStateError).getError(), result.getErrorCode());
        assertEquals(dataReader.getError(GsmaDataReader.ErrorList.invalidExpectedStateError).getErrorDescription(), result.getErrorMessage());

    }

    @Test
    public void test_request_token_invalid_nonce() throws IOException, InterruptedException, JSONException, URISyntaxException, JsonDeserializationException {
        MobileConnectRequestOptions options = authorize(Scopes.MOBILECONNECTAUTHORIZATION);
        Thread.sleep(timeout);
        URI uri = new URI(dataReader.getRedirectUri()+"?code=invalid&state=state&nonce=nonce");
        mobileConnectAndroidInterface.requestToken(uri, "state", "invalid", options, new IMobileConnectContract.IMobileConnectCallback() {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus) {
                result = mobileConnectStatus;
            }
        });
        Thread.sleep(timeout);
        assertEquals(dataReader.getError(GsmaDataReader.ErrorList.invalidCodeError).getError(), result.getErrorCode());
        assertEquals(dataReader.getError(GsmaDataReader.ErrorList.invalidCodeError).getErrorDescription(), result.getErrorMessage());
    }


    private MobileConnectRequestOptions authorize(String scope, String prompt, String acrValue, String state, String nonce) throws FileNotFoundException, InterruptedException {
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
                getResponseData().getSubscriberId(), state, nonce, mobileConnectRequestOptions, new IMobileConnectContract.IMobileConnectCallback() {
            @Override
            public void onComplete(MobileConnectStatus
                                           mobileConnectStatus) {
                mobileConnectAndroidInterface.attemptAuthenticationWithWebView((FragmentActivity) mActivity,
                        new TestAuthListener(),
                        mobileConnectStatus.getUrl(),
                        "state", "nonce",
                        null);
            }
        });

        return mobileConnectRequestOptions;
    }

    private MobileConnectRequestOptions authorize(String scope, String prompt, String acrValue) throws FileNotFoundException, InterruptedException {
        return authorize(scope, prompt, acrValue, "state", "nonce");
    }

    private MobileConnectRequestOptions authorize(String scope) throws FileNotFoundException, InterruptedException {
        return authorize(scope, "login");
    }

    private MobileConnectRequestOptions authorize(String scope, String prompt) throws FileNotFoundException, InterruptedException {
        return authorize(scope, prompt, "2");
    }

    private MobileConnectStatus getDiscoveryResult() throws FileNotFoundException, InterruptedException {

        MobileConnectRequestOptions requestOptions = createOptions();
        final MobileConnectStatus[] actResp = new MobileConnectStatus[1];
        mobileConnectAndroidInterface.attemptDiscovery(null,
                dataReader.getMcc(),
                dataReader.getMnc(),
                requestOptions,
                new IMobileConnectContract.IMobileConnectCallback() {
                    @Override
                    public void onComplete(MobileConnectStatus
                                                   mobileConnectStatus) {
                        gotResponse = true;
                        actResp[0] = mobileConnectStatus;
                    }
                });

        Thread.sleep(timeout);
        return actResp[0];

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