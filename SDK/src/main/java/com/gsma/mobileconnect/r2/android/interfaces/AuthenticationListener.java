package com.gsma.mobileconnect.r2.android.interfaces;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

/**
 * This Listener handles the tokens being returned from the Authentication service.
 *
 * @since 1.0
 */
public interface AuthenticationListener
{
    /**
     * In the event an Authentication failed this method will be called by the SDK. It will return a status object that
     * will contain further details of the error or a notification that discovery is required.
     *
     * @param mobileConnectStatus A populated {@link MobileConnectStatus} containing the errors.
     */
    void authenticationFailed(final MobileConnectStatus mobileConnectStatus);

    void authenticationSuccess(final MobileConnectStatus mobileConnectStatus);

    /**
     * This is called when the Authentication dialog has been dismissed.
     */
    void onAuthenticationDialogClose();
}