package com.gsma.mobileconnect.r2.android.main;

import android.content.Context;
import android.content.DialogInterface;
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
import com.gsma.mobileconnect.r2.MobileConnectWebInterface;
import com.gsma.mobileconnect.r2.android.interfaces.AuthenticationListener;
import com.gsma.mobileconnect.r2.android.interfaces.DiscoveryListener;
import com.gsma.mobileconnect.r2.android.interfaces.WebViewCallBack;
import com.gsma.mobileconnect.r2.android.sdk.R;
import com.gsma.mobileconnect.r2.android.view.DiscoveryAuthenticationDialog;
import com.gsma.mobileconnect.r2.android.view.InteractiveWebView;
import com.gsma.mobileconnect.r2.android.webviewclient.AuthenticationWebViewClient;
import com.gsma.mobileconnect.r2.android.webviewclient.DiscoveryWebViewClient;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.OperatorUrls;
import com.gsma.mobileconnect.r2.utils.LogUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static com.gsma.mobileconnect.r2.android.main.IMobileConnectContract.IMobileConnectCallback;
import static com.gsma.mobileconnect.r2.android.main.IMobileConnectContract.IMobileConnectCallbackManually;
import static com.gsma.mobileconnect.r2.android.main.IMobileConnectContract.IMobileConnectOperation;
import static com.gsma.mobileconnect.r2.android.main.IMobileConnectContract.IMobileConnectManually;

/**
 * This class interfaces with the underlying Java SDK. It wraps calls to the Java SDK in
 * {@link AsyncTask}s and sends the result via a {@link IMobileConnectCallback}
 *
 * @since 2.0
 */
public class MobileConnectAndroidView implements IMobileConnectContract.IView
{
    private static final String TAG = MobileConnectAndroidView.class.getSimpleName();

    private IMobileConnectContract.IUserActionsListener presenter;

    private static boolean isRegistered;

    /**
     * @param mobileConnectInterface The {@link MobileConnectConfig} containing the necessary set-up.
     */
    public MobileConnectAndroidView(@NonNull final MobileConnectInterface mobileConnectInterface)
    {
        this.presenter = new MobileConnectAndroidPresenter(mobileConnectInterface);
        this.presenter.setView(this);
    }

    public MobileConnectAndroidView(@NonNull final MobileConnectWebInterface mobileConnectWebInterface)
    {
        this.presenter = new MobileConnectAndroidPresenter(mobileConnectWebInterface);
        this.presenter.setView(this);
    }

    public MobileConnectAndroidView(@NonNull final MobileConnectWebInterface mobileConnectWebInterface,
                                    @NonNull final MobileConnectInterface mobileConnectInterface )
    {
        this.presenter = new MobileConnectAndroidPresenter(mobileConnectWebInterface, mobileConnectInterface);
        this.presenter.setView(this);
    }

    public IMobileConnectContract.IUserActionsListener getPresenter()
    {
        return presenter;
    }

    public void setPresenter(@NonNull final IMobileConnectContract.IUserActionsListener presenter)
    {
        this.presenter = presenter;
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
        Log.i(TAG,
              String.format("Attempting Discovery With WebView with url=%s redirectUrl=%s", operatorUrl, redirectUrl));
        initiateWebView(activityContext, discoveryListener, operatorUrl, redirectUrl, mobileConnectRequestOptions);
    }

    public void attemptAuthenticationWithWebView(@NonNull final Context activityContext,
                                                 @NonNull final AuthenticationListener authenticationListener,
                                                 @NonNull final String url,
                                                 @NonNull final String state,
                                                 @NonNull final String nonce,
                                                 @Nullable final MobileConnectRequestOptions
                                                         mobileConnectRequestOptions)
    {
        Log.i(TAG, String.format("Attempting Authentication With WebView url=%s", url));
        initiateWebView(activityContext, authenticationListener, url, state, nonce, mobileConnectRequestOptions);
    }

