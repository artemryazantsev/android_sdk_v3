package android.mobileconnect.gsma.com.demo;

import android.mobileconnect.gsma.com.library.DiscoveryListener;
import android.mobileconnect.gsma.com.library.MobileConnectAndroidInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gsma.mobileconnect.r2.MobileConnect;
import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectStatus;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DiscoveryListener
{
    private Button performDiscoveryButton;

    private Button performAuthorizationButton;

    private Button webViewButton;

    private MobileConnectAndroidInterface mobileConnectAndroidInterface;

    private MobileConnectConfig mobileConnectConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        performDiscoveryButton = (Button) findViewById(R.id.button_perform_discovery);
        performAuthorizationButton = (Button) findViewById(R.id.button_perform_authorisation);
        webViewButton = (Button) findViewById(R.id.button_perform_discovery_with_web_view);

        performDiscoveryButton.setOnClickListener(this);
        performAuthorizationButton.setOnClickListener(this);
        webViewButton.setOnClickListener(this);

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

        mobileConnectConfig = new MobileConnectConfig.Builder().withClientId(getString(R.string.client_id))
                                                               .withClientSecret(getString(R.string.client_secret))
                                                               .withDiscoveryUrl(discoveryUri)
                                                               .withRedirectUrl(redirectUri)
                                                               .build();

        // todo setup only a single thread for this service
        MobileConnectInterface mobileConnectInterface = MobileConnect.buildInterface(mobileConnectConfig);

        mobileConnectAndroidInterface = new MobileConnectAndroidInterface(mobileConnectInterface);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.button_perform_discovery:
            {
                break;
            }
            case R.id.button_perform_authorisation:
            {
                break;
            }
            case R.id.button_perform_discovery_with_web_view:
            {
                mobileConnectAndroidInterface.doDiscoveryWithWebView(mobileConnectConfig,
                                                                     this,
                                                                     this,
                                                                     mobileConnectConfig.getDiscoveryUrl().toString());
                break;
            }
        }
    }

    @Override
    public void discoveryComplete(MobileConnectStatus mobileConnectStatus)
    {
        Toast.makeText(this, "Discovery Complete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void discoveryFailed(MobileConnectStatus mobileConnectStatus)
    {
        Toast.makeText(this, "Discovery Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDiscoveryDialogClose()
    {
        Toast.makeText(this, "Discovery Dialog Closed", Toast.LENGTH_SHORT).show();
    }
}