package com.gsma.mobileconnect.r2.android.main;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gsma.mobileconnect.r2.MobileConnectWebInterface;
import com.gsma.mobileconnect.r2.android.bus.BusManager;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;

import static com.gsma.mobileconnect.r2.android.main.IMobileConnectContract.IMobileConnectCallbackManually;
import static com.gsma.mobileconnect.r2.android.main.IMobileConnectContract.IMobileConnectManually;

/**
 * An AsyncTask to wrap all API calls to {@link MobileConnectWebInterface} asynchronously.
 */
public class MobileConnectWebAsyncTask extends AsyncTask<Void, Void, DiscoveryResponse> {
    private IMobileConnectManually mobileConnectManually;
    private IMobileConnectCallbackManually mobileConnectCallback;

    public MobileConnectWebAsyncTask(@NonNull final IMobileConnectManually mobileConnectManually,
                                     @NonNull final IMobileConnectCallbackManually mobileConnectCallbackManually) {
        this.mobileConnectManually = mobileConnectManually;
        this.mobileConnectCallback = mobileConnectCallbackManually;
    }

    @Override
    protected DiscoveryResponse doInBackground(@Nullable final Void... voids) {
        return mobileConnectManually.manually();
    }

    @Override
    protected void onPostExecute(final DiscoveryResponse discoveryResponse)
    {
        super.onPostExecute(discoveryResponse);
        if (this.mobileConnectCallback != null)
        {
            if (discoveryResponse != null &&
                    !discoveryResponse.hasExpired())
            {
                BusManager.post(discoveryResponse);
            }
            this.mobileConnectCallback.onComplete(discoveryResponse);
        }
    }
}
