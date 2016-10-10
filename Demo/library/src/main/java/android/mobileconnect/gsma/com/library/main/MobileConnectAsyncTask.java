package android.mobileconnect.gsma.com.library.main;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;

import static android.mobileconnect.gsma.com.library.main.MobileConnectContract.IMobileConnectOperation;
import static android.mobileconnect.gsma.com.library.main.MobileConnectContract.IMobileConnectCallback;

/**
 * An AsyncTask to wrap all API calls to {@link MobileConnectInterface} asynchronously.
 */
public class MobileConnectAsyncTask extends AsyncTask<Void, Void, MobileConnectStatus> {
    private IMobileConnectOperation mobileConnectOperation;

    private IMobileConnectCallback IMobileConnectCallback;

    private DiscoveryResponse discoveryResponse;

    public DiscoveryResponse getDiscoveryResponse() {
        return discoveryResponse;
    }

    public MobileConnectAsyncTask(@NonNull final IMobileConnectOperation mobileConnectOperation,
                                  @NonNull final IMobileConnectCallback IMobileConnectCallback) {

        this.mobileConnectOperation = mobileConnectOperation;
        this.IMobileConnectCallback = IMobileConnectCallback;
    }

    @Override
    protected MobileConnectStatus doInBackground(@Nullable final Void... voids) {
        return mobileConnectOperation.operation();
    }

    @Override
    protected void onPostExecute(final MobileConnectStatus mobileConnectStatus) {
        super.onPostExecute(mobileConnectStatus);
        if (IMobileConnectCallback != null) {
            if (mobileConnectStatus.getDiscoveryResponse() != null &&
                    !mobileConnectStatus.getDiscoveryResponse().hasExpired()) {
                discoveryResponse = mobileConnectStatus.getDiscoveryResponse();
            }
            IMobileConnectCallback.onComplete(mobileConnectStatus);
        }
    }
}
