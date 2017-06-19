package com.gsma.mobileconnect.r2.android.webviewclient;

import android.annotation.SuppressLint;
import com.gsma.mobileconnect.r2.android.interfaces.WebViewCallBack;
import com.gsma.mobileconnect.r2.android.view.DiscoveryAuthenticationDialog;
import android.net.Uri;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Created by usmaan.dad on 14/10/2016.
 */
public class MobileConnectWebViewClientUnitTest
{
    private MobileConnectWebViewClient mobileConnectWebViewClient;

    @Mock
    protected ProgressBar mockProgressBar;

    @Mock
    protected DiscoveryAuthenticationDialog mockDialog;

    @Mock
    private WebViewCallBack mockWebViewCallback;

    private String redirectUrl = "http://redirectUrl";

    private boolean handleErrorCalled;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        handleErrorCalled = false;

        mobileConnectWebViewClient = new MobileConnectWebViewClient(mockDialog, mockProgressBar, redirectUrl)
        {
            @Override
            protected boolean qualifyUrl(String url)
            {
                return true;
            }

            @Override
            protected void handleError(MobileConnectStatus status)
            {
                handleErrorCalled = true;
            }

            @Override
            protected WebViewCallBack getWebViewCallback()
            {
                return mockWebViewCallback;
            }
        };
    }

    @After
    public void tearDown() throws Exception
    {
        mobileConnectWebViewClient = null;
        mockDialog = null;
        mockProgressBar = null;
        redirectUrl = null;
    }

    @Test
    public void onReceivedErrorWithErrorDesc() throws Exception
    {
        // Given
        WebView mockWebView = Mockito.mock(WebView.class);
        String url = redirectUrl + "?error=123&error_description=desc";

        // When
        mobileConnectWebViewClient.onReceivedError(mockWebView, 401, "desc", url);

        // Then
        Assert.assertTrue(handleErrorCalled);
    }

    @Test
    public void onReceivedErrorWithDesc() throws Exception
    {
        // Given
        WebView mockWebView = Mockito.mock(WebView.class);
        String url = redirectUrl + "?error=123&description=desc";

        // When
        mobileConnectWebViewClient.onReceivedError(mockWebView, 401, "desc", url);

        // Then
        Assert.assertTrue(handleErrorCalled);
    }

    @Test
    public void onReceivedErrorWithNoDesc() throws Exception
    {
        // Given
        WebView mockWebView = Mockito.mock(WebView.class);
        String url = redirectUrl + "?error=123&desc=desc";

        // When
        mobileConnectWebViewClient.onReceivedError(mockWebView, 401, "desc", url);

        // Then
        Assert.assertTrue(handleErrorCalled);
    }

    @SuppressLint("NewApi")
    @Test
    public void onReceivedErrorForAndroidMWithErrorDesc() throws Exception
    {
        // Given
        WebView mockWebView = Mockito.mock(WebView.class);
        WebResourceRequest mockRequest = Mockito.mock(WebResourceRequest.class);
        WebResourceError mockError = Mockito.mock(WebResourceError.class);
        String url = redirectUrl + "?error=123&error_description=desc";

        Uri mockUri = Mockito.mock(Uri.class);

        Mockito.when(mockRequest.getUrl()).thenReturn(mockUri);
        Mockito.when(mockUri.toString()).thenReturn(url);

        // When
        mobileConnectWebViewClient.onReceivedError(mockWebView, mockRequest, mockError);

        // Then
        Assert.assertTrue(handleErrorCalled);

    }

    @SuppressLint("NewApi")
    @Test
    public void onReceivedErrorForAndroidMWithDesc() throws Exception
    {
        // Given
        WebView mockWebView = Mockito.mock(WebView.class);
        WebResourceRequest mockRequest = Mockito.mock(WebResourceRequest.class);
        WebResourceError mockError = Mockito.mock(WebResourceError.class);
        String url = redirectUrl + "?error=123&description=desc";

        Uri mockUri = Mockito.mock(Uri.class);

        Mockito.when(mockRequest.getUrl()).thenReturn(mockUri);
        Mockito.when(mockUri.toString()).thenReturn(url);

        // When
        mobileConnectWebViewClient.onReceivedError(mockWebView, mockRequest, mockError);

        // Then
        Assert.assertTrue(handleErrorCalled);
    }

    @SuppressLint("NewApi")
    @Test
    public void onReceivedErrorForAndroidMWithNoDesc() throws Exception
    {
        // Given
        WebView mockWebView = Mockito.mock(WebView.class);
        WebResourceRequest mockRequest = Mockito.mock(WebResourceRequest.class);
        WebResourceError mockError = Mockito.mock(WebResourceError.class);
        String url = redirectUrl + "?error=123&desc=desc";

        Uri mockUri = Mockito.mock(Uri.class);

        Mockito.when(mockRequest.getUrl()).thenReturn(mockUri);
        Mockito.when(mockUri.toString()).thenReturn(url);

        // When
        mobileConnectWebViewClient.onReceivedError(mockWebView, mockRequest, mockError);

        // Then
        Assert.assertTrue(handleErrorCalled);
    }

    @Test
    public void shouldOverrideUrlLoadingDoesNotStartWithRedirectUrl() throws Exception
    {
        // Given
        WebView mockWebView = Mockito.mock(WebView.class);
        String url = "www.google.com/";

        // When
        boolean returnValue = mobileConnectWebViewClient.shouldOverrideUrlLoading(mockWebView, url);

        // Then
        Mockito.verify(mockProgressBar).setVisibility(View.VISIBLE);

        Assert.assertFalse(returnValue);
    }

    @Test
    public void shouldOverrideUrlLoadingStartsWithRedirectUrlError() throws Exception
    {
        // Given
        WebView mockWebView = Mockito.mock(WebView.class);
        String url = redirectUrl + "?error=123";

        // When
        boolean returnValue = mobileConnectWebViewClient.shouldOverrideUrlLoading(mockWebView, url);

        // Then
        Mockito.verify(mockProgressBar).setVisibility(View.VISIBLE);

        Mockito.verify(mockWebView).stopLoading();
        Mockito.verify(mockWebView)
               .loadData(ArgumentMatchers.anyString(),
                         ArgumentMatchers.anyString(),
                         (String) ArgumentMatchers.isNull());

        Mockito.verify(mockDialog, Mockito.atLeastOnce()).cancel();

        Assert.assertTrue(returnValue);
        Assert.assertTrue(handleErrorCalled);
    }

    @Test
    public void shouldOverrideUrlLoadingStartsWithRedirectUrlSuccess() throws Exception
    {
        // Given
        WebView mockWebView = Mockito.mock(WebView.class);

        // When
        boolean returnValue = mobileConnectWebViewClient.shouldOverrideUrlLoading(mockWebView, redirectUrl);

        // Then
        Mockito.verify(mockProgressBar).setVisibility(View.VISIBLE);

        Mockito.verify(mockWebView).stopLoading();
        Mockito.verify(mockWebView)
               .loadData(ArgumentMatchers.anyString(),
                         ArgumentMatchers.anyString(),
                         (String) ArgumentMatchers.isNull());

        Mockito.verify(mockDialog, Mockito.atLeastOnce()).cancel();
        Mockito.verify(mockWebViewCallback).onSuccess(redirectUrl);

        Assert.assertTrue(returnValue);
        Assert.assertFalse(handleErrorCalled);
    }

    @Test
    public void onPageFinished() throws Exception
    {
        // Given
        WebView mockWebView = Mockito.mock(WebView.class);
        String url = "www.google.com/";

        // When
        mobileConnectWebViewClient.onPageFinished(mockWebView, url);

        // Then
        Mockito.verify(mockProgressBar).setVisibility(View.GONE);
    }

    @Test
    public void handleResult() throws Exception
    {
        // Given
        String url = "www.google.com/";
        // When
        mobileConnectWebViewClient.handleResult(url);

        // Then
        Mockito.verify(mockWebViewCallback).onSuccess(url);
    }

    @Test
    public void testGetErrorStatusCode() throws Exception
    {
        // Given
        String url = "www.google.com/?error=404&error_description=notfound";

        // When
        //discoveryWebViewClient.onReceivedError(null, 1, null, url);

        // Then
        //Mockito.verify(mockWebViewCallback).onError(mobileConnectStatus);
    }
}