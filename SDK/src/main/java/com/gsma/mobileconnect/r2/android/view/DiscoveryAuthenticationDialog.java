package com.gsma.mobileconnect.r2.android.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;

/**
 * A simple {@link Dialog} which simply expands to the size of the parent window.
 * <p>
 * A singleton instance is provided which should be used when using
 *
 * @since 2.0
 */
public class DiscoveryAuthenticationDialog extends Dialog
{
    public DiscoveryAuthenticationDialog(final Context context)
    {
        super(context);
        initialise();
    }

    /**
     * Make the dialog match the size of the parent layout's window.
     */
    private void initialise()
    {
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        setOnShowListener(new OnShowListener()
        {
            @Override
            public void onShow(final DialogInterface dialog)
            {
                getWindow().setAttributes(layoutParams);
            }
        });
    }
}