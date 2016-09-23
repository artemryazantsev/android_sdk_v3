package android.mobileconnect.gsma.com.demo.fragments;

import android.mobileconnect.gsma.com.demo.R;
import android.mobileconnect.gsma.com.library.AuthenticationListener;
import android.mobileconnect.gsma.com.library.DiscoveryListener;
import android.mobileconnect.gsma.com.library.MobileConnectAndroidInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponse;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponseData;
import com.gsma.mobileconnect.r2.cache.CacheAccessException;
import com.gsma.mobileconnect.r2.constants.Scopes;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Created by usmaan.dad on 24/08/2016.
 */
public class AuthenticationFragment extends BaseAuthFragment implements ITitle,
                                                                        DiscoveryListener,
                                                                        MobileConnectAndroidInterface
                                                                                .IMobileConnectCallback,
                                                                        AuthenticationListener
{
    public static AuthenticationFragment newInstance()
    {
        return new AuthenticationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_authentication, container, false);

        setupUIAndMobileConnectAndroid(view, this);

        return view;
    }

    @Override
    public String getTitle()
    {
        return "Authentication";
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

    private void handleRedirect(final MobileConnectStatus mobileConnectStatus)
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

                StringBuilder stringBuilder = new StringBuilder(Scopes.MOBILECONNECTAUTHENTICATION);

                if (addressSwitch.isChecked())
                {
                    stringBuilder.append(" address");
                }
                if (emailSwitch.isChecked())
                {
                    stringBuilder.append(" email");
                }
                if (phoneSwitch.isChecked())
                {
                    stringBuilder.append(" phone");
                }
                if (profileSwitch.isChecked())
                {
                    stringBuilder.append(" profile");
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
                displayIdToken(mobileConnectStatus);
                if (addressSwitch.isChecked() || profileSwitch.isChecked() || phoneSwitch.isChecked() ||
                    emailSwitch.isChecked())
                {
                    getUserInfo(mobileConnectStatus);
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

    private void getUserInfo(MobileConnectStatus mobileConnectStatus)
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

    private void onRequestUserInfoComplete(MobileConnectStatus mobileConnectStatus)
    {

    }

    @Nullable
    private DiscoveryResponse getDiscoveryResponse(MobileConnectStatus mobileConnectStatus)
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
                                                           displayIdToken(mobileConnectStatus);
                                                       }
                                                   });
    }

    private void displayIdToken(MobileConnectStatus mobileConnectStatus)
    {
        String idToken = getIdToken(mobileConnectStatus);

        if (idToken == null)
        {
            idToken = "Failed to receive token. Please try again.";
        }

        Toast.makeText(getActivity(), idToken, Toast.LENGTH_SHORT).show();
    }

    private String getIdToken(final MobileConnectStatus mobileConnectStatus)
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

    private String getAccessToken(final MobileConnectStatus mobileConnectStatus)
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

    @Override
    public void onAuthorizationDialogClose()
    {
        Toast.makeText(getActivity(), "Dialog closed", Toast.LENGTH_SHORT).show();
    }
}