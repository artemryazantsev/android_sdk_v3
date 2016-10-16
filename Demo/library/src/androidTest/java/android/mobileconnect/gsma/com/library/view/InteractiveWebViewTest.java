package android.mobileconnect.gsma.com.library.view;

import android.mobileconnect.gsma.com.library.TestActivity;
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
}
