package com.gsma.mobileconnect.r2.android.demo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.gsma.mobileconnect.r2.android.demo.R;
import com.gsma.mobileconnect.r2.android.demo.interfaces.OnBackPressedListener;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;


public class IndianDemoAppFragment extends BaseAuthFragment implements OnBackPressedListener {


    private android.support.v7.widget.CardView indianBottomRequestOptions;
    private android.support.v7.widget.CardView indianBottomRequestParams;
    private BottomSheetBehavior mBottomSheetIndianOptionsConfig;
    private BottomSheetBehavior mBottomSheetIndianParamsConfig;
    private RadioButton rbIndianMsisdn;
    private RadioButton rbIndianMccMnc;
    private RadioButton rbIndianNone;
    private RadioButton rbOpenid;
    private RadioButton rbMcIndiaTc;
    private RadioButton rbMcMnvValidate;
    private RadioButton rbMcMnvValidatePlus;

    private TextInputEditText tvIndianMsisdn;
    private TextInputEditText tvIndianMcc;
    private TextInputEditText tvIndianMnc;
    private LinearLayout layoutIndianMsisdn;
    private LinearLayout layoutIndianMcc;
    private LinearLayout layoutIndianMnc;


    private Button btnIndianRequestOptions;
    private Button btnIndianRequestParams;

    private Button btnIndianMobileConnect;


