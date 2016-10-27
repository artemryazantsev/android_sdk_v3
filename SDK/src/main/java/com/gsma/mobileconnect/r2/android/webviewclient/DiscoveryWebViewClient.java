package com.gsma.mobileconnect.r2.android.webviewclient;

import android.util.Log;
import android.widget.ProgressBar;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.interfaces.WebViewCallBack;
import com.gsma.mobileconnect.r2.android.view.DiscoveryAuthenticationDialog;

/**
 * A custom {@link android.webkit.WebViewClient} to handle Discovery in a {@link android.webkit.WebView}
 *
 * @since 2.0
 */
public class DiscoveryWebViewClient extends MobileConnectWebViewClient
{
    private WebViewCallBack webViewCallBack;

    private static final String TAG = DiscoveryWebViewClient.class.getSimpleName();

    public DiscoveryWebViewClient(final DiscoveryAuthenticationDialog dialog,
                                  final ProgressBar progressBar,
                                  final String redirectUrl,
                                  final WebViewCallBack webViewCallBack)
    {
        super(dialog, progressBar, redirectUrl);
        this.webViewCallBack = webViewCallBack;
    }

    @Override
    protected boolean qualifyUrl(final String url)
    {
        Log.i(TAG, String.format("Qualifying Discovery Url=%s", url));
        return url.contains("mcc_mnc=");
    }

    @Override
    protected void handleError(final MobileConnectStatus status)
    {
        Log.i(TAG, "Discovery Failed");
        this.webViewCallBack.onError(status);
    }

    @Override
    protected WebViewCallBack getWebViewCallback()
    {
        return webViewCallBack;
    }
}