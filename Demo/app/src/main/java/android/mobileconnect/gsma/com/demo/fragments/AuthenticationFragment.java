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
import com.gsma.mobileconnect.r2.constants.Scopes;

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
        final MobileConnectRequestOptions mobileConnectRequestOptions = new MobileConnectRequestOptions.Builder()
                .withAuthenticationOptions(
                new AuthenticationOptions.Builder().withScope(Scopes.MOBILECONNECTAUTHENTICATION)
                                                   .withContext("demo")
                                                   .withBindingMessage("demo auth")
                                                   .build()).build();

        final String state =
                mobileConnectStatus.getState() == null ? UUID.randomUUID().toString() : mobileConnectStatus.getState();
        final String nonce =
                mobileConnectStatus.getNonce() == null ? UUID.randomUUID().toString() : mobileConnectStatus.getNonce();

        switch (mobileConnectStatus.getResponseType())
        {
            case ERROR:
                break;
            case OPERATOR_SELECTION:
                break;
            case START_DISCOVERY:
                break;
            case START_AUTHENTICATION:
                this.discoveryResponse = mobileConnectStatus.getDiscoveryResponse();
                startAuthentication(mobileConnectStatus, mobileConnectRequestOptions, state, nonce);
                break;
            case AUTHENTICATION:
                break;
            case COMPLETE:
                break;
            case USER_INFO:
                break;
            case IDENTITY:
                break;
        }
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
                                                                  performAuthentication(mobileConnectStatus);
                                                              }
                                                          });
    }

    private void performAuthentication(final MobileConnectStatus mobileConnectStatus)
    {
        switch (mobileConnectStatus.getResponseType())
        {
            case ERROR:
                break;
            case OPERATOR_SELECTION:
                break;
            case START_DISCOVERY:
                break;
            case START_AUTHENTICATION:
                break;
            case AUTHENTICATION:
                mobileConnectAndroidInterface.attemptAuthenticationWithWebView(getActivity(), this, mobileConnectStatus.getUrl(), this.discoveryResponse);
                break;
            case COMPLETE:
                break;
            case USER_INFO:
                break;
            case IDENTITY:
                break;
        }
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

    @Override
    public void onComplete(MobileConnectStatus mobileConnectStatus)
    {
        this.mobileConnectAndroidInterface.attemptDiscoveryWithWebView(getActivity(),
                                                                       this,
                                                                       mobileConnectStatus.getUrl(),
                                                                       mobileConnectConfig.getRedirectUrl().toString());
    }

    @Override
    public void tokenReceived(RequestTokenResponse requestTokenResponse)
    {

    }

    @Override
    public void authorizationFailed(MobileConnectStatus mobileConnectStatus)
    {
        Toast.makeText(getActivity(), "asdfasd", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void authorizationSuccess(String s)
    {
        Toast.makeText(getActivity(), "asdfasd", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthorizationDialogClose()
    {

    }
}