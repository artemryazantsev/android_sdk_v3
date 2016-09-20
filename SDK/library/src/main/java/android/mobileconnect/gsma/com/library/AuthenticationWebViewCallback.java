package android.mobileconnect.gsma.com.library;

import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;

import java.net.URI;

/**
 * Created by usmaan.dad on 12/09/2016.
 */
public interface AuthenticationWebViewCallback
{
    /**
     * This gets called when Authentication succeeds and a 'code' parameter is retrieved. The next step is then
     * usually to call {@link
     * MobileConnectAndroidInterface#handleRedirect(URI, DiscoveryResponse, String, String,
     * MobileConnectAndroidInterface.IMobileConnectCallback)}  to understand what the next step is.
     *
     * @param url The url containing the 'code' paremter when Authentication succeeds.
     */
    public void onSuccess(String url);
}