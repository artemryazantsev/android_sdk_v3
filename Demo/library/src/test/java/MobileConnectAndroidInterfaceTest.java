import android.mobileconnect.gsma.com.library.AndroidMobileConnectEncodeDecoder;
import android.mobileconnect.gsma.com.library.MobileConnectAndroidInterface;

import com.gsma.mobileconnect.r2.MobileConnect;
import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by usmaan.dad on 29/09/2016.
 */
@RunWith(RobolectricTestRunner.class)
public class MobileConnectAndroidInterfaceTest
{
    @Ignore
    @Test
    public void attemptDiscoveryWithoutMSISDN()
    {
        URI discoveryUri = null;
        try
        {
            discoveryUri = new URI("https://reference.mobileconnect.io/discovery/");
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        URI redirectUri = null;
        try
        {
            redirectUri = new URI("https://reference.mobileconnect.io/discovery/");
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        MobileConnectConfig mobileConnectConfig = new MobileConnectConfig.Builder().withClientId("ZWRhNjU3OWI3MGIwYTRh")
                                                                                   .withClientSecret(
                                                                                           "ZWRhNjU3OWI3MGIwYTRh")
                                                                                   .withDiscoveryUrl(discoveryUri)
                                                                                   .withRedirectUrl(redirectUri)
                                                                                   .withCacheResponsesWithSessionId(
                                                                                           false)
                                                                                   .build();

        final MobileConnect mobileConnect = MobileConnect.build(mobileConnectConfig,
                                                                new AndroidMobileConnectEncodeDecoder());

        MobileConnectInterface mobileConnectInterface = mobileConnect.getMobileConnectInterface();

        MobileConnectAndroidInterface mobileConnectAndroidInterface = new MobileConnectAndroidInterface(
                mobileConnectInterface);

        DiscoveryOptions.Builder discoveryOptionsBuilder = new DiscoveryOptions.Builder();


        MobileConnectRequestOptions requestOptions = new MobileConnectRequestOptions.Builder().withDiscoveryOptions(
                discoveryOptionsBuilder.withMsisdn(null).build()).build();


        mobileConnectAndroidInterface.attemptDiscovery(null,
                                                       null,
                                                       null,
                                                       requestOptions,
                                                       new MobileConnectAndroidInterface.IMobileConnectCallback()
                                                       {
                                                           @Override
                                                           public void onComplete(MobileConnectStatus
                                                                                          mobileConnectStatus)
                                                           {
                                                               assertThat(mobileConnectStatus, notNullValue());
                                                           }
                                                       });
    }
}
