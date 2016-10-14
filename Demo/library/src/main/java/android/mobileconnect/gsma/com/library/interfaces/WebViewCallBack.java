package android.mobileconnect.gsma.com.library.interfaces;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

/**
 *
 */

public interface WebViewCallBack
{
    /**
     * When the {@link android.webkit.WebView} receives an error from the API
     *
     * @param mobileConnectStatus The status which is constructed containing the details of the error.
     */
    void onError(final MobileConnectStatus mobileConnectStatus);

    /**
     * When the API redirects the {@link android.webkit.WebView} to a URL containing the correct parameters.
     *
     * @param url The URL containing the desired parameters
     */
    void onSuccess(final String url);
}
