package android.mobileconnect.gsma.com.library.main;

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
        void performAsyncTask(@NonNull IMobileConnectOperation mobileConnectOperation,
                              @NonNull IMobileConnectCallback mobileConnectCallback);

        void attemptDiscovery(String msisdn,
                              String mcc,
                              String mnc,
                              MobileConnectRequestOptions options,
                              @NonNull IMobileConnectCallback mobileConnectCallback);
    }

    interface UserActionsListener
    {
        void setView(View view);

        DiscoveryResponse getDiscoveryResponse();

        void performDiscovery(@Nullable final String msisdn,
                              @Nullable final String mcc,
                              @Nullable final String mnc,
                              @Nullable final MobileConnectRequestOptions options,
                              @NonNull final IMobileConnectCallback mobileConnectCallback);

        void handleUrlRedirect(final URI redirectedUrl,
                               final String expectedState,
                               final String expectedNonce,
                               @NonNull final IMobileConnectCallback mobileConnectCallback,
                               final MobileConnectRequestOptions mobileConnectRequestOptions);
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
