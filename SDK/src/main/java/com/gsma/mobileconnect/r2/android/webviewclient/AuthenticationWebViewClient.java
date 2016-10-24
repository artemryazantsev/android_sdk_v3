package com.gsma.mobileconnect.r2.android.webviewclient;

import android.util.Log;
import android.widget.ProgressBar;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.interfaces.AuthenticationListener;
import com.gsma.mobileconnect.r2.android.interfaces.WebViewCallBack;
import com.gsma.mobileconnect.r2.android.view.DiscoveryAuthenticationDialog;

/**
 * A custom {@link android.webkit.WebViewClient} to handle Authorization in a {@link android.webkit.WebView}
 *
 * @since 2.0
 */
public class AuthenticationWebViewClient extends MobileConnectWebViewClient
{
    private final WebViewCallBack webViewCallBack;

    private AuthenticationListener listener;

    private static final String TAG = AuthenticationWebViewClient.class.getSimpleName();

    public AuthenticationWebViewClient(final DiscoveryAuthenticationDialog dialog,
                                       final ProgressBar progressBar,
                                       final AuthenticationListener listener,
                                       final String redirectUri,
                                       final WebViewCallBack webViewCallBack)
    {
        super(dialog, progressBar, redirectUri);
        this.listener = listener;
        this.webViewCallBack = webViewCallBack;
    }

    @Override
    protected boolean qualifyUrl(final String url)
    {
        Log.i(TAG, String.format("Qualifying Authentication Url=%s", url));
        return url.contains("code");
    }

    @Override
    protected void handleError(final MobileConnectStatus status)
    {
        Log.i(TAG, "Authentication Failed");
        this.listener.authenticationFailed(status);
    }

    @Override
    protected WebViewCallBack getWebViewCallback()
    {
        return this.webViewCallBack;
    }
}