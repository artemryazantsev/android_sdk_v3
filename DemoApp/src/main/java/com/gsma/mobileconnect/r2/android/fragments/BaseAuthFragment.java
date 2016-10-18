package com.gsma.mobileconnect.r2.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.gsma.mobileconnect.r2.MobileConnect;
import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.activity.ResultActivity;
import com.gsma.mobileconnect.r2.android.compatibility.AndroidMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.android.demo.R;
import com.gsma.mobileconnect.r2.android.interfaces.AuthenticationListener;
import com.gsma.mobileconnect.r2.android.interfaces.DiscoveryListener;
import com.gsma.mobileconnect.r2.android.main.IMobileConnectContract;
import com.gsma.mobileconnect.r2.android.main.MobileConnectAndroidView;
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.constants.Scopes;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * The base class for
 * {@link AuthenticationFragment} and  {@link AuthorizationFragment} containing common API calls to
 * {@link MobileConnectAndroidView}
 * <p/>
 */
public abstract class BaseAuthFragment extends Fragment implements DiscoveryListener,
                                                                   AuthenticationListener,
                                                                   IMobileConnectContract.IMobileConnectCallback
{
    // Views
    protected Button goButton;

    protected CheckBox msisdnCheckBox;

    protected TextInputLayout msisdnTextInputLayout;

    protected TextInputEditText msisdnTextInputEditText;

    protected Switch addressSwitch;

    protected Switch emailSwitch;

    protected Switch phoneSwitch;

    protected Switch profileSwitch;

    protected Switch nationalitySwitch;

    protected Switch phoneNumberSwitch;

    protected Switch signUpSwitch;

    public static MobileConnectAndroidView mobileConnectAndroidView;

    protected MobileConnectConfig mobileConnectConfig;

    private String authType;

    public static MobileConnectStatus mobileConnectStatus;

    /**
     * Sets-up the {@link BaseAuthFragment#mobileConnectAndroidView} with the configuration based on the values in
     * strings.xml
     * <p/>
     * This should be called from the {@link Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)} and after the
     * layout has been set.
     */
    protected void setupUIAndMobileConnectAndroid(final View view, final String authType)
    {
        this.authType = authType;

        setupUI(view);

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

        MobileConnect mobileConnect = new MobileConnect.Builder(mobileConnectConfig,
                                                                new AndroidMobileConnectEncodeDecoder()).build();

        MobileConnectInterface mobileConnectInterface = mobileConnect.getMobileConnectInterface();

        mobileConnectAndroidView = new MobileConnectAndroidView(mobileConnectInterface);
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
     * Bind the views from the layout and add click-event listener to the Go Button.
     *
     * @param view The Layout which is being inflated for the relevant fragment
     */
    private void setupUI(final View view)
    {
        goButton = (Button) view.findViewById(R.id.button_go);
        msisdnCheckBox = (CheckBox) view.findViewById(R.id.check_box_msisdn);
        msisdnTextInputLayout = (TextInputLayout) view.findViewById(R.id.text_input_layout_msisdn);
        msisdnTextInputEditText = (TextInputEditText) view.findViewById(R.id.text_input_edit_text_msisdn);
        addressSwitch = (Switch) view.findViewById(R.id.switch_address);
        emailSwitch = (Switch) view.findViewById(R.id.switch_email);
        phoneSwitch = (Switch) view.findViewById(R.id.switch_phone);
        profileSwitch = (Switch) view.findViewById(R.id.switch_profile);
        nationalitySwitch = (Switch) view.findViewById(R.id.switch_nationality);
        phoneNumberSwitch = (Switch) view.findViewById(R.id.switch_phone_number);
        signUpSwitch = (Switch) view.findViewById(R.id.switch_sign_up);

        goButton.setOnClickListener(new View.OnClickListener()
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

                mobileConnectAndroidView.attemptDiscovery(msisdn, null, null, requestOptions, BaseAuthFragment.this);
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
                Toast.makeText(getActivity(),
                               String.format("Error - %s", mobileConnectStatus.getErrorMessage()),
                               Toast.LENGTH_LONG).show();
                break;
            }
            case OPERATOR_SELECTION:
            {
                mobileConnectAndroidView.attemptDiscoveryWithWebView(getActivity(),
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

                final StringBuilder stringBuilder = new StringBuilder(authType);

                populateUserInfoScopes(stringBuilder);

                if (authType.equals(Scopes.MOBILECONNECTAUTHORIZATION))
                {
                    populateIdentityScopes(stringBuilder);
                }

                authenticationOptionsBuilder.withScope(stringBuilder.toString());

                final MobileConnectRequestOptions mobileConnectRequestOptions = mobileConnectRequestOptionsBuilder
                        .withAuthenticationOptions(
                        authenticationOptionsBuilder.build()).build();

                startAuthentication(mobileConnectStatus, mobileConnectRequestOptions, state, nonce);
                break;
            }
            case AUTHENTICATION:
            {
                mobileConnectAndroidView.attemptAuthenticationWithWebView(getActivity(),
                                                                          this,
                                                                          mobileConnectStatus.getUrl(),
                                                                          state,
                                                                          nonce,
                                                                          null);
                break;
            }
            case COMPLETE:
            {
                BaseAuthFragment.mobileConnectStatus = mobileConnectStatus;
                displayResult();
                break;
            }
        }
    }

    private void populateIdentityScopes(StringBuilder stringBuilder)
    {
        if (nationalitySwitch != null && nationalitySwitch.isChecked())
        {
            stringBuilder.append(String.format(" %s", Scopes.MOBILECONNECTIDENTITYNATIONALID));
        }
        if (phoneNumberSwitch != null && phoneNumberSwitch.isChecked())
        {
            stringBuilder.append(String.format(" %s", Scopes.MOBILECONNECTIDENTITYPHONE));
        }
        if (signUpSwitch != null && signUpSwitch.isChecked())
        {
            stringBuilder.append(String.format(" %s", Scopes.MOBILECONNECTIDENTITYSIGNUP));
        }
    }

    private void populateUserInfoScopes(StringBuilder stringBuilder)
    {
        if (addressSwitch != null && addressSwitch.isChecked())
        {
            stringBuilder.append(" address");
        }
        if (emailSwitch != null && emailSwitch.isChecked())
        {
            stringBuilder.append(" email");
        }
        if (phoneSwitch != null && phoneSwitch.isChecked())
        {
            stringBuilder.append(" phone");
        }
        if (profileSwitch != null && profileSwitch.isChecked())
        {
            stringBuilder.append(" profile");
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
                                                         public void onComplete(final MobileConnectStatus
                                                                                        mobileConnectStatus)
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
        final Intent intent = new Intent(getActivity(), ResultActivity.class);

        boolean anySwitchesOn = false;

        if (authType.equals(Scopes.MOBILECONNECTAUTHENTICATION))
        {
            if (addressSwitch.isChecked() || profileSwitch.isChecked() || phoneSwitch.isChecked() ||
                emailSwitch.isChecked())
            {
                anySwitchesOn = true;
            }
        }
        else
        {
            if (nationalitySwitch.isChecked() || signUpSwitch.isChecked() ||
                phoneNumberSwitch.isChecked())
            {
                anySwitchesOn = true;
            }
        }

        intent.putExtra("anySwitchesOn", anySwitchesOn);

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
        Toast.makeText(getActivity(), "Discovery Failed", Toast.LENGTH_SHORT).show();
    }

    /**
     * This is called when the discovery dialog has been dismissed. This is independent of whether the Discovery
     * process was successful or not.
     */
    @Override
    public void onDiscoveryDialogClose()
    {
        Toast.makeText(getActivity(), "Dialog Closed", Toast.LENGTH_SHORT).show();
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

        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getActivity(), "Dialog closed", Toast.LENGTH_SHORT).show();
    }
}