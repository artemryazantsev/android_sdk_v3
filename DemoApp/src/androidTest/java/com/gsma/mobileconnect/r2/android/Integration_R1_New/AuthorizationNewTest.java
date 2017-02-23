package com.gsma.mobileconnect.r2.android.Integration_R1_New;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentActivity;

import com.gsma.mobileconnect.r2.MobileConnect;
import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.MobileConnectWebInterface;
import com.gsma.mobileconnect.r2.android.GsmaDataReader;
import com.gsma.mobileconnect.r2.android.Integration_R1.TestAuthListener;
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
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class AuthorizationNewTest extends ActivityTestRule<MainActivity> {
    public AuthorizationNewTest() {
        super(MainActivity.class);
    }

    public static DiscoveryResponse discoveryResponseData = null;
    public static RequestTokenResponse requestTokenResponse = null;
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
            "https://operator-a.integration.sandbox.mobileconnect.io/oidc/authorize",
            "https://operator-a.integration.sandbox.mobileconnect.io/oidc/accesstoken",
            "https://operator-a.integration.sandbox.mobileconnect.io/oidc/userinf",
            "https://operator-a.integration.sandbox.mobileconnect.io/oidc/revoke",
            "https://operator-a.integration.sandbox.mobileconnect.io/oidc/premiuminfo",
            "openid profile email",
            "https://operator-a.integration.sandbox.mobileconnect.io/.well-known/openid-configuration",
            "https://operator-a.integration.sandbox.mobileconnect.io/oidc/operator-g/jwks.json");
    private static String secretKey = "ZDlmYzkwOGYtYjYzYS00NjhiLWE2NWUtMjgwZTE0NTRhNzNiOm9wZXJhdG9yLWc=";
    private static String clientKey = "NzNjMmI4ZmMtOGUxOC00MTlmLTk5NWEtMDE5YjhjMzVkN2M5Om9wZXJhdG9yLWc=";
    private static String subId = "613c39e9cd43100d44e257e3cbbe3677810bcfa19c51946509bea2616b22871334f6c0368818494862509fc2628dee7111414ca7572387a2ec8f577fd467e612b992ab5af81a27bfecf01082d3b10f01d16a7e03aa55015116bcc4ecb898c2f1a2bc3857f07b040c634f15c34a3b3f41b375bf85b68fe6028d9d69f81e8092f1826f02d0931f04583d1cc5a62bf95c73a06754198918d85043b6dfd0f6e75d7691bd658c90c430fdf275d1dafc7811f82b2079d0a065ec2f7a531c9151117aae86fb6fcd8038ec686719f079a3718622347d04c40eb50805e46cb7751110efd25bb3f7f11fb999b8d57d188df05551fbda581e051ba3d69962b2fbea95204800";

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
        authorize(Scopes.MOBILECONNECTAUTHENTICATION);
        Thread.sleep(timeout*2);

        Assert.assertNotNull(requestTokenResponse.getResponseData().getAccessToken());
        Assert.assertNotNull(requestTokenResponse.getDecodedIdTokenPayload());
//        JSONObject expObject = dataReader.getDecodedIdToken();
        //    JSONObject actObject = new JSONObject(requestTokenResponse.getDecodedIdTokenPayload());
        //     Asserts.assertRequestTokenResponse(expObject, actObject);
        //    requestTokenResponse = null;
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
        Thread.sleep(timeout*2);

        Assert.assertNotNull(requestTokenResponse.getResponseData().getAccessToken());
        requestTokenResponse = null;
    }

    @Test
    public void test_authorization_null_scope() throws IOException, InterruptedException, JSONException, JsonDeserializationException {
        authorize(null);
        Thread.sleep(timeout*2);

        Assert.assertNotNull(requestTokenResponse.getResponseData().getAccessToken());
        requestTokenResponse = null;
    }

    //Half-manual - need to click confirmation button
    @Test
    public void test_authorization_prompt_login() throws FileNotFoundException, InterruptedException, JSONException {
        authorize(Scopes.MOBILECONNECTAUTHENTICATION,"login");
        int timeSpend = 0;
        while(discoveryResponseData == null&&timeSpend<manualTimeout) {
            Thread.sleep(timeout);
            timeSpend++;
        }

        Assert.assertNotNull(requestTokenResponse.getResponseData().getAccessToken());
        requestTokenResponse = null;
    }

    //Half-manual - need to click confirmation button
    @Test
    public void test_authorization_prompt_consent() throws FileNotFoundException, InterruptedException, JSONException {
        authorize(Scopes.MOBILECONNECTAUTHENTICATION,"consent");
        int timeSpend = 0;
        while(discoveryResponseData == null&&timeSpend<manualTimeout) {
            Thread.sleep(timeout);
            timeSpend++;
        }

        Assert.assertNotNull(requestTokenResponse.getResponseData().getAccessToken());
        requestTokenResponse = null;
    }

    //Half-manual - need to click confirmation button
    @Test
    public void test_authorization_prompt_none() throws FileNotFoundException, InterruptedException, JSONException {
        authorize(Scopes.MOBILECONNECTAUTHENTICATION,"none");
        int timeSpend = 0;
        while(discoveryResponseData == null&&timeSpend<manualTimeout) {
            Thread.sleep(timeout);
            timeSpend++;
        }

        Assert.assertNotNull(requestTokenResponse.getResponseData().getAccessToken());
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


        authenticationOptionsBuilder.withScope(Scopes.MOBILECONNECTAUTHENTICATION);

        final MobileConnectRequestOptions mobileConnectRequestOptions = mobileConnectRequestOptionsBuilder
                .withAuthenticationOptions(
                        authenticationOptionsBuilder.build()).build();
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
        while(requestTokenResponse == null&&timeSpend<manualTimeout*2) {
            Thread.sleep(timeout);
            timeSpend++;
        }

        Assert.assertNotNull(requestTokenResponse.getResponseData().getAccessToken());
    }


    @Test
    public void test_authorization_acr_values() throws FileNotFoundException, InterruptedException, JSONException {
        authorize(Scopes.MOBILECONNECTAUTHENTICATION,"login","3");
        Thread.sleep(timeout*2);

        Assert.assertNotNull(requestTokenResponse.getResponseData().getAccessToken());
        requestTokenResponse = null;
    }

    @Test
    public void test_authorization_invalid_acr_values() throws FileNotFoundException, InterruptedException, JSONException, JsonDeserializationException {
        authorize(Scopes.MOBILECONNECTAUTHENTICATION,"login","1");
        Thread.sleep(timeout*2);
        assertNotNull(authError);
        assertNotNull(authErrorDecription);
    }




    private void authorize(String scope, String prompt, String acrValue, String state, String nonce) throws FileNotFoundException, InterruptedException {
        DiscoveryResponse discoveryResponse = getDiscoveryResult();
        AuthenticationOptions.Builder authenticationOptionsBuilder = new AuthenticationOptions.Builder().withPrompt(prompt)
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
                getResponseData().getSubscriberId(), state, nonce, mobileConnectRequestOptions,  new IMobileConnectContract.IMobileConnectCallback()
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
}
