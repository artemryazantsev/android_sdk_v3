package com.gsma.mobileconnect.r2.android.interfaces;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

/**
 * Implementation of this should be done inside the
 * {@link com.gsma.mobileconnect.r2.android.main.MobileConnectAsyncTask}
 *
 * @since 2.0
 */
public interface IMobileConnectOperation
{
    MobileConnectStatus operation();
}
