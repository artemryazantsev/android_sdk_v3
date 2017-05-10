package com.gsma.mobileconnect.r2.android.webviewclient;

import android.annotation.TargetApi;
import android.net.UrlQuerySanitizer;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.interfaces.WebViewCallBack;
import com.gsma.mobileconnect.r2.android.view.DiscoveryAuthenticationDialog;

/**
 * A Base {@link WebViewClient} which intercepts callbacks from the MobileConnect API
 * <p/>
 * It specifically looks for special query parameters in the returned URL from the API and informs the subclass via
 * the abstract methods.
 * <p/>
 *
 * @since 1.0
 */
abstract class MobileConnectWebViewClient extends WebViewClient
{
    protected ProgressBar progressBar;

    protected DiscoveryAuthenticationDialog dialog;

    private String redirectUrl;

    private static final String TAG = MobileConnectWebViewClient.class.getSimpleName();

    MobileConnectWebViewClient(final DiscoveryAuthenticationDialog dialog,
                               final ProgressBar progressBar,
                               final String redirectUrl)
    {
        this.progressBar = progressBar;
        this.dialog = dialog;
        this.redirectUrl = redirectUrl;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(final WebView view,
                                final int errorCode,
                                final String description,
                                final String failingUrl)
    {
        Log.i(TAG, String.format("onReceivedError description=%s, failingUrl=%s", description, failingUrl));
        this.dialog.cancel();
        this.handleError(getErrorStatus(failingUrl));
    }

    @TargetApi(android.os.Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(final WebView view, final WebResourceRequest request, final WebResourceError error)
    {
        dialog.cancel();
        handleError(getErrorStatus(request.getUrl().toString()));
    }

    private MobileConnectStatus getErrorStatus(final String url)
    {
        final UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(url);
        final String error = sanitizer.getValue("error");
        String errorDescription = sanitizer.getValue("error_description");

        if (errorDescription == null)
        {
            errorDescription = sanitizer.getValue("description");
        }

        if (errorDescription == null)
        {
            errorDescription = "An error occurred.";
        }

        return MobileConnectStatus.error(error, errorDescription, new Exception(errorDescription));
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, final String url)
    {
        Log.i(TAG, String.format("Loading URL=%s", url));
        this.progressBar.setVisibility(View.VISIBLE);

        if (!url.startsWith(this.redirectUrl))
        {
            return false;
        }

        // If there is a delay before true is returned by this method then WebView will sometimes just continue
        // loading the
        // url regardless, so we have to ensure the callback url is not being loaded. We load a blank page for this
        // reason.
         view.stopLoading();
         view.loadData("", "text/html", null);

        // Check for response errors in the URL
        if (url.contains("error"))
        {
            handleError(getErrorStatus(url));
            this.dialog.cancel();
        }

        /*
        * Check to see if the url contains the discovery token
        * identifier - it could be a url parameter or a page
        * fragment. The following checks and string manipulations
        * retrieve the actual discovery token
        */
        if (qualifyUrl(url))
        {
            handleResult(url);
            Log.i(TAG, "Closing dialog");
            this.dialog.cancel();
        }

//        this.dialog.show();
//        view.loadUrl(url);

        return true;
    }

    @Override
    public void onPageFinished(final WebView view, final String url)
    {
        super.onPageFinished(view, url);
        Log.i(TAG, String.format("onPageFinished=%s", url));
        this.progressBar.setVisibility(View.GONE);
    }

    /**
     * When the URL contains the parameter which we are interested in.
     * This should be called if {@link #qualifyUrl(String)} returns True.
     *
     * @param url The URL containing the desired query parameter
     */
    void handleResult(final String url)
    {
        Log.i(TAG, String.format("Successful Callback=%s", url));
        getWebViewCallback().onSuccess(url);
    }

    /**
     * Check if the URL contains the parameters which we are interested in. This is typically called first and if
     * True is returned, {@link #handleResult(String)}
     *
     * @param url The URL to be interrogated.
     * @return True if the contains the query parameter we're looking for
     */
    protected abstract boolean qualifyUrl(final String url);

    /**
     * In the case of an error returned by the API.
     *
     * @param status The {@link MobileConnectStatus} containing the Error.
     */
    protected abstract void handleError(final MobileConnectStatus status);

    protected abstract WebViewCallBack getWebViewCallback();
}
