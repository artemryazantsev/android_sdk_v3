package com.gsma.mobileconnect.r2.android.webviewclient;

import com.gsma.mobileconnect.r2.android.interfaces.WebViewCallBack;
import com.gsma.mobileconnect.r2.android.view.DiscoveryAuthenticationDialog;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class DiscoveryWebViewClientTest
{
    private DiscoveryWebViewClient discoveryWebViewClient;

    @Mock
    private WebViewCallBack mockWebViewCallback;

    @Mock
    private MobileConnectStatus mockMobileConnectStatus;

    @Mock
    private DiscoveryAuthenticationDialog mockDialog;

    @Before
    public void setUp() throws Exception
    {
        discoveryWebViewClient = new DiscoveryWebViewClient(mockDialog, null, null, mockWebViewCallback);
    }

    @Test
    public void testQualifyUrlTrue() throws Exception
    {
        // Given
        String url = "www.google.com/?mcc_mnc=901";

        // When
        boolean containsMNCMCC = discoveryWebViewClient.qualifyUrl(url);

        // Then
        assertEquals(containsMNCMCC, true);
    }

    @Test
    public void testQualifyUrlFalse() throws Exception
    {
        // Given
        String url = "www.google.com/";

        // When
        boolean containsMNCMCC = discoveryWebViewClient.qualifyUrl(url);

        // Then
        assertEquals(containsMNCMCC, false);
    }

    @Test
    public void testHandleError() throws Exception
    {
        // Given
        // When
        discoveryWebViewClient.handleError(mockMobileConnectStatus);

        // Then
        Mockito.verify(mockWebViewCallback).onError(mockMobileConnectStatus);
    }

    @Test
    public void testHandleResult() throws Exception
    {
        // Given
        String url = "www.google.com/";
        // When
        discoveryWebViewClient.handleResult(url);

        // Then
        Mockito.verify(mockWebViewCallback).onSuccess(url);
    }

    @Test
    public void getWebViewCallback() throws Exception
    {
        assertEquals(discoveryWebViewClient.getWebViewCallback(), mockWebViewCallback);
    }
}