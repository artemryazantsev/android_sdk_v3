package android.mobileconnect.gsma.com.library.main;

import android.content.Context;
import android.content.DialogInterface;
import android.mobileconnect.gsma.com.library.R;
import android.mobileconnect.gsma.com.library.callback.AuthenticationListener;
import android.mobileconnect.gsma.com.library.callback.AuthenticationWebViewCallback;
import android.mobileconnect.gsma.com.library.callback.DiscoveryListener;
import android.mobileconnect.gsma.com.library.callback.WebViewCallBack;
import android.mobileconnect.gsma.com.library.view.DiscoveryAuthenticationDialog;
import android.mobileconnect.gsma.com.library.view.InteractiveWebView;
import android.mobileconnect.gsma.com.library.webviewclient.AuthenticationWebViewClient;
import android.mobileconnect.gsma.com.library.webviewclient.DiscoveryWebViewClient;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static android.mobileconnect.gsma.com.library.main.MobileConnectContract.IMobileConnectCallback;
import static android.mobileconnect.gsma.com.library.main.MobileConnectContract.IMobileConnectOperation;

/**
 * This class interfaces with the underlying Java SDK. It wraps calls to the Java SDK in
 * {@link AsyncTask}s and sends the result via a {@link IMobileConnectCallback}
 * <p/>
 * Created by usmaan.dad on 11/08/2016.
 */
public class MobileConnectAndroidView implements MobileConnectContract.View
{
    private final MobileConnectContract.UserActionsListener presenter;

    private DiscoveryResponseObservable discoveryResponseObservable;

    /**
     * @param mobileConnectInterface The {@link MobileConnectConfig} containing the necessary set-up.
     */
    public MobileConnectAndroidView(@NonNull final MobileConnectInterface mobileConnectInterface)
    {
        this.presenter = new MobileConnectAndroidPresenter(mobileConnectInterface);
        this.presenter.setView(this);
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
                                            final String redirectUrl,
                                            final MobileConnectRequestOptions mobileConnectRequestOptions)
    {
        initiateWebView(activityContext, discoveryListener, operatorUrl, redirectUrl, mobileConnectRequestOptions);
    }

    public void attemptAuthenticationWithWebView(@NonNull final Context activityContext,
                                                 @NonNull final AuthenticationListener authenticationListener,
                                                 @NonNull final String url,
                                                 @NonNull final String state,
                                                 @NonNull String nonce,
                                                 @Nullable final MobileConnectRequestOptions
                                                         mobileConnectRequestOptions)
    {
        initiateWebView(activityContext, authenticationListener, url, state, nonce, mobileConnectRequestOptions);
    }

