package com.gsma.mobileconnect.r2.android.Integration_R2_New;


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
import com.gsma.mobileconnect.r2.android.Asserts;
import com.gsma.mobileconnect.r2.android.GsmaDataReader;
import com.gsma.mobileconnect.r2.android.Integration_R2.AuthorizationTest;
import com.gsma.mobileconnect.r2.android.MobileConnectConfigCreator;
import com.gsma.mobileconnect.r2.android.activity.MainActivity;
import com.gsma.mobileconnect.r2.android.compatibility.AndroidMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.android.main.IMobileConnectContract;
import com.gsma.mobileconnect.r2.android.main.MobileConnectAndroidView;
import com.gsma.mobileconnect.r2.android.utils.LoginHintUtilities;
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponse;
import com.gsma.mobileconnect.r2.constants.Scopes;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.OperatorUrls;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class LoginHintNewTest extends ActivityTestRule<MainActivity> {
    public LoginHintNewTest() {
        super(MainActivity.class);
    }

    public static DiscoveryResponse discoveryResponseData = null;
    public static String authError = null;
    public static String authErrorDecription = null;
    public static String idToken = null;
    public static RequestTokenResponse requestTokenResponse;

    GsmaDataReader dataReader;
    long timeout = 14000;
    MobileConnectAndroidView mobileConnectAndroidInterface;
    boolean gotResponse = false;
    private Activity mActivity;
    MobileConnectStatus result = null;

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
    public void test_authorization_valid_generated_encr_msisdn() throws FileNotFoundException, InterruptedException, JSONException {
        dataReader = new GsmaDataReader(mActivity.getApplicationContext());
        authorize(LoginHintUtilities.generateForEncryptedMsisdn(subId));
        Thread.sleep(timeout*4);

        Assert.assertNotNull(AuthorizationNewTest.requestTokenResponse.getResponseData().getAccessToken());
        JSONObject expObject = dataReader.getDecodedIdToken();
        JSONObject actObject = new JSONObject(AuthorizationNewTest.requestTokenResponse.getDecodedIdTokenPayload());
        Asserts.assertRequestTokenResponse(expObject, actObject);
        AuthorizationNewTest.requestTokenResponse = null;
    }

    @Test
    public void test_authorization_valid_generated_msisdn() throws FileNotFoundException, InterruptedException, JSONException {
        dataReader = new GsmaDataReader(mActivity.getApplicationContext());
        authorize(LoginHintUtilities.generateForMsisdn(dataReader.getMsisdn()));
        Thread.sleep(timeout*4);

        Assert.assertNotNull(AuthorizationNewTest.requestTokenResponse.getResponseData().getAccessToken());
        JSONObject expObject = dataReader.getDecodedIdToken();
        JSONObject actObject = new JSONObject(AuthorizationNewTest.requestTokenResponse.getDecodedIdTokenPayload());
        Asserts.assertRequestTokenResponse(expObject, actObject);
        AuthorizationNewTest.requestTokenResponse = null;
    }

    @Test
    public void test_authorization_valid_generated_pcr() throws FileNotFoundException, InterruptedException, JSONException {
        dataReader = new GsmaDataReader(mActivity.getApplicationContext());
        authorize(LoginHintUtilities.generateForEncryptedMsisdn(dataReader.getEncrMsisdn()),"login");
        Thread.sleep(timeout*3);

        String token = AuthorizationNewTest.requestTokenResponse.getResponseData().getAccessToken();
        AuthorizationNewTest.requestTokenResponse = null;
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

        Assert.assertNotNull(AuthorizationNewTest.requestTokenResponse.getResponseData().getAccessToken());
        JSONObject expObject = dataReader.getDecodedIdToken();
        JSONObject actObject = new JSONObject(AuthorizationNewTest.requestTokenResponse.getDecodedIdTokenPayload());
        Asserts.assertRequestTokenResponse(expObject, actObject);
        AuthorizationTest.requestTokenResponse = null;
    }

    private void authorize(String loginHint) throws FileNotFoundException, InterruptedException {
        authorize(loginHint, "login");
    }

    private void authorize(String loginHint, String prompt) throws FileNotFoundException, InterruptedException {
        DiscoveryResponse discoveryResponse = getDiscoveryResult();
        AuthenticationOptions.Builder authenticationOptionsBuilder = new AuthenticationOptions.Builder().withPrompt(prompt)
                .withClientName(discoveryResponse.getClientName())
                .withContext(
                        "context").withBindingMessage("binding").withAcrValues("2").withLoginHint(loginHint);

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

        mobileConnectAndroidInterface.startAuthentication(null,"state", "nonce", mobileConnectRequestOptions,  new IMobileConnectContract.IMobileConnectCallback()
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
