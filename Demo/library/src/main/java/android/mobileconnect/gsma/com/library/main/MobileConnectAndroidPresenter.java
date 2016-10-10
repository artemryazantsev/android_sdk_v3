package android.mobileconnect.gsma.com.library.main;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;

import java.net.URI;

import static android.mobileconnect.gsma.com.library.main.MobileConnectContract.IMobileConnectCallback;
import static android.mobileconnect.gsma.com.library.main.MobileConnectContract.IMobileConnectOperation;

/**
 * This class interfaces with the underlying Java SDK. It wraps calls to the Java SDK in
 * {@link AsyncTask}s and sends the result via a {@link MobileConnectContract.IMobileConnectCallback}
 * <p/>
 * Created by usmaan.dad on 11/08/2016.
 */
public class MobileConnectAndroidPresenter implements MobileConnectContract.UserActionsListener {
    private final MobileConnectInterface mobileConnectInterface;

    private MobileConnectContract.View view;

    private String mcc;
    private String mnc;

    private DiscoveryResponse discoveryResponse;

    /**
     * @param mobileConnectInterface The {@link MobileConnectConfig} containing the necessary set-up.
     */
    public MobileConnectAndroidPresenter(@NonNull final MobileConnectInterface mobileConnectInterface) {
        this.mobileConnectInterface = mobileConnectInterface;
    }

    @Override
    public void setView(MobileConnectContract.View view) {
        this.view = view;
    }

    /**
     * Returns a cached DiscoveryResponse.
     *
     * @return The cached DiscoveryResponse
     */
    @Override
    public DiscoveryResponse getDiscoveryResponse() {
        return this.discoveryResponse;
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
                                 @NonNull final MobileConnectContract.IMobileConnectCallback mobileConnectCallback)
    {
        IMobileConnectOperation mobileConnectOperation = new IMobileConnectOperation() {
                @Override
                public MobileConnectStatus operation() {
                    return MobileConnectAndroidPresenter.this.mobileConnectInterface.attemptDiscovery(msisdn,
                            mcc,
                            mnc,
                            options);
                }
        };
        this.view.performAsyncTask(mobileConnectOperation, mobileConnectCallback);
        //this.discoveryResponse = mobileConnectCallback.getDiscoveryResponse();
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
    public void attemptDiscoveryAfterOperatorSelection(@NonNull final IMobileConnectCallback mobileConnectCallback,
                                                       @Nullable final URI redirectUri) {
        new MobileConnectAsyncTask(new IMobileConnectOperation() {
            @Override
            public MobileConnectStatus operation() {
                return MobileConnectAndroidPresenter.this.mobileConnectInterface.attemptDiscoveryAfterOperatorSelection(
                        redirectUri);
            }
        }, mobileConnectCallback).execute();
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
    public void startAuthentication(final String encryptedMSISDN,
                                    final String state,
                                    final String nonce,
                                    final MobileConnectRequestOptions options,
                                    @NonNull final IMobileConnectCallback mobileConnectCallback) {
        new MobileConnectAsyncTask(new IMobileConnectOperation() {
            @Override
            public MobileConnectStatus operation() {
                return MobileConnectAndroidPresenter.this.mobileConnectInterface.startAuthentication(discoveryResponse,
                        encryptedMSISDN,
                        state,
                        nonce,
                        options);
            }
        }, mobileConnectCallback).execute();
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
    public void requestToken(final URI redirectedUrl,
                             final String expectedState,
                             final String expectedNonce,
                             @NonNull final IMobileConnectCallback mobileConnectCallback,
                             final MobileConnectRequestOptions mobileConnectRequestOptions) {
        new MobileConnectAsyncTask(new IMobileConnectOperation() {
            @Override
            public MobileConnectStatus operation() {
                return MobileConnectAndroidPresenter.this.mobileConnectInterface.requestToken(discoveryResponse,
                        redirectedUrl,
                        expectedState,
                        expectedNonce,
                        mobileConnectRequestOptions);
                // same here
            }
        }, mobileConnectCallback).execute();
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
    public void handleUrlRedirect(final URI redirectedUrl,
                                  final String expectedState,
                                  final String expectedNonce,
                                  @NonNull final IMobileConnectCallback mobileConnectCallback,
                                  final MobileConnectRequestOptions mobileConnectRequestOptions) {
        new MobileConnectAsyncTask(new IMobileConnectOperation() {
            @Override
            public MobileConnectStatus operation() {
                return MobileConnectAndroidPresenter.this.mobileConnectInterface.handleUrlRedirect(redirectedUrl,
                        discoveryResponse,
                        expectedState,
                        expectedNonce,
                        mobileConnectRequestOptions);
            }
        }, mobileConnectCallback).execute();
    }

    /**
     * Request user info using the access token returned by <see cref="requestToken(DiscoveryResponse,
     * URI, String, String)"/>
     *
     * @param accessToken Access token from requestToken stage
     */
    @SuppressWarnings("unused")
    public void requestIdentity(final String accessToken, @NonNull final IMobileConnectCallback mobileConnectCallback) {
        new MobileConnectAsyncTask(new IMobileConnectOperation() {
            @Override
            public MobileConnectStatus operation() {
                return MobileConnectAndroidPresenter.this.mobileConnectInterface.requestIdentity(discoveryResponse,
                        accessToken);
            }
        }, mobileConnectCallback).execute();
    }

    /**
     * Refresh token using using the refresh token provided in the RequestToken response
     *
     * @param refreshToken Refresh token returned from RequestToken request
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    @SuppressWarnings("unused")
    public void refreshToken(final String refreshToken, @NonNull final IMobileConnectCallback mobileConnectCallback) {
        new MobileConnectAsyncTask(new IMobileConnectOperation() {
            @Override
            public MobileConnectStatus operation() {
                return MobileConnectAndroidPresenter.this.mobileConnectInterface.refreshToken(refreshToken,
                        discoveryResponse);
            }
        }, mobileConnectCallback).execute();
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
    public void revokeToken(final String token,
                            final String tokenTypeHint,
                            @NonNull final IMobileConnectCallback mobileConnectCallback) {
        new MobileConnectAsyncTask(new IMobileConnectOperation() {
            @Override
            public MobileConnectStatus operation() {
                return MobileConnectAndroidPresenter.this.mobileConnectInterface.revokeToken(token,
                        tokenTypeHint,
                        discoveryResponse);
            }
        }, mobileConnectCallback).execute();
    }

    /**
     * Request user info using the access token returned by <see cref="requestToken(DiscoveryResponse,
     * URI, String, String)"/>
     *
     * @param accessToken Access token from requestToken stage
     */
    public void requestUserInfo(final String accessToken, final IMobileConnectCallback mobileConnectCallback) {
        new MobileConnectAsyncTask(new IMobileConnectOperation() {
            @Override
            public MobileConnectStatus operation() {
                return MobileConnectAndroidPresenter.this.mobileConnectInterface.requestUserInfo(discoveryResponse,
                        accessToken);
            }
        }, mobileConnectCallback).execute();
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(final String mcc) {
        this.mcc = mcc;
    }

    public void setMnc(final String mnc) {
        this.mnc = mnc;
    }

    public String getMnc() {
        return mnc;
    }

}