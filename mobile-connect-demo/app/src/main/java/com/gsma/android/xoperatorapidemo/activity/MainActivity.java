package com.gsma.android.xoperatorapidemo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.gsma.android.R;
import com.gsma.android.xoperatorapidemo.utils.AppSettings;
import com.gsma.android.xoperatorapidemo.utils.PhoneState;
import com.gsma.android.xoperatorapidemo.utils.PhoneUtils;
import com.gsma.mobileconnect.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.helpers.AuthorizationListener;
import com.gsma.mobileconnect.helpers.AuthorizationService;
import com.gsma.mobileconnect.helpers.DiscoveryListener;
import com.gsma.mobileconnect.helpers.DiscoveryService;
import com.gsma.mobileconnect.helpers.MobileConnectConfig;
import com.gsma.mobileconnect.helpers.MobileConnectStatus;
import com.gsma.mobileconnect.model.DiscoveryModel;
import com.gsma.mobileconnect.oidc.ParsedIdToken;
import com.gsma.mobileconnect.oidc.RequestTokenResponse;
import com.gsma.mobileconnect.utils.AndroidJsonUtils;
import com.gsma.mobileconnect.utils.JsonUtils;
import com.gsma.mobileconnect.utils.NoFieldException;
import com.gsma.mobileconnect.utils.ParsedOperatorIdentifiedDiscoveryResult;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MainActivity extends Activity implements AuthorizationListener, View.OnClickListener, DiscoveryListener
{
    private static final String TAG = "MainActivity";

    private static final int PERMISSIONS_CODE_GRANT_AND_DISCOVER = 22;

    private static final int PERMISSIONS_CODE_GRANT = 44;

    private static final int PERMISSIONS_CODE_SMS = 33;

    public static MainActivity mainActivityInstance = null;

    static Handler phoneStatusHandler = null;

    private static final boolean discoveryComplete = false;

    private static boolean connectionExists = true;

    private static MobileConnectStatus status;

    TextView vMCC = null;

    TextView vMNC = null;

    TextView vStatus = null;

    TextView vDiscoveryStatus = null;

    Button startOperatorId = null;

    RelativeLayout rlayout;

    DiscoveryService discoveryService = null;

    AuthorizationService authorizationService = null;

    MobileConnectConfig config;

    private final BroadcastReceiver ConnectivityChangedReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(final Context context, final Intent intent)
        {
            updatePhoneState();
        }
    };

    /*
     * method called when the application first starts.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        Log.d(TAG, "Starting the app...");

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate called");

        this.config = AppSettings.getMobileConnectConfig();

        this.vMCC = (TextView) findViewById(R.id.valueMCC);
        this.vMNC = (TextView) findViewById(R.id.valueMNC);
        this.vMCC.setText(getText(R.string.valueUnknown));
        this.vMNC.setText(getText(R.string.valueUnknown));
        this.vStatus = (TextView) findViewById(R.id.valueStatus);
        this.vDiscoveryStatus = (TextView) findViewById(R.id.valueDiscoveryStatus);

        this.startOperatorId = (Button) findViewById(R.id.startOperatorId);

        this.rlayout = (RelativeLayout) findViewById(R.id.mainActivity);
        this.rlayout.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(final View v)
            {
                final Toast noInternetConnection = Toast.makeText(getApplicationContext(),
                                                                  "No internet Connection",
                                                                  Toast.LENGTH_SHORT);
                noInternetConnection.show();
            }
        });

        CookieSyncManager.createInstance(this.getApplicationContext());
        CookieManager.getInstance().setAcceptCookie(true);

        phoneStatusHandler = new Handler()
        {
            @Override
            public void handleMessage(final Message msg)
            {
                MainActivity.this.vStatus.setText(getString(msg.what));
            }
        };

        this.vMCC.setText(DiscoveryModel.getInstance().getMcc());
        this.vMNC.setText(DiscoveryModel.getInstance().getMnc());
    }

    @Override
    protected void onStop()
    {
        this.unregisterReceiver(this.ConnectivityChangedReceiver);
        super.onStop();
    }

    @Override
    public void onClick(final View v)
    {
        final Toast noInternetConnection = Toast.makeText(getApplicationContext(),
                                                          "No internet Connection",
                                                          Toast.LENGTH_SHORT);
        noInternetConnection.show();

    }

    /**
     * Update the phone state based on information gathered from the Android SDK.
     */
    public void updatePhoneState()
    {
        final List<String> permissions = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            permissions.add(Manifest.permission.READ_SMS);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
            PackageManager.PERMISSION_GRANTED)
        {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (permissions.size() > 0)
        {
            ActivityCompat.requestPermissions(this,
                                              permissions.toArray(new String[permissions.size()]),
                                              PERMISSIONS_CODE_GRANT);
            return;
        }

        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        final PhoneState state = PhoneUtils.getPhoneState(telephonyManager, connectivityManager);

        final boolean connected = state.isConnected(); // Is the device connected to
        // the Internet
        final boolean usingMobileData = state.isUsingMobileData(); // Is the device
        // connected using cellular/mobile data
        final boolean roaming = state.isRoaming(); // Is the device roaming

        int connectivityStatus = R.string.statusDisconnected;
        if (roaming)
        {
            connectivityStatus = R.string.statusRoaming;
        }
        else if (usingMobileData)
        {
            connectivityStatus = R.string.statusOnNet;
        }
        else if (connected)
        {
            connectivityStatus = R.string.statusOffNet;
        }

        if (!roaming && !usingMobileData && (wifi.getConnectionInfo().getNetworkId() == -1))
        {
            //no wifi or roaming or mobile data
            this.rlayout.setClickable(true);
            this.startOperatorId.setEnabled(false);
            connectionExists = false;
        }
        else
        {
            //assume an internet connection is avilable
            this.rlayout.setClickable(false);
            this.startOperatorId.setEnabled(true);
            connectionExists = true;
        }

        phoneStatusHandler.sendEmptyMessage(connectivityStatus);
    }

    /*
     * on start or return to the main screen reset the screen so that discovery
     * can be started
     */
    @Override
    public void onStart()
    {
        super.onStart();

        Log.d(TAG, "called onStart");

        this.vMCC.setText(getText(R.string.valueUnknown));
        this.vMNC.setText(getText(R.string.valueUnknown));
        this.vDiscoveryStatus.setText(getString(R.string.discoveryStatusPending));

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);

        this.registerReceiver(this.ConnectivityChangedReceiver, intentFilter);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "called onResume");
    }

    /**
     * Call the Discovery SDK.
     */
    private void runDiscovery()
    {
        Log.d(TAG, "Run Discovery");

        updatePhoneState();

        if (connectionExists)
        {
            this.vDiscoveryStatus.setText(getString(R.string.discoveryStatusStarted));

            this.discoveryService = new DiscoveryService();

            status = this.discoveryService.callMobileConnectForStartDiscovery(this.config);

            Log.d(TAG, "Making initial discovery request");
            Log.d(TAG, "Initial response=" + status.toString());
            if (status.getDiscoveryResponse() != null && status.getDiscoveryResponse().getResponseData() != null)
            {
                Log.d(TAG, "Response = " + status.getDiscoveryResponse().getResponseData().toString());
            }
            if (status.isError())
            {
                final Toast toast = Toast.makeText(getApplicationContext(),
                                                   status.getDescription(),
                                                   Toast.LENGTH_SHORT);
                toast.show();
            }
            else if (status.isOperatorSelection())
            {
                Log.d(TAG, "Operator Selection required");
                this.discoveryService.doDiscoveryWithWebView(this.config, this, this, status.getUrl());
            }
            else
            {
                final Message msg = new Message();
                msg.what = R.string.discoveryStatusCompleted;
                msg.obj = status;
                //                discoveryHandler.sendMessage(msg);
                // todo test by adding a phone number in to the config and checking if this branch works.
            }
        }
        else
        {

            final String error = "Device is not currently connected to the Internet";
            final Toast toast = Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void runMobileConnectLogin()
    {
        final DiscoveryResponse resp = status.getDiscoveryResponse();

        final ParsedOperatorIdentifiedDiscoveryResult parsedOperatorIdentifiedDiscoveryResult = JsonUtils
                .parseOperatorIdentifiedDiscoveryResult(
                resp.getResponseData());

        final String authorizationHref = parsedOperatorIdentifiedDiscoveryResult.getAuthorizationHref();

        final String encryptedMSISDN = DiscoveryModel.getInstance().getEncryptedMSISDN();

        final HashMap<String, Object> authOptions = new HashMap<String, Object>();

        if (encryptedMSISDN != null)
        {
            final String hint = "ENCR_MSISDN:" + encryptedMSISDN;
            authOptions.put("login_hint", hint);
        }

        try
        {
            final String openIDConnectScopes = "openid";
            final String returnUri = this.config.getApplicationURL();
            final String state = UUID.randomUUID().toString();
            final String nonce = UUID.randomUUID().toString();
            final int maxAge = 3600;
            final String acrValues = "2";

            this.config.setDiscoveryRedirectURL(returnUri);
            this.config.setAuthorizationState(state);

            if (parsedOperatorIdentifiedDiscoveryResult.getAuthorizationHref() == null)
            {
                final String error;
                if (this.config.getIdentifiedMCC() != null)
                {
                    error = "Authorisation URI for MMC/MNC not known";
                }
                else
                {
                    error = "Authorisation failed because MMC/MNC not found";
                }
                final Toast toast = Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT);
                toast.show();
            }
            else
            {
                this.authorizationService = new AuthorizationService();

                this.authorizationService.authenticate(this.config,
                                                       authorizationHref,
                                                       openIDConnectScopes,
                                                       returnUri,
                                                       state,
                                                       nonce,
                                                       maxAge,
                                                       acrValues,
                                                       this,
                                                       this,
                                                       resp,
                                                       authOptions);
            }
        }
        catch (final UnsupportedEncodingException ueo)
        {
            Log.e(TAG, "UnsupportedEncodingException handling");
        }
    }

    /*
     * default method to add a menu
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults)
    {
        if (requestCode == PERMISSIONS_CODE_GRANT_AND_DISCOVER)
        {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                runDiscovery();
            }
        }
    }

    /**
     * Called from the Layout XML. This is the button click response that initiates Authorisation.
     *
     * @param view
     * @throws UnsupportedEncodingException
     */
    public void startOperatorId(final View view)
    {
        final List<String> permissions = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            permissions.add(Manifest.permission.READ_SMS);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
            PackageManager.PERMISSION_GRANTED)
        {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (permissions.size() > 0)
        {
            ActivityCompat.requestPermissions(this,
                                              permissions.toArray(new String[permissions.size()]),
                                              PERMISSIONS_CODE_GRANT_AND_DISCOVER);

            return;
        }
        runDiscovery();
    }

    public void displayAuthorizationResponse(final String state,
                                             final String authorizationCode,
                                             final String error,
                                             final String clientId,
                                             final String clientSecret,
                                             final String scopes,
                                             final String returnUri,
                                             final String accessToken,
                                             final String PCR)
    {

        final DiscoveryResponse resp = status.getDiscoveryResponse();

        final ParsedOperatorIdentifiedDiscoveryResult parsedOperatorIdentifiedDiscoveryResult = AndroidJsonUtils
                .parseOperatorIdentifiedDiscoveryResult(
                resp.getResponseData());

        final Intent intent = new Intent(this, AuthorizationCompleteActivity.class);
        intent.putExtra("state", state);
        intent.putExtra("code", authorizationCode);
        intent.putExtra("error", error);
        intent.putExtra("clientId", clientId);
        intent.putExtra("clientSecret", clientSecret);
        intent.putExtra("scopes", scopes);
        intent.putExtra("returnUri", returnUri);
        intent.putExtra("accessToken", accessToken);
        intent.putExtra("PCR", PCR);
        intent.putExtra("userinfoUri", parsedOperatorIdentifiedDiscoveryResult.getUserInfoHref());

        startActivity(intent);
    }

    @Override
    public void tokenReceived(final RequestTokenResponse tokenResponse)
    {
        final String state = this.config.getAuthorizationState();
        final String clientId = this.config.getClientId();
        final String clientSecret = this.config.getClientSecret();
        final String openIDConnectScopes = this.config.getAuthorizationScope();
        final String returnUri = this.config.getDiscoveryRedirectURL();

        final String accessToken;
        final String error;
        String pcr = null;
        if (tokenResponse.hasErrorResponse())
        {
            accessToken = null;
            error = tokenResponse.getErrorResponse().get_error();
        }
        else
        {
            accessToken = tokenResponse.getResponseData().get_access_token();
            final ParsedIdToken idtoken = tokenResponse.getResponseData().getParsedIdToken();
            if (idtoken != null)
            {
                pcr = idtoken.get_pcr();
            }

            Log.d(TAG, "access_token=" + accessToken);
            Log.d(TAG, "pcr=" + pcr);
            error = null;
        }

        displayAuthorizationResponse(state,
                                     accessToken,
                                     error,
                                     clientId,
                                     clientSecret,
                                     openIDConnectScopes,
                                     returnUri,
                                     accessToken,
                                     pcr);
    }

    @Override
    public void authorizationFailed(final MobileConnectStatus mobileConnectStatus)
    {
        Log.d(TAG, "AuthorizationFailed");
        final Toast authorizationFailed = Toast.makeText(getApplicationContext(),
                                                         "Authorization Failed : " + mobileConnectStatus.getError(),
                                                         Toast.LENGTH_SHORT);
        authorizationFailed.show();
    }

    @Override
    public void discoveryComplete(final MobileConnectStatus mobileConnectStatus)
    {
        this.vMCC.setText(DiscoveryModel.getInstance().getMcc());
        this.vMNC.setText(DiscoveryModel.getInstance().getMnc());
        this.vDiscoveryStatus.setText(getString(R.string.discoveryStatusCompleted));
        Log.d(TAG, "Discovery Complete");
        status = mobileConnectStatus;
        runMobileConnectLogin();
    }

    @Override
    public void discoveryFailed(final MobileConnectStatus mobileConnectStatus)
    {
        this.vDiscoveryStatus.setText(getString(R.string.discoveryStatusFailer));
        Log.d(TAG, getString(R.string.discoveryStatusFailer));
        Log.d(TAG, getString(R.string.discoveryStatusFailer));
    }
}