    public DiscoveryResponse getDiscoveryResponse()
    {
        return this.presenter.getDiscoveryResponse();
    }

    private void initiateWebView(@NonNull final Context activityContext,
                                 @NonNull final DiscoveryListener discoveryListener,
                                 @NonNull final String operatorUrl,
                                 @NonNull final String redirectUrl,
                                 final MobileConnectRequestOptions mobileConnectRequestOptions)
    {
        final RelativeLayout webViewLayout = (RelativeLayout) LayoutInflater.from(activityContext)
                                                                            .inflate(R.layout.layout_web_view, null);

        final InteractiveWebView webView = (InteractiveWebView) webViewLayout.findViewById(R.id.web_view);
        final ProgressBar progressBar = (ProgressBar) webViewLayout.findViewById(R.id.progressBar);

        final DiscoveryAuthenticationDialog dialog = new DiscoveryAuthenticationDialog(activityContext);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(final DialogInterface dialogInterface)
            {
                Log.i(TAG, "Closing Dialog");
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
                Log.i(TAG, "Discovery Failed");
                discoveryListener.discoveryFailed(mobileConnectStatus);
                dialog.cancel();
            }

            @Override
            public void onSuccess(final String url)
            {
                URI uri = getUri(url);

                MobileConnectAndroidView.this.presenter.performHandleUrlRedirect(uri,
                                                                                 UUID.randomUUID().toString(),
                                                                                 UUID.randomUUID().toString(),
                                                                                 mobileConnectRequestOptions,
                                                                                 new IMobileConnectCallback()
                                                                                 {
                                                                                     @Override
                                                                                     public void onComplete(
                                                                                             MobileConnectStatus
                                                                                                     mobileConnectStatus)
                                                                                     {
                                                                                         Log.i(TAG,
                                                                                               "Discovery Complete");
                                                                                         discoveryListener
                                                                                                 .onDiscoveryResponse(
                                                                                                 mobileConnectStatus);
                                                                                         dialog.cancel();
                                                                                     }
                                                                                 });
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
            Log.e(TAG, "Failed to show dialog", exception);
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
        catch (final URISyntaxException exception)
        {
            uri = null;
            Log.e(TAG, String.format("Failed to parse url=%s", url), exception);
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
        final RelativeLayout webViewLayout = (RelativeLayout) LayoutInflater.from(activityContext)
                                                                            .inflate(R.layout.layout_web_view, null);

        final InteractiveWebView webView = (InteractiveWebView) webViewLayout.findViewById(R.id.web_view);
        final ProgressBar progressBar = (ProgressBar) webViewLayout.findViewById(R.id.progressBar);

        final DiscoveryAuthenticationDialog dialog = new DiscoveryAuthenticationDialog(activityContext);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(final DialogInterface dialogInterface)
            {
                Log.i(TAG, "Authentication Dialog Closing");
                closeWebViewAndNotify(authenticationListener, webView);
            }
        });

        dialog.setContentView(webViewLayout);

        webView.setWebChromeClient(new WebChromeClient());

        Uri uri = Uri.parse(authenticationUrl);
        String redirectUrl = uri.getQueryParameter("redirect_uri");

        final WebViewCallBack webViewCallBack = new WebViewCallBack()
        {
            @Override
            public void onError(final MobileConnectStatus mobileConnectStatus)
            {
                Log.i(TAG, "Authentication Failure");
                authenticationListener.authenticationFailed(mobileConnectStatus);
            }

            @Override
            public void onSuccess(final String url)
            {
                Log.i(TAG, "Authentication Successful");
                handleRedirectAfterAuthentication(url, state, nonce, authenticationListener, mobileRequestOptions);
            }
        };

        final AuthenticationWebViewClient webViewClient = new AuthenticationWebViewClient(dialog,
                                                                                          progressBar,
                                                                                          authenticationListener,
                                                                                          redirectUrl,
                                                                                          webViewCallBack);

        webView.setWebViewClient(webViewClient);

        webView.loadUrl(authenticationUrl);

        try
        {
            dialog.show();
        }
        catch (final WindowManager.BadTokenException exception)
        {
            Log.i(TAG, String.format("Failed to show Dialog", exception));
        }
    }

