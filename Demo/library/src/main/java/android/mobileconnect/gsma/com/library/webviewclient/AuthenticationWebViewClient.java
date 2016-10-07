package android.mobileconnect.gsma.com.library.webviewclient;

import android.mobileconnect.gsma.com.library.callback.AuthenticationListener;
import android.mobileconnect.gsma.com.library.callback.AuthenticationWebViewCallback;
import android.mobileconnect.gsma.com.library.view.DiscoveryAuthenticationDialog;
import android.widget.ProgressBar;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

public class AuthenticationWebViewClient extends MobileConnectWebViewClient
{
    private final AuthenticationWebViewCallback authenticationWebViewCallback;

    AuthenticationListener listener;

    public AuthenticationWebViewClient(final DiscoveryAuthenticationDialog dialog,
                                       final ProgressBar progressBar,
                                       final AuthenticationListener listener,
                                       final String redirectUri,
                                       final AuthenticationWebViewCallback authenticationWebViewCallback)
    {
        super(dialog, progressBar, redirectUri);
        this.listener = listener;
        this.authenticationWebViewCallback = authenticationWebViewCallback;
    }

    @Override
    protected boolean qualifyUrl(final String url)
    {
        return url.contains("code");
    }

    @Override
    protected void handleError(final MobileConnectStatus status)
    {
        this.listener.authorizationFailed(status);
    }

    @Override
    protected void handleResult(final String url)
    {
        authenticationWebViewCallback.onSuccess(url);
    }
}