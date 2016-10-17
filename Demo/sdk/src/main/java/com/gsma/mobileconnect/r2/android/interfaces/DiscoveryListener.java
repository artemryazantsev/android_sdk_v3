package com.gsma.mobileconnect.r2.android.interfaces;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

/**
 * Callback to provide successful, failure or a closure of the Dialog during the Discovery process
 *
 * @since 1.0
 */
public interface DiscoveryListener
{
    /**
     * A discovery (successful or not) has been completed and has returned a populated
     * {@link MobileConnectStatus}. Interrogating the
     * {@link com.gsma.mobileconnect.r2.MobileConnectStatus.ResponseType} within the {@link MobileConnectStatus} will
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
     * This is called when the discovery dialog has been dismissed. This is independent of whether the Discovery
     * process was successful or not.
     */
    void onDiscoveryDialogClose();
}