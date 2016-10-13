package android.mobileconnect.gsma.com.library.view;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by usmaan.dad on 12/10/2016.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class InteractiveWebViewTest
{
    @Test
    public void constructors()
    {
        Context baseContext = InstrumentationRegistry.getContext();

        InteractiveWebView webView1 = new InteractiveWebView(baseContext);
        InteractiveWebView webView2 = new InteractiveWebView(baseContext, null);
        InteractiveWebView webView3 = new InteractiveWebView(baseContext, null, 1);
    }
}