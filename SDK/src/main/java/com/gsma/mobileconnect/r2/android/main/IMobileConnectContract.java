package com.gsma.mobileconnect.r2.android.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.interfaces.AuthenticationListener;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;

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

        void handleRedirectAfterAuthentication(String url,
                                               String state,
                                               String nonce,
                                               AuthenticationListener authenticationListener,
                                               MobileConnectRequestOptions mobileConnectRequestOptions);

        void performAsyncTask(@NonNull IMobileConnectOperation mobileConnectOperation,
                              @NonNull IMobileConnectCallback mobileConnectCallback);

        @SuppressWarnings("unused")
        void attemptDiscovery(String msisdn,
                              String mcc,
                              String mnc,
                              MobileConnectRequestOptions options,
                              @NonNull IMobileConnectCallback mobileConnectCallback);

        @SuppressWarnings("unused")
        void attemptDiscoveryAfterOperatorSelection(@NonNull IMobileConnectCallback mobileConnectCallback,
                                                    @Nullable URI redirectUri);

        @SuppressWarnings("unused")
        void startAuthentication(String encryptedMSISDN,
                                 String state,
                                 String nonce,
                                 MobileConnectRequestOptions options,
                                 @NonNull IMobileConnectCallback mobileConnectCallback) throws IllegalArgumentException;

        @SuppressWarnings("unused")
        void requestToken(URI redirectedUrl,
                          String expectedState,
                          String expectedNonce,
                          MobileConnectRequestOptions mobileConnectRequestOptions,
                          @NonNull IMobileConnectCallback mobileConnectCallback);

        @SuppressWarnings("unused")
        void handleUrlRedirect(URI redirectedUrl,
                               String expectedState,
                               String expectedNonce,
                               MobileConnectRequestOptions mobileConnectRequestOptions,
                               @NonNull IMobileConnectCallback mobileConnectCallback);

        @SuppressWarnings("unused")
        void requestIdentity(String accessToken, @NonNull IMobileConnectCallback mobileConnectCallback);

        @SuppressWarnings("unused")
        void refreshToken(String refreshToken, @NonNull IMobileConnectCallback mobileConnectCallback);

        @SuppressWarnings("unused")
        void revokeToken(String token, String tokenTypeHint, @NonNull IMobileConnectCallback mobileConnectCallback);

        @SuppressWarnings("unused")
        void requestUserInfo(String accessToken, IMobileConnectCallback mobileConnectCallback);
    }

    interface IUserActionsListener
    {
        void setView(IView view);

        DiscoveryResponse getDiscoveryResponse();

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
                                   @NonNull final IMobileConnectCallback mobileConnectCallback);

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
        void onComplete(final MobileConnectStatus mobileConnectStatus);
    }

    /**
     * The method which shall be ran within an {@link android.os.AsyncTask}
     */
    interface IMobileConnectOperation
    {
        MobileConnectStatus operation();
    }
}