    private EditText etIndianClientId;
    private EditText etIndianClientSecret;
    private EditText etIndianDiscoveryUrl;
    private EditText etIndianRedirectUrl;
    private EditText etIndianXRedirect;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_indian_demo_app, container, false);
        init(view);
        connectMobileIndian();
        btnIndianRequestOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetIndianOptionsConfig.getState() != mBottomSheetIndianOptionsConfig.STATE_EXPANDED) {
                    mBottomSheetIndianOptionsConfig.setState(mBottomSheetIndianOptionsConfig.STATE_EXPANDED);
                }
                else {
                    mBottomSheetIndianOptionsConfig.setState(mBottomSheetIndianOptionsConfig.STATE_HIDDEN);
                }
            }
        });

        btnIndianRequestParams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetIndianParamsConfig.setState(mBottomSheetIndianParamsConfig.STATE_EXPANDED);
            }
        });

        rbIndianMsisdn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkRadioButtonsAndSetView(view);
            }
        });

        rbIndianMccMnc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkRadioButtonsAndSetView(view);
            }
        });

        rbIndianNone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkRadioButtonsAndSetView(view);
            }
        });

        btnIndianMobileConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

        return view;
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetIndianOptionsConfig.getState() == mBottomSheetIndianOptionsConfig.STATE_EXPANDED) {
            mBottomSheetIndianOptionsConfig.setState(mBottomSheetIndianOptionsConfig.STATE_HIDDEN);
        }
        else if (mBottomSheetIndianParamsConfig.getState() == mBottomSheetIndianParamsConfig.STATE_EXPANDED) {
            mBottomSheetIndianParamsConfig.setState(mBottomSheetIndianParamsConfig.STATE_HIDDEN);
        } else {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onComplete(DiscoveryResponse discoveryResponse) {
        super.onComplete(mobileConnectStatus);
    }

    private void checkRadioButtonsAndSetView (View view) {
        if (rbIndianMsisdn.isChecked()) {
            layoutIndianMsisdn.setVisibility(View.VISIBLE);
            layoutIndianMcc.setVisibility(View.GONE);
            layoutIndianMnc.setVisibility(View.GONE);
        } else if (rbIndianMccMnc.isChecked()){
            layoutIndianMsisdn.setVisibility(View.GONE);
            layoutIndianMcc.setVisibility(View.VISIBLE);
            layoutIndianMnc.setVisibility(View.VISIBLE);
        } else {
            layoutIndianMsisdn.setVisibility(View.GONE);
            layoutIndianMcc.setVisibility(View.GONE);
            layoutIndianMnc.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void init(View view) {
        indianBottomRequestOptions = (android.support.v7.widget.CardView)view.findViewById(R.id.bottom_sheet_indian_demo_app_options);
        indianBottomRequestParams = (android.support.v7.widget.CardView)view.findViewById(R.id.bottom_sheet_indian_demo_app_params);
        mBottomSheetIndianOptionsConfig = BottomSheetBehavior.from(indianBottomRequestOptions);
        mBottomSheetIndianParamsConfig = BottomSheetBehavior.from(indianBottomRequestParams);
        rbIndianMsisdn = (RadioButton)view.findViewById(R.id.rbIndianMsisdn);
        rbIndianMccMnc = (RadioButton)view.findViewById(R.id.rbIndianMccMnc);
        rbIndianNone = (RadioButton)view.findViewById(R.id.rbIndianNone);

        rbOpenid = (RadioButton)view.findViewById(R.id.rb_openid);
        rbMcIndiaTc = (RadioButton)view.findViewById(R.id.rb_mc_india_tc);
        rbMcMnvValidate = (RadioButton)view.findViewById(R.id.rb_mc_mnv_validate);
        rbMcMnvValidatePlus = (RadioButton)view.findViewById(R.id.rb_mc_mnv_validate_plus);

        tvIndianMsisdn = (TextInputEditText) view.findViewById(R.id.etIndianMsisdn);
        tvIndianMcc = (TextInputEditText)view.findViewById(R.id.etIndianMcc);
        tvIndianMnc = (TextInputEditText)view.findViewById(R.id.etIndianMnc);

        layoutIndianMsisdn = (LinearLayout)view.findViewById(R.id.layoutIndianMsisdn);
        layoutIndianMcc = (LinearLayout)view.findViewById(R.id.layoutIndianMcc);
        layoutIndianMnc = (LinearLayout)view.findViewById(R.id.layoutIndianMnc);

        btnIndianRequestOptions = (Button)view.findViewById(R.id.btnIndianRequestOptions);
        btnIndianRequestParams = (Button)view.findViewById(R.id.btnIndianRequestParameters);

        btnIndianMobileConnect = (Button)view.findViewById(R.id.btnIndianMobileConnect);

        etIndianClientId = (EditText)view.findViewById(R.id.indian_client_id_field);
        etIndianClientSecret = (EditText)view.findViewById(R.id.indian_client_secret_field);
        etIndianDiscoveryUrl = (EditText)view.findViewById(R.id.indian_client_discovery_url);
        etIndianRedirectUrl = (EditText)view.findViewById(R.id.indian_client_redirect_url);
        etIndianXRedirect = (EditText)view.findViewById(R.id.indian_client_x_redirect);

        etIndianClientId.setText(getString(R.string.indian_client_id));
        etIndianClientSecret.setText(getString(R.string.indian_client_secret));
        etIndianDiscoveryUrl.setText(getString(R.string.indian_discovery_url));
        etIndianRedirectUrl.setText(getString(R.string.indian_redirect_url));
        etIndianXRedirect.setText(getString(R.string.indian_x_redirect));


        tvIndianMsisdn.setText(getString(R.string.indianCode) + getString(R.string.indianMsisdn));
        tvIndianMsisdn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().contains(getString(R.string.indianCode))){
                    tvIndianMsisdn.setText(getString(R.string.indianCode));
                    Selection.setSelection(tvIndianMsisdn.getText(), tvIndianMsisdn.getText().length());

                }
            }
        });

    }

    private void sendRequest() {
        scopes = getScopes();
        if (rbIndianMsisdn.isChecked()) {
            makeDiscoveryDemo(tvIndianMsisdn.getText().toString());
        } else if (rbIndianMccMnc.isChecked()) {
            makeDiscoveryDemo(tvIndianMcc.getText().toString(), tvIndianMnc.getText().toString());
        } else {
           makeDiscoveryDemo(null);
        }
    }

    private StringBuilder getScopes() {
        if (rbOpenid.isChecked()) {
           return new StringBuilder(getString(R.string.scope_openid));
        } else if (rbMcIndiaTc.isChecked()) {
            return new StringBuilder(getString(R.string.scope_mc_india_tc));
        } else if (rbMcMnvValidate.isChecked()) {
            return new StringBuilder(getString(R.string.scope_mc_mnv_validate));
        } else {
            return new StringBuilder(getString(R.string.scope_mc_mnv_validate_plus));
        }
    }
}
