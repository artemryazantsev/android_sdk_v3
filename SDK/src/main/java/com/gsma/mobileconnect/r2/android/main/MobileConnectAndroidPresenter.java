package com.gsma.mobileconnect.r2.android.main;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.MobileConnectWebInterface;
import com.gsma.mobileconnect.r2.android.bus.BusManager;
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.OperatorUrls;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.squareup.otto.Subscribe;

import java.net.URI;

import static com.gsma.mobileconnect.r2.android.main.IMobileConnectContract.IMobileConnectCallback;
import static com.gsma.mobileconnect.r2.android.main.IMobileConnectContract.IMobileConnectCallbackManually;
import static com.gsma.mobileconnect.r2.android.main.IMobileConnectContract.IMobileConnectOperation;
import static com.gsma.mobileconnect.r2.android.main.IMobileConnectContract.IMobileConnectManually;

/**
 * This class interfaces with the underlying Java SDK. It wraps calls to the Java SDK in
 * {@link AsyncTask}s and sends the result via a {@link IMobileConnectContract.IMobileConnectCallback}
 * <p/>
 *
 * @since 2.0
 */
public class MobileConnectAndroidPresenter implements IMobileConnectContract.IUserActionsListener
{

    private MobileConnectWebInterface mobileConnectWebInterface;


    private IMobileConnectContract.IView view;
    private MobileConnectInterface mobileConnectInterface;
    private DiscoveryResponse discoveryResponse;

    private static final String MOBILE_PROMPT_VALUE = "mobile";

    private static final String TAG = MobileConnectAndroidPresenter.class.getSimpleName();


    /*
     * @param mobileConnectInterface The {@link MobileConnectConfig} containing the necessary set-up.
   */
    public MobileConnectAndroidPresenter(@NonNull final MobileConnectInterface mobileConnectInterface)
    {
        this.mobileConnectInterface = mobileConnectInterface;

    }

    public MobileConnectAndroidPresenter(@NonNull final MobileConnectWebInterface mobileConnectWebInterface)
    {
        this.mobileConnectWebInterface = mobileConnectWebInterface;
    }

    public MobileConnectAndroidPresenter(@NonNull final MobileConnectWebInterface mobileConnectWebInterface,
                                         @NonNull final MobileConnectInterface mobileConnectInterface)
    {
        this.mobileConnectWebInterface = mobileConnectWebInterface;
        this.mobileConnectInterface = mobileConnectInterface;
    }





    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(final DiscoveryResponse discoveryResponse)
    {
        this.discoveryResponse = discoveryResponse;
    }

    @Override
    public void setView(IMobileConnectContract.IView view)
    {
        this.view = view;
    }

    /**
     * Returns a cached DiscoveryResponse.
     *
     * @return The cached DiscoveryResponse
     */
    @Override
    public DiscoveryResponse getDiscoveryResponse()
    {
        return this.discoveryResponse;
    }

    /**
     * Set new discovery response for a cached DiscoveryResponse
     * @param newDiscovery - new Discovery Response
     */
    @Override
    public void setDiscoveryResponse(DiscoveryResponse newDiscovery){
        this.discoveryResponse = newDiscovery;
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
    public void performDiscovery(final String msisdn,
                                 final String mcc,
                                 final String mnc,
                                 final MobileConnectRequestOptions options,
                                 @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        IMobileConnectOperation mobileConnectOperation = new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidPresenter.this.mobileConnectInterface.attemptDiscovery(msisdn,
                                                                                                  mcc,
                                                                                                  mnc,
                                                                                                  options);
            }
        };

