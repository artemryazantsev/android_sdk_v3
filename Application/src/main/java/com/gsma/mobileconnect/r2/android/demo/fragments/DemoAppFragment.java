package com.gsma.mobileconnect.r2.android.demo.fragments;


import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;


import com.gsma.mobileconnect.r2.android.demo.R;
import com.gsma.mobileconnect.r2.android.demo.activity.MainActivity;
import com.gsma.mobileconnect.r2.android.demo.interfaces.OnBackPressedListener;
import com.gsma.mobileconnect.r2.constants.Scopes;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;

import com.gsma.mobileconnect.r2.android.demo.utils.IpUtils;

public class DemoAppFragment extends BaseAuthFragment implements OnBackPressedListener{

    private android.support.v7.widget.CardView bottomRequestOptions;
    private android.support.v7.widget.CardView bottomRequestParams;
    private BottomSheetBehavior mBottomSheetOptionsConfig;
    private BottomSheetBehavior mBottomSheetParamsConfig;

    private Button btnRequestOptions;
    private Button btnRequestParams;

    private Switch switchAddress;
    private Switch switchEmail;
    private Switch switchPhone;
    private Switch switchProfile;
    private Switch switchNationality;
    private Switch switchPhoneNumber;
    private Switch switchSingUp;

    private RadioButton rbAuthentication;
    private RadioButton rbAuthorization;

    private TextInputEditText etMsisdn;
    private TextInputLayout msisdnLayout;

    private CheckBox cbMsisdn;

    private EditText etClientId;
    private EditText etClientSecret;
    private EditText etDiscoveryUrl;
    private EditText etRedirectUrl;
    private EditText etXRedirect;

    private Button btnMobileConnect;

    private String authType;

