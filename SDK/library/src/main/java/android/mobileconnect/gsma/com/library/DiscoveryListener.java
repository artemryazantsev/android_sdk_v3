package android.mobileconnect.gsma.com.library;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

/**
 * Created by usmaan.dad on 12/08/2016.
 */
public interface DiscoveryListener
{
    /**
     * A successful discovery has been completed and has returned a populated
     * {@link MobileConnectStatus}.
     *
     * @param mobileConnectStatus The result of the discovery.
     */
    void discoveryComplete(MobileConnectStatus mobileConnectStatus);

    /**
     * The discovery has failed to succeed. A a populated
     * {@link MobileConnectStatus} with the error
     *
     * @param mobileConnectStatus The {@link MobileConnectStatus} containing the error
     */
    void discoveryFailed(MobileConnectStatus mobileConnectStatus);

    /**
     * This is called when the discovery dialog has been dismissed.
     */
    void onDiscoveryDialogClose();
}