        this.view.performAsyncTask(mobileConnectOperation, mobileConnectCallback);
    }

    /**
     * Manually attempt discovery
     * @param secretKey - secret key
     * @param clientKey - client key
     * @param subscriberId - subsriber id
     * @param name - client name
     * @param operatorUrls - operator URLS
     * @param mobileConnectCallbackManually The callback in which a {@link DiscoveryResponse} shall be provided after
     *                              completion
     */
    @Override
    public void manualDiscovery(final String secretKey,
                                final String clientKey,
                                final String subscriberId,
                                final String name,
                                final OperatorUrls operatorUrls,
                                @NonNull final IMobileConnectCallbackManually mobileConnectCallbackManually)
    {
        IMobileConnectManually manualOperation = new IMobileConnectManually()

        {
            @Override
            public DiscoveryResponse manually() {
                try{
                    discoveryResponse = MobileConnectAndroidPresenter.this.mobileConnectWebInterface.generateDiscoveryManually(
                            secretKey, clientKey, subscriberId, name, operatorUrls);
                }catch (JsonDeserializationException e){
                    e.printStackTrace();
                }
                return discoveryResponse;
            }
        };
        this.view.performAsyncTask(manualOperation, mobileConnectCallbackManually);
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
    @SuppressWarnings("unused")
    public void performDiscoveryAfterOperatorSelection(@NonNull final IMobileConnectCallback mobileConnectCallback,
                                                       @Nullable final URI redirectUri)
    {
        IMobileConnectOperation mobileConnectOperation = new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidPresenter.this.mobileConnectInterface.attemptDiscoveryAfterOperatorSelection(
                        redirectUri);
            }
        };

        this.view.performAsyncTask(mobileConnectOperation, mobileConnectCallback);
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
    public void performAuthentication(final String encryptedMSISDN,
                                      final String state,
                                      final String nonce,
                                      final MobileConnectRequestOptions options,
                                      @NonNull final IMobileConnectCallback mobileConnectCallback) throws
                                                                                                   IllegalArgumentException
    {
        if (options != null)
        {
            final AuthenticationOptions authenticationOptions = options.getAuthenticationOptions();

            if (authenticationOptions != null)
            {
                final String prompt = authenticationOptions.getPrompt();

                if (prompt != null && prompt.equalsIgnoreCase(MOBILE_PROMPT_VALUE))
                {
                    throw new IllegalArgumentException(
                            "The value 'mobile' for prompt is not allowed when performing Authentication");
                }
            }
        }

        IMobileConnectOperation mobileConnectOperation = new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidPresenter.this.mobileConnectInterface.startAuthentication(discoveryResponse,
                                                                                                     encryptedMSISDN,
                                                                                                     state,
                                                                                                     nonce,
                                                                                                     options);
            }
        };


        this.view.performAsyncTask(mobileConnectOperation, mobileConnectCallback);
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
    public void performRequestToken(final URI redirectedUrl,
                                    final String expectedState,
                                    final String expectedNonce,
                                    final MobileConnectRequestOptions mobileConnectRequestOptions,
                                    @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        IMobileConnectOperation mobileConnectOperation = new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidPresenter.this.mobileConnectInterface.requestToken(discoveryResponse,
                                                                                              redirectedUrl,
                                                                                              expectedState,
                                                                                              expectedNonce,
                                                                                              mobileConnectRequestOptions);
            }
        };

        this.view.performAsyncTask(mobileConnectOperation, mobileConnectCallback);
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
    @Override
    public void performHandleUrlRedirect(final URI redirectedUrl,
                                         final String expectedState,
                                         final String expectedNonce,
                                         final MobileConnectRequestOptions mobileConnectRequestOptions,
                                         @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        IMobileConnectOperation mobileConnectOperation = new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidPresenter.this.mobileConnectInterface.handleUrlRedirect(redirectedUrl,
                                                                                                   discoveryResponse,
                                                                                                   expectedState,
                                                                                                   expectedNonce,
                                                                                                   mobileConnectRequestOptions);
            }
        };

        this.view.performAsyncTask(mobileConnectOperation, mobileConnectCallback);
    }

    /**
     * Request user info using the access token returned by <see cref="requestToken(DiscoveryResponse,
     * URI, String, String)"/>
     *
     * @param accessToken Access token from requestToken stage
     */
    @SuppressWarnings("unused")
    public void performRequestIdentity(final String accessToken,
                                       @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        IMobileConnectOperation mobileConnectOperation = new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidPresenter.this.mobileConnectInterface.requestIdentity(discoveryResponse,
                                                                                                 accessToken);
            }
        };

        this.view.performAsyncTask(mobileConnectOperation, mobileConnectCallback);
    }

    /**
     * Refresh token using using the refresh token provided in the RequestToken response
     *
     * @param refreshToken Refresh token returned from RequestToken request
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    @SuppressWarnings("unused")
    public void performRefreshToken(final String refreshToken,
                                    @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        IMobileConnectOperation mobileConnectOperation = new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidPresenter.this.mobileConnectInterface.refreshToken(refreshToken,
                                                                                              discoveryResponse);
            }
        };

        this.view.performAsyncTask(mobileConnectOperation, mobileConnectCallback);
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
    public void performRevokeToken(final String token,
                                   final String tokenTypeHint,
                                   @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        IMobileConnectOperation mobileConnectOperation = new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidPresenter.this.mobileConnectInterface.revokeToken(token,
                                                                                             tokenTypeHint,
                                                                                             discoveryResponse);
            }
        };

        this.view.performAsyncTask(mobileConnectOperation, mobileConnectCallback);
    }

    /**
     * Request user info using the access token returned by <see cref="requestToken(DiscoveryResponse,
     * URI, String, String)"/>
     *
     * @param accessToken Access token from requestToken stage
     */
    public void performRequestUserInfo(final String accessToken, final IMobileConnectCallback mobileConnectCallback)
    {
        IMobileConnectOperation mobileConnectOperation = new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidPresenter.this.mobileConnectInterface.requestUserInfo(discoveryResponse,
                                                                                                 accessToken);
            }
        };

        this.view.performAsyncTask(mobileConnectOperation, mobileConnectCallback);
    }

    @Override
    public void initialise()
    {
        try
        {
            BusManager.register(this);
        }
        catch (final IllegalArgumentException exception)
        {
            Log.e(TAG, "Failed to register OTTO", exception);
        }
    }

    @Override
    public void cleanUp()
    {
        try
        {
            BusManager.unregister(this);
        }
        // Despite having two different instances of this class, the 'this' parameter is the same and it causes it to
        // crash. This has apparently been reported on Otto's Github page as a bug and doesn't seem to have been
        // resolved
        catch (final IllegalArgumentException exception)
        {
            Log.e(TAG, "Failed to unregister OTTO", exception);
        }
    }
}