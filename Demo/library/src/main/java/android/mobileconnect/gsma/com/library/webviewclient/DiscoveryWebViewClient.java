package android.mobileconnect.gsma.com.library.webviewclient;

import android.mobileconnect.gsma.com.library.callback.WebViewCallBack;
import android.mobileconnect.gsma.com.library.view.DiscoveryAuthenticationDialog;
import android.util.Log;
import android.widget.ProgressBar;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

public class DiscoveryWebViewClient extends MobileConnectWebViewClient
{
    private WebViewCallBack webViewCallBack;

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
        return url.contains("mcc_mnc=");
    }

    @Override
    protected void handleError(final MobileConnectStatus status)
    {
        webViewCallBack.onError(status);
    }

    @Override
    protected void handleResult(final String url)
    {
        Log.d("url", url);
        webViewCallBack.onSuccess(url);
    }
}