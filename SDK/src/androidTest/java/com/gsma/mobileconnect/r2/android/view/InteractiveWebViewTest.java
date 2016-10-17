package com.gsma.mobileconnect.r2.android.view;

import android.content.res.Resources;
import com.gsma.mobileconnect.r2.android.TestActivity;
import android.support.test.annotation.UiThreadTest;
import android.test.ActivityInstrumentationTestCase2;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class InteractiveWebViewTest extends ActivityInstrumentationTestCase2<TestActivity>
{
    public InteractiveWebViewTest()
    {
        super(TestActivity.class);
    }

    public void setUp() throws Exception
    {
        super.setUp();
    }

    public void tearDown() throws Exception
    {
        getActivity().finish();
        super.tearDown();
    }

    @UiThreadTest
    public void testWebSettings()
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                // Given
                InteractiveWebView interactiveWebView = new InteractiveWebView(getActivity());

                final WebSettings settings = interactiveWebView.getSettings();

                // When
                // Then
                assertNotNull(interactiveWebView);
                assertEquals(WebView.SCROLLBARS_INSIDE_OVERLAY, interactiveWebView.getScrollBarStyle());
                assertTrue(interactiveWebView.isFocusable());

                assertTrue(settings.getJavaScriptEnabled());
                assertEquals(WebSettings.LOAD_NO_CACHE, settings.getCacheMode());
                assertFalse(settings.supportMultipleWindows());
                assertTrue(settings.getDomStorageEnabled());
                assertTrue(settings.getDatabaseEnabled());
                assertFalse(settings.supportZoom());
                assertFalse(settings.getUseWideViewPort());
            }
        });
    }

    @UiThreadTest
    public void testConstructorWithAttributeSet()
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                // Given
                InteractiveWebView interactiveWebView = new InteractiveWebView(getActivity(), null);
                assertNotNull(interactiveWebView);
            }
        });
    }

    @UiThreadTest
    public void testConstructorWithAttributeSetAndStyleAttr()
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                // Given
                InteractiveWebView interactiveWebView = new InteractiveWebView(getActivity(),
                                                                               null,
                                                                               Resources.getSystem()
                                                                                        .getIdentifier(
                                                                                                "webViewStyle",
                                                                                                "attr",
                                                                                                "android"));
                assertNotNull(interactiveWebView);
            }
        });
    }
}
