package android.mobileconnect.gsma.com.library.callback;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

/**
 * This Listener handles the tokens being returned from the Authorization service.
 * Created by nick.copley on 23/02/2016.
 */
public interface AuthenticationListener
{
    /**
     * In the event an Authorization failed this method will be called by the SDK. It will return a status object that
     * will contain further details of the error or a notification that discovery is required.
     *
     * @param mobileConnectStatus A populated {@link MobileConnectStatus} containing the errors.
     */
    void authorizationFailed(MobileConnectStatus mobileConnectStatus);

    void authorizationSuccess(final MobileConnectStatus mobileConnectStatus);

    /**
     * This is called when the authorization dialog has been dismissed.
     */
    void onAuthorizationDialogClose();
}