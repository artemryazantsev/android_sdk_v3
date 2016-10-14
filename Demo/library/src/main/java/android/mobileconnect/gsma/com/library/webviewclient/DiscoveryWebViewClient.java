package android.mobileconnect.gsma.com.library.webviewclient;

import android.mobileconnect.gsma.com.library.interfaces.WebViewCallBack;
import android.mobileconnect.gsma.com.library.view.DiscoveryAuthenticationDialog;
import android.widget.ProgressBar;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

/**
 * A custom {@link android.webkit.WebViewClient} to handle Discovery in a {@link android.webkit.WebView}
 *
 * @since 2.0
 */
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
    protected WebViewCallBack getWebViewCallback()
    {
        return webViewCallBack;
    }
}