    public static void setIsRegistered(boolean isRegistered) {
        MobileConnectAndroidView.isRegistered = isRegistered;
    }

    public boolean isRegistered(){
        return isRegistered;
    }

    @Override
    public void handleRedirectAfterAuthentication(final String url,
                                                  final String state,
                                                  final String nonce,
                                                  final AuthenticationListener authenticationListener,
                                                  final MobileConnectRequestOptions mobileConnectRequestOptions)
    {
        final URI uri;

        try
        {
            uri = new URI(url);
        }
        catch (final URISyntaxException exception)
        {
            Log.e(TAG, String.format("Failed to parse URI=%s", url), exception);
            final MobileConnectStatus mobileConnectStatus = MobileConnectStatus.error(exception.getMessage(),
                                                                                      "Redirect failed",
                                                                                      exception);
            authenticationListener.authenticationFailed(mobileConnectStatus);
            return;
        }

        handleUrlRedirect(uri, state, nonce, mobileConnectRequestOptions, new IMobileConnectCallback()
        {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus)
            {
                if (mobileConnectStatus.getErrorCode() == null)
                {
                    Log.i(TAG, "Authentication Successful");
                    authenticationListener.authenticationSuccess(mobileConnectStatus);
                }
                else
                {
                    Log.i(TAG, "Authentication Failure");
                    authenticationListener.authenticationFailed(mobileConnectStatus);
                }
            }
        });
    }

    /**
     * Stop any processing on the {@link WebView} and notify that it has been stopped. This is called regardless of
     * success, failure or if the user has intentionally closed the dialog.
     *
     * @param listener The callback to notify of the closing of the Dialog
     * @param webView  The view to stop loading.
     */
    private void closeWebViewAndNotify(final DiscoveryListener listener, final InteractiveWebView webView)
    {
        Log.i(TAG, "Closing WebView & Notifying");
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
        Log.i(TAG, "Closing WebView & Notifying");
        webView.stopLoading();
        webView.loadData("", "text/html", null);
        listener.onAuthenticationDialogClose();
    }

    /**
     * It is mandatory to call this before any operations are called.
     */
    @Override
    public void initialise()
    {
        this.presenter.initialise();
    }

    /**
     * It is mandatory to call this when your view is being destroyed.
     */
    @Override
    public void cleanUp()
    {
        this.presenter.cleanUp();
    }

    @Override
    public void performAsyncTask(@NonNull final IMobileConnectOperation mobileConnectOperation,
                                 @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        Log.i(TAG, "Performing Async Task");
        new MobileConnectAsyncTask(mobileConnectOperation, mobileConnectCallback).execute();
    }

    @Override
    public void performAsyncTask(@NonNull final IMobileConnectManually mobileConnectManually,
                                 @NonNull final IMobileConnectCallbackManually mobileConnectCallbackManually)
    {
        Log.i(TAG, "Performing Async Task");
        new MobileConnectWebAsyncTask(mobileConnectManually, mobileConnectCallbackManually).execute();
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
        Log.i(TAG, String.format("Attempt Discovery for msisdn=%s, mcc=%s, mnc=%s", LogUtils.mask(msisdn), mcc, mnc));
        this.presenter.performDiscovery(msisdn, mcc, mnc, options, mobileConnectCallback);
    }

    /**
     * Asynchronously manually attempt discovery using the supplied parameters
     * @param secretKey - consumer secret
     * @param clientKey - consumer key
     * @param subscriberId - subsriber id
     * @param name - client name
     * @param operatorUrls Operator URLS
     * @param mobileConnectCallbackManually The callback in which a {@link MobileConnectStatus} shall be provided after
     *                              completion
     */
    @Override
    public void generateDiscoveryManually(final String secretKey,
                                          final String clientKey,
                                          final String subscriberId,
                                          final String name,
                                          final OperatorUrls operatorUrls,
                                          @NonNull final IMobileConnectCallbackManually mobileConnectCallbackManually)
    {
        Log.i(TAG, String.format("Manually generate discovery for secretKey=%s, clientKey=%s, subscriberId=%s, name=%s",
                secretKey, clientKey, subscriberId, name));
        this.presenter.manualDiscovery(secretKey, clientKey, subscriberId, name, operatorUrls, mobileConnectCallbackManually);
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
        Log.i(TAG, String.format("Attempt Discovery After Operator Selection for redirectUri=%s", redirectUri));
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
        Log.i(TAG,
              String.format("Start Authentication for encryptedMSISDN=%s, state=%s, nonce=%s",
                            LogUtils.mask(encryptedMSISDN),
                            state,
                            LogUtils.mask(nonce)));

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
     */
    @SuppressWarnings("unused")
    @Override
    public void requestToken(final URI redirectedUrl,
                             final String expectedState,
                             final String expectedNonce,
                             final MobileConnectRequestOptions mobileConnectRequestOptions,
                             @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        Log.i(TAG,
              String.format("Request Token for redirectUrl=%s, expectedState=%s, expectedNonce=%s",
                            redirectedUrl,
                            expectedState,
                            LogUtils.mask(expectedNonce)));

        this.presenter.performRequestToken(redirectedUrl,
                                           expectedState,
                                           expectedNonce,
                                           mobileConnectRequestOptions,
                                           mobileConnectCallback);
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
     */
    @SuppressWarnings("unused")
    @Override
    public void handleUrlRedirect(final URI redirectedUrl,
                                  final String expectedState,
                                  final String expectedNonce,
                                  final MobileConnectRequestOptions mobileConnectRequestOptions,
                                  @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        Log.i(TAG,
              String.format("Handle URL Redirect for redirectUrl=%s, expectedState=%s, expectedNonce=%s",
                            redirectedUrl,
                            expectedState,
                            LogUtils.mask(expectedNonce)));

        this.presenter.performHandleUrlRedirect(redirectedUrl,
                                                expectedState,
                                                expectedNonce,
                                                mobileConnectRequestOptions,
                                                mobileConnectCallback);
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
        Log.i(TAG, String.format("Request Identity for accessToken=%s", LogUtils.mask(accessToken)));
        this.presenter.performRequestIdentity(accessToken, mobileConnectCallback);
    }

    /**
     * Refresh token using using the refresh token provided in the RequestToken response
     *
     * @param refreshToken Refresh token returned from RequestToken request
     *                     connect process
     */
    @SuppressWarnings("unused")
    @Override
    public void refreshToken(final String refreshToken, @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        Log.i(TAG, String.format("Refresh Token for refreshToken=%s", LogUtils.mask(refreshToken)));
        this.presenter.performRefreshToken(refreshToken, mobileConnectCallback);
    }

    /**
     * Revoke token using using the access / refresh token provided in the RequestToken response
     *
     * @param token         Access/Refresh token returned from RequestToken request
     * @param tokenTypeHint Hint to indicate the type of token being passed in
     *                      connect process
     */
    @SuppressWarnings("unused")
    @Override
    public void revokeToken(final String token,
                            final String tokenTypeHint,
                            @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        Log.i(TAG, String.format("Revoke Token for token=%s, tokenTypeHint=%s", LogUtils.mask(token), tokenTypeHint));
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
    public void requestUserInfo(final String accessToken, @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        Log.i(TAG, String.format("Request User Info for accessToken=%s", LogUtils.mask(accessToken)));
        this.presenter.performRequestUserInfo(accessToken, mobileConnectCallback);
    }
}