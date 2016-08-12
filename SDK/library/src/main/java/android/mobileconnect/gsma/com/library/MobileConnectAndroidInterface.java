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
 * Created by usmaan.dad on 11/08/2016.
 */
public class MobileConnectAndroidInterface
{
    private final MobileConnectInterface mobileConnectInterface;

    public MobileConnectAndroidInterface(MobileConnectInterface mobileConnectInterface)
    {

        this.mobileConnectInterface = mobileConnectInterface;
    }

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

    public void requestToken(@NonNull final IMobileConnectCallback IMobileConnectCallback,
                             final DiscoveryResponse discoveryResponse,
                             final URI redirectedUrl,
                             final String expectedState,
                             final String expectedNonce)
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

    public void handleRedirect(@NonNull final IMobileConnectCallback IMobileConnectCallback,
                               final URI redirectedUrl,
                               final DiscoveryResponse discoveryResponse,
                               final String expectedState,
                               final String expectedNonce)
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

    public void requestIdentity(@NonNull final IMobileConnectCallback IMobileConnectCallback,
                                final DiscoveryResponse discoveryResponse,
                                final String accessToken,
                                final MobileConnectRequestOptions options)
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

        public MobileConnectAsyncTask(IMobileConnectOperation mobileConnectOperation,
                                      IMobileConnectCallback IMobileConnectCallback)
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