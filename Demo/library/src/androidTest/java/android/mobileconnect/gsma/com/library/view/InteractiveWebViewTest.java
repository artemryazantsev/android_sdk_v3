package android.mobileconnect.gsma.com.library.view;

import android.mobileconnect.gsma.com.library.TestActivity;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by usmaan.dad on 12/10/2016.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class InteractiveWebViewTest extends ActivityInstrumentationTestCase2<TestActivity>
{
    private TestActivity activity;

    public InteractiveWebViewTest()
    {
        super(TestActivity.class);
    }

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        // Injecting the Instrumentation instance is required
        // for your test to run with AndroidJUnitRunner.
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
    }

    @Test
    public void constructors()
    {
        // Given
        InteractiveWebView webView = new InteractiveWebView(activity);

        // When
    }
}