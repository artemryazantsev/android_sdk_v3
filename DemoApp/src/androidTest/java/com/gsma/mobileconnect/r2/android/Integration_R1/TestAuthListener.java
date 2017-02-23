package com.gsma.mobileconnect.r2.android.Integration_R1;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.interfaces.AuthenticationListener;

/**
 * Created by n.rusak on 10/14/16.
 */

public class TestAuthListener implements AuthenticationListener {

    @Override
    public void authenticationFailed(MobileConnectStatus mobileConnectStatus) {
        AuthorizationTest.authError = mobileConnectStatus.getErrorCode();
        AuthorizationTest.authErrorDecription = mobileConnectStatus.getErrorMessage();
    }

    @Override
    public void authenticationSuccess(MobileConnectStatus mobileConnectStatus) {
        AuthorizationTest.requestTokenResponse = mobileConnectStatus.getRequestTokenResponse();
    }

    @Override
    public void onAuthenticationDialogClose() {

    }
}
