package android.mobileconnect.gsma.com.library;

import android.mobileconnect.gsma.com.library.view.DiscoveryAuthenticationDialog;
import android.util.Log;
import android.widget.ProgressBar;

import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public class DiscoveryWebViewClient extends MobileConnectWebViewClient
{
    private DiscoveryListener listener;

    private MobileConnectConfig config;

    private MobileConnectAndroidInterface mobileConnectAndroidInterface;

    public DiscoveryWebViewClient(DiscoveryAuthenticationDialog dialog,
                                  ProgressBar progressBar,
                                  String redirectUrl,
                                  DiscoveryListener listener,
                                  MobileConnectConfig config)
    {
        super(dialog, progressBar, redirectUrl);
        this.listener = listener;
        this.config = config;
    }

    @Override
    protected boolean qualifyUrl(String url)
    {
        return url.contains("mcc_mnc=");
    }

    @Override
    protected void handleError(MobileConnectStatus status)
    {
        listener.discoveryFailed(status);
    }

    @Override
    protected void handleResult(String url)
    {
        DiscoveryModel.getInstance().setDiscoveryServiceRedirectedURL(url);

        if (url != null)
        {
            URI uri = null;

            try
            {
                uri = new URI(url);
            }
            catch (URISyntaxException e)
            {
                Log.e("Invalid Redirect URI", e.getMessage());
            }

            mobileConnectAndroidInterface.handleRedirect(uri,
                                                         null,
                                                         UUID.randomUUID().toString(),
                                                         UUID.randomUUID().toString(),
                                                         new MobileConnectAndroidInterface.IMobileConnectCallback()
                                                         {
                                                             @Override
                                                             public void onComplete(MobileConnectStatus

                                                                                            mobileConnectStatus)
                                                             {
                                                                 if (listener == null)
                                                                 {
                                                                     return;
                                                                 }

                                                                 switch (mobileConnectStatus.getResponseType())
                                                                 {
                                                                     case ERROR:
                                                                         break;
                                                                     case OPERATOR_SELECTION:
                                                                         break;
                                                                     case START_DISCOVERY:
                                                                         break;
                                                                     case START_AUTHENTICATION:
                                                                     {
                                                                         listener.discoveryComplete
                                                                                 (mobileConnectStatus);

                                                                     }
                                                                     break;
                                                                 }
                                                             }
                                                         });
        }
    }
}