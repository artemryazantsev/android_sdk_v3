package com.gsma.mobileconnect.r2.android.main;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.bus.BusManager;

import static com.gsma.mobileconnect.r2.android.main.IMobileConnectContract.IMobileConnectCallback;
import static com.gsma.mobileconnect.r2.android.main.IMobileConnectContract.IMobileConnectOperation;

/**
 * An AsyncTask to wrap all API calls to {@link MobileConnectInterface} asynchronously.
 *
 * @since 2.0
 */
public class MobileConnectAsyncTask extends AsyncTask<Void, Void, MobileConnectStatus>
{
    private IMobileConnectOperation mobileConnectOperation;
    private IMobileConnectCallback mobileConnectCallback;

    public MobileConnectAsyncTask(@NonNull final IMobileConnectOperation mobileConnectOperation,
                                  @NonNull final IMobileConnectCallback IMobileConnectCallback)
    {

        this.mobileConnectOperation = mobileConnectOperation;
        this.mobileConnectCallback = IMobileConnectCallback;
    }



    @Override
    protected MobileConnectStatus doInBackground(@Nullable final Void... voids)
    {
        return mobileConnectOperation.operation();
    }




    @Override
    protected void onPostExecute(final MobileConnectStatus mobileConnectStatus)
    {
        super.onPostExecute(mobileConnectStatus);
        if (this.mobileConnectCallback != null)
        {
            if (mobileConnectStatus.getDiscoveryResponse() != null &&
                !mobileConnectStatus.getDiscoveryResponse().hasExpired())
            {
                BusManager.post(mobileConnectStatus.getDiscoveryResponse());
            }
            this.mobileConnectCallback.onComplete(mobileConnectStatus);
        }
    }
}