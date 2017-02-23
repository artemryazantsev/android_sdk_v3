package com.gsma.mobileconnect.r2.android.Integration_R1_New;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.Integration_R1.AuthorizationTest;
import com.gsma.mobileconnect.r2.android.interfaces.AuthenticationListener;

public class TestAuthListenerNew  implements AuthenticationListener {
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
