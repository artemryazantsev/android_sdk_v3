package com.gsma.mobileconnect.r2.android.exampleapps;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.main.IMobileConnectContract;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponse;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponseData;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.identity.IdentityResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultActivity extends AppCompatActivity
{
    private TextView nameTextView;

    private TextView idTokenTextView;

    private TextView accessTokenTextView;

    private Button identityButton;

    private LinearLayout userInfoLayout;

    private LinearLayout identityLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        nameTextView = (TextView) findViewById(R.id.text_view_name);
        idTokenTextView = (TextView) findViewById(R.id.text_view_id_token);
        accessTokenTextView = (TextView) findViewById(R.id.text_view_access_token);
        identityButton = (Button) findViewById(R.id.button_identity);
        userInfoLayout = (LinearLayout) findViewById(R.id.layout_user_info);
        identityLayout = (LinearLayout) findViewById(R.id.layout_identity);

        // Initially hide the userInfo and identity layouts
        userInfoLayout.setVisibility(View.GONE);
        identityLayout.setVisibility(View.GONE);

        final String idToken = getIdToken(BaseActivity.mobileConnectStatus);
        final String accessToken = getAccessToken(BaseActivity.mobileConnectStatus);

        String clientName;

        DiscoveryResponse discoveryResponse = BaseActivity.mobileConnectAndroidView.getDiscoveryResponse();

        if (discoveryResponse != null)
        {
            clientName = discoveryResponse.getClientName();
        }
        else
        {
            clientName = "Unable to get client name";
        }

        nameTextView.setText(clientName);
        if (idToken == null)
        {
            idTokenTextView.setText("Failed to receive access token. Please try again");
        }
        else
        {
            idTokenTextView.setText(idToken);
        }

        if (accessToken != null)
        {
            accessTokenTextView.setText(accessToken);
        }
        else
        {
            accessTokenTextView.setText("Failed to receive access token. Please try again.");
        }

        identityButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                BaseActivity.mobileConnectAndroidView.requestIdentity(accessToken,
                                                                      new IMobileConnectContract
                                                                              .IMobileConnectCallback()
                                                                      {
                                                                          @Override
                                                                          public void onComplete(MobileConnectStatus
                                                                                                         mobileConnectStatus)
                                                                          {
                                                                              identityButton.setVisibility(View.GONE);
                                                                              displayIdentityResponse(
                                                                                      mobileConnectStatus,
                                                                                      false);
                                                                          }
                                                                      });
            }
        });
    }

    private void displayIdentityResponse(final MobileConnectStatus mobileConnectStatus, final boolean isUserInfo)
    {
        if (mobileConnectStatus.getIdentityResponse() != null)
        {
            IdentityResponse identityResponse = mobileConnectStatus.getIdentityResponse();

            if (identityResponse.getResponseJson() != null)
            {
                String responseJson = identityResponse.getResponseJson();
                try
                {
                    JSONObject jsonObject = new JSONObject(responseJson);

                    if (isUserInfo)
                    {
                        userInfoLayout.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        identityLayout.setVisibility(View.VISIBLE);
                    }

                    for (int i = 0; i < jsonObject.names().length(); i++)
                    {
                        final String name = (String) jsonObject.names().get(i);
                        String value;

                        try
                        {
                            value = jsonObject.getString(name);
                        }
                        catch (Exception exception)
                        {
                            value = "Value isn't a String.";
                        }

                        final TextView nameTextView = new TextView(this);
                        nameTextView.setText(name);
                        nameTextView.setTypeface(Typeface.DEFAULT_BOLD);

                        final TextView valueTextView = new TextView(this);
                        valueTextView.setText(value);

                        if (isUserInfo)
                        {
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(userInfoLayout
                                                                                                     .getLayoutParams
                                                                                                             ());
                            params.leftMargin = 8;

                            valueTextView.setLayoutParams(params);

                            userInfoLayout.addView(nameTextView);
                            userInfoLayout.addView(valueTextView);
                        }
                        else // else it's Identity
                        {
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(identityLayout
                                                                                                     .getLayoutParams
                                                                                                             ());
                            params.leftMargin = 8;

                            valueTextView.setLayoutParams(params);

                            identityLayout.addView(nameTextView);
                            identityLayout.addView(valueTextView);
                        }
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }
    }

    protected String getIdToken(final MobileConnectStatus mobileConnectStatus)
    {
        if (mobileConnectStatus != null && mobileConnectStatus.getRequestTokenResponse() != null)
        {
            RequestTokenResponse requestTokenResponse = mobileConnectStatus.getRequestTokenResponse();

            if (requestTokenResponse != null)
            {
                RequestTokenResponseData responseData = requestTokenResponse.getResponseData();

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
            RequestTokenResponse requestTokenResponse = mobileConnectStatus.getRequestTokenResponse();

            if (requestTokenResponse != null)
            {
                RequestTokenResponseData responseData = requestTokenResponse.getResponseData();

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