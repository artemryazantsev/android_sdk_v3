package android.mobileconnect.gsma.com.library.callback;

import android.mobileconnect.gsma.com.library.MobileConnectAndroidInterface;

/**
 * Created by usmaan.dad on 12/09/2016.
 */
public interface AuthenticationWebViewCallback
{
    /**
     * This gets called when Authentication succeeds and a 'code' parameter is retrieved. The next step is then
     * usually to call {@link
     * MobileConnectAndroidInterface#handleUrlRedirect}  to understand what the next step is.
     *
     * @param url The url containing the 'code' paremter when Authentication succeeds.
     */
    void onSuccess(String url);
}