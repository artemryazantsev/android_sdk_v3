package com.gsma.mobileconnect.r2.android.exampleapps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.gsma.mobileconnect.r2.MobileConnect;
import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.compatibility.AndroidMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.android.interfaces.AuthenticationListener;
import com.gsma.mobileconnect.r2.android.interfaces.DiscoveryListener;
import com.gsma.mobileconnect.r2.android.main.IMobileConnectContract;
import com.gsma.mobileconnect.r2.android.main.MobileConnectAndroidView;
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Created by usmaan.dad on 17/10/2016.
 */

public class AuthorizationActivity extends BaseActivity implements DiscoveryListener,
                                                                   AuthenticationListener,
                                                                   IMobileConnectContract.IMobileConnectCallback
{
    // UI Views
    private CheckBox msisdnCheckBox;

    private TextInputEditText msisdnTextInputEditText;

    private TextInputLayout msisdnTextInputLayout;

    private Toolbar toolbar;

    // Mobile Connect Objects
    private MobileConnectConfig mobileConnectConfig;

    private Button getTokenButton;

    private String scope;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_without_msisdn);

        getTokenButton = (Button) findViewById(R.id.button_get_token);
        msisdnCheckBox = (CheckBox) findViewById(R.id.check_box_msisdn);
        msisdnTextInputEditText = (TextInputEditText) findViewById(R.id.text_input_edit_text_msisdn);
        msisdnTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_layout_msisdn);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        final Intent intent = getIntent();

        if (intent != null)
        {
            scope = intent.getStringExtra("scope");
            final String title = intent.getStringExtra("title");
            toolbar.setTitle(title);
        }

        URI discoveryUri = null;
        URI redirectUri = null;

        try
        {
            discoveryUri = new URI(getString(R.string.discovery_url));
            redirectUri = new URI(getString(R.string.redirect_url));
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        mobileConnectConfig = new MobileConnectConfig.Builder().withClientId(getString(R.string.client_id))
                                                               .withClientSecret(getString(R.string.client_secret))
                                                               .withDiscoveryUrl(discoveryUri)
                                                               .withRedirectUrl(redirectUri)
                                                               .withCacheResponsesWithSessionId(false)
                                                               .build();

        final MobileConnect mobileConnect = new MobileConnect.Builder(mobileConnectConfig,
                                                                      new AndroidMobileConnectEncodeDecoder()).build();

        final MobileConnectInterface mobileConnectInterface = mobileConnect.getMobileConnectInterface();

        mobileConnectAndroidView = new MobileConnectAndroidView(mobileConnectInterface);

        getTokenButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                String msisdn = null;
                final DiscoveryOptions.Builder discoveryOptionsBuilder = new DiscoveryOptions.Builder();

                if (msisdnCheckBox.isChecked())
                {
                    msisdn = msisdnTextInputEditText.getText().toString();
                    discoveryOptionsBuilder.withMsisdn(msisdn);
                }

                discoveryOptionsBuilder.withRedirectUrl(mobileConnectConfig.getRedirectUrl());

                final MobileConnectRequestOptions requestOptions = new MobileConnectRequestOptions.Builder()
                        .withDiscoveryOptions(
                        discoveryOptionsBuilder.withMsisdn(msisdn).build()).build();

                mobileConnectAndroidView.attemptDiscovery(msisdn,
                                                          null,
                                                          null,
                                                          requestOptions,
                                                          AuthorizationActivity.this);
            }
        });

        msisdnCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked)
            {
                if (checked)
                {
                    msisdnTextInputLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    msisdnTextInputLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mobileConnectAndroidView.initialise();
    }

    @Override
    public void onStop()
    {
        mobileConnectAndroidView.cleanUp();
        super.onStop();
    }

    /**
     * This should be called every time after calling any API from the
     * {@link MobileConnectAndroidView}. It interrogates the
     * {@link com.gsma.mobileconnect.r2.MobileConnectStatus.ResponseType} object from within the
     * {@link MobileConnectStatus} object and calls the correct API.
     *
     * @param mobileConnectStatus The status to be interrogated.
     */
    protected void handleRedirect(final MobileConnectStatus mobileConnectStatus)
    {
        final String state =
                mobileConnectStatus.getState() == null ? UUID.randomUUID().toString() : mobileConnectStatus.getState();
        final String nonce =
                mobileConnectStatus.getNonce() == null ? UUID.randomUUID().toString() : mobileConnectStatus.getNonce();

        switch (mobileConnectStatus.getResponseType())
        {
            case ERROR:
            {
                Toast.makeText(this,
                               String.format("Error - %s", mobileConnectStatus.getErrorMessage()),
                               Toast.LENGTH_LONG).show();
                break;
            }
            case OPERATOR_SELECTION:
            {
                mobileConnectAndroidView.attemptDiscoveryWithWebView(this,
                                                                     this,
                                                                     mobileConnectStatus.getUrl(),
                                                                     mobileConnectConfig.getRedirectUrl().toString(),
                                                                     null);
                break;
            }
            case START_AUTHENTICATION:
            {
                AuthenticationOptions.Builder authenticationOptionsBuilder = new AuthenticationOptions.Builder()
                        .withContext(
                        "demo").withBindingMessage("demo auth");

                final MobileConnectRequestOptions.Builder mobileConnectRequestOptionsBuilder = new
                        MobileConnectRequestOptions.Builder();

                authenticationOptionsBuilder.withScope(scope);

                final MobileConnectRequestOptions mobileConnectRequestOptions = mobileConnectRequestOptionsBuilder
                        .withAuthenticationOptions(
                        authenticationOptionsBuilder.build()).build();

                startAuthentication(mobileConnectStatus, mobileConnectRequestOptions, state, nonce);
                break;
            }
            case AUTHENTICATION:
            {
                mobileConnectAndroidView.attemptAuthenticationWithWebView(this,
                                                                          this,
                                                                          mobileConnectStatus.getUrl(),
                                                                          state,
                                                                          nonce,
                                                                          null);
                break;
            }
            case COMPLETE:
            {
                BaseActivity.mobileConnectStatus = mobileConnectStatus;
                displayResult();
                break;
            }
        }
    }

    /**
     * Calls the
     * {@link MobileConnectAndroidView#startAuthentication(String, String, String, MobileConnectRequestOptions,
     * IMobileConnectContract.IMobileConnectCallback)} (String, String, String, MobileConnectRequestOptions,
     * MobileConnectAndroidInterface.IMobileConnectCallback)} API
     *
     * @param mobileConnectStatus         The status returned from the previous API call
     * @param mobileConnectRequestOptions The request options if any
     * @param state                       The same state used in the previous API call OR a randomly generated
     *                                    {@link UUID} if no API was called before.
     * @param nonce                       The same nonce used in the previous API call OR a randomly generated
     *                                    {@link UUID} if no API was called before.
     */
    private void startAuthentication(@NonNull final MobileConnectStatus mobileConnectStatus,
                                     @Nullable final MobileConnectRequestOptions mobileConnectRequestOptions,
                                     @NonNull final String state,
                                     @NonNull final String nonce)
    {
        mobileConnectAndroidView.startAuthentication(mobileConnectStatus.getDiscoveryResponse()
                                                                        .getResponseData()
                                                                        .getSubscriberId(),
                                                     state,
                                                     nonce,
                                                     mobileConnectRequestOptions,
                                                     new IMobileConnectContract.IMobileConnectCallback()
                                                     {
                                                         @Override
                                                         public void onComplete(MobileConnectStatus mobileConnectStatus)
                                                         {
                                                             handleRedirect(mobileConnectStatus);
                                                         }
                                                     });
    }

    /**
     * Launch the {@link ResultActivity} to display the result
     */
    protected void displayResult()
    {
        final Intent intent = new Intent(this, ResultActivity.class);
        startActivity(intent);
    }

    /**
     * A discovery has been performed. It may or may not be successful. Calling
     * {@link #handleRedirect(MobileConnectStatus)} from within here will determine the next step.
     *
     * @param mobileConnectStatus The result of the discovery.
     */
    @Override
    public void onDiscoveryResponse(final MobileConnectStatus mobileConnectStatus)
    {
        handleRedirect(mobileConnectStatus);
    }

    /**
     * The Authorization performed via the WebView failed.
     */
    @Override
    public void discoveryFailed(final MobileConnectStatus mobileConnectStatus)
    {
        Toast.makeText(this, "Discovery Failed", Toast.LENGTH_SHORT).show();
    }

    /**
     * This is called when the discovery dialog has been dismissed. This is independent of whether the Discovery
     * process was successful or not.
     */
    @Override
    public void onDiscoveryDialogClose()
    {
        Toast.makeText(this, "Dialog Closed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Discovery was complete. Check what to do next.
     *
     * @param mobileConnectStatus The status returned
     */
    @Override
    public void onComplete(final MobileConnectStatus mobileConnectStatus)
    {
        handleRedirect(mobileConnectStatus);
    }

    /**
     * The Authorization performed via the WebView failed.
     *
     * @param mobileConnectStatus A populated {@link MobileConnectStatus} containing the errors.
     */
    @Override
    public void authenticationFailed(final MobileConnectStatus mobileConnectStatus)
    {
        String error = null;

        if (mobileConnectStatus != null)
        {
            error = mobileConnectStatus.getErrorMessage();
        }

        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    /**
     * The Authorization performed via the WebView was a success.
     *
     * @param mobileConnectStatus The status returned from Authorization
     */
    @Override
    public void authenticationSuccess(final MobileConnectStatus mobileConnectStatus)
    {
        handleRedirect(mobileConnectStatus);
    }

    @Override
    public void onAuthenticationDialogClose()
    {
        Toast.makeText(this, "Dialog closed", Toast.LENGTH_SHORT).show();
    }
}