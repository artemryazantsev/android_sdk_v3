package android.mobileconnect.gsma.com.library.main;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectStatus;

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

    private DiscoveryResponseObservable discoveryResponseObservable;

    public MobileConnectAsyncTask(@NonNull final IMobileConnectOperation mobileConnectOperation,
                                  @NonNull final IMobileConnectCallback IMobileConnectCallback,
                                  @Nullable final DiscoveryResponseObservable discoveryResponseObservable)
    {

        this.mobileConnectOperation = mobileConnectOperation;
        this.IMobileConnectCallback = IMobileConnectCallback;
        this.discoveryResponseObservable = discoveryResponseObservable;
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
                //                discoveryResponseObservable.setValue(mobileConnectStatus.getDiscoveryResponse());
                //                discoveryResponseObservable.notifyObservers();
                final EventBus eventBus = EventBus.getDefault();

                eventBus.postSticky(new DiscoveryResponseEvent(mobileConnectStatus.getDiscoveryResponse()));
            }
            IMobileConnectCallback.onComplete(mobileConnectStatus);
        }
    }
}