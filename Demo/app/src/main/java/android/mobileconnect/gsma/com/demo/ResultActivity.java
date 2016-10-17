package android.mobileconnect.gsma.com.demo;

import android.content.Intent;
import android.graphics.Typeface;
import android.mobileconnect.gsma.com.demo.fragments.BaseAuthFragment;
import android.mobileconnect.gsma.com.library.main.IMobileConnectContract;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponse;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponseData;
import com.gsma.mobileconnect.r2.constants.Parameters;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.identity.IdentityResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultActivity extends AppCompatActivity
{
    private TextView nameTextView;

    private TextView idTokenTextView;

    private TextView accessTokenTextView;

    private Button userInfoButton;

    private Button identityButton;

    private Button refreshButton;

    private Button revokeButton;

    private LinearLayout actionButtonsLayout;

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
        userInfoButton = (Button) findViewById(R.id.button_user_info);
        identityButton = (Button) findViewById(R.id.button_identity);
        refreshButton = (Button) findViewById(R.id.button_refresh);
        revokeButton = (Button) findViewById(R.id.button_revoke);
        actionButtonsLayout = (LinearLayout) findViewById(R.id.layout_action_buttons);
        userInfoLayout = (LinearLayout) findViewById(R.id.layout_user_info);
        identityLayout = (LinearLayout) findViewById(R.id.layout_identity);

        // Initially hide the userInfo and identity layouts
        userInfoLayout.setVisibility(View.GONE);
        identityLayout.setVisibility(View.GONE);

        final String idToken = getIdToken(BaseAuthFragment.mobileConnectStatus);
        final String accessToken = getAccessToken(BaseAuthFragment.mobileConnectStatus);

        String clientName;

        DiscoveryResponse discoveryResponse = BaseAuthFragment.mobileConnectAndroidView.getDiscoveryResponse();

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

        userInfoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                BaseAuthFragment.mobileConnectAndroidView.requestUserInfo(accessToken,
                                                                          new IMobileConnectContract
                                                                                  .IMobileConnectCallback()
                                                                          {
                                                                              @Override
                                                                              public void onComplete
                                                                                      (MobileConnectStatus
                                                                                               mobileConnectStatus)
                                                                              {
                                                                                  userInfoButton.setVisibility(View.GONE);
                                                                                  displayIdentityResponse(
                                                                                          mobileConnectStatus,
                                                                                          true);
                                                                              }
                                                                          });
            }
        });

        identityButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                BaseAuthFragment.mobileConnectAndroidView.requestIdentity(accessToken,
                                                                          new IMobileConnectContract
                                                                                  .IMobileConnectCallback()
                                                                          {
                                                                              @Override
                                                                              public void onComplete
                                                                                      (MobileConnectStatus
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

        refreshButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                BaseAuthFragment.mobileConnectAndroidView.refreshToken(accessToken,
                                                                       new IMobileConnectContract
                                                                               .IMobileConnectCallback()
                                                                       {
                                                                           @Override
                                                                           public void onComplete(MobileConnectStatus
                                                                                                          mobileConnectStatus)
                                                                           {
                                                                               displayIdentityResponse(
                                                                                       mobileConnectStatus,
                                                                                       false);
                                                                           }
                                                                       });
            }
        });

        revokeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                BaseAuthFragment.mobileConnectAndroidView.revokeToken(accessToken,
                                                                      Parameters.ACCESS_TOKEN_HINT,
                                                                      new IMobileConnectContract
                                                                              .IMobileConnectCallback()
                                                                      {
                                                                          @Override
                                                                          public void onComplete(MobileConnectStatus
                                                                                                         mobileConnectStatus)
                                                                          {
                                                                              if (mobileConnectStatus != null)
                                                                              {
                                                                                  Toast.makeText(ResultActivity.this,
                                                                                                 mobileConnectStatus
                                                                                                         .getOutcome(),
                                                                                                 Toast.LENGTH_SHORT)
                                                                                       .show();
                                                                              }
                                                                          }
                                                                      });
            }
        });

        Intent intent = getIntent();

        if (intent != null)
        {
            boolean anySwitchesOn = intent.getBooleanExtra("anySwitchesOn", false);

            if (anySwitchesOn)
            {

            }
        }
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
