package android.mobileconnect.gsma.com.library.interfaces;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

/**
 * Implementation of this should be done inside the
 * {@link android.mobileconnect.gsma.com.library.main.MobileConnectAsyncTask}
 *
 * @since 2.0
 */
public interface IMobileConnectOperation
{
    MobileConnectStatus operation();
}
