package com.gsma.mobileconnect.r2.android.demo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.demo.R;
import com.gsma.mobileconnect.r2.android.demo.activity.MainActivity;
import com.gsma.mobileconnect.r2.android.demo.interfaces.OnBackPressedListener;
import com.gsma.mobileconnect.r2.android.main.IMobileConnectContract;
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.constants.Scopes;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;


public class WithoutDiscoveryAppFragment extends BaseAuthFragment implements OnBackPressedListener {

    android.support.v7.widget.CardView bottomRequestParameters;
    android.support.v7.widget.CardView bottomEndpoints;

    private Button btnRequestParameters;
    private Button btnEndpointConfig;
    private Button btnMobileConnect;

    private CheckBox cbEncryptedMsisdn;

    private RadioButton r1_SDK;
    private RadioButton r2_SDK;

    //Request params
    private EditText etClientId;
    private EditText etClientName;
    private EditText etClientSecret;
    private EditText etClientSubscriberId;

    //Endpoints
    private EditText etEndpointProviderMetadata;
    private EditText etEndpointAuthorizationUrl;
    private EditText etEndpointRequestTokenUrl;
    private EditText etEndpointUserInfoUrl;
    private EditText etEndpointRevokeTokenUrl;


    private BottomSheetBehavior mBottomSheetRequestConfig;
    private BottomSheetBehavior mBottomSheetEndpoints;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_without_discovery_app, container, false);

        init(view);
        checkVisibilityOfElements();
        checkData();

        mBottomSheetRequestConfig = BottomSheetBehavior.from(bottomRequestParameters);
        mBottomSheetEndpoints = BottomSheetBehavior.from(bottomEndpoints);


        btnRequestParameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBottomSheetEndpoints.getState() != mBottomSheetEndpoints.STATE_EXPANDED) {
                    mBottomSheetEndpoints.setState(BottomSheetBehavior.STATE_HIDDEN);
                    if (mBottomSheetRequestConfig.getState() != mBottomSheetRequestConfig.STATE_EXPANDED) {
                        mBottomSheetRequestConfig.setState(mBottomSheetRequestConfig.STATE_EXPANDED);
                    }
                }
            }
        });

        btnEndpointConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (mBottomSheetEndpoints.getState() != mBottomSheetEndpoints.STATE_EXPANDED) {
                        mBottomSheetEndpoints.setState(BottomSheetBehavior.STATE_EXPANDED);
                        if (mBottomSheetRequestConfig.getState() == mBottomSheetRequestConfig.STATE_EXPANDED) {
                            mBottomSheetRequestConfig.setState(mBottomSheetRequestConfig.STATE_HIDDEN);
                    }
                }
            }
        });

        cbEncryptedMsisdn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkVisibilityOfSubscriberId();
            }
        });

        r1_SDK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etClientName.setVisibility(View.GONE);
                etClientName.setHint(getResources().getString(R.string.empty_string));
                checkVisibilityOfEndpoints();
                checkData();
            }
        });



        r2_SDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                etClientName.setVisibility(View.VISIBLE);
                etClientName.setHint(getResources().getString(R.string.client_name_field));
                checkVisibilityOfEndpoints();
                checkData();
            }
        });



        btnMobileConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

        setupHelpListeners();

        connectMobileWithoutDiscovery();

        return view;
    }

    private void sendRequest() {
        Bundle urlConfigs = new Bundle();

        String clientId = etClientId.getText().toString();
        String clientSecret = etClientSecret.getText().toString();
        String subscriberId = cbEncryptedMsisdn.isChecked() ? etClientSubscriberId.getText().toString() : null;
        String clientName = r2_SDK.isChecked() ? etClientName.getText().toString() : getResources().getString(R.string.empty_string);


        if (r1_SDK.isChecked()) {
            urlConfigs.putString(getString(R.string.authorization_url), etEndpointAuthorizationUrl.getText().toString());
            urlConfigs.putString(getString(R.string.requestToken_url), etEndpointRequestTokenUrl.getText().toString());
            urlConfigs.putString(getString(R.string.userInfo_url), etEndpointUserInfoUrl.getText().toString());
            urlConfigs.putString(getString(R.string.revokeToken_url), etEndpointRevokeTokenUrl.getText().toString());
            makeManualDiscovery(clientId, clientSecret, subscriberId,
                    null, clientName, urlConfigs);
        } else {
            makeManualDiscovery(clientId, clientSecret, subscriberId,
                    etEndpointProviderMetadata.getText().toString(), clientName, null);
        }
    }

    /**
     * Initialization of without discovery fragment UI elements
     * @param view
     */
    private void init(View view) {
        bottomRequestParameters = (android.support.v7.widget.CardView)view.findViewById(R.id.bottom_sheet_request_params);
        bottomEndpoints = (android.support.v7.widget.CardView)view.findViewById(R.id.bottom_sheet_endpoints);
        btnRequestParameters = (Button)view.findViewById(R.id.btnRequestParameters);
        btnEndpointConfig = (Button)view.findViewById(R.id.btnEndpoints);
        btnMobileConnect = (Button)view.findViewById(R.id.btnMCWithoutDiscovery);

        cbEncryptedMsisdn = (CheckBox) view.findViewById((R.id.encrypted_msisdn));
        r1_SDK = (RadioButton) view.findViewById((R.id.radioButtonSDK1));
        r2_SDK = (RadioButton) view.findViewById((R.id.radioButtonSDK2));

        etClientId = (EditText)view.findViewById(R.id.client_id_field);
        etClientName = (EditText)view.findViewById(R.id.client_name_field);
        etClientSecret = (EditText)view.findViewById(R.id.client_secret_field);
        etClientSubscriberId = (EditText)view.findViewById(R.id.client_subscriberId_field);

        etEndpointProviderMetadata = (EditText)view.findViewById(R.id.endpoint_provider_metadata);
        etEndpointAuthorizationUrl = (EditText)view.findViewById(R.id.endpoint_authorization_url);
        etEndpointRequestTokenUrl = (EditText)view.findViewById(R.id.endpoint_request_token_url);
        etEndpointUserInfoUrl = (EditText)view.findViewById(R.id.endpoint_user_info_url);
        etEndpointRevokeTokenUrl = (EditText)view.findViewById(R.id.endpoint_revoke_token_url);

    }
    private void checkVisibilityOfElements () {
        checkVisibilityOfEndpoints();
        checkVisibilityOfSubscriberId();
    }

    private void checkVisibilityOfEndpoints() {
        if (r2_SDK.isChecked()) {
            etEndpointProviderMetadata.setVisibility(View.VISIBLE);
            etEndpointAuthorizationUrl.setVisibility(View.GONE);
            etEndpointRequestTokenUrl.setVisibility(View.GONE);
            etEndpointUserInfoUrl.setVisibility(View.GONE);
            etEndpointRevokeTokenUrl.setVisibility(View.GONE);

        } else {
            etEndpointProviderMetadata.setVisibility(View.GONE);
            etEndpointAuthorizationUrl.setVisibility(View.VISIBLE);
            etEndpointRequestTokenUrl.setVisibility(View.VISIBLE);
            etEndpointUserInfoUrl.setVisibility(View.VISIBLE);
            etEndpointRevokeTokenUrl.setVisibility(View.VISIBLE);
        }
    }

    private void checkVisibilityOfSubscriberId () {
        if (cbEncryptedMsisdn.isChecked()) {
            etClientSubscriberId.setVisibility(View.VISIBLE);
        } else {
            etClientSubscriberId.setVisibility(View.GONE);
        }
    }

    private void checkData () {
        if (r2_SDK.isChecked()) {
            etClientId.setText(getResources().getString(R.string.client_id_v2));
            etClientSecret.setText(getResources().getString(R.string.client_secret_v2));
            etClientSubscriberId.setText(getResources().getString(R.string.client_subId_v2));
            etClientName.setText(getResources().getString(R.string.client_name));
        } else {
            etClientId.setText(getResources().getString(R.string.client_id_v1));
            etClientSecret.setText(getResources().getString(R.string.client_secret_v1));
            etClientSubscriberId.setText(getResources().getString(R.string.client_subId_v1));
        }
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
                    .withContext(getString(R.string.context)).withBindingMessage(getString(R.string.binding)).withScope(Scopes.MOBILECONNECTAUTHENTICATION);
        }

        final MobileConnectRequestOptions.Builder mobileConnectRequestOptionsBuilder = new
                MobileConnectRequestOptions.Builder();

        final MobileConnectRequestOptions mobileConnectRequestOptions = mobileConnectRequestOptionsBuilder
                .withAuthenticationOptions(
                        authenticationOptionsBuilder.build()).build();

        BaseAuthFragment.mobileConnectAndroidView.startAuthentication(discoveryResponse
                        .getResponseData()
                        .getSubscriberId(), getString(R.string.number_one), getString(R.string.number_one), mobileConnectRequestOptions,
                new IMobileConnectContract.IMobileConnectCallback() {
                    @Override
                    public void onComplete(final MobileConnectStatus
                                                   mobileConnectStatus) {
                        handleRedirect(mobileConnectStatus);
                    }
                });
    }


    @Override
    public void onBackPressed() {

        if (mBottomSheetRequestConfig.getState() == mBottomSheetRequestConfig.STATE_EXPANDED) {
            mBottomSheetRequestConfig.setState(mBottomSheetRequestConfig.STATE_HIDDEN);
        }
        else if (mBottomSheetEndpoints.getState() == mBottomSheetEndpoints.STATE_EXPANDED) {
            mBottomSheetEndpoints.setState(mBottomSheetEndpoints.STATE_HIDDEN);
        } else {
            getActivity().onBackPressed();
        }
    }

    private void setupHelpListeners () {


        cbEncryptedMsisdn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((MainActivity)getActivity()).makeToast(getString(R.string.help__encrypted_msisdn), true);
                return true;
            }
        });


        r2_SDK.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((MainActivity)getActivity()).makeAlert(getString(R.string.providermetadata_title), getString(R.string.help_provider_metadata));
                return true;
            }
        });

    }
}
