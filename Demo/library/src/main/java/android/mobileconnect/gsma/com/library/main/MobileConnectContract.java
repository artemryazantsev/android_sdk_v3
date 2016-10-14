package android.mobileconnect.gsma.com.library.main;

import android.mobileconnect.gsma.com.library.callback.AuthenticationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;

import java.net.URI;

public interface MobileConnectContract
{
    interface View
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
                                 @NonNull IMobileConnectCallback mobileConnectCallback);

        @SuppressWarnings("unused")
        void requestToken(URI redirectedUrl,
                          String expectedState,
                          String expectedNonce,
                          @NonNull IMobileConnectCallback mobileConnectCallback,
                          MobileConnectRequestOptions mobileConnectRequestOptions);

        @SuppressWarnings("unused")
        void handleUrlRedirect(URI redirectedUrl,
                               String expectedState,
                               String expectedNonce,
                               @NonNull IMobileConnectCallback mobileConnectCallback,
                               MobileConnectRequestOptions mobileConnectRequestOptions);

        @SuppressWarnings("unused")
        void requestIdentity(String accessToken, @NonNull IMobileConnectCallback mobileConnectCallback);

        @SuppressWarnings("unused")
        void refreshToken(String refreshToken, @NonNull IMobileConnectCallback mobileConnectCallback);

        @SuppressWarnings("unused")
        void revokeToken(String token, String tokenTypeHint, @NonNull IMobileConnectCallback mobileConnectCallback);

        @SuppressWarnings("unused")
        void requestUserInfo(String accessToken, IMobileConnectCallback mobileConnectCallback);
    }

    interface UserActionsListener
    {
        void setView(View view);

        DiscoveryResponse getDiscoveryResponse();

        void setDiscoveryResponse(final DiscoveryResponse discoveryResponse);

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
                                 @NonNull final IMobileConnectCallback mobileConnectCallback,
                                 final MobileConnectRequestOptions mobileConnectRequestOptions);

        void performHandleUrlRedirect(final URI redirectedUrl,
                                      final String expectedState,
                                      final String expectedNonce,
                                      @NonNull final IMobileConnectCallback mobileConnectCallback,
                                      final MobileConnectRequestOptions mobileConnectRequestOptions);

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
     * On completion of an API to {@link android.mobileconnect.gsma.com.library.MobileConnectAndroidInterface}
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
