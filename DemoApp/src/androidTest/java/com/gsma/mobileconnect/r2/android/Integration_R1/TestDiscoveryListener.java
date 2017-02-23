package com.gsma.mobileconnect.r2.android.Integration_R1;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.interfaces.DiscoveryListener;
import com.gsma.mobileconnect.r2.android.main.IMobileConnectContract;

/**
 * Created by n.rusak on 10/14/16.
 */

public class TestDiscoveryListener implements DiscoveryListener {

    public static MobileConnectStatus response = null;


    @Override
    public void onDiscoveryResponse(MobileConnectStatus mobileConnectStatus) {
        response = mobileConnectStatus;
    }

    @Override
    public void discoveryFailed(MobileConnectStatus mobileConnectStatus) {

    }

    @Override
    public void onDiscoveryDialogClose() {

    }
}