    private void initiateWebView(@NonNull final Context activityContext,
                                 @NonNull final DiscoveryListener discoveryListener,
                                 @NonNull final String operatorUrl,
                                 @NonNull final String redirectUrl,
                                 final MobileConnectRequestOptions mobileConnectRequestOptions)
    {
        RelativeLayout webViewLayout = (RelativeLayout) LayoutInflater.from(activityContext)
                                                                      .inflate(R.layout.layout_web_view, null);

        final InteractiveWebView webView = (InteractiveWebView) webViewLayout.findViewById(R.id.web_view);
        final ProgressBar progressBar = (ProgressBar) webViewLayout.findViewById(R.id.progressBar);

        final DiscoveryAuthenticationDialog dialog = new DiscoveryAuthenticationDialog(activityContext);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialogInterface)
            {
                Log.e("Discovery Dialog", "cancelled");
                closeWebViewAndNotify(discoveryListener, webView);
            }
        });

        dialog.setContentView(webViewLayout);

        webView.setWebChromeClient(new WebChromeClient());

        final WebViewCallBack discoveryWebViewCallback = new WebViewCallBack()
        {
            @Override
            public void onError(final MobileConnectStatus mobileConnectStatus)
            {
                discoveryListener.discoveryFailed(mobileConnectStatus);
            }

            @Override
            public void onSuccess(final String url)
            {
                URI uri = getUri(url);

                MobileConnectAndroidView.this.presenter.performHandleUrlRedirect(uri,
                                                                                 UUID.randomUUID().toString(),
                                                                                 UUID.randomUUID().toString(),
                                                                                 new IMobileConnectCallback()
                                                                                 {
                                                                                     @Override
                                                                                     public void onComplete(
                                                                                             MobileConnectStatus
                                                                                                     mobileConnectStatus)
                                                                                     {
                                                                                         discoveryListener
                                                                                                 .onDiscoveryResponse(
                                                                                                 mobileConnectStatus);
                                                                                     }
                                                                                 },
                                                                                 mobileConnectRequestOptions);
            }
        };

        final DiscoveryWebViewClient webViewClient = new DiscoveryWebViewClient(dialog,
                                                                                progressBar,
                                                                                redirectUrl,
                                                                                discoveryWebViewCallback);

        webView.setWebViewClient(webViewClient);
        webView.loadUrl(operatorUrl);

        try
        {
            dialog.show();
        }
        catch (final WindowManager.BadTokenException exception)
        {
            Log.e("Discovery Dialog", exception.getMessage());
        }
    }

    @Nullable
    private URI getUri(final String url)
    {
        URI uri;
        try
        {
            uri = new URI(url);
        }
        catch (URISyntaxException e)
        {
            uri = null;
            Log.e("Invalid Redirect URI", e.getMessage());
        }
        return uri;
    }

    private void initiateWebView(@NonNull final Context activityContext,
                                 @NonNull final AuthenticationListener authenticationListener,
                                 @NonNull final String authenticationUrl,
                                 @NonNull final String state,
                                 @NonNull final String nonce,
                                 final MobileConnectRequestOptions mobileRequestOptions)
    {
        RelativeLayout webViewLayout = (RelativeLayout) LayoutInflater.from(activityContext)
                                                                      .inflate(R.layout.layout_web_view, null);

        final InteractiveWebView webView = (InteractiveWebView) webViewLayout.findViewById(R.id.web_view);
        final ProgressBar progressBar = (ProgressBar) webViewLayout.findViewById(R.id.progressBar);

        final DiscoveryAuthenticationDialog dialog = new DiscoveryAuthenticationDialog(activityContext);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialogInterface)
            {
                Log.e("Authentication Dialog", "cancelled");
                closeWebViewAndNotify(authenticationListener, webView);
            }
        });

        dialog.setContentView(webViewLayout);

        webView.setWebChromeClient(new WebChromeClient());

        Uri uri = Uri.parse(authenticationUrl);
        String redirectUrl = uri.getQueryParameter("redirect_uri");

        final AuthenticationWebViewClient webViewClient = new AuthenticationWebViewClient(dialog,
                                                                                          progressBar,
                                                                                          authenticationListener,
                                                                                          redirectUrl,
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
                                                                                                          authenticationListener,
                                                                                                          mobileRequestOptions);
                                                                                              }
                                                                                          });

        webView.setWebViewClient(webViewClient);

        webView.loadUrl(authenticationUrl);

        try
        {
            dialog.show();
        }
        catch (final WindowManager.BadTokenException exception)
        {
            Log.e("Discovery Dialog", exception.getMessage());
        }
    }

    @Override
    public void handleRedirectAfterAuthentication(final String url,
                                                  final String state,
                                                  final String nonce,
                                                  final AuthenticationListener authenticationListener,
                                                  final MobileConnectRequestOptions mobileConnectRequestOptions)
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

        handleUrlRedirect(uri, state, nonce, new IMobileConnectCallback()
        {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus)
            {
                authenticationListener.authorizationSuccess(mobileConnectStatus);
            }
        }, mobileConnectRequestOptions);
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
     * It is mandatory to call this before any operations are called.
     */
    @Override
    public void initialise()
    {
        this.discoveryResponseObservable = new DiscoveryResponseObservable();
    }

    /**
     * It is mandatory to call this when your view is being destroyed.
     */
    @Override
    public void cleanUp()
    {
        this.discoveryResponseObservable.deleteObservers();
    }

    @Override
    public void performAsyncTask(@NonNull final IMobileConnectOperation mobileConnectOperation,
                                 @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        new MobileConnectAsyncTask(mobileConnectOperation, mobileConnectCallback, discoveryResponseObservable).execute();
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
     *                              Connect process
     */
    @Override
    public void attemptDiscovery(final String msisdn,
                                 final String mcc,
                                 final String mnc,
                                 final MobileConnectRequestOptions options,
                                 @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        this.presenter.performDiscovery(msisdn, mnc, mnc, options, mobileConnectCallback);
    }

    /**
     * Asynchronously attempt discovery using the values returned from the operator selection redirect
     * <p/>
     *
     * @param mobileConnectCallback The callback in which a {@link MobileConnectStatus} shall be provided after
     *                              completion
     * @param redirectUri           URI redirected to by the completion of the operator selection UI
     *                              Connect process
     */
    @Override
    @SuppressWarnings("unused")
    public void attemptDiscoveryAfterOperatorSelection(@NonNull final IMobileConnectCallback mobileConnectCallback,
                                                       @Nullable final URI redirectUri)
    {
        this.presenter.performDiscoveryAfterOperatorSelection(mobileConnectCallback, redirectUri);
    }

    /**
     * Asynchronously creates an authorization url with parameters to begin the authorization process
     *
     * @param mobileConnectCallback The callback in which a {@link MobileConnectStatus} shall be provided after
     *                              completion
     * @param encryptedMSISDN       Encrypted MSISDN/Subscriber Id returned from the Discovery process
     * @param state                 Unique state value, this will be returned by the authorization
     *                              process and should be checked for equality as a secURIty measure
     * @param nonce                 Unique value to associate a client session with an id token
     * @param options               Optional parameters
     *                              Connect process
     */
    @SuppressWarnings("unused")
    @Override
    public void startAuthentication(final String encryptedMSISDN,
                                    final String state,
                                    final String nonce,
                                    final MobileConnectRequestOptions options,
                                    @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        this.presenter.performAuthentication(encryptedMSISDN, state, nonce, options, mobileConnectCallback);
    }

    /**
     * Request token using the values returned from the authorization redirect
     *
     * @param redirectedUrl               URI redirected to by the completion of the authorization UI
     * @param expectedState               The state value returned from the StartAuthorization call should be
     *                                    passed here, it will be used to validate the authenticity of the
     *                                    authorization process
     * @param expectedNonce               The nonce value returned from the StartAuthorization call should be
     *                                    passed here, it will be used to ensure the token was not requested
     *                                    using a replay attack
     * @param mobileConnectRequestOptions Optional parameters
     *                                    Connect process
     */
    @SuppressWarnings("unused")
    @Override
    public void requestToken(final URI redirectedUrl,
                             final String expectedState,
                             final String expectedNonce,
                             @NonNull final IMobileConnectCallback mobileConnectCallback,
                             final MobileConnectRequestOptions mobileConnectRequestOptions)
    {
        this.presenter.performRequestToken(redirectedUrl,
                                           expectedState,
                                           expectedNonce,
                                           mobileConnectCallback,
                                           mobileConnectRequestOptions);
    }

    /**
     * Handles continuation of the process following a completed redirect. Only the redirectedUrl is
     * required, however if the redirect being handled is the result of calling the Authorization
     * URL then the remaining parameters are required.
     *
     * @param redirectedUrl               Url redirected to by the completion of the previous step
     * @param expectedState               The state value returned from the StartAuthorization call should be
     *                                    passed here, it will be used to validate the authenticity of the
     *                                    authorization process
     * @param expectedNonce               The nonce value returned from the StartAuthorization call should be
     *                                    passed here, it will be used to ensure the token was not requested
     *                                    using a replay attack
     * @param mobileConnectRequestOptions Optional parameters
     *                                    Connect process
     */
    @SuppressWarnings("unused")
    @Override
    public void handleUrlRedirect(final URI redirectedUrl,
                                  final String expectedState,
                                  final String expectedNonce,
                                  @NonNull final IMobileConnectCallback mobileConnectCallback,
                                  final MobileConnectRequestOptions mobileConnectRequestOptions)
    {
        this.presenter.performHandleUrlRedirect(redirectedUrl,
                                                expectedState,
                                                expectedNonce,
                                                mobileConnectCallback,
                                                mobileConnectRequestOptions);
    }

    /**
     * Request user info using the access token returned by <see cref="requestToken(DiscoveryResponse,
     * URI, String, String)"/>
     *
     * @param accessToken Access token from requestToken stage
     */
    @SuppressWarnings("unused")
    @Override
    public void requestIdentity(final String accessToken, @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        this.presenter.performRequestIdentity(accessToken, mobileConnectCallback);
    }

    /**
     * Refresh token using using the refresh token provided in the RequestToken response
     *
     * @param refreshToken Refresh token returned from RequestToken request
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    @SuppressWarnings("unused")
    @Override
    public void refreshToken(final String refreshToken, @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        this.presenter.performRefreshToken(refreshToken, mobileConnectCallback);
    }

    /**
     * Revoke token using using the access / refresh token provided in the RequestToken response
     *
     * @param token         Access/Refresh token returned from RequestToken request
     * @param tokenTypeHint Hint to indicate the type of token being passed in
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    @SuppressWarnings("unused")
    @Override
    public void revokeToken(final String token,
                            final String tokenTypeHint,
                            @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        this.presenter.performRevokeToken(token, tokenTypeHint, mobileConnectCallback);
    }

    /**
     * Request user info using the access token returned by <see cref="requestToken(DiscoveryResponse,
     * URI, String, String)"/>
     *
     * @param accessToken Access token from requestToken stage
     */
    @SuppressWarnings("unused")
    @Override
    public void requestUserInfo(final String accessToken, final IMobileConnectCallback mobileConnectCallback)
    {
        this.presenter.performRequestUserInfo(accessToken, mobileConnectCallback);
    }
}