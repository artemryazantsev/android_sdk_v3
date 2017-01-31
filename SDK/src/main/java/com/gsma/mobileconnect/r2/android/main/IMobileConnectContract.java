package com.gsma.mobileconnect.r2.android.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.interfaces.AuthenticationListener;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.OperatorUrls;

import java.net.URI;

/**
 * A contract to make the {@link MobileConnectAndroidPresenter} conform to {@link MobileConnectAndroidView}
 *
 * @since 2.0
 */
public interface IMobileConnectContract
{
    interface IView
    {
        /**
         * It is mandatory to call this before any operations are called.
         */
        @SuppressWarnings("unused")
        void initialise();

        /**
         * It is mandatory to call this when your view is being destroyed.
         */
        @SuppressWarnings("unused")
        void cleanUp();

        void handleRedirectAfterAuthentication(final String url,
                                               final String state,
                                               final String nonce,
                                               final AuthenticationListener authenticationListener,
                                               final MobileConnectRequestOptions mobileConnectRequestOptions);

        void performAsyncTask(@NonNull final IMobileConnectOperation mobileConnectOperation,
                              @NonNull final IMobileConnectCallback mobileConnectCallback);

        void performAsyncTask(@NonNull final IMobileConnectManually mobileConnectManually,
                              @NonNull final IMobileConnectCallbackManually mobileConnectCallbackManually);


        @SuppressWarnings("unused")
        void attemptDiscovery(final String msisdn,
                              final String mcc,
                              final String mnc,
                              final MobileConnectRequestOptions options,
                              @NonNull final IMobileConnectCallback mobileConnectCallback);

        void generateDiscoveryManually(final String secretKey,
                                       final String clientKey,
                                       final String subscriberId,
                                       final String name,
                                       final OperatorUrls operatorUrls,
                                       @NonNull final IMobileConnectCallbackManually mobileConnectCallbackManually);

        @SuppressWarnings("unused")
        void attemptDiscoveryAfterOperatorSelection(@NonNull final IMobileConnectCallback mobileConnectCallback,
                                                    @Nullable final URI redirectUri);

        @SuppressWarnings("unused")
        void startAuthentication(final String encryptedMSISDN,
                                 final String state,
                                 final String nonce,
                                 final MobileConnectRequestOptions options,
                                 @NonNull final IMobileConnectCallback mobileConnectCallback);

        @SuppressWarnings("unused")
        void requestToken(final URI redirectedUrl,
                          final String expectedState,
                          final String expectedNonce,
                          final MobileConnectRequestOptions mobileConnectRequestOptions,
                          @NonNull final IMobileConnectCallback mobileConnectCallback);

        @SuppressWarnings("unused")
        void handleUrlRedirect(final URI redirectedUrl,
                               final String expectedState,
                               final String expectedNonce,
                               final MobileConnectRequestOptions mobileConnectRequestOptions,
                               @NonNull final IMobileConnectCallback mobileConnectCallback);

        @SuppressWarnings("unused")
        void requestIdentity(final String accessToken, @NonNull final IMobileConnectCallback mobileConnectCallback);

        @SuppressWarnings("unused")
        void refreshToken(final String refreshToken, @NonNull final IMobileConnectCallback mobileConnectCallback);

        @SuppressWarnings("unused")
        void revokeToken(final String token, String tokenTypeHint, @NonNull IMobileConnectCallback mobileConnectCallback);

        @SuppressWarnings("unused")
        void requestUserInfo(String accessToken, IMobileConnectCallback mobileConnectCallback);
    }

    interface IUserActionsListener
    {
        void setView(IView view);

        DiscoveryResponse getDiscoveryResponse();
        void setDiscoveryResponse(final DiscoveryResponse discoveryResponse);

        void manualDiscovery(@Nullable final String secretKey,
                             @Nullable final String clientKey,
                             @Nullable final String subsriberId,
                             @Nullable final String name,
                             @Nullable final OperatorUrls operatorUrls,
                             @NonNull final IMobileConnectCallbackManually mobileConnectCallbackManually
                             );

        void performDiscovery(@Nullable final String msisdn,
                              @Nullable final String mcc,
                              @Nullable final String mnc,
                              @Nullable final MobileConnectRequestOptions options,
                              @NonNull final IMobileConnectCallback mobileConnectCallback);

        void performDiscoveryAfterOperatorSelection(@NonNull final IMobileConnectCallback mobileConnectCallback,
                                                    @Nullable final URI redirectUri);

        void performAuthentication(final String encryptedMSISDN,
                                   final String state,
                                   final String nonce,
                                   final MobileConnectRequestOptions options,
                                   @NonNull final IMobileConnectCallback mobileConnectCallback) throws
                                                                                                IllegalArgumentException;

        void performRequestToken(final URI redirectedUrl,
                                 final String expectedState,
                                 final String expectedNonce,
                                 final MobileConnectRequestOptions mobileConnectRequestOptions,
                                 @NonNull final IMobileConnectCallback mobileConnectCallback);

        void performHandleUrlRedirect(final URI redirectedUrl,
                                      final String expectedState,
                                      final String expectedNonce,
                                      final MobileConnectRequestOptions mobileConnectRequestOptions,
                                      @NonNull final IMobileConnectCallback mobileConnectCallback);

        void performRequestIdentity(final String accessToken,
                                    @NonNull final IMobileConnectCallback mobileConnectCallback);

        void performRefreshToken(final String refreshToken,
                                 @NonNull final IMobileConnectCallback mobileConnectCallback);

        void performRevokeToken(final String token,
                                final String tokenTypeHint,
                                @NonNull final IMobileConnectCallback mobileConnectCallback);

        void performRequestUserInfo(final String accessToken, final IMobileConnectCallback mobileConnectCallback);

        void initialise();

        void cleanUp();
    }

    /**
     * On completion of an API to {@link com.gsma.mobileconnect.r2.android.main.MobileConnectAndroidView}
     */
    interface IMobileConnectCallback
    {
        void  onComplete(final MobileConnectStatus mobileConnectStatus);
    }

    /**
     * On completion of an API to {@link com.gsma.mobileconnect.r2.android.main.MobileConnectAndroidView}
     */
    interface IMobileConnectCallbackManually
    {
        void onComplete(final DiscoveryResponse discoveryResponse);
    }

    /**
     * The method which shall be ran within an {@link android.os.AsyncTask}
     */
    interface IMobileConnectOperation
    {
        MobileConnectStatus operation();
    }

    /**
     * The method which shall be ran within an {@link android.os.AsyncTask}
     */
    interface IMobileConnectManually
    {
        DiscoveryResponse manually();
    }
}
