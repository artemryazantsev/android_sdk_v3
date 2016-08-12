package android.mobileconnect.gsma.com.library;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;

import java.net.URI;

/**
 * This class interfaces with the underlying Java SDK. It wraps calls to the Java SDK in
 * {@link AsyncTask}s and sends the result via a {@link IMobileConnectCallback}
 * <p>
 * <p>
 * Created by usmaan.dad on 11/08/2016.
 */
public class MobileConnectAndroidInterface
{
    private final MobileConnectInterface mobileConnectInterface;

    /**
     * @param mobileConnectInterface The {@link MobileConnectInterface} containing the necessary set-up of services.
     */
    public MobileConnectAndroidInterface(@NonNull MobileConnectInterface mobileConnectInterface)
    {
        this.mobileConnectInterface = mobileConnectInterface;
    }

    @SuppressWarnings("unused")
    public void attemptDiscovery(@NonNull final IMobileConnectCallback IMobileConnectCallback,
                                 @Nullable final String msisdn,
                                 @Nullable final String mcc,
                                 @Nullable final String mnc,
                                 @Nullable final MobileConnectRequestOptions options)
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
        }, IMobileConnectCallback).execute();
    }

    @SuppressWarnings("unused")
    public void attemptDiscoveryAfterOperationSelection(@NonNull final IMobileConnectCallback IMobileConnectCallback,
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
        }, IMobileConnectCallback).execute();
    }

    @SuppressWarnings("unused")
    public void startAuthentication(@NonNull final IMobileConnectCallback IMobileConnectCallback,
                                    final DiscoveryResponse discoveryResponse,
                                    final String encryptedMSISDN,
                                    final String state,
                                    final String nonce,
                                    final MobileConnectRequestOptions options)
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
        }, IMobileConnectCallback).execute();
    }

    @SuppressWarnings("unused")
    public void requestToken(@NonNull final IMobileConnectCallback IMobileConnectCallback,
                             @Nullable final DiscoveryResponse discoveryResponse,
                             @Nullable final URI redirectedUrl,
                             @Nullable final String expectedState,
                             @Nullable final String expectedNonce)
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
        }, IMobileConnectCallback).execute();
    }

    @SuppressWarnings("unused")
    public void handleRedirect(@NonNull final IMobileConnectCallback IMobileConnectCallback,
                               @Nullable final URI redirectedUrl,
                               @Nullable final DiscoveryResponse discoveryResponse,
                               @Nullable final String expectedState,
                               @Nullable final String expectedNonce)
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
        }, IMobileConnectCallback).execute();
    }

    @SuppressWarnings("unused")
    public void requestIdentity(@NonNull final IMobileConnectCallback IMobileConnectCallback,
                                @Nullable final DiscoveryResponse discoveryResponse,
                                @Nullable final String accessToken,
                                @Nullable final MobileConnectRequestOptions options)
    {
        new MobileConnectAsyncTask(new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidInterface.this.mobileConnectInterface.requestIdentity(discoveryResponse,
                                                                                                 accessToken,
                                                                                                 options);
            }
        }, IMobileConnectCallback).execute();
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
        protected MobileConnectStatus doInBackground(Void... voids)
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

    private interface IMobileConnectCallback
    {
        void onComplete(MobileConnectStatus mobileConnectStatus);
    }
}