    private TextInputEditText etIp;
    private TextInputLayout ipLayout;
    private CheckBox cbXSourceIp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_demo_app, container, false);

        init(view);
        connectMobileDemo();

        checkVisibilityOfSwitchesAndAuthType();

        btnRequestOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetOptionsConfig.getState() != mBottomSheetOptionsConfig.STATE_EXPANDED) {
                    mBottomSheetOptionsConfig.setState(mBottomSheetOptionsConfig.STATE_EXPANDED);
                }
                else {
                    mBottomSheetOptionsConfig.setState(mBottomSheetOptionsConfig.STATE_HIDDEN);
                }


            }
        });

        btnRequestParams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetParamsConfig.setState(mBottomSheetParamsConfig.STATE_EXPANDED);
            }
        });




        rbAuthorization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkVisibilityOfSwitchesAndAuthType();
            }
        });

        rbAuthentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkVisibilityOfSwitchesAndAuthType();
            }
        });

        cbMsisdn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (cbMsisdn.isChecked()) {
                    msisdnLayout.setVisibility(View.VISIBLE);
                    etMsisdn.setText(getResources().getString(R.string.msisdn));
                } else {
                    msisdnLayout.setVisibility(View.GONE);
                    etMsisdn.setText(null);
                }
            }
        });

        cbXSourceIp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (cbXSourceIp.isChecked()) {
                    ipLayout.setVisibility(View.VISIBLE);
                    etIp.setText(IpUtils.getIPAddress(true));
                } else {
                    ipLayout.setVisibility(View.GONE);
                    etIp.setText(null);
                }
            }
        });

        btnMobileConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();
            }
        });

        setupHelpListeners();

        return view;
    }



    private void init(View view) {

        bottomRequestOptions = (android.support.v7.widget.CardView)view.findViewById(R.id.bottom_sheet_demo_app_options);
        bottomRequestParams = (android.support.v7.widget.CardView)view.findViewById(R.id.bottom_sheet_demo_app_params);
        mBottomSheetOptionsConfig = BottomSheetBehavior.from(bottomRequestOptions);
        mBottomSheetParamsConfig = BottomSheetBehavior.from(bottomRequestParams);
        btnRequestOptions = (Button)view.findViewById(R.id.btnRequestOptions);
        btnRequestParams = (Button)view.findViewById(R.id.btnRequestParametersDemo);
        switchAddress = (Switch)view.findViewById(R.id.switch_address);
        switchEmail = (Switch)view.findViewById(R.id.switch_email);
        switchPhone = (Switch)view.findViewById(R.id.switch_phone);
        switchProfile = (Switch)view.findViewById(R.id.switch_profile);
        switchNationality = (Switch)view.findViewById(R.id.switch_nationality);
        switchPhoneNumber = (Switch)view.findViewById(R.id.switch_phone_number);
        switchSingUp = (Switch)view.findViewById(R.id.switch_sign_up);
        rbAuthentication = (RadioButton)view.findViewById(R.id.RBauthentication);
        rbAuthorization = (RadioButton)view.findViewById(R.id.RBauthorization);

        etMsisdn = (TextInputEditText) view.findViewById(R.id.etMsisdnDemo);
        msisdnLayout = (TextInputLayout) view.findViewById(R.id.layoutMsisdnDemo);
        cbMsisdn = (CheckBox)view.findViewById(R.id.encrypted_msisdn);

        btnMobileConnect = (Button)view.findViewById(R.id.btnMCDemo);

        etClientId = (EditText)view.findViewById(R.id.client_id_field);
        etClientSecret = (EditText)view.findViewById(R.id.client_secret_field);
        etDiscoveryUrl = (EditText)view.findViewById(R.id.client_discovery_url);
        etRedirectUrl = (EditText)view.findViewById(R.id.client_redirect_url);
        etXRedirect = (EditText)view.findViewById(R.id.client_x_redirect);

        etClientId.setText(getResources().getString(R.string.client_id));
        etClientSecret.setText(getResources().getString(R.string.client_secret));
        etDiscoveryUrl.setText(getResources().getString(R.string.discovery_url));
        etRedirectUrl.setText(getResources().getString(R.string.redirect_url));
        etXRedirect.setText(getResources().getString(R.string.x_redirect));

        cbXSourceIp = (CheckBox)view.findViewById(R.id.cbIp);
        etIp = (TextInputEditText) view.findViewById(R.id.etIpDemo);
        ipLayout = (TextInputLayout) view.findViewById(R.id.layoutIpDemo);

    }

    private void sendRequest () {

        String msisdn = null;
        String xSourceIp = null;

        if (cbMsisdn.isChecked()) {
            msisdn = etMsisdn.getText().toString();
        }

        if (cbXSourceIp.isChecked()) {
            xSourceIp = etIp.getText().toString();
        }

        final StringBuilder scopesStringBuilder = new StringBuilder(authType);

        if (authType.equals(Scopes.MOBILECONNECTAUTHENTICATION)) {
            setUserInfoScopes(scopesStringBuilder);
        }

        if (authType.equals(Scopes.MOBILECONNECTAUTHORIZATION)) {
            setIdentityScopes(scopesStringBuilder);
        }

        scopes = scopesStringBuilder;

        makeDiscoveryDemo(msisdn, xSourceIp);
    }


    private void setUserInfoScopes(StringBuilder scopesStringBuilder) {
        if (switchAddress != null && switchAddress.isChecked()) {
            scopesStringBuilder.append(" address");
        }
        if (switchEmail != null && switchEmail.isChecked()) {
            scopesStringBuilder.append(" email");
        }
        if (switchPhone != null && switchPhone.isChecked()) {
            scopesStringBuilder.append(" phone");
        }
        if (switchProfile != null && switchProfile.isChecked()) {
            scopesStringBuilder.append(" profile");
        }
    }

    private void setIdentityScopes(StringBuilder scopesStringBuilder) {
        if (switchNationality != null && switchNationality.isChecked()) {
            scopesStringBuilder.append(String.format(" %s", Scopes.MOBILECONNECTIDENTITYNATIONALID));
        }
        if (switchPhoneNumber != null && switchPhoneNumber.isChecked()) {
            scopesStringBuilder.append(String.format(" %s", Scopes.MOBILECONNECTIDENTITYPHONE));
        }
        if (switchSingUp != null && switchSingUp.isChecked()) {
            scopesStringBuilder.append(String.format(" %s", Scopes.MOBILECONNECTIDENTITYSIGNUP));
        }
    }

    private void checkVisibilityOfSwitchesAndAuthType() {
        if(rbAuthentication.isChecked()) {
            switchNationality.setVisibility(View.GONE);
            switchPhoneNumber.setVisibility(View.GONE);
            switchSingUp.setVisibility(View.GONE);

            switchNationality.setChecked(false);
            switchPhoneNumber.setChecked(false);
            switchSingUp.setChecked(false);
            switchAddress.setVisibility(View.VISIBLE);
            switchEmail.setVisibility(View.VISIBLE);
            switchPhone.setVisibility(View.VISIBLE);
            switchProfile.setVisibility(View.VISIBLE);
            authType = Scopes.MOBILECONNECTAUTHENTICATION;
        } else {
            switchNationality.setVisibility(View.VISIBLE);
            switchPhoneNumber.setVisibility(View.VISIBLE);
            switchSingUp.setVisibility(View.VISIBLE);


            switchAddress.setChecked(false);
            switchEmail.setChecked(false);
            switchPhone.setChecked(false);
            switchProfile.setChecked(false);
            switchAddress.setVisibility(View.GONE);
            switchEmail.setVisibility(View.GONE);
            switchPhone.setVisibility(View.GONE);
            switchProfile.setVisibility(View.GONE);
            authType = Scopes.MOBILECONNECTAUTHORIZATION;
        }
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetOptionsConfig.getState() == mBottomSheetOptionsConfig.STATE_EXPANDED) {
            mBottomSheetOptionsConfig.setState(mBottomSheetOptionsConfig.STATE_HIDDEN);
        }
        else if (mBottomSheetParamsConfig.getState() == mBottomSheetParamsConfig.STATE_EXPANDED) {
            mBottomSheetParamsConfig.setState(mBottomSheetParamsConfig.STATE_HIDDEN);
        } else {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onComplete(DiscoveryResponse discoveryResponse) {
        super.onComplete(mobileConnectStatus);
    }

    private void setupHelpListeners () {

        cbXSourceIp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((MainActivity)getActivity()).makeToast(getString(R.string.help_x_source_ip), true);
                return true;
            }
        });

        cbMsisdn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((MainActivity)getActivity()).makeToast(getString(R.string.help_msisdn), true);
                return true;
            }
        });


        rbAuthentication.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((MainActivity)getActivity()).makeAlert(getString(R.string.authentication), getString(R.string.help_authentication));
                return true;
            }
        });

        rbAuthorization.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((MainActivity)getActivity()).makeAlert(getString(R.string.authorization), getString(R.string.help_authorization));
                return true;
            }
        });

    }
}
