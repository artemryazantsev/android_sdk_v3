package android.mobileconnect.gsma.com.library.main;

import android.mobileconnect.gsma.com.library.bus.BusManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectStatus;

import static android.mobileconnect.gsma.com.library.main.IMobileConnectContract.IMobileConnectCallback;
import static android.mobileconnect.gsma.com.library.main.IMobileConnectContract.IMobileConnectOperation;

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
        if (mobileConnectCallback != null)
        {
            if (mobileConnectStatus.getDiscoveryResponse() != null &&
                !mobileConnectStatus.getDiscoveryResponse().hasExpired())
            {
                BusManager.post(mobileConnectStatus.getDiscoveryResponse());
            }
            mobileConnectCallback.onComplete(mobileConnectStatus);
        }
    }
}