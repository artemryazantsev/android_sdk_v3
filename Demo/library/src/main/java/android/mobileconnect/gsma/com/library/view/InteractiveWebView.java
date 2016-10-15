package android.mobileconnect.gsma.com.library.view;

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
        initialise(context);
    }

    public InteractiveWebView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        initialise(context);
    }

    public InteractiveWebView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initialise(context);
    }

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

            WebSettings settings = getSettings();

            settings.setJavaScriptEnabled(true);
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            settings.setSupportMultipleWindows(false);
            settings.setDomStorageEnabled(true);
            settings.setDatabaseEnabled(true);
            settings.setSupportZoom(false);
            settings.setUseWideViewPort(false);

            String databasePath = context.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
            settings.setDatabasePath(databasePath);
        }
    }
}