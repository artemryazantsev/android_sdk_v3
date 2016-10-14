package android.mobileconnect.gsma.com.library.webviewclient;

import android.mobileconnect.gsma.com.library.interfaces.WebViewCallBack;
import android.mobileconnect.gsma.com.library.view.DiscoveryAuthenticationDialog;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;

/**
 * Created by usmaan.dad on 14/10/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class DiscoveryWebViewClientTest
{
    private DiscoveryWebViewClient discoveryWebViewClient;

    @Mock
    private WebViewCallBack webViewCallback;

    @Mock
    private MobileConnectStatus mobileConnectStatus;

    @Mock
    private DiscoveryAuthenticationDialog dialog;

    @Before
    public void setUp() throws Exception
    {
        discoveryWebViewClient = new DiscoveryWebViewClient(dialog, null, null, webViewCallback);
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
        discoveryWebViewClient.handleError(mobileConnectStatus);

        // Then
        Mockito.verify(webViewCallback).onError(mobileConnectStatus);
    }

    @Test
    public void testHandleResult() throws Exception
    {
        // Given
        String url = "www.google.com/";
        // When
        discoveryWebViewClient.handleResult(url);

        // Then
        Mockito.verify(webViewCallback).onSuccess(url);
    }
}