package android.mobileconnect.gsma.com.demo.fragments;

import android.mobileconnect.gsma.com.demo.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by usmaan.dad on 24/08/2016.
 */
public class AuthenticationFragment extends BaseAuthFragment implements ITitle
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
        View view =  inflater.inflate(R.layout.fragment_authentication, container, false);

        setupMobileConnectAndroid();

        return view;
    }

    @Override
    public String getTitle()
    {
        return "Authentication";
    }
}
