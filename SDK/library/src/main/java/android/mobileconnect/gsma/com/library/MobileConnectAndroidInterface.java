package android.mobileconnect.gsma.com.library;

import android.content.Context;
import android.content.DialogInterface;
import android.mobileconnect.gsma.com.library.view.DiscoveryAuthenticationDialog;
import android.mobileconnect.gsma.com.library.view.InteractableWebView;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.cache.CacheAccessException;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.DiscoveryService;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class interfaces with the underlying Java SDK. It wraps calls to the Java SDK in
 * {@link AsyncTask}s and sends the result via a {@link IMobileConnectCallback}
 * <p/>
 * Created by usmaan.dad on 11/08/2016.
 */
public class MobileConnectAndroidInterface
{
    private final MobileConnectInterface mobileConnectInterface;

    private String mcc;

    private String mnc;

    private DiscoveryService discoveryService;

    /**
     * @param mobileConnectInterface The {@link MobileConnectConfig} containing the necessary set-up.
     */
    public MobileConnectAndroidInterface(@NonNull MobileConnectInterface mobileConnectInterface,
                                         DiscoveryService discoveryService)
    {
        this.mobileConnectInterface = mobileConnectInterface;
        this.discoveryService = discoveryService;
    }

    /**
     * Launches a Dialog containing a {@link WebView} and loads the passed-in operatorUrl.
     *
     * @param activityContext   The {@link Context} in which the Dialog is used to draw
     * @param discoveryListener The callback determining success, failure or the dialog being closed
     * @param operatorUrl       The url to load in to the {@link WebView}
     * @param redirectUrl       The redirect url expected to be received from the API
     */
    public void attemptDiscoveryWithWebView(final Context activityContext,
                                            final DiscoveryListener discoveryListener,
                                            final String operatorUrl,
                                            final String redirectUrl)
    {
        initiateWebView(activityContext, discoveryListener, operatorUrl, redirectUrl);
    }

    public void attemptAuthenticationWithWebView(@NonNull final Context activityContext,
                                                 @NonNull final AuthenticationListener authenticationListener,
                                                 @NonNull final String url,
                                                 @NonNull final DiscoveryResponse discoveryResponse,
                                                 @NonNull final String state,
                                                 @NonNull String nonce)
    {
        initiateWebView(activityContext, authenticationListener, url, discoveryResponse, state, nonce);
    }

