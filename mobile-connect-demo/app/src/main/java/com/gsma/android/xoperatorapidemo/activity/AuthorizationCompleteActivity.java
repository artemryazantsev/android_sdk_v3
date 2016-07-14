package com.gsma.android.xoperatorapidemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gsma.android.R;
import com.gsma.mobileconnect.helpers.RetrieveUserinfoTask;
import com.gsma.mobileconnect.helpers.UserInfo;
import com.gsma.mobileconnect.helpers.UserInfoListener;

/*
 * initiate the process of sign-in using the OperatorID API. 
 * the sign-in process is based on the user accessing the operator portal
 * through a browser. It is based on OpenID Connect
 * 
 * details on using an external browser are not finalised therefore at the moment
 * this uses a WebView
 */
public class AuthorizationCompleteActivity extends Activity implements UserInfoListener
{
    private static final String TAG = "AuthCompleteActivity";

    private AuthorizationCompleteActivity authorizationCompleteActivityInstance;

    private TextView statusField = null;

    private TextView authorizationCompleteTokenValue = null;

    private TextView authorizationCompletePCRValue = null;

    /*
     * method called when this activity is created - handles the receiving of
     * endpoint parameters and setting up the WebView
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.authorizationCompleteActivityInstance = this;
        setContentView(R.layout.activity_identity_authorization_complete);

        this.statusField = (TextView) findViewById(R.id.authorizationCompleteStatus);

        this.authorizationCompleteTokenValue = (TextView) findViewById(R.id.authorizationCompleteTokenValue);
        this.authorizationCompletePCRValue = (TextView) findViewById(R.id.authorizationCompletePCRValue);
    }

    /*
     * when this activity starts
     *
     * @see android.app.Activity#onStart()
     */
    public void onStart()
    {
        super.onStart();

        this.authorizationCompleteTokenValue.setText(getString(R.string.authorizationCompleteTokenValue));
        this.authorizationCompletePCRValue.setText(getString(R.string.authorizationCompletePCRValue));

        final Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            final String userinfoUri = extras.getString("userinfoUri");
            final String code = extras.getString("code");
            final String error = extras.getString("error");
            final String clientId = extras.getString("clientId");
            final String clientSecret = extras.getString("clientSecret");
            final String accessToken = extras.getString("accessToken");
            final String PCR = extras.getString("PCR");

            if (accessToken != null)
            {
                this.authorizationCompleteTokenValue.setText(accessToken);
            }
            if (PCR != null)
            {
                this.authorizationCompletePCRValue.setText(PCR);
            }

            String statusDescription = "Unknown";
            boolean authorized = false;
            if (code != null && code.trim().length() > 0)
            {
                statusDescription = "Authorized";
                authorized = true;
            }
            else if (error != null && error.trim().length() > 0)
            {
                statusDescription = "Not authorized";
            }

            this.statusField.setText(statusDescription);

            if (authorized)
            {
                //MobileConnectConfig config,String userinfoUri, String accessToken, UserInfoListener listener
                final RetrieveUserinfoTask task = new RetrieveUserinfoTask(userinfoUri,
                                                                           code,
                                                                           clientId,
                                                                           clientSecret,
                                                                           this);
                task.execute();
            }
        }
    }

    /*
     * go back to the main screen
     */
    public void home(final View view)
    {
        final Intent intent = new Intent(this.authorizationCompleteActivityInstance, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void userReceived(final UserInfo userInfo)
    {
        Log.d(TAG, "userInfo" + userInfo);
    }
}
