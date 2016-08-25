package android.mobileconnect.gsma.com.demo.fragments;

import android.mobileconnect.gsma.com.demo.R;
import android.mobileconnect.gsma.com.library.MobileConnectAndroidInterface;
import android.support.v4.app.Fragment;

import com.gsma.mobileconnect.r2.MobileConnect;
import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectInterface;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by usmaan.dad on 25/08/2016.
 */
public class BaseAuthFragment extends Fragment
{
    private MobileConnectAndroidInterface mobileConnectAndroidInterface;

    private MobileConnectConfig mobileConnectConfig;

    /**
     * Sets-up the {@link BaseAuthFragment#mobileConnectAndroidInterface} with the configuration based on the values in
     * strings.xml
     */
    protected void setupMobileConnectAndroid()
    {
        URI discoveryUri = null;
        URI redirectUri = null;

        try
        {
            discoveryUri = new URI(getString(R.string.discovery_url));
            redirectUri = new URI(getString(R.string.redirect_url));
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        mobileConnectConfig = new MobileConnectConfig.Builder().withClientId(getString(R.string.client_key))
                                                               .withClientSecret(getString(R.string.client_secret))
                                                               .withDiscoveryUrl(discoveryUri)
                                                               .withRedirectUrl(redirectUri)
                                                               .build();

        // todo setup only a single thread for this service
        MobileConnectInterface mobileConnectInterface = MobileConnect.buildInterface(mobileConnectConfig);

        mobileConnectAndroidInterface = new MobileConnectAndroidInterface(mobileConnectInterface);
    }
}
