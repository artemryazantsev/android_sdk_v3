package android.mobileconnect.gsma.com.library.webviewclient;

import android.mobileconnect.gsma.com.library.interfaces.AuthenticationListener;
import android.mobileconnect.gsma.com.library.interfaces.WebViewCallBack;
import android.mobileconnect.gsma.com.library.view.DiscoveryAuthenticationDialog;
import android.widget.ProgressBar;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

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