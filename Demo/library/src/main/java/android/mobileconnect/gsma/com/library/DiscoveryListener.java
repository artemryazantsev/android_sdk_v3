package android.mobileconnect.gsma.com.library;

import android.support.annotation.Nullable;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

/**
 * Created by usmaan.dad on 12/08/2016.
 */
public interface DiscoveryListener
{
    /**
     * A response has been received by the WebView which may contain the MNC, MCC details
     *
     * @param url The URL potentially containing the MCC, MNC
     */
    void onDiscoveryRedirect(@Nullable final String url);

    /**
     * A successful discovery has been completed and has returned a populated
     * {@link MobileConnectStatus}.
     *
     * @param mobileConnectStatus The result of the discovery.
     */
    void onDiscoveryResponse(final MobileConnectStatus mobileConnectStatus);

    /**
     * The discovery has failed to succeed. A a populated
     * {@link MobileConnectStatus} with the error
     *
     * @param mobileConnectStatus The {@link MobileConnectStatus} containing the error
     */
    void discoveryFailed(final MobileConnectStatus mobileConnectStatus);

    /**
     * This is called when the discovery dialog has been dismissed.
     */
    void onDiscoveryDialogClose();
}