    private void initiateWebView(@NonNull final Context activityContext,
                                 @NonNull final DiscoveryListener discoveryListener,
                                 @NonNull final String operatorUrl,
                                 @NonNull final String redirectUrl)
    {
        RelativeLayout webViewLayout = (RelativeLayout) LayoutInflater.from(activityContext)
                                                                      .inflate(R.layout.layout_web_view, null);

        final InteractableWebView webView = (InteractableWebView) webViewLayout.findViewById(R.id.web_view);
        final ProgressBar progressBar = (ProgressBar) webViewLayout.findViewById(R.id.progressBar);

        final DiscoveryAuthenticationDialog dialog = new DiscoveryAuthenticationDialog(activityContext);

        dialog.setContentView(webViewLayout);

        webView.setWebChromeClient(new WebChromeClient());

        final DiscoveryWebViewClient webViewClient = new DiscoveryWebViewClient(dialog,
                                                                                progressBar,
                                                                                redirectUrl,
                                                                                discoveryListener,
                                                                                this);
        webView.setWebViewClient(webViewClient);

        webView.loadUrl(operatorUrl);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(final DialogInterface dialogInterface)
            {
                Log.e("Discovery Dialog", "dismissed");
                dialogInterface.dismiss();
                closeWebViewAndNotify(discoveryListener, webView);
            }
        });

        try
        {
            dialog.show();
        }
        catch (final WindowManager.BadTokenException exception)
        {
            Log.e("Discovery Dialog", exception.getMessage());
        }
    }

    private void initiateWebView(@NonNull final Context activityContext,
                                 @NonNull final AuthenticationListener authenticationListener,
                                 @NonNull final String authenticationUrl,
                                 @NonNull final DiscoveryResponse discoveryResponse,
                                 @NonNull final String state,
                                 @NonNull final String nonce)
    {
        RelativeLayout webViewLayout = (RelativeLayout) LayoutInflater.from(activityContext)
                                                                      .inflate(R.layout.layout_web_view, null);

        final InteractableWebView webView = (InteractableWebView) webViewLayout.findViewById(R.id.web_view);
        final ProgressBar progressBar = (ProgressBar) webViewLayout.findViewById(R.id.progressBar);

        final DiscoveryAuthenticationDialog dialog = new DiscoveryAuthenticationDialog(activityContext);

        dialog.setContentView(webViewLayout);

        webView.setWebChromeClient(new WebChromeClient());

        Uri uri = Uri.parse(authenticationUrl);
        String redirectUrl = uri.getQueryParameter("redirect_uri");

        final AuthenticationWebViewClient webViewClient = new AuthenticationWebViewClient(dialog,
                                                                                          progressBar,
                                                                                          authenticationListener,
                                                                                          redirectUrl,
                                                                                          discoveryResponse,
                                                                                          new AuthenticationWebViewCallback()
                                                                                          {
                                                                                              @Override
                                                                                              public void onSuccess(
                                                                                                      String url)
                                                                                              {
                                                                                                  handleRedirectAfterAuthentication(
                                                                                                          url,
                                                                                                          state,
                                                                                                          nonce,
                                                                                                          authenticationListener);
                                                                                              }
                                                                                          });

        webView.setWebViewClient(webViewClient);

        webView.loadUrl(authenticationUrl);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(final DialogInterface dialogInterface)
            {
                Log.e("Authentication Dialog", "dismissed");
                dialogInterface.dismiss();
                closeWebViewAndNotify(authenticationListener, webView);
            }
        });

        try
        {
            dialog.show();
        }
        catch (final WindowManager.BadTokenException exception)
        {
            Log.e("Discovery Dialog", exception.getMessage());
        }
    }

    private void handleRedirectAfterAuthentication(final String url,
                                                   final String state,
                                                   final String nonce,
                                                   final AuthenticationListener authenticationListener)
    {
        URI uri = null;

        try
        {
            uri = new URI(url);
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        if (uri == null)
        {
            return;
        }

        DiscoveryResponse discoveryResponse = null;

        try
        {
            discoveryResponse = discoveryService.getCachedDiscoveryResponse(mcc, mnc);
        }
        catch (CacheAccessException e)
        {
            e.printStackTrace();
        }

        handleRedirect(uri, discoveryResponse, state, nonce, new IMobileConnectCallback()
        {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus)
            {
                authenticationListener.authorizationSuccess(mobileConnectStatus);
            }
        });
    }

    private String getAuthUrl(final String authenticationUrl, final String urlWithParameters)
    {
        Uri uri = Uri.parse(urlWithParameters);

        Uri.Builder builder = Uri.parse(authenticationUrl).buildUpon();

        if (uri != null)
        {
            for (String queryParamName : uri.getQueryParameterNames())
            {
                builder.appendQueryParameter(queryParamName, uri.getQueryParameter(queryParamName));
            }
        }

        return builder.build().toString();
    }

    /**
     * Stop any processing on the {@link WebView} and notify that it has been stopped. This is called regardless of
     * success, failure or if the user has intentionally closed the dialog.
     *
     * @param listener The callback to notify of the closing of the Dialog
     * @param webView  The view to stop loading.
     */
    private void closeWebViewAndNotify(final DiscoveryListener listener, final WebView webView)
    {
        webView.stopLoading();
        webView.loadData("", "text/html", null);
        listener.onDiscoveryDialogClose();
    }

    /**
     * Stop any processing on the {@link WebView} and notify that it has been stopped. This is called regardless of
     * success, failure or if the user has intentionally closed the dialog.
     *
     * @param listener The callback to notify of the closing of the Dialog
     * @param webView  The view to stop loading.
     */
    private void closeWebViewAndNotify(final AuthenticationListener listener, final WebView webView)
    {
        webView.stopLoading();
        webView.loadData("", "text/html", null);
        listener.onAuthorizationDialogClose();
    }

    /**
     * Asynchronously attempt discovery using the supplied parameters. If msisdn, mcc and mnc are null the result
     * will be operator selection, otherwise valid parameters will result in a StartAuthorization
     * status
     *
     * @param mobileConnectCallback The callback in which a {@link MobileConnectStatus} shall be provided after
     *                              completion
     * @param msisdn                MSISDN from user
     * @param mcc                   Mobile Country Code
     * @param mnc                   Mobile Network Code
     * @param options               Optional parameters
     * @return MobileConnectStatus object with required information for continuing the Mobile
     * Connect process
     */
    @SuppressWarnings("unused")
    public void attemptDiscovery(final String msisdn,
                                 final String mcc,
                                 final String mnc,
                                 final MobileConnectRequestOptions options,
                                 @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        new MobileConnectAsyncTask(new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidInterface.this.mobileConnectInterface.attemptDiscovery(msisdn,
                                                                                                  mcc,
                                                                                                  mnc,
                                                                                                  options);
            }
        }, mobileConnectCallback).execute();
    }

    /**
     * Asynchronously attempt discovery using the values returned from the operator selection redirect
     * <p/>
     *
     * @param mobileConnectCallback The callback in which a {@link MobileConnectStatus} shall be provided after
     *                              completion
     * @param redirectUri           URI redirected to by the completion of the operator selection UI
     * @return MobileConnectStatus object with required information for continuing the Mobile
     * Connect process
     */
    @SuppressWarnings("unused")
    public void attemptDiscoveryAfterOperatorSelection(@NonNull final IMobileConnectCallback mobileConnectCallback,
                                                       @Nullable final URI redirectUri)
    {
        new MobileConnectAsyncTask(new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidInterface.this.mobileConnectInterface.attemptDiscoveryAfterOperatorSelection(
                        redirectUri);
            }
        }, mobileConnectCallback).execute();
    }

    /**
     * Asynchronously creates an authorization url with parameters to begin the authorization process
     *
     * @param mobileConnectCallback The callback in which a {@link MobileConnectStatus} shall be provided after
     *                              completion
     * @param discoveryResponse     The response returned by the discovery process
     * @param encryptedMSISDN       Encrypted MSISDN/Subscriber Id returned from the Discovery process
     * @param state                 Unique state value, this will be returned by the authorization
     *                              process and should be checked for equality as a secURIty measure
     * @param nonce                 Unique value to associate a client session with an id token
     * @param options               Optional parameters
     * @return MobileConnectStatus object with required information for continuing the Mobile
     * Connect process
     */
    @SuppressWarnings("unused")
    public void startAuthentication(final DiscoveryResponse discoveryResponse,
                                    final String encryptedMSISDN,
                                    final String state,
                                    final String nonce,
                                    final MobileConnectRequestOptions options,
                                    @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        new MobileConnectAsyncTask(new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidInterface.this.mobileConnectInterface.startAuthentication(discoveryResponse,
                                                                                                     encryptedMSISDN,
                                                                                                     state,
                                                                                                     nonce,
                                                                                                     options);
            }
        }, mobileConnectCallback).execute();
    }

    @SuppressWarnings("unused")
    public void requestToken(final DiscoveryResponse discoveryResponse,
                             final URI redirectedUrl,
                             final String expectedState,
                             final String expectedNonce,
                             @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        new MobileConnectAsyncTask(new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidInterface.this.mobileConnectInterface.requestToken(discoveryResponse,
                                                                                              redirectedUrl,
                                                                                              expectedState,
                                                                                              expectedNonce);
            }
        }, mobileConnectCallback).execute();
    }

    @SuppressWarnings("unused")
    public void handleRedirect(final URI redirectedUrl,
                               final DiscoveryResponse discoveryResponse,
                               final String expectedState,
                               final String expectedNonce,
                               @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        new MobileConnectAsyncTask(new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidInterface.this.mobileConnectInterface.handleUrlRedirect(redirectedUrl,
                                                                                                   discoveryResponse,
                                                                                                   expectedState,
                                                                                                   expectedNonce);
            }
        }, mobileConnectCallback).execute();
    }

    @SuppressWarnings("unused")
    public void requestIdentity(final DiscoveryResponse discoveryResponse,
                                final String accessToken,
                                @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        new MobileConnectAsyncTask(new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidInterface.this.mobileConnectInterface.requestIdentity(discoveryResponse,
                                                                                                 accessToken);
            }
        }, mobileConnectCallback).execute();
    }

    public void requestUserInfo(final DiscoveryResponse discoveryResponse,
                                final String accessToken,
                                final IMobileConnectCallback mobileConnectCallback)
    {
        new MobileConnectAsyncTask(new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidInterface.this.mobileConnectInterface.requestUserInfo(discoveryResponse,
                                                                                                 accessToken);
            }
        }, mobileConnectCallback).execute();
    }

    public String getMcc()
    {
        return mcc;
    }

    public void setMcc(String mcc)
    {
        this.mcc = mcc;
    }

    public void setMnc(String mnc)
    {
        this.mnc = mnc;
    }

    public String getMnc()
    {
        return mnc;
    }

    private class MobileConnectAsyncTask extends AsyncTask<Void, Void, MobileConnectStatus>
    {
        private IMobileConnectOperation mobileConnectOperation;

        private IMobileConnectCallback IMobileConnectCallback;

        public MobileConnectAsyncTask(@NonNull IMobileConnectOperation mobileConnectOperation,
                                      @NonNull IMobileConnectCallback IMobileConnectCallback)
        {

            this.mobileConnectOperation = mobileConnectOperation;
            this.IMobileConnectCallback = IMobileConnectCallback;
        }

        @Override
        protected MobileConnectStatus doInBackground(@Nullable Void... voids)
        {
            return mobileConnectOperation.operation();
        }

        @Override
        protected void onPostExecute(MobileConnectStatus mobileConnectStatus)
        {
            super.onPostExecute(mobileConnectStatus);
            if (IMobileConnectCallback != null)
            {
                IMobileConnectCallback.onComplete(mobileConnectStatus);
            }
        }
    }

    public interface IMobileConnectCallback
    {
        void onComplete(MobileConnectStatus mobileConnectStatus);
    }
}