package android.mobileconnect.gsma.com.library.webviewclient;

import android.mobileconnect.gsma.com.library.callback.AuthenticationListener;
import android.mobileconnect.gsma.com.library.callback.WebViewCallBack;
import android.mobileconnect.gsma.com.library.view.DiscoveryAuthenticationDialog;
import android.widget.ProgressBar;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

public class AuthenticationWebViewClient extends MobileConnectWebViewClient
{
    private final WebViewCallBack webViewCallBack;

    AuthenticationListener listener;

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
    protected void handleResult(final String url)
    {
        webViewCallBack.onSuccess(url);
    }
}