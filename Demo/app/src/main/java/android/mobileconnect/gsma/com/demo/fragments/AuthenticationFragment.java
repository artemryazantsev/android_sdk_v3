package android.mobileconnect.gsma.com.demo.fragments;

import android.mobileconnect.gsma.com.demo.R;
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

/**
 * Created by usmaan.dad on 24/08/2016.
 */
public class AuthenticationFragment extends BaseAuthFragment implements ITitle,
                                                                        DiscoveryListener,
                                                                        MobileConnectAndroidInterface
                                                                                .IMobileConnectCallback
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
    public void discoveryComplete(MobileConnectStatus mobileConnectStatus)
    {
        MobileConnectRequestOptions requestOptions = new MobileConnectRequestOptions.Builder()
                .withAuthenticationOptions(
                new AuthenticationOptions.Builder().withAcrValues("2").build()).build();

        this.mobileConnectAndroidInterface.startAuthentication(mobileConnectStatus.getDiscoveryResponse(),
                                                               mobileConnectStatus.getDiscoveryResponse()
                                                                                  .getResponseData()
                                                                                  .getSubscriberId(),
                                                               null,
                                                               null,
                                                               requestOptions,
                                                               new MobileConnectAndroidInterface
                                                                       .IMobileConnectCallback()

                                                               {
                                                                   @Override
                                                                   public void onComplete(MobileConnectStatus
                                                                                                  mobileConnectStatus)
                                                                   {
                                                                       Toast.makeText(getActivity(),
                                                                                      "Authorization Complete",
                                                                                      Toast.LENGTH_SHORT).show();
                                                                   }
                                                               });
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
        this.mobileConnectAndroidInterface.doDiscoveryWithWebView(getActivity(),
                                                                  this,
                                                                  mobileConnectStatus.getUrl(),
                                                                  mobileConnectConfig.getRedirectUrl().toString());
    }
}