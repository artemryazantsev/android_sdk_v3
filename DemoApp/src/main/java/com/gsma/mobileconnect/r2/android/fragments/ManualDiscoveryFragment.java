package com.gsma.mobileconnect.r2.android.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.activity.ResultsActivity;
import com.gsma.mobileconnect.r2.android.demo.R;
import com.gsma.mobileconnect.r2.android.interfaces.ITitle;
import com.gsma.mobileconnect.r2.android.main.IMobileConnectContract;
import com.gsma.mobileconnect.r2.android.main.MobileConnectAndroidView;
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.constants.Scope;
import com.gsma.mobileconnect.r2.constants.Scopes;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;

public class ManualDiscoveryFragment extends BaseAuthFragment implements ITitle {

    private EditText client_id_field;
    private EditText client_name_field;
    private EditText client_secret_field;
    private EditText client_subscriberId_field;
    private CheckBox encrypted_msisdn;
    private RadioButton r1_SDK;
    private RadioButton r2_SDK;
    private Button send_main_activity_button;

    public static ManualDiscoveryFragment newInstance() {
        return new ManualDiscoveryFragment();
    }

    @Override
    public String getTitle() {
        return "Manual Discovery";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manual_discovery, container, false);

        r1_SDK = (RadioButton) view.findViewById((R.id.radioButtonSDK1));
        r2_SDK = (RadioButton) view.findViewById((R.id.radioButtonSDK2));
        encrypted_msisdn = (CheckBox) view.findViewById((R.id.encrypted_msisdn));
        send_main_activity_button = (Button) view.findViewById((R.id.send_params));
        client_id_field = (EditText) view.findViewById((R.id.client_id_field));
        client_secret_field = (EditText) view.findViewById((R.id.client_secret_field));
        client_subscriberId_field = (EditText) view.findViewById((R.id.client_subscriberId_field));
        client_name_field = (EditText) view.findViewById((R.id.client_name_field));

        r1_SDK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client_name_field.setVisibility(View.GONE);
            }
        });

        r2_SDK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client_name_field.setVisibility(View.VISIBLE);
            }
        });

        send_main_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

        ConnectMobileAndroid();
        return view;
    }

    public void sendRequest() {

        String client_id_value = client_id_field.getText().toString();
        String client_secret_value = client_secret_field.getText().toString();
        String client_subId_value =  encrypted_msisdn.isChecked() ? client_subscriberId_field.getText().toString() : null;
        String providermetadata_value = r2_SDK.isChecked() ? getResources().getString(R.string.providermetadata) : null;
        String client_name_value = r2_SDK.isChecked() ? client_name_field.getText().toString() : "";
        String msisdn_value = getResources().getString(R.string.msisdn);

        MakeManualDiscovery(client_id_value, client_secret_value, client_subId_value,
                providermetadata_value, client_name_value, msisdn_value);
    }

    @Override
    public void onComplete(DiscoveryResponse discoveryResponse) {
        BaseAuthFragment.mobileConnectAndroidView.getPresenter().setDiscoveryResponse(discoveryResponse);
        AuthenticationOptions.Builder authenticationOptionsBuilder;

        if (r1_SDK.isChecked()) {
            authenticationOptionsBuilder = new AuthenticationOptions.Builder()
                    .withScope(Scopes.MOBILECONNECT);
        } else {
            authenticationOptionsBuilder = new AuthenticationOptions.Builder()
                    .withContext("context").withBindingMessage("binding").withScope(Scopes.MOBILECONNECTAUTHENTICATION);
        }

        final MobileConnectRequestOptions.Builder mobileConnectRequestOptionsBuilder = new
                MobileConnectRequestOptions.Builder();

        final MobileConnectRequestOptions mobileConnectRequestOptions = mobileConnectRequestOptionsBuilder
                .withAuthenticationOptions(
                        authenticationOptionsBuilder.build()).build();

        BaseAuthFragment.mobileConnectAndroidView.startAuthentication(discoveryResponse
                        .getResponseData()
                        .getSubscriberId(), "1", "1", mobileConnectRequestOptions,
                new IMobileConnectContract.IMobileConnectCallback() {
                    @Override
                    public void onComplete(final MobileConnectStatus
                                                   mobileConnectStatus) {
                        handleRedirect(mobileConnectStatus);
                    }
                });
    }
}
