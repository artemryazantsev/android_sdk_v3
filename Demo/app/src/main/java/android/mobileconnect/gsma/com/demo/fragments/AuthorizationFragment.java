package android.mobileconnect.gsma.com.demo.fragments;

import android.mobileconnect.gsma.com.demo.R;
import android.mobileconnect.gsma.com.library.MobileConnectAndroidInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

/**
 * Created by usmaan.dad on 24/08/2016.
 */
public class AuthorizationFragment extends BaseAuthFragment implements ITitle,
                                                                       MobileConnectAndroidInterface.IMobileConnectCallback
{
    public static AuthorizationFragment newInstance()
    {
        return new AuthorizationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_authorization, container, false);

        setupUIAndMobileConnectAndroid(view, this);

        return view;
    }

    @Override
    public String getTitle()
    {
        return "Authorization";
    }

    @Override
    public void onComplete(MobileConnectStatus mobileConnectStatus)
    {

    }
}