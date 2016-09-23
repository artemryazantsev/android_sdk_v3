package android.mobileconnect.gsma.com.demo.fragments;

import android.mobileconnect.gsma.com.demo.R;
import android.mobileconnect.gsma.com.library.AndroidMobileConnectEncodeDecoder;
import android.mobileconnect.gsma.com.library.MobileConnectAndroidInterface;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.gsma.mobileconnect.r2.MobileConnect;
import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryService;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by usmaan.dad on 25/08/2016.
 */
public class BaseAuthFragment extends Fragment
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

    // Mobile Connect Fields
    protected MobileConnectAndroidInterface mobileConnectAndroidInterface;

    protected MobileConnectConfig mobileConnectConfig;

    protected DiscoveryService discoveryService;

    /**
     * Sets-up the {@link BaseAuthFragment#mobileConnectAndroidInterface} with the configuration based on the values in
     * strings.xml
     */
    protected void setupUIAndMobileConnectAndroid(View view,
                                                  MobileConnectAndroidInterface.IMobileConnectCallback
                                                          mobileConnectCallback)
    {
        setupUI(view, mobileConnectCallback);

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

        this.discoveryService = (DiscoveryService)mobileConnect.getDiscoveryService();

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
}