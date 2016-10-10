package android.mobileconnect.gsma.com.library.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;

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
                              @NonNull  final IMobileConnectCallback mobileConnectCallback);
    }

    interface IMobileConnectCallback
    {
        void onComplete(final MobileConnectStatus mobileConnectStatus);
    }

    interface IMobileConnectOperation
    {
        MobileConnectStatus operation();
    }
}
