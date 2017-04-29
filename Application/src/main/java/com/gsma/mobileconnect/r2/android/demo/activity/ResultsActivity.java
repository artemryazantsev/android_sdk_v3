package com.gsma.mobileconnect.r2.android.demo.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.demo.R;
import com.gsma.mobileconnect.r2.android.demo.fragments.BaseAuthFragment;
import com.gsma.mobileconnect.r2.android.main.IMobileConnectContract;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponse;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponseData;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.identity.IdentityResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultsActivity extends BaseActivity {

    private TextView tvTokenId;
    private TextView tvMessage;
    private TextView tvAccessToken;
    private TextView tvClientName;
    private Toolbar toolbar;

    private LinearLayout userInfoLayout;
    private LinearLayout identityLayout;

    private CardView cardClientName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        configureToolbar(toolbar, getString(R.string.results_activity_title));

        init();

        userInfoLayout.setVisibility(View.GONE);
        identityLayout.setVisibility(View.GONE);

        final String idToken = getIdToken(BaseAuthFragment.mobileConnectStatus);
        final String accessToken = getAccessToken(BaseAuthFragment.mobileConnectStatus);


        String clientName;

        final DiscoveryResponse discoveryResponse = BaseAuthFragment.mobileConnectAndroidView.getDiscoveryResponse();

        if (discoveryResponse != null) {
            clientName = discoveryResponse.getClientName();
        }
        else {
            clientName = getString(R.string.unable_to_get_client_name);
        }

        if (clientName == null || clientName.equals(getString(R.string.empty_string))) {
            tvClientName.setVisibility(View.GONE);
            cardClientName.setVisibility(View.GONE);
        } else {
            tvClientName.setText(clientName);
        }

        if (idToken == null) {
            tvTokenId.setText(getString(R.string.failed_receive_token));
        }
        else {
            tvTokenId.setText(idToken);
        }

        if (accessToken != null) {
            tvAccessToken.setText(accessToken);
        }
        else {
            tvAccessToken.setText(getString(R.string.failed_receive_token));
        }

        BaseAuthFragment.mobileConnectAndroidView.requestUserInfo(accessToken,
                new IMobileConnectContract
                        .IMobileConnectCallback()
                {
                    @Override
                    public void onComplete(final
                                           MobileConnectStatus mobileConnectStatus)
                    {
                        displayIdentityResponse(
                                mobileConnectStatus,
                                true);
                    }
                });

        BaseAuthFragment.mobileConnectAndroidView.requestIdentity(accessToken,
                new IMobileConnectContract
                        .IMobileConnectCallback()
                {
                    @Override
                    public void onComplete(final
                                           MobileConnectStatus mobileConnectStatus)
                    {
                        displayIdentityResponse(
                                mobileConnectStatus,
                                false);
                    }
                });



        if (BaseAuthFragment.mobileConnectStatus.getRequestTokenResponse() != null) {
            tvMessage.setText(getResources().getString(R.string.successful_request_message));
        } else {
            tvMessage.setText(BaseAuthFragment.mobileConnectStatus.getRequestTokenResponse().getErrorResponse().toString());
        }
    }

    protected void init() {
        tvTokenId = (TextView) findViewById(R.id.txbTokenId);
        tvMessage = (TextView) findViewById(R.id.txbMessage);
        tvAccessToken = (TextView) findViewById(R.id.txbAccessToken);
        tvClientName = (TextView) findViewById(R.id.txbClientName);
        cardClientName = (CardView) findViewById(R.id.cardClientName);
        userInfoLayout = (LinearLayout) findViewById(R.id.layout_user_info);
        identityLayout = (LinearLayout) findViewById(R.id.layout_identity);
    }


    private void displayIdentityResponse(final MobileConnectStatus mobileConnectStatus, final boolean isUserInfo)
    {
        if (mobileConnectStatus.getIdentityResponse() != null)
        {
            final IdentityResponse identityResponse = mobileConnectStatus.getIdentityResponse();

            if (identityResponse.getResponseJson() != null)
            {
                final String responseJson = identityResponse.getResponseJson();
                try
                {
                    final JSONObject jsonObject = new JSONObject(responseJson);

                    if (isUserInfo)
                    {
                        userInfoLayout.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        if (userInfoLayout.getVisibility()==View.GONE) {
                            identityLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    for (int i = 0; i < jsonObject.names().length(); i++)
                    {
                        final String name = (String) jsonObject.names().get(i);
                        String value;
                        if (jsonObject.names().get(i).equals(getString(R.string.address))) {
                            JSONObject addressObject = new JSONObject(jsonObject.get(name).toString());
                            for (int j = 0; j < addressObject.names().length(); j++) {
                                final String addressName = (String)addressObject.names().get(j);
                                String addressValue;
                                try
                                {
                                    addressValue = (String)addressObject.get(addressName);
                                }
                                catch (Exception exception)
                                {
                                    addressValue = getString(R.string.not_sting_mess);
                                }
                                addElement(addressName, addressValue, isUserInfo);
                            }
                            continue;
                        }
                        try
                        {
                            value = String.valueOf(jsonObject.get(name));
                        }
                        catch (Exception exception)
                        {
                            value = getString(R.string.not_sting_mess);
                        }

                        addElement(name, value, isUserInfo);
                    }
                }
                catch (final JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }
    }

    private void addElement (String name, String value, boolean isUserInfo) {
        final TextView nameTextView = new TextView(this);
        nameTextView.setText(optimizeName(name));
        nameTextView.setTypeface(Typeface.DEFAULT_BOLD);
        final TextView valueTextView = new TextView(this);
        valueTextView.setText(value);

        if (isUserInfo)
        {
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(userInfoLayout
                    .getLayoutParams());
            params.leftMargin = 16;

            valueTextView.setLayoutParams(params);

            userInfoLayout.addView(nameTextView);
            userInfoLayout.addView(valueTextView);
        }
        else // else it's Identity
        {
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(identityLayout
                    .getLayoutParams());
            params.leftMargin = 16;

            valueTextView.setLayoutParams(params);

            identityLayout.addView(nameTextView);
            identityLayout.addView(valueTextView);
        }
    }

    private String optimizeName (String name) {
        name = name.replaceAll("_", " ");
        char [] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ') {
                chars[i+1] = Character.toUpperCase(chars[i+1]);
            }
        }
        return String.valueOf(chars);
    }

    protected String getIdToken(final MobileConnectStatus mobileConnectStatus)
    {
        if (mobileConnectStatus != null && mobileConnectStatus.getRequestTokenResponse() != null)
        {
            final RequestTokenResponse requestTokenResponse = mobileConnectStatus.getRequestTokenResponse();

            if (requestTokenResponse != null)
            {
                final RequestTokenResponseData responseData = requestTokenResponse.getResponseData();

                if (responseData != null)
                {
                    if (responseData.getIdToken() != null)
                    {
                        return responseData.getIdToken();
                    }
                }
            }
        }
        return null;
    }

    protected String getAccessToken(final MobileConnectStatus mobileConnectStatus)
    {
        if (mobileConnectStatus != null && mobileConnectStatus.getRequestTokenResponse() != null)
        {
            final RequestTokenResponse requestTokenResponse = mobileConnectStatus.getRequestTokenResponse();

            if (requestTokenResponse != null)
            {
                final RequestTokenResponseData responseData = requestTokenResponse.getResponseData();

                if (responseData != null)
                {
                    if (responseData.getAccessToken() != null)
                    {
                        return responseData.getAccessToken();
                    }
                }
            }
        }
        return null;
    }


}
