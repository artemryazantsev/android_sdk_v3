package android.mobileconnect.gsma.com.library.main;

import android.mobileconnect.gsma.com.library.TestActivity;
import android.mobileconnect.gsma.com.library.compatibility.AndroidMobileConnectEncodeDecoder;
import android.mobileconnect.gsma.com.library.interfaces.AuthenticationListener;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.gsma.mobileconnect.r2.MobileConnect;
import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;

import junit.framework.Assert;

import org.mockito.Matchers;
import org.mockito.Mockito;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

import static android.content.ContentValues.TAG;

public class MobileConnectAndroidViewTests extends ActivityInstrumentationTestCase2<TestActivity>
{
    private MobileConnectAndroidView mobileConnectAndroidView;

    private MobileConnectContract.UserActionsListener mockPresenter;

    private MobileConnectContract.IMobileConnectCallback mobileConnectCallback;

    public MobileConnectAndroidViewTests()
    {
        super(TestActivity.class);
    }

    public void setUp() throws Exception
    {
        super.setUp();

        mobileConnectCallback = new MobileConnectContract.IMobileConnectCallback()
        {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus)
            {

            }
        };


        MobileConnectConfig mobileConnectConfig = new MobileConnectConfig.Builder().withClientId("clientId")
                                                                                   .withClientSecret("clientSecret")
                                                                                   .withDiscoveryUrl(new URI(
                                                                                           "http://discoveryUri"))
                                                                                   .withRedirectUrl(new URI(
                                                                                           "http://redirectUri"))
                                                                                   .withCacheResponsesWithSessionId(
                                                                                           false)
                                                                                   .build();

        MobileConnect mobileConnect = new MobileConnect.Builder(mobileConnectConfig,
                                                                new AndroidMobileConnectEncodeDecoder()).build();

        MobileConnectInterface mobileConnectInterface = mobileConnect.getMobileConnectInterface();

        mobileConnectAndroidView = new MobileConnectAndroidView(mobileConnectInterface);

        mockPresenter = Mockito.mock(MobileConnectContract.UserActionsListener.class);
        mobileConnectAndroidView.setPresenter(mockPresenter);
    }

    public void tearDown() throws Exception
    {
        getActivity().finish();
        // Without this sleep then activity.finish does not complete before the next test starts and the register on
        // the bus fails
        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            Log.d(TAG, "Waking up to finish tear down");
        }

        super.tearDown();
    }

    public void testConstructor()
    {
        Assert.assertNotNull("Check constructor", mobileConnectAndroidView);
        Assert.assertNotNull("Check presenter", mobileConnectAndroidView.getPresenter());
    }

    public void testAttemptDiscovery()
    {
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

    public void testAttemptDiscoveryAfterOperatorSelection() throws Exception
    {
        // Given
        URI redirectUri = new URI("http://redirect.html");

        // When
        mobileConnectAndroidView.attemptDiscoveryAfterOperatorSelection(mobileConnectCallback, redirectUri);

        // Then
        Mockito.verify(mockPresenter).performDiscoveryAfterOperatorSelection(mobileConnectCallback, redirectUri);
    }

    public void testStartAuthentication() throws Exception
    {
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

    public void testRequestToken() throws Exception
    {
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

    public void testHandleUrlRedirect() throws Exception
    {
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

    public void testRequestIdentity() throws Exception
    {
        // Given
        final String accessToken = "accessToken";

        // When
        mobileConnectAndroidView.requestIdentity(accessToken, mobileConnectCallback);

        // Then
        Mockito.verify(mockPresenter).performRequestIdentity(accessToken, mobileConnectCallback);
    }

    public void testRefreshToken() throws Exception
    {
        // Given
        final String refreshToken = "refreshToken";

        // When
        mobileConnectAndroidView.refreshToken(refreshToken, mobileConnectCallback);

        // Then
        Mockito.verify(mockPresenter).performRefreshToken(refreshToken, mobileConnectCallback);
    }

    public void testRevokeToken() throws Exception
    {
        // Given
        final String accessToken = "accessToken";
        final String tokenTypeHint = "tokenTypeHint";

        // When
        mobileConnectAndroidView.revokeToken(accessToken, tokenTypeHint, mobileConnectCallback);

        // Then
        Mockito.verify(mockPresenter).performRevokeToken(accessToken, tokenTypeHint, mobileConnectCallback);
    }

    public void testRequestUserInfo() throws Exception
    {
        // Given
        final String accessToken = "accessToken";

        // When
        mobileConnectAndroidView.requestUserInfo(accessToken, mobileConnectCallback);

        // Then
        Mockito.verify(mockPresenter).performRequestUserInfo(accessToken, mobileConnectCallback);
    }

    public void testHandleUrlRedirectAfterAuthenticationInValidURL() throws Exception
    {
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

    public void testHandleUrlRedirectAfterAuthenticationValidURL() throws Exception
    {
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
                                         Matchers.any(MobileConnectContract.IMobileConnectCallback.class));
    }

    public void testGetDiscoveryResponse()
    {
        // Given
        // When
        mobileConnectAndroidView.getDiscoveryResponse();

        // Then
        Mockito.verify(mockPresenter).getDiscoveryResponse();
    }

    public void testPerformAsyncTask() throws Exception
    {
        // Given
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final MobileConnectContract.IMobileConnectOperation mockMobileConnectOperation =
                Mockito.mock(MobileConnectContract.IMobileConnectOperation.class);
        MobileConnectStatus mockMobileConnectStatus = Mockito.mock(MobileConnectStatus.class);
        final MobileConnectContract.IMobileConnectCallback mockMobileConnectCallback =
                Mockito.mock(MobileConnectContract.IMobileConnectCallback.class);

        Mockito.when(mockMobileConnectOperation.operation()).thenReturn(mockMobileConnectStatus);

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
    public void testFragmentLayout() {
        Assert.assertEquals("Check correct fragment layout", R.layout.fragment_video_pager, pagerFragment
        .getFragmentLayout());
    }

    public void testFragmentTag() {
        Assert.assertEquals("Check correct fragment tag", VideoPagerFragment.class.getSimpleName(), pagerFragment
        .getFragmentTAG());
    }

    public void testViewElements() {
        Assert.assertNotNull("Check fragment exists", pagerFragment);

        Espresso.onView(ViewMatchers.withId(R.id.videoPager)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.videoPagerTabs)).check(ViewAssertions.matches(ViewMatchers
        .isDisplayed()));
    }*/
}
