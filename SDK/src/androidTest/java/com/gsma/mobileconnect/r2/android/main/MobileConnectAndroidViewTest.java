package com.gsma.mobileconnect.r2.android.main;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.gsma.mobileconnect.r2.MobileConnect;
import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.TestActivity;
import com.gsma.mobileconnect.r2.android.compatibility.AndroidMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.android.interfaces.AuthenticationListener;
import com.gsma.mobileconnect.r2.android.interfaces.DiscoveryListener;
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class MobileConnectAndroidViewTest //extends ActivityInstrumentationTestCase2<TestActivity>
{
    @Rule
    public ActivityTestRule<TestActivity> activityRule = new ActivityTestRule<>(TestActivity.class);

    private MobileConnectAndroidView mobileConnectAndroidView;

    private IMobileConnectContract.IUserActionsListener mockPresenter;

    private IMobileConnectContract.IMobileConnectCallback mobileConnectCallback;

    private MobileConnectInterface mobileConnectInterface;

    /*
    public MobileConnectAndroidViewTest()
    {
        super(TestActivity.class);
    }
    */

    @Before
    public void setUp() throws Exception {
        mobileConnectCallback = new IMobileConnectContract.IMobileConnectCallback() {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus) {

            }
        };


        MobileConnectConfig mobileConnectConfig = new MobileConnectConfig.Builder().withClientId("clientId")
                .withClientSecret("clientSecret")
                .withDiscoveryUrl(new URI("http://discoveryUri"))
                .withRedirectUrl(new URI("http://redirectUri"))
                .withCacheResponsesWithSessionId(false)
                .withXRedirect("APP")
                .build();

        MobileConnect mobileConnect = new MobileConnect.Builder(mobileConnectConfig,
                new AndroidMobileConnectEncodeDecoder()).build();

        mobileConnectInterface = mobileConnect.getMobileConnectInterface();

        mobileConnectAndroidView = new MobileConnectAndroidView(mobileConnectInterface);

        mockPresenter = Mockito.mock(IMobileConnectContract.IUserActionsListener.class);
        mobileConnectAndroidView.setPresenter(mockPresenter);
    }

    @After
    public void tearDown() throws Exception {
        activityRule.getActivity().finish();
    }

    @Test
    public void testConstructor() {
        Assert.assertNotNull("Check constructor", mobileConnectAndroidView);
        Assert.assertNotNull("Check presenter", mobileConnectAndroidView.getPresenter());
    }

    @Test
    public void testAttemptDiscovery() {
        // Given
        final String msisdn = "msisdn";
        final String mcc = "mcc";
        final String mnc = "mnc";
        final MobileConnectRequestOptions options = null;

        // When
        mobileConnectAndroidView.attemptDiscovery(msisdn, mcc, mnc, options, mobileConnectCallback);

        // Then
        Mockito.verify(mockPresenter).performDiscovery(msisdn, mcc, mnc, options, mobileConnectCallback);
    }

    @Test
    public void testAttemptDiscoveryAfterOperatorSelection() throws Exception {
        // Given
        URI redirectUri = new URI("http://redirect.html");

        // When
        mobileConnectAndroidView.attemptDiscoveryAfterOperatorSelection(mobileConnectCallback, redirectUri);

        // Then
        Mockito.verify(mockPresenter).performDiscoveryAfterOperatorSelection(mobileConnectCallback, redirectUri);
    }

    @Test
    public void testStartAuthentication() throws Exception {
        // Given
        final String msisdn = "msisdn";
        final String state = "state";
        final String nonce = "nonce";
        final MobileConnectRequestOptions options = null;

        // When
        mobileConnectAndroidView.startAuthentication(msisdn, state, nonce, options, mobileConnectCallback);

        // Then
        Mockito.verify(mockPresenter).performAuthentication(msisdn, state, nonce, options, mobileConnectCallback);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStartAuthenticationWithMobileAsPrompt() throws Exception {
        // Given
        final String msisdn = "msisdn";
        final String state = "state";
        final String nonce = "nonce";
        final AuthenticationOptions authenticationOptions = new AuthenticationOptions.Builder().withPrompt("mobile")
                .build();
        final MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder().withAuthenticationOptions(
                authenticationOptions).build();

        MobileConnectAndroidPresenter presenter = new MobileConnectAndroidPresenter(mobileConnectInterface);

        mobileConnectAndroidView.setPresenter(presenter);

        // When
        mobileConnectAndroidView.startAuthentication(msisdn, state, nonce, options, mobileConnectCallback);
    }

    @Test
    public void testRequestToken() throws Exception {
        // Given
        URI redirectUri = new URI("http://redirect.html");
        final String state = "state";
        final String nonce = "nonce";
        final MobileConnectRequestOptions options = null;

        // When
        mobileConnectAndroidView.requestToken(redirectUri, state, nonce, options, mobileConnectCallback);

        // Then
        Mockito.verify(mockPresenter).performRequestToken(redirectUri, state, nonce, options, mobileConnectCallback);
    }

    @Test
    public void testHandleUrlRedirect() throws Exception {
        // Given
        URI redirectUri = new URI("http://redirect.html");
        final String state = "state";
        final String nonce = "nonce";
        final MobileConnectRequestOptions options = null;

        // When
        mobileConnectAndroidView.handleUrlRedirect(redirectUri, state, nonce, options, mobileConnectCallback);

        // Then
        Mockito.verify(mockPresenter)
                .performHandleUrlRedirect(redirectUri, state, nonce, options, mobileConnectCallback);
    }

    @Test
    public void testRequestIdentity() throws Exception {
        // Given
        final String accessToken = "accessToken";

        // When
        mobileConnectAndroidView.requestIdentity(accessToken, mobileConnectCallback);

        // Then
        Mockito.verify(mockPresenter).performRequestIdentity(accessToken, mobileConnectCallback);
    }

    @Test
    public void testRefreshToken() throws Exception {
        // Given
        final String refreshToken = "refreshToken";

        // When
        mobileConnectAndroidView.refreshToken(refreshToken, mobileConnectCallback);

        // Then
        Mockito.verify(mockPresenter).performRefreshToken(refreshToken, mobileConnectCallback);
    }

    @Test
    public void testRevokeToken() throws Exception {
        // Given
        final String accessToken = "accessToken";
        final String tokenTypeHint = "tokenTypeHint";

        // When
        mobileConnectAndroidView.revokeToken(accessToken, tokenTypeHint, mobileConnectCallback);

        // Then
        Mockito.verify(mockPresenter).performRevokeToken(accessToken, tokenTypeHint, mobileConnectCallback);
    }

    @Test
    public void testRequestUserInfo() throws Exception {
        // Given
        final String accessToken = "accessToken";

        // When
        mobileConnectAndroidView.requestUserInfo(accessToken, mobileConnectCallback);

        // Then
        Mockito.verify(mockPresenter).performRequestUserInfo(accessToken, mobileConnectCallback);
    }

    @Test
    public void testHandleUrlRedirectAfterAuthenticationInValidURL() throws Exception {
        // Given
        final String incorrectUrl = "$£%^$%^";
        final String state = "state";
        final String nonce = "nonce";
        final AuthenticationListener mockAuthenticationListener = Mockito.mock(AuthenticationListener.class);
        final MobileConnectRequestOptions mobileConnectRequestOptions = Mockito.mock(MobileConnectRequestOptions.class);

        // When
        mobileConnectAndroidView.handleRedirectAfterAuthentication(incorrectUrl,
                state,
                nonce,
                mockAuthenticationListener,
                mobileConnectRequestOptions);

        // Then
        Mockito.verify(mockAuthenticationListener).authenticationFailed(Matchers.any(MobileConnectStatus.class));
    }

    @Test
    public void testHandleUrlRedirectAfterAuthenticationValidURL() throws Exception {
        // Given
        URI redirectUri = new URI("http://redirect.html");
        final String state = "state";
        final String nonce = "nonce";
        final AuthenticationListener mockAuthenticationListener = Mockito.mock(AuthenticationListener.class);
        final MobileConnectRequestOptions mobileConnectRequestOptions = Mockito.mock(MobileConnectRequestOptions.class);

        // When
        mobileConnectAndroidView.handleRedirectAfterAuthentication(redirectUri.toString(),
                state,
                nonce,
                mockAuthenticationListener,
                mobileConnectRequestOptions);

        // Then
        Mockito.verify(mockPresenter)
                .performHandleUrlRedirect(Matchers.eq(redirectUri),
                        Matchers.eq(state),
                        Matchers.eq(nonce),
                        Matchers.eq(mobileConnectRequestOptions),
                        Matchers.any(IMobileConnectContract.IMobileConnectCallback.class));
    }

    @Test
    public void testGetDiscoveryResponse() {
        // Given
        // When
        mobileConnectAndroidView.getDiscoveryResponse();

        // Then
        Mockito.verify(mockPresenter).getDiscoveryResponse();
    }

    @Test
    public void testPerformAsyncTask() throws Exception {
        // Given
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final IMobileConnectContract.IMobileConnectOperation mockMobileConnectOperation = Mockito.mock(
                IMobileConnectContract.IMobileConnectOperation.class);
        MobileConnectStatus mockMobileConnectStatus = Mockito.mock(MobileConnectStatus.class);
        final IMobileConnectContract.IMobileConnectCallback mockMobileConnectCallback = Mockito.mock(
                IMobileConnectContract.IMobileConnectCallback.class);

        when(mockMobileConnectOperation.operation()).thenReturn(mockMobileConnectStatus);

        // When
        //When
        new Thread(new Runnable() {
            @Override
            public void run() {
                mobileConnectAndroidView.performAsyncTask(mockMobileConnectOperation, mockMobileConnectCallback);
                countDownLatch.countDown();
            }
        }).start();

        //Then
        countDownLatch.await();
        Thread.sleep(2000L);
        Mockito.verify(mockMobileConnectCallback).onComplete(mockMobileConnectStatus);
    }

    /*
    public void testAttemptAuthenticationWithWebView() throws Exception
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                // Given
                final AuthenticationListener mockAuthenticationListener = Mockito.mock(AuthenticationListener.class);
                final String url = "http://authenticationurl";
                final String state = "state";
                final String nonce = "nonce";
                final MobileConnectRequestOptions mockMobileConnectRequestOptions = Mockito.mock(
                        MobileConnectRequestOptions.class);

                // When
                mobileConnectAndroidView.attemptAuthenticationWithWebView(getActivity(),
                                                                          mockAuthenticationListener,
                                                                          url,
                                                                          state,
                                                                          nonce,
                                                                          mockMobileConnectRequestOptions);

                // Then
                Espresso.onView(ViewMatchers.withId(R.id.web_view))
                        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
            }
        });
    }

    public void testAttemptDiscoveryWithWebView() throws Exception
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                // Given
                final DiscoveryListener mockDiscoveryListener = Mockito.mock(DiscoveryListener.class);
                final String operatorUrl = "http://operatorUrl";
                final String redirectUrl = "http://authenticationurl";

                final MobileConnectRequestOptions mockMobileConnectRequestOptions = Mockito.mock(
                        MobileConnectRequestOptions.class);

                // When
                mobileConnectAndroidView.attemptDiscoveryWithWebView(getActivity(),
                                                                     mockDiscoveryListener,
                                                                     operatorUrl,
                                                                     redirectUrl,
                                                                     mockMobileConnectRequestOptions);

                // Then
                Espresso.onView(ViewMatchers.withId(R.id.web_view))
                        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
            }
        });
    }
    */

    @Test
    public void testAttemptAuthenticationWithWebView() throws Exception {
        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Given
                final AuthenticationListener mockAuthenticationListener = Mockito.mock(AuthenticationListener.class);
                final String url = "http://authenticationurl";
                final String state = "state";
                final String nonce = "nonce";
                final MobileConnectRequestOptions mockMobileConnectRequestOptions = Mockito.mock(
                        MobileConnectRequestOptions.class);

                // When
                mobileConnectAndroidView.attemptAuthenticationWithWebView(InstrumentationRegistry.getTargetContext(),
                        mockAuthenticationListener,
                        url,
                        state,
                        nonce,
                        mockMobileConnectRequestOptions);

                // Then
                //Espresso.onView(ViewMatchers.withId(R.id.web_view))
                //        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
            }
        });
    }

    @Test
    public void testAttemptDiscoveryWithWebView() throws Exception {
        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Given
                final DiscoveryListener mockDiscoveryListener = Mockito.mock(DiscoveryListener.class);
                final String operatorUrl = "http://operatorUrl";
                final String redirectUrl = "http://authenticationurl";

                final MobileConnectRequestOptions mockMobileConnectRequestOptions = Mockito.mock(
                        MobileConnectRequestOptions.class);

                // When
                mobileConnectAndroidView.attemptDiscoveryWithWebView(InstrumentationRegistry.getTargetContext(),
                        mockDiscoveryListener,
                        operatorUrl,
                        redirectUrl,
                        mockMobileConnectRequestOptions);

                // Then
                //Espresso.onView(ViewMatchers.withId(R.id.web_view))
                //        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
            }
        });
    }
}
