package com.gsma.mobileconnect.r2.android.Integration_R2_New;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.interfaces.AuthenticationListener;

public class TestAuthListenerNew implements AuthenticationListener {
    @Override
    public void authenticationFailed(MobileConnectStatus mobileConnectStatus) {
        AuthorizationNewTest.authError = mobileConnectStatus.getErrorCode();
        AuthorizationNewTest.authErrorDecription = mobileConnectStatus.getErrorMessage();
    }

    @Override
    public void authenticationSuccess(MobileConnectStatus mobileConnectStatus) {
        AuthorizationNewTest.requestTokenResponse = mobileConnectStatus.getRequestTokenResponse();
    }

    @Override
    public void onAuthenticationDialogClose() {

    }
}
