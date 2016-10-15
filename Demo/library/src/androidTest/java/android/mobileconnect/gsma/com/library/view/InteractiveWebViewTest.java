package android.mobileconnect.gsma.com.library.view;

import android.mobileconnect.gsma.com.library.TestActivity;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

/**
 * Created by usmaan.dad on 12/10/2016.
 */
@RunWith(AndroidJUnit4.class)
public class InteractiveWebViewTest extends ActivityInstrumentationTestCase2<TestActivity>
{
    public InteractiveWebViewTest()
    {
        super(TestActivity.class);
    }

    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
    }

    @Test
    public void testWebSettings()
    {
        // Given
        final InteractiveWebView interactiveWebView = Mockito.mock(InteractiveWebView.class);
        final WebSettings settings = interactiveWebView.getSettings();

        // When
        // Then
        assertNotNull(interactiveWebView);
        assertEquals(WebView.SCROLLBARS_INSIDE_OVERLAY, interactiveWebView.getScrollBarStyle());
        assertTrue(interactiveWebView.isFocusable());

        assertTrue(settings.getJavaScriptEnabled());
        assertEquals(WebSettings.LOAD_NO_CACHE, settings.getCacheMode());
        assertTrue(settings.supportMultipleWindows());
        assertTrue(settings.getDomStorageEnabled());
        assertTrue(settings.getDatabaseEnabled());
        assertTrue(settings.supportZoom());
        assertTrue(settings.getUseWideViewPort());
    }
}