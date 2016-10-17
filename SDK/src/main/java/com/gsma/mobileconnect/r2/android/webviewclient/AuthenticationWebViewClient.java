package com.gsma.mobileconnect.r2.android.webviewclient;

import com.gsma.mobileconnect.r2.android.interfaces.AuthenticationListener;
import com.gsma.mobileconnect.r2.android.interfaces.WebViewCallBack;
import com.gsma.mobileconnect.r2.android.view.DiscoveryAuthenticationDialog;
import android.widget.ProgressBar;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

/**
 * A custom {@link android.webkit.WebViewClient} to handle Authorization in a {@link android.webkit.WebView}
 *
 * @since 2.0
 */
public class AuthenticationWebViewClient extends MobileConnectWebViewClient
{
    private final WebViewCallBack webViewCallBack;

    private AuthenticationListener listener;

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
        return url.contains("code");
    }

    @Override
    protected void handleError(final MobileConnectStatus status)
    {
        this.listener.authenticationFailed(status);
    }

    @Override
    protected WebViewCallBack getWebViewCallback()
    {
        return webViewCallBack;
    }
}