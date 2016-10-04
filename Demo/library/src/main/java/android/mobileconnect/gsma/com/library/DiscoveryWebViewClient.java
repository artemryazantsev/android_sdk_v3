package android.mobileconnect.gsma.com.library;

import android.mobileconnect.gsma.com.library.view.DiscoveryAuthenticationDialog;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ProgressBar;

import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public class DiscoveryWebViewClient extends MobileConnectWebViewClient
{
    private DiscoveryListener discoveryListener;

    private MobileConnectAndroidInterface mobileConnectAndroidInterface;

    private MobileConnectRequestOptions mobileConnectRequestOptions;

    public DiscoveryWebViewClient(final DiscoveryAuthenticationDialog dialog,
                                  final ProgressBar progressBar,
                                  final String redirectUrl,
                                  final DiscoveryListener discoveryListener,
                                  final MobileConnectAndroidInterface mobileConnectAndroidInterface,
                                  final MobileConnectRequestOptions mobileConnectRequestOptions)
    {
        super(dialog, progressBar, redirectUrl);
        this.discoveryListener = discoveryListener;
        this.mobileConnectAndroidInterface = mobileConnectAndroidInterface;
        this.mobileConnectRequestOptions = mobileConnectRequestOptions;
    }

    @Override
    protected boolean qualifyUrl(final String url)
    {
        if (url.contains("mcc_mnc="))
        {
            final Uri uri = Uri.parse(url);

            if (uri != null)
            {
                final String param = uri.getQueryParameter("mcc_mnc");
                final String[] mccAndMnc = param.split("_");

                if (mccAndMnc.length == 2)
                {
                    this.mobileConnectAndroidInterface.setMcc(mccAndMnc[0]);
                    this.mobileConnectAndroidInterface.setMnc(mccAndMnc[1]);
                }
            }

            return true;
        }
        return false;
    }

    @Override
    protected void handleError(final MobileConnectStatus status)
    {
        discoveryListener.discoveryFailed(status);
    }

    @Override
    protected void handleResult(final String url)
    {
        DiscoveryModel.getInstance().setDiscoveryServiceRedirectedURL(url);
        Log.d("url", url);

        URI uri = getUri(url);

        mobileConnectAndroidInterface.handleUrlRedirect(uri,
                                                        UUID.randomUUID().toString(),
                                                        UUID.randomUUID().toString(),
                                                        new MobileConnectAndroidInterface.IMobileConnectCallback()
                                                     {
                                                         @Override
                                                         public void onComplete(MobileConnectStatus mobileConnectStatus)
                                                         {
                                                             discoveryListener.onDiscoveryResponse(mobileConnectStatus);
                                                         }
                                                     },
                                                        mobileConnectRequestOptions);

    }

    @Nullable
    private URI getUri(final String url)
    {
        URI uri;
        try
        {
            uri = new URI(url);
        }
        catch (URISyntaxException e)
        {
            uri = null;
            Log.e("Invalid Redirect URI", e.getMessage());
        }
        return uri;
    }
}