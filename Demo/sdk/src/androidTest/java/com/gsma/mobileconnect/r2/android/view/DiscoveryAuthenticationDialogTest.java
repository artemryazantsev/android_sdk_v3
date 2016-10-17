package com.gsma.mobileconnect.r2.android.view;

import com.gsma.mobileconnect.r2.android.TestActivity;
import android.support.test.annotation.UiThreadTest;
import android.test.ActivityInstrumentationTestCase2;
import android.view.WindowManager;


public class DiscoveryAuthenticationDialogTest extends ActivityInstrumentationTestCase2
{
    public DiscoveryAuthenticationDialogTest()
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
                final DiscoveryAuthenticationDialog dialog = new DiscoveryAuthenticationDialog(getActivity());
                final WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();

                // When
                // Then
                assertNotNull(layoutParams);
                assertEquals(WindowManager.LayoutParams.MATCH_PARENT, layoutParams.width);
                assertEquals(WindowManager.LayoutParams.MATCH_PARENT, layoutParams.height);
            }
        });
    }
}