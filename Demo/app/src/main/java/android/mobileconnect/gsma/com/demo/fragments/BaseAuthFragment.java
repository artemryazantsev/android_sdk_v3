package android.mobileconnect.gsma.com.demo.fragments;

import android.mobileconnect.gsma.com.demo.R;
import android.mobileconnect.gsma.com.library.AndroidMobileConnectEncodeDecoder;
import android.mobileconnect.gsma.com.library.AuthenticationListener;
import android.mobileconnect.gsma.com.library.DiscoveryListener;
import android.mobileconnect.gsma.com.library.MobileConnectAndroidInterface;
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
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponse;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponseData;
import com.gsma.mobileconnect.r2.cache.CacheAccessException;
import com.gsma.mobileconnect.r2.constants.Scopes;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.DiscoveryService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Created by usmaan.dad on 25/08/2016.
 */
public class BaseAuthFragment extends Fragment implements DiscoveryListener,
                                                          MobileConnectAndroidInterface.IMobileConnectCallback,
                                                          AuthenticationListener
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

    // Mobile Connect Fields
    protected MobileConnectAndroidInterface mobileConnectAndroidInterface;

    protected MobileConnectConfig mobileConnectConfig;

    protected DiscoveryService discoveryService;

    private String authType;

    /**
     * Sets-up the {@link BaseAuthFragment#mobileConnectAndroidInterface} with the configuration based on the values in
     * strings.xml
     */
    protected void setupUIAndMobileConnectAndroid(View view, String authType)
    {
        this.authType = authType;

        setupUI(view, this);

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

        mobileConnectConfig = new MobileConnectConfig.Builder().withClientId(getString(R.string.client_key))
                                                               .withClientSecret(getString(R.string.client_secret))
                                                               .withDiscoveryUrl(discoveryUri)
                                                               .withRedirectUrl(redirectUri)
                                                               .withCacheResponsesWithSessionId(false)
                                                               .build();

        MobileConnect mobileConnect = MobileConnect.build(mobileConnectConfig, new AndroidMobileConnectEncodeDecoder());

        this.discoveryService = (DiscoveryService) mobileConnect.getDiscoveryService();

        MobileConnectInterface mobileConnectInterface = mobileConnect.getMobileConnectInterface();

        mobileConnectAndroidInterface = new MobileConnectAndroidInterface(mobileConnectInterface, discoveryService);
    }

    private void setupUI(View view, final MobileConnectAndroidInterface.IMobileConnectCallback mobileConnectCallback)
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
                DiscoveryOptions.Builder discoveryOptionsBuilder = new DiscoveryOptions.Builder();

                if (msisdnCheckBox.isChecked())
                {
                    msisdn = msisdnTextInputEditText.getText().toString();
                    discoveryOptionsBuilder.withMsisdn(msisdn);
                }

                discoveryOptionsBuilder.withRedirectUrl(mobileConnectConfig.getRedirectUrl());

                MobileConnectRequestOptions requestOptions = new MobileConnectRequestOptions.Builder()
                        .withDiscoveryOptions(
                        discoveryOptionsBuilder.withMsisdn(msisdn).build()).build();

                mobileConnectAndroidInterface.attemptDiscovery(msisdn,
                                                               null,
                                                               null,
                                                               requestOptions,
                                                               mobileConnectCallback);
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

    protected void handleRedirect(final MobileConnectStatus mobileConnectStatus)
    {
        final String state =
                mobileConnectStatus.getState() == null ? UUID.randomUUID().toString() : mobileConnectStatus.getState();
        final String nonce =
                mobileConnectStatus.getNonce() == null ? UUID.randomUUID().toString() : mobileConnectStatus.getNonce();

        switch (mobileConnectStatus.getResponseType())
        {
            case ERROR:
                Toast.makeText(getActivity(),
                               String.format("Error - %s", mobileConnectStatus.getErrorMessage()),
                               Toast.LENGTH_SHORT).show();
                break;
            case OPERATOR_SELECTION:
                this.mobileConnectAndroidInterface.attemptDiscoveryWithWebView(getActivity(),
                                                                               this,
                                                                               mobileConnectStatus.getUrl(),
                                                                               mobileConnectConfig.getRedirectUrl()
                                                                                                  .toString());
                break;
            case START_DISCOVERY:
                //todo
                break;
            case START_AUTHENTICATION:
                AuthenticationOptions.Builder authenticationOptionsBuilder = new AuthenticationOptions.Builder()
                        .withContext(
                        "demo").withBindingMessage("demo auth");

                final MobileConnectRequestOptions.Builder mobileConnectRequestOptionsBuilder = new
                        MobileConnectRequestOptions.Builder();

                StringBuilder stringBuilder = new StringBuilder(authType);

                if (authType.equals(Scopes.MOBILECONNECTAUTHENTICATION))
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
                else if (authType.equals(Scopes.MOBILECONNECTAUTHORIZATION))
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

                authenticationOptionsBuilder.withScope(stringBuilder.toString());

                MobileConnectRequestOptions mobileConnectRequestOptions = mobileConnectRequestOptionsBuilder
                        .withAuthenticationOptions(
                        authenticationOptionsBuilder.build()).build();

                startAuthentication(mobileConnectStatus, mobileConnectRequestOptions, state, nonce);
                break;
            case AUTHENTICATION:
            {
                DiscoveryResponse discoveryResponse = getDiscoveryResponse(mobileConnectStatus);

                if (discoveryResponse == null)
                {
                    Toast.makeText(getActivity(),
                                   "Failed to retrieve DiscoveryResponse. Unable to proceed to Authentication",
                                   Toast.LENGTH_SHORT).show();
                    return;
                }

                mobileConnectAndroidInterface.attemptAuthenticationWithWebView(getActivity(),
                                                                               this,
                                                                               mobileConnectStatus.getUrl(),
                                                                               discoveryResponse,
                                                                               state,
                                                                               nonce);
                break;
            }
            case COMPLETE:
            {
                displayResult(mobileConnectStatus);
                if (authType.equals(Scopes.MOBILECONNECTAUTHENTICATION))
                {
                    if (addressSwitch.isChecked() || profileSwitch.isChecked() || phoneSwitch.isChecked() ||
                        emailSwitch.isChecked())
                    {
                        getUserInfo(mobileConnectStatus);
                    }
                }
                else
                {
                    getIdentity(mobileConnectStatus);
                }
                break;
            }
            case USER_INFO:
            {
                getUserInfo(mobileConnectStatus);
                break;
            }
            case IDENTITY:
                break;
        }
    }

    private void getIdentity(final MobileConnectStatus mobileConnectStatus)
    {
        DiscoveryResponse discoveryResponse = getDiscoveryResponse(mobileConnectStatus);
        mobileConnectAndroidInterface.requestIdentity(discoveryResponse,
                                                      getAccessToken(mobileConnectStatus),
                                                      new MobileConnectAndroidInterface.IMobileConnectCallback()
                                                      {
                                                          @Override
                                                          public void onComplete(MobileConnectStatus
                                                                                         mobileConnectStatus)
                                                          {
                                                              if (mobileConnectStatus != null)
                                                              {

                                                              }
                                                          }
                                                      });
    }

    protected void getUserInfo(final MobileConnectStatus mobileConnectStatus)
    {
        DiscoveryResponse discoveryResponse = getDiscoveryResponse(mobileConnectStatus);
        mobileConnectAndroidInterface.requestUserInfo(discoveryResponse,
                                                      getAccessToken(mobileConnectStatus),
                                                      new MobileConnectAndroidInterface.IMobileConnectCallback()
                                                      {
                                                          @Override
                                                          public void onComplete(MobileConnectStatus
                                                                                         mobileConnectStatus)
                                                          {
                                                              onRequestUserInfoComplete(mobileConnectStatus);
                                                          }
                                                      });
    }

    private void startAuthentication(MobileConnectStatus mobileConnectStatus,
                                     MobileConnectRequestOptions mobileConnectRequestOptions,
                                     String state,
                                     String nonce)
    {
        mobileConnectAndroidInterface.startAuthentication(mobileConnectStatus.getDiscoveryResponse(),
                                                          mobileConnectStatus.getDiscoveryResponse()
                                                                             .getResponseData()
                                                                             .getSubscriberId(),
                                                          state,
                                                          nonce,
                                                          mobileConnectRequestOptions,
                                                          new MobileConnectAndroidInterface.IMobileConnectCallback()
                                                          {
                                                              @Override
                                                              public void onComplete(MobileConnectStatus
                                                                                             mobileConnectStatus)
                                                              {
                                                                  handleRedirect(mobileConnectStatus);
                                                              }
                                                          });
    }

    @Nullable
    protected DiscoveryResponse getDiscoveryResponse(MobileConnectStatus mobileConnectStatus)
    {
        DiscoveryResponse discoveryResponse = mobileConnectStatus.getDiscoveryResponse();

        if (discoveryResponse == null)
        {
            try
            {
                discoveryResponse = discoveryService.getCachedDiscoveryResponse(mobileConnectAndroidInterface.getMcc(),
                                                                                mobileConnectAndroidInterface.getMnc());
            }
            catch (CacheAccessException e)
            {
                e.printStackTrace();
            }
        }
        return discoveryResponse;
    }

    protected void displayResult(MobileConnectStatus mobileConnectStatus)
    {
        String idToken = getIdToken(mobileConnectStatus);
        String accessToken = getAccessToken(mobileConnectStatus);

        DiscoveryResponse discoveryResponse = getDiscoveryResponse(mobileConnectStatus);

        String applicationShortName;

        if (discoveryResponse != null)
        {
            applicationShortName = discoveryResponse.getApplicationShortName();
        }
        else
        {
            applicationShortName = "Unable to get application short name";
        }

        if (idToken == null)
        {
            idToken = "Failed to receive token. Please try again.";
        }

        Toast.makeText(getActivity(), idToken, Toast.LENGTH_SHORT).show();
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

    protected void onRequestUserInfoComplete(MobileConnectStatus mobileConnectStatus)
    {

    }

    @Override
    public void onDiscoveryRedirect(@Nullable String s)
    {

    }

    @Override
    public void onDiscoveryResponse(MobileConnectStatus mobileConnectStatus)
    {
        handleRedirect(mobileConnectStatus);
    }

    @Override
    public void discoveryFailed(MobileConnectStatus mobileConnectStatus)
    {
        Toast.makeText(getActivity(), "Discovery Failed", Toast.LENGTH_SHORT).show();
    }

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
    public void onComplete(MobileConnectStatus mobileConnectStatus)
    {
        handleRedirect(mobileConnectStatus);
    }

    @Override
    public void tokenReceived(RequestTokenResponse requestTokenResponse)
    {

    }

    @Override
    public void authorizationFailed(MobileConnectStatus mobileConnectStatus)
    {
        Toast.makeText(getActivity(), "Authentication Failure", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void authorizationSuccess(final MobileConnectStatus mobileConnectStatus)
    {
        handleRedirect(mobileConnectStatus);
    }

    private void requestToken(MobileConnectStatus mobileConnectStatus)
    {
        Toast.makeText(getActivity(), "Authentication Success", Toast.LENGTH_SHORT).show();
        URI uri = null;

        try
        {
            uri = new URI(mobileConnectStatus.getUrl());
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        if (uri == null)
        {
            Toast.makeText(getActivity(), "Failed to get redirect url", Toast.LENGTH_SHORT).show();
            return;
        }

        String nonce =
                mobileConnectStatus.getNonce() == null ? mobileConnectStatus.getNonce() : UUID.randomUUID().toString();
        String state =
                mobileConnectStatus.getState() == null ? mobileConnectStatus.getState() : UUID.randomUUID().toString();

        DiscoveryResponse discoveryResponse = getDiscoveryResponse(mobileConnectStatus);

        mobileConnectAndroidInterface.requestToken(discoveryResponse,
                                                   uri,
                                                   state,
                                                   nonce,
                                                   new MobileConnectAndroidInterface.IMobileConnectCallback()
                                                   {
                                                       @Override
                                                       public void onComplete(MobileConnectStatus mobileConnectStatus)
                                                       {
                                                           displayResult(mobileConnectStatus);
                                                       }
                                                   });
    }

    @Override
    public void onAuthorizationDialogClose()
    {
        Toast.makeText(getActivity(), "Dialog closed", Toast.LENGTH_SHORT).show();
    }
}