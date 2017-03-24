package com.gsma.mobileconnect.r2.android.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.View;
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
import com.gsma.mobileconnect.r2.MobileConnectWebInterface;
import com.gsma.mobileconnect.r2.android.activity.MainActivity;
import com.gsma.mobileconnect.r2.android.activity.ResultsActivity;
import com.gsma.mobileconnect.r2.android.compatibility.AndroidMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.android.demo.R;
import com.gsma.mobileconnect.r2.android.interfaces.AuthenticationListener;
import com.gsma.mobileconnect.r2.android.interfaces.DiscoveryListener;
import com.gsma.mobileconnect.r2.android.main.IMobileConnectContract;
import com.gsma.mobileconnect.r2.android.main.MobileConnectAndroidView;
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;
import com.gsma.mobileconnect.r2.discovery.OperatorUrls;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public abstract class BaseAuthFragment extends Fragment implements DiscoveryListener,
        AuthenticationListener,
        IMobileConnectContract.IMobileConnectCallback,
        IMobileConnectContract.IMobileConnectCallbackManually {

    public static MobileConnectAndroidView mobileConnectAndroidView;

    protected MobileConnectConfig mobileConnectConfig;

    public static MobileConnectStatus mobileConnectStatus;


    public void ConnectMobileAndroid() {
        URI discoveryUri = null;
        URI redirectUri = null;

        try {
            discoveryUri = new URI(getString(R.string.discovery_url));
            redirectUri = new URI(getString(R.string.redirect_url));
        } catch (URISyntaxException e) {
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

        MobileConnectWebInterface mobileConnectWebInterface = mobileConnect.getMobileConnectWebInterface();
        MobileConnectInterface mobileConnectInterface = mobileConnect.getMobileConnectInterface();

        mobileConnectAndroidView = new MobileConnectAndroidView(mobileConnectWebInterface, mobileConnectInterface);
        mobileConnectAndroidView.initialise();
    }

    @Override
    public void onStart() {
        super.onStart();
        mobileConnectAndroidView.initialise();
    }

    @Override
    public void onStop() {
        mobileConnectAndroidView.cleanUp();
        super.onStop();
    }

    public void MakeManualDiscovery(String client_id, String client_secret,
                                    String client_subId, String providermetadata,
                                    String client_name, String msisdn, Bundle urlConfigs) {
        final DiscoveryOptions.Builder discoveryOptionsBuilder = new DiscoveryOptions.Builder();

        discoveryOptionsBuilder.withRedirectUrl(mobileConnectConfig.getRedirectUrl());

        OperatorUrls operator_url;
        if(providermetadata != null) {
            operator_url = new OperatorUrls.Builder().withProviderMetadataUri(providermetadata)
                    .build();
        }else{
            operator_url  = new OperatorUrls.Builder()
                    .withAuthorizationUrl(urlConfigs.getString("authorization_url"))
                    .withRequestTokenUrl(urlConfigs.getString("requestToken_url"))
                    .withUserInfoUrl(urlConfigs.getString("userInfo_url"))
                    .withRevokeTokenUrl(urlConfigs.getString("revokeToken_url"))
                    .build();
        }

        mobileConnectAndroidView.generateDiscoveryManually(client_secret, client_id,
                client_subId, client_name, operator_url, this);
    }

    /**
     * This should be called every time after calling any API from the
     * {@link MobileConnectAndroidView}. It interrogates the
     * {@link com.gsma.mobileconnect.r2.MobileConnectStatus.ResponseType} object from within the
     * {@link MobileConnectStatus} object and calls the correct API.
     *
     * @param mobileConnectStatus The status to be interrogated.
     */
    protected void handleRedirect(final MobileConnectStatus mobileConnectStatus) {
        final String state =
                mobileConnectStatus.getState() == null ? UUID.randomUUID().toString() : mobileConnectStatus.getState();
        final String nonce =
                mobileConnectStatus.getNonce() == null ? UUID.randomUUID().toString() : mobileConnectStatus.getNonce();

        switch (mobileConnectStatus.getResponseType()) {
            case ERROR: {
                showAlertMessage("Error", String.format("Error appeared - %s", mobileConnectStatus.getErrorMessage()));
                break;
            }
            case AUTHENTICATION: {
                mobileConnectAndroidView.attemptAuthenticationWithWebView(getActivity(),
                        this,
                        mobileConnectStatus.getUrl(),
                        state,
                        nonce,
                        null);
                break;
            }
            case COMPLETE: {
                BaseAuthFragment.mobileConnectStatus = mobileConnectStatus;
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
    protected void startAuthentication(@NonNull final MobileConnectStatus mobileConnectStatus,
                                     @Nullable final MobileConnectRequestOptions mobileConnectRequestOptions,
                                     @NonNull final String state,
                                     @NonNull final String nonce) {
        mobileConnectAndroidView.startAuthentication(mobileConnectStatus.getDiscoveryResponse()
                        .getResponseData()
                        .getSubscriberId(),
                state,
                nonce,
                mobileConnectRequestOptions,
                new IMobileConnectContract.IMobileConnectCallback() {
                    @Override
                    public void onComplete(final MobileConnectStatus
                                                   mobileConnectStatus) {
                        handleRedirect(mobileConnectStatus);
                    }
                });
    }

    /**
     * Launch the {@link ResultsActivity} to display the result
     */
    protected void displayResult() {
        Intent intent = new Intent(getActivity(), ResultsActivity.class);
        startActivity(intent);
    }

    /**
     * A discovery has been performed. It may or may not be successful. Calling
     * {@link #handleRedirect(MobileConnectStatus)} from within here will determine the next step.
     *
     * @param mobileConnectStatus The result of the discovery.
     */
    @Override
    public void onDiscoveryResponse(final MobileConnectStatus mobileConnectStatus) {
        handleRedirect(mobileConnectStatus);
    }

    /**
     * The Authorization performed via the WebView failed.
     */
    @Override
    public void discoveryFailed(final MobileConnectStatus mobileConnectStatus) {
        showAlertMessage("Error", "Discovery Failed");
    }

    /**
     * This is called when the discovery dialog has been dismissed. This is independent of whether the Discovery
     * process was successful or not.
     */
    @Override
    public void onDiscoveryDialogClose() {
        Toast.makeText(getActivity(), "Dialog Closed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Discovery was complete. Check what to do next.
     *
     * @param mobileConnectStatus The status returned
     */
    @Override
    public void onComplete(final MobileConnectStatus mobileConnectStatus) {
        handleRedirect(mobileConnectStatus);
    }

    /**
     * The Authorization performed via the WebView failed.
     *
     * @param mobileConnectStatus A populated {@link MobileConnectStatus} containing the errors.
     */
    @Override
    public void authenticationFailed(final MobileConnectStatus mobileConnectStatus) {
        String error = null;

        if (mobileConnectStatus != null) {
            error = mobileConnectStatus.getErrorMessage();
        }

        showAlertMessage("Error", error);
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    /**
     * The Authorization performed via the WebView was a success.
     *
     * @param mobileConnectStatus The status returned from Authorization
     */
    @Override
    public void authenticationSuccess(final MobileConnectStatus mobileConnectStatus) {
        handleRedirect(mobileConnectStatus);
    }

    @Override
    public void onAuthenticationDialogClose() {
        Toast.makeText(getActivity(), "Dialog closed", Toast.LENGTH_SHORT).show();

    }

    private void showAlertMessage(String title, String message)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}