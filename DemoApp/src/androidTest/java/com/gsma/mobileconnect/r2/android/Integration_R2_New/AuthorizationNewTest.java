package com.gsma.mobileconnect.r2.android.Integration_R2_New;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.FragmentActivity;

import com.gsma.mobileconnect.r2.MobileConnect;
import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.MobileConnectWebInterface;
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
import com.gsma.mobileconnect.r2.discovery.OperatorUrls;
import com.gsma.mobileconnect.r2.discovery.ProviderMetadataUnavailableException;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class AuthorizationNewTest extends ActivityTestRule<MainActivity> {
    public AuthorizationNewTest() {
        super(MainActivity.class);
    }

    public static DiscoveryResponse discoveryResponseData = null;
    public static RequestTokenResponse requestTokenResponse;
    public static String authError = null;
    public static String authErrorDecription = null;
    public static String idToken = null;

    GsmaDataReader dataReader;
    long timeout = 10000;
    long manualTimeout = 4;
    MobileConnectAndroidView mobileConnectAndroidInterface;
    boolean gotResponse = false;
    private Activity mActivity;

    private MobileConnectWebInterface mobileConnectWebInterface;
    private MobileConnectInterface mobileConnectInterface;
    private List<String> listUrls = Arrays.asList(
            "https://operator-g.integration.sandbox.mobileconnect.io/oidc/authorize",
            "https://operator-g.integration.sandbox.mobileconnect.io/oidc/accesstoken",
            "https://operator-g.integration.sandbox.mobileconnect.io/oidc/userinfo",
            "https://operator-g.integration.sandbox.mobileconnect.io/oidc/revoke",
            "https://operator-g.integration.sandbox.mobileconnect.io/oidc/premiuminfo",
            "openid profile email",
            "https://operator-g.integration.sandbox.mobileconnect.io/.well-known/openid-configuration",
            "https://operator-g.integration.sandbox.mobileconnect.io/oidc/operator-g/jwks.json");
    private static String secretKey = "ZDlmYzkwOGYtYjYzYS00NjhiLWE2NWUtMjgwZTE0NTRhNzNiOm9wZXJhdG9yLWc=";
    private static String clientKey = "NzNjMmI4ZmMtOGUxOC00MTlmLTk5NWEtMDE5YjhjMzVkN2M5Om9wZXJhdG9yLWc=";
    private static String subId = "e85964c062912cbcd3dc4ae5fe774c8c310b47db59db187c3c23ca6b50f79e15ae93eda1d83da8ad0a0571c9a8284210ae8ae4bbebf22b61ece8e3e58c5362ab5e015556ee2a4705ee16579786c1713cb1f08e19470b18058fcb0341fdfcfb0d1ad015f75e972daeb602c96de9b867ff00e08d119798d529a7ef4a3e75601f64ad1a27654635d34b95c6cb4d3aa7a40ea50e41c8223ab37c62172843873b75d62d57e4bf32a8d9b50d45c83f483ea0c50578e710c82fe08677498864a492138cb104c3f0c21387068a949d224454ca7d0759947ab96885ab2b678949592823167cba3ba7acac0d4deeda7018b722968f5647639054bdd6bbf265cf35d29563f3";

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
    public void test_authorization_valid() throws FileNotFoundException, InterruptedException, JSONException {
        authorize(Scopes.MOBILECONNECTAUTHORIZATION);
        Thread.sleep(timeout*2);
        Assert.assertNotNull(requestTokenResponse.getResponseData().getAccessToken());
        JSONObject expObject = dataReader.getDecodedIdToken();
        JSONObject actObject = new JSONObject(requestTokenResponse.getDecodedIdTokenPayload());
        Asserts.assertRequestTokenResponse(expObject, actObject);
        requestTokenResponse = null;
    }

    @Test
    public void test_authorization_no_state_nonce() throws FileNotFoundException, InterruptedException, JSONException {
        try {
            authorize(Scopes.MOBILECONNECTAUTHORIZATION, "login", "2", "", "nonce");
            Thread.sleep(timeout * 2);
            assertFalse("Successfull url with empty state", true);
        }
        catch (Exception e){
            try {
                authorize(Scopes.MOBILECONNECTAUTHORIZATION, "login", "2", "state", "");
                Thread.sleep(timeout * 2);
            }
            catch (Exception e1){
                return;
            }
        }

        assertFalse("Successfull url with empty nonce", true);
    }

    @Test
    public void test_authorization_invalid_scope() throws IOException, InterruptedException, JSONException, JsonDeserializationException {
        authorize("invalid");
        Thread.sleep(timeout);

        assertEquals(dataReader.getErrorUnsupportedScope().getError(), authError);
        assertEquals(dataReader.getErrorUnsupportedScope().getErrorDescription(), authErrorDecription);
        authError = null;
        authErrorDecription = null;
    }

    @Test
    public void test_authorization_empty_scope() throws IOException, InterruptedException, JSONException, JsonDeserializationException {
        authorize("");
        Thread.sleep(timeout);

        Assert.assertNotNull(requestTokenResponse.getResponseData().getAccessToken());
        JSONObject expObject = dataReader.getDecodedIdToken();
        JSONObject actObject = new JSONObject(requestTokenResponse.getDecodedIdTokenPayload());
        Asserts.assertRequestTokenResponse(expObject, actObject);
        requestTokenResponse = null;
    }

    @Test
    public void test_authorization_null_scope() throws IOException, InterruptedException, JSONException, JsonDeserializationException {
        authorize(null);
        Thread.sleep(timeout*2);

        Assert.assertNotNull(requestTokenResponse.getResponseData().getAccessToken());
        JSONObject expObject = dataReader.getDecodedIdToken();
        JSONObject actObject = new JSONObject(requestTokenResponse.getDecodedIdTokenPayload());
        Asserts.assertRequestTokenResponse(expObject, actObject);
        requestTokenResponse = null;
    }

    //Half-manual - need to click confirmation button
    @Test
    public void test_authorization_prompt_login() throws FileNotFoundException, InterruptedException, JSONException {
        authorize(Scopes.MOBILECONNECTAUTHORIZATION,"login");
        int timeSpend = 0;
        while(discoveryResponseData == null&&timeSpend<manualTimeout) {
            Thread.sleep(timeout);
            timeSpend++;
        }

        Assert.assertNotNull(requestTokenResponse.getResponseData().getAccessToken());
        JSONObject expObject = dataReader.getDecodedIdToken();
        JSONObject actObject = new JSONObject(requestTokenResponse.getDecodedIdTokenPayload());
        Asserts.assertRequestTokenResponse(expObject, actObject);
        requestTokenResponse = null;
    }

    //Half-manual - need to click confirmation button
    @Test
    public void test_authorization_prompt_consent() throws FileNotFoundException, InterruptedException, JSONException {
        authorize(Scopes.MOBILECONNECTAUTHORIZATION,"consent");
        int timeSpend = 0;
        while(discoveryResponseData == null&&timeSpend<manualTimeout) {
            Thread.sleep(timeout);
            timeSpend++;
        }

        Assert.assertNotNull(requestTokenResponse.getResponseData().getAccessToken());
        JSONObject expObject = dataReader.getDecodedIdToken();
        JSONObject actObject = new JSONObject(requestTokenResponse.getDecodedIdTokenPayload());
        Asserts.assertRequestTokenResponse(expObject, actObject);
        requestTokenResponse = null;
    }

    //Half-manual - need to click confirmation button
    @Test
    public void test_authorization_prompt_none() throws FileNotFoundException, InterruptedException, JSONException {
        authorize(Scopes.MOBILECONNECTAUTHORIZATION,"none");
        int timeSpend = 0;
        while(discoveryResponseData == null&&timeSpend<manualTimeout) {
            Thread.sleep(timeout);
            timeSpend++;
        }

        Assert.assertNotNull(requestTokenResponse.getResponseData().getAccessToken());
        JSONObject expObject = dataReader.getDecodedIdToken();
        JSONObject actObject = new JSONObject(requestTokenResponse.getDecodedIdTokenPayload());
        Asserts.assertRequestTokenResponse(expObject, actObject);
        requestTokenResponse = null;
    }

    //Half-manual - need to login manually
    @Test
    public void test_authorization_no_login_hint() throws FileNotFoundException, InterruptedException, JSONException {
        DiscoveryResponse discoveryResponse = getDiscoveryResult();
        AuthenticationOptions.Builder authenticationOptionsBuilder = new AuthenticationOptions.Builder().withPrompt("login")
                .withClientName(discoveryResponse.getClientName())
                .withContext(
                        "context").withBindingMessage("binding");

        final MobileConnectRequestOptions.Builder mobileConnectRequestOptionsBuilder = new
                MobileConnectRequestOptions.Builder();


        authenticationOptionsBuilder.withScope(Scopes.MOBILECONNECTAUTHORIZATION);

        final MobileConnectRequestOptions mobileConnectRequestOptions = mobileConnectRequestOptionsBuilder
                .withAuthenticationOptions(
                        authenticationOptionsBuilder.build()).build();

        mobileConnectAndroidInterface = new MobileConnectAndroidView(
                mobileConnectInterface);

        mobileConnectAndroidInterface.initialise();
        mobileConnectAndroidInterface.getPresenter().setDiscoveryResponse(discoveryResponse);

        mobileConnectAndroidInterface.startAuthentication(null, "state", "nonce", mobileConnectRequestOptions,  new IMobileConnectContract.IMobileConnectCallback()
        {
            @Override
            public void onComplete(MobileConnectStatus
                                           mobileConnectStatus)
            {
                mobileConnectAndroidInterface.attemptAuthenticationWithWebView((FragmentActivity)mActivity,
                        new TestAuthListenerNew(),
                        mobileConnectStatus.getUrl(),
                        "state", "nonce",
                        null);
            }
        });
        int timeSpend = 0;
        Thread.sleep(timeout*2);

        Assert.assertNotNull(requestTokenResponse.getResponseData().getAccessToken());
        JSONObject expObject = dataReader.getDecodedIdToken();
        JSONObject actObject = new JSONObject(requestTokenResponse.getDecodedIdTokenPayload());
        Asserts.assertRequestTokenResponse(expObject, actObject);
        requestTokenResponse = null;
    }

    @Test
    public void test_is_scope_supported() throws FileNotFoundException, InterruptedException, ProviderMetadataUnavailableException {

        DiscoveryResponse discoveryResponse = getDiscoveryResult();
        assertTrue(discoveryResponse.isMobileConnectServiceSupported(Scopes.MOBILECONNECTIDENTITYNATIONALID));
        assertTrue(discoveryResponse.isMobileConnectServiceSupported(Scopes.MOBILECONNECT));
        assertFalse(discoveryResponse.isMobileConnectServiceSupported(Scopes.MOBILECONNECTIDENTITYSIGNUPPLUS));
        assertTrue(discoveryResponse.isMobileConnectServiceSupported(Scopes.MOBILECONNECTIDENTITYSIGNUP));
        assertTrue(discoveryResponse.isMobileConnectServiceSupported(Scopes.MOBILECONNECTAUTHENTICATION));
        assertTrue(discoveryResponse.isMobileConnectServiceSupported(Scopes.MOBILECONNECTAUTHORIZATION));
        assertTrue(discoveryResponse.isMobileConnectServiceSupported(Scopes.MOBILECONNECTIDENTITYPHONE));
    }

    @Test
    public void test_authorization_mc_authn() throws FileNotFoundException, InterruptedException, JSONException {
        authorize(Scopes.MOBILECONNECTAUTHORIZATION);
        Thread.sleep(timeout*2);

        idToken = requestTokenResponse.getResponseData().getIdToken();
        requestTokenResponse = null;

        authorize(Scopes.MOBILECONNECTAUTHENTICATION,"login");
        Thread.sleep(timeout*2);

        Assert.assertNotNull(requestTokenResponse.getResponseData().getAccessToken());
        JSONObject expObject = dataReader.getDecodedIdToken();
        JSONObject actObject = new JSONObject(requestTokenResponse.getDecodedIdTokenPayload());
        Asserts.assertRequestTokenAuthnResponse(expObject, actObject);

        idToken = null;
    }

    @Test
    public void test_authorization_acr_values() throws FileNotFoundException, InterruptedException, JSONException {
        authorize(Scopes.MOBILECONNECTAUTHORIZATION,"login","3");
        Thread.sleep(timeout*2);

        Assert.assertNotNull(requestTokenResponse.getResponseData().getAccessToken());
        requestTokenResponse = null;
    }

    @Test
    public void test_authorization_invalid_acr_values() throws FileNotFoundException, InterruptedException, JSONException, JsonDeserializationException {
        authorize(Scopes.MOBILECONNECTAUTHORIZATION,"login","1");
        Thread.sleep(timeout);
        assertEquals(dataReader.getErrorUnsupportedAcrValues().getError(), authError);
        assertEquals(dataReader.getErrorUnsupportedAcrValues().getErrorDescription(), authErrorDecription);
        authError = null;
        authErrorDecription = null;
    }

    @Test
    public void test_authorization_invalid_ui_locale() throws FileNotFoundException, InterruptedException {
        authorizeInvalidUiLocale();
        Thread.sleep(timeout*2);
        assertNotNull(authError);
        assertNotNull(authErrorDecription);
        authError = null;
        authErrorDecription = null;
    }

    private void authorizeInvalidUiLocale() throws FileNotFoundException, InterruptedException {
        DiscoveryResponse discoveryResponse = getDiscoveryResult();
        AuthenticationOptions.Builder authenticationOptionsBuilder = new AuthenticationOptions.Builder().withUiLocales("invalid").withPrompt("login")
                .withClientName(discoveryResponse.getClientName())
                .withContext(
                        "context").withBindingMessage("binding").withAcrValues("2");

        final MobileConnectRequestOptions.Builder mobileConnectRequestOptionsBuilder = new
                MobileConnectRequestOptions.Builder();


        authenticationOptionsBuilder.withScope(Scopes.MOBILECONNECTAUTHORIZATION);

        final MobileConnectRequestOptions mobileConnectRequestOptions = mobileConnectRequestOptionsBuilder
                .withAuthenticationOptions(
                        authenticationOptionsBuilder.build()).build();
        mobileConnectAndroidInterface.startAuthentication(discoveryResponse.
                getResponseData().getSubscriberId(), "state", "nonce", mobileConnectRequestOptions,
                new IMobileConnectContract.IMobileConnectCallback()
        {
            @Override
            public void onComplete(MobileConnectStatus
                                           mobileConnectStatus)
            {
                mobileConnectAndroidInterface.attemptAuthenticationWithWebView((FragmentActivity)mActivity,
                        new TestAuthListenerNew(),
                        mobileConnectStatus.getUrl(),
                        "state", "nonce",
                        null);
            }
        });
    }

    private void authorize(String scope, String prompt, String acrValue, String state, String nonce) throws FileNotFoundException, InterruptedException {
        //MobileConnectStatus mobileConnectStatus = getDiscoveryResult();
        DiscoveryResponse discoveryResponse = getDiscoveryResult();
       // former();
        AuthenticationOptions.Builder authenticationOptionsBuilder = new AuthenticationOptions.Builder().withPrompt(prompt)
                .withClientName(discoveryResponse.getClientName())
                .withContext(
                        "context").withBindingMessage("binding").withAcrValues(acrValue);

        final MobileConnectRequestOptions.Builder mobileConnectRequestOptionsBuilder = new
                MobileConnectRequestOptions.Builder();


        authenticationOptionsBuilder.withScope(scope);

        final MobileConnectRequestOptions mobileConnectRequestOptions = mobileConnectRequestOptionsBuilder
                .withAuthenticationOptions(
                        authenticationOptionsBuilder.build()).build();

        mobileConnectAndroidInterface = new MobileConnectAndroidView(
                mobileConnectInterface);

        mobileConnectAndroidInterface.initialise();
        mobileConnectAndroidInterface.getPresenter().setDiscoveryResponse(discoveryResponse);

        mobileConnectAndroidInterface.startAuthentication(discoveryResponse.
                getResponseData().getSubscriberId(), state, nonce, mobileConnectRequestOptions,
                new IMobileConnectContract.IMobileConnectCallback()
        {
            @Override
            public void onComplete(MobileConnectStatus
                                           mobileConnectStatus)
            {
                mobileConnectAndroidInterface.attemptAuthenticationWithWebView((FragmentActivity)mActivity,
                        new TestAuthListenerNew(),
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
        authorize(scope, "login");
    }

    private void authorize(String scope, String prompt) throws FileNotFoundException, InterruptedException {
        authorize(scope, prompt, "2");
    }

    private DiscoveryResponse getDiscoveryResult() throws FileNotFoundException, InterruptedException {
        dataReader = new GsmaDataReader(mActivity.getApplicationContext());


        MobileConnectConfig mobileConnectConfig = new MobileConnectConfigCreator(mActivity.getApplicationContext()).createConfig();

        final MobileConnect mobileConnect = MobileConnect.build(mobileConnectConfig,
                new AndroidMobileConnectEncodeDecoder());

        mobileConnectWebInterface = mobileConnect.getMobileConnectWebInterface();
        mobileConnectInterface = mobileConnect.getMobileConnectInterface();

        mobileConnectAndroidInterface = new MobileConnectAndroidView(
                mobileConnectWebInterface);

        mobileConnectAndroidInterface.initialise();
        OperatorUrls operatorUrls = new OperatorUrls.Builder()
                .withAuthorizationUrl(listUrls.get(0))
                .withRequestTokenUrl(listUrls.get(1))
                .withUserInfoUrl(listUrls.get(2))
                .withRevokeTokenUrl(listUrls.get(3))
                .withPremiumInfoUri(listUrls.get(4))
                .withScopeUri(listUrls.get(5))
                .withProviderMetadataUri(listUrls.get(6))
                .withJwksUri(listUrls.get(7)).build();

        final DiscoveryResponse[] actResp = new DiscoveryResponse[1];
        mobileConnectAndroidInterface.generateDiscoveryManually(
                secretKey,
                clientKey,
                subId,
                dataReader.getAppShortName(),
                operatorUrls,
                new IMobileConnectContract.IMobileConnectCallbackManually()
                {
                    @Override
                    public void onComplete(DiscoveryResponse
                                                   discoveryResponse)
                    {
                        gotResponse = true;
                        actResp[0] = discoveryResponse;
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

    private MobileConnectStatus former() throws FileNotFoundException, InterruptedException {
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
}
