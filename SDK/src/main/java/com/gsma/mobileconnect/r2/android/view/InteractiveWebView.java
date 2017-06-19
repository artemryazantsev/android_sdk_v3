package com.gsma.mobileconnect.r2.android.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * A custom {@link WebView} to enable Javascript
 *
 * @since 2.0
 */
public class InteractiveWebView extends WebView
{
    public InteractiveWebView(final Context context)
    {
        super(context);
        initialise();
    }

    public InteractiveWebView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        initialise();
    }

    public InteractiveWebView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initialise();
    }

    private void initialise()
    {
        if (!isInEditMode())
        {
            if (Build.VERSION.SDK_INT >= 19)
            {
                setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
            else
            {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
            setFocusable(true);
            setFocusableInTouchMode(true);
            requestFocus(View.FOCUS_DOWN);

            final WebSettings settings = getSettings();

            settings.setJavaScriptEnabled(true);
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            settings.setSupportMultipleWindows(false);
            settings.setDomStorageEnabled(true);
            settings.setDatabaseEnabled(true);
            settings.setSupportZoom(false);
            settings.setUseWideViewPort(false);
        }
    }
}