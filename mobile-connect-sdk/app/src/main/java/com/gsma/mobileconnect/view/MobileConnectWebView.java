package com.gsma.mobileconnect.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * A Custom {@link WebView} with pre-initialised Javascript.
 * <p/>
 * Created by Usmaan.Dad on 6/17/2016.
 */
public class MobileConnectWebView extends WebView
{
    public MobileConnectWebView(final Context context)
    {
        super(context);
        initialise(context);
    }

    public MobileConnectWebView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        initialise(context);
    }

    public MobileConnectWebView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initialise(context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initialise(final Context context)
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

            final String databasePath = context.getApplicationContext()
                                               .getDir("database", Context.MODE_PRIVATE)
                                               .getPath();
            settings.setDatabasePath(databasePath);
        }
    }
}