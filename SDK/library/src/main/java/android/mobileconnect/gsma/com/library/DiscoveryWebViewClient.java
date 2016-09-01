package android.mobileconnect.gsma.com.library;

import android.mobileconnect.gsma.com.library.view.DiscoveryAuthenticationDialog;
import android.util.Log;
import android.widget.ProgressBar;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public class DiscoveryWebViewClient extends MobileConnectWebViewClient
{
    private DiscoveryListener discoveryListener;

    private MobileConnectAndroidInterface mobileConnectAndroidInterface;

    public DiscoveryWebViewClient(final DiscoveryAuthenticationDialog dialog,
                                  final ProgressBar progressBar,
                                  final String redirectUrl,
                                  final DiscoveryListener discoveryListener,
                                  final MobileConnectAndroidInterface mobileConnectAndroidInterface)
    {
        super(dialog, progressBar, redirectUrl);
        this.discoveryListener = discoveryListener;
        this.mobileConnectAndroidInterface = mobileConnectAndroidInterface;
    }

    @Override
    protected boolean qualifyUrl(String url)
    {
        return url.contains("mcc_mnc=");
    }

    @Override
    protected void handleError(MobileConnectStatus status)
    {
        discoveryListener.discoveryFailed(status);
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
                                                                 if (discoveryListener == null)
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
                                                                         discoveryListener.discoveryComplete
                                                                                 (mobileConnectStatus);

                                                                     }
                                                                     break;
                                                                 }
                                                             }
                                                         });
        }
    }
}