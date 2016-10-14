package android.mobileconnect.gsma.com.library.main;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;

import org.greenrobot.eventbus.EventBus;

import static android.mobileconnect.gsma.com.library.main.MobileConnectContract.IMobileConnectCallback;
import static android.mobileconnect.gsma.com.library.main.MobileConnectContract.IMobileConnectOperation;

/**
 * An AsyncTask to wrap all API calls to {@link MobileConnectInterface} asynchronously.
 */
public class MobileConnectAsyncTask extends AsyncTask<Void, Void, MobileConnectStatus>
{
    private IMobileConnectOperation mobileConnectOperation;

    private IMobileConnectCallback IMobileConnectCallback;

    public MobileConnectAsyncTask(@NonNull final IMobileConnectOperation mobileConnectOperation,
                                  @NonNull final IMobileConnectCallback IMobileConnectCallback)
    {

        this.mobileConnectOperation = mobileConnectOperation;
        this.IMobileConnectCallback = IMobileConnectCallback;
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
        if (IMobileConnectCallback != null)
        {
            if (mobileConnectStatus.getDiscoveryResponse() != null &&
                !mobileConnectStatus.getDiscoveryResponse().hasExpired())
            {
                EventBus.getDefault().postSticky(mobileConnectStatus.getDiscoveryResponse());
            }
            IMobileConnectCallback.onComplete(mobileConnectStatus);
        }
    }
}