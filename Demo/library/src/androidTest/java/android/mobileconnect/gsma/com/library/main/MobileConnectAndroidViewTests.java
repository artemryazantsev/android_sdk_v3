package android.mobileconnect.gsma.com.library.main;

import android.mobileconnect.gsma.com.library.TestActivity;
import android.mobileconnect.gsma.com.library.compatibility.AndroidMobileConnectEncodeDecoder;
import android.support.annotation.NonNull;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.gsma.mobileconnect.r2.MobileConnect;
import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;

import junit.framework.Assert;

import org.mockito.Mockito;

import java.net.URI;

import static android.content.ContentValues.TAG;

public class MobileConnectAndroidViewTests extends ActivityInstrumentationTestCase2<TestActivity> {

    private MobileConnectAndroidView mobileConnectAndroidView;
    private MobileConnectConfig mobileConnectConfig;
    private MobileConnectContract.UserActionsListener mockPresenter;
    private MobileConnectContract.IMobileConnectCallback mobileConnectCallback;

    public MobileConnectAndroidViewTests() {
        super(TestActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();

        mockPresenter = Mockito.mock(MobileConnectContract.UserActionsListener.class);
        mobileConnectCallback = new MobileConnectContract.IMobileConnectCallback()
        {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus)
            {

            }
        };


        mobileConnectConfig = new MobileConnectConfig.Builder().withClientId("clientId")
                                                               .withClientSecret("clientSecret")
                                                               .withDiscoveryUrl(new URI("http://discoveryUri"))
                                                               .withRedirectUrl(new URI("http://redirectUri"))
                                                               .withCacheResponsesWithSessionId(false)
                                                               .build();

        MobileConnect mobileConnect = new MobileConnect.Builder(mobileConnectConfig,
                                                                new AndroidMobileConnectEncodeDecoder()).build();

        MobileConnectInterface mobileConnectInterface = mobileConnect.getMobileConnectInterface();

        mobileConnectAndroidView = new MobileConnectAndroidView(mobileConnectInterface);
    }

    public void tearDown() throws Exception {
        getActivity().finish();
        // Without this sleep then activity.finish does not complete before the next test starts and the register on the bus fails
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Log.d(TAG, "Waking up to finish tear down");
        }

        super.tearDown();
    }

    public void testConstructor() {
        Assert.assertNotNull("Check constructor", mobileConnectAndroidView);
        Assert.assertNotNull("Check presenter", mobileConnectAndroidView.getPresenter());
    }

    public void testAttemptDiscovery()
    {
        // Given
        mobileConnectAndroidView.setPresenter(mockPresenter);
        final String msisdn = "msisdn";
        final String mcc = "mcc";
        final String mnc = "mnc";
        final MobileConnectRequestOptions options = null;

        // When
        mobileConnectAndroidView.attemptDiscovery(msisdn, mcc, mnc, options, mobileConnectCallback);

        // Then
        Mockito.verify(mockPresenter).performDiscovery(msisdn, mcc, mnc, options, mobileConnectCallback);
    }

    /*


    public void testFragmentLayout() {
        Assert.assertEquals("Check correct fragment layout", R.layout.fragment_video_pager, pagerFragment.getFragmentLayout());
    }

    public void testFragmentTag() {
        Assert.assertEquals("Check correct fragment tag", VideoPagerFragment.class.getSimpleName(), pagerFragment.getFragmentTAG());
    }

    public void testViewElements() {
        Assert.assertNotNull("Check fragment exists", pagerFragment);

        Espresso.onView(ViewMatchers.withId(R.id.videoPager)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.videoPagerTabs)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }*/
}
