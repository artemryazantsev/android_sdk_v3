package com.gsma.mobileconnect.r2.android.webviewclient;

import com.gsma.mobileconnect.r2.android.interfaces.AuthenticationListener;
import com.gsma.mobileconnect.r2.android.interfaces.WebViewCallBack;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertEquals;


public class AuthenticationWebViewClientTest
{
    private AuthenticationWebViewClient authenticationWebViewClient;

    @Mock
    private AuthenticationListener mockListener;

    @Mock
    private WebViewCallBack mockWebViewCallback;

    @Mock
    private MobileConnectStatus mockMobileConnectStatus;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);
        authenticationWebViewClient = new AuthenticationWebViewClient(null,
                                                                      null,
                                                                      mockListener,
                                                                      null,
                                                                      mockWebViewCallback);
    }

    @Test
    public void testQualifyUrlTrue()
    {
        // Given
        String url = "www.google.com/?code=901";

        // When
        boolean containsMNCMCC = authenticationWebViewClient.qualifyUrl(url);

        // Then
        assertEquals(containsMNCMCC, true);
    }

    @Test
    public void testQualifyUrlFalse()
    {
        // Given
        String url = "www.google.com/";

        // When
        boolean containsMNCMCC = authenticationWebViewClient.qualifyUrl(url);

        // Then
        assertEquals(containsMNCMCC, false);
    }

    @Test
    public void getWebViewCallback() throws Exception
    {
        assertEquals(authenticationWebViewClient.getWebViewCallback(), mockWebViewCallback);
    }

    @Test
    public void testHandleError() throws Exception
    {
        // Given
        // When
        authenticationWebViewClient.handleError(mockMobileConnectStatus);

        // Then
        Mockito.verify(mockListener).authenticationFailed(mockMobileConnectStatus);
    }

}
