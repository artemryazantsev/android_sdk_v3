package com.gsma.mobileconnect.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;

/**
 * A simple {@link Dialog} which simply expands to the size of the parent window.
 * <p/>
 * A singleton instance is provided which should be used when using
 * {@link com.gsma.mobileconnect.helpers.DiscoveryService} and {@link com.gsma.mobileconnect.helpers.AuthorizationService} as they both require a {@link Dialog} for the same purpose.
 * Created by Usmaan.Dad on 6/17/2016.
 */
public class DiscoveryAuthenticationDialog extends Dialog
{
    private WindowManager.LayoutParams layoutParams;

    private static DiscoveryAuthenticationDialog instance;

    /**
     * When discovering and authentic
     *
     * @param context An Android {@link Context}
     * @return A singleton {@link DiscoveryAuthenticationDialog} object
     */
    public static DiscoveryAuthenticationDialog getInstance(final Context context)
    {
        if (instance == null)
        {
            instance = new DiscoveryAuthenticationDialog(context);
        }
        return instance;
    }

    private DiscoveryAuthenticationDialog(final Context context)
    {
        super(context);
        initialise();
    }

    /**
     * Initialises the current instance of the {@link Dialog} such that it's made to match the size of the parent
     * window.
     */
    private void initialise()
    {
        setCancelable(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.layoutParams = new WindowManager.LayoutParams();
        this.layoutParams.copyFrom(getWindow().getAttributes());
        this.layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        this.layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        setOnShowListener(new OnShowListener()
        {
            @Override
            public void onShow(final DialogInterface dialog)
            {
                getWindow().setAttributes(DiscoveryAuthenticationDialog.this.layoutParams);
            }
        });
    }
}
