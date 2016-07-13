package com.gsma.mobileconnect.helpers;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.net.UrlQuerySanitizer;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * A Custom {@link WebViewClient} which handles callbacks from the server and checks for errors or successful callbacks.
 * <p/>
 * Created by Usmaan.Dad on 6/22/2016.
 */
abstract class MobileConnectWebViewClient extends WebViewClient
{
    private final ProgressBar progressBar;

    final Dialog dialog;

    private final String redirectUrl;

    public MobileConnectWebViewClient(final Dialog dialog, final ProgressBar progressBar, final String redirectUrl)
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
        this.handleError(getErrorStatus(failingUrl));
    }

    @TargetApi(android.os.Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(final WebView view, final WebResourceRequest request, final WebResourceError error)
    {
        this.dialog.cancel();
        this.handleError(getErrorStatus(request.getUrl().toString()));
    }

    private MobileConnectStatus getErrorStatus(final String url)
    {
        final UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(url);
        final String error = sanitizer.getValue("error");
        final String errorDescription = sanitizer.getValue("error_description");

        return MobileConnectStatus.error(error, errorDescription, new Exception(errorDescription));
    }

    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, final String url)
    {
        Log.d(MobileConnectWebViewClient.class.getSimpleName(), "onPageStarted disco url=" + url);
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
        }

        return true;
    }

    @Override
    public void onPageFinished(final WebView view, final String url)
    {
        super.onPageFinished(view, url);
        this.progressBar.setVisibility(View.GONE);
    }

    protected abstract boolean qualifyUrl(String url);

    protected abstract void handleError(MobileConnectStatus status);

    protected abstract void handleResult(String url);
}
