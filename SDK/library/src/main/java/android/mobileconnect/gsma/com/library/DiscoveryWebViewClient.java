package android.mobileconnect.gsma.com.library;

import android.mobileconnect.gsma.com.library.view.DiscoveryAuthenticationDialog;
import android.widget.ProgressBar;

import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectStatus;

public class DiscoveryWebViewClient extends MobileConnectWebViewClient
{
    private DiscoveryListener listener;

    private MobileConnectConfig config;

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

        if (DiscoveryModel.getInstance().getDiscoveryServiceRedirectedURL() != null)
        {
            MobileConnectStatus status = callMobileConnectOnDiscoveryRedirect(config);

            if (!status.isError() && !status.isStartDiscovery())
            {
                dialog.cancel();
                listener.discoveryComplete(status);
            }
        }
    }
}