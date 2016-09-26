package android.mobileconnect.gsma.com.library;

import android.mobileconnect.gsma.com.library.view.DiscoveryAuthenticationDialog;
import android.util.Log;
import android.widget.ProgressBar;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponse;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponseData;

public class AuthenticationWebViewClient extends MobileConnectWebViewClient
{
    private final String TAG = AuthenticationWebViewClient.class.getSimpleName();

    private final AuthenticationWebViewCallback authenticationWebViewCallback;

    AuthenticationListener listener;

    public AuthenticationWebViewClient(final DiscoveryAuthenticationDialog dialog,
                                       final ProgressBar progressBar,
                                       final AuthenticationListener listener,
                                       final String redirectUri,
                                       final AuthenticationWebViewCallback authenticationWebViewCallback)
    {
        super(dialog, progressBar, redirectUri);
        this.listener = listener;
        this.authenticationWebViewCallback = authenticationWebViewCallback;
    }

    @Override
    protected boolean qualifyUrl(final String url)
    {
        return url.contains("code");
    }

    @Override
    protected void handleError(final MobileConnectStatus status)
    {
        this.listener.authorizationFailed(status);
    }

    @Override
    protected void handleResult(final String url)
    {
        this.dialog.cancel();

        DiscoveryModel.getInstance().setDiscoveryServiceRedirectedURL(url);
        authenticationWebViewCallback.onSuccess(url);
    }

    protected void notifyListener(final MobileConnectStatus mobileConnectStatus, final AuthenticationListener listener)
    {
        if (mobileConnectStatus == null)
        {
            listener.authorizationFailed(MobileConnectStatus.startDiscovery());
        }
        else if (mobileConnectStatus.getErrorCode() != null)
        {
            // An error occurred, the error, description and (optionally) exception is available.
            Log.d(TAG, "Authorization has failed");
            Log.d(TAG, mobileConnectStatus.getErrorCode());
            Log.d(TAG, mobileConnectStatus.getErrorMessage());

            listener.authorizationFailed(mobileConnectStatus);
        }
        else if (mobileConnectStatus.getResponseType() == MobileConnectStatus.ResponseType.START_DISCOVERY)
        {
            // The operator could not be identified, start the discovery process.
            Log.d(TAG, "The operator could not be identified, need to restart the discovery process.");
            listener.authorizationFailed(mobileConnectStatus);
        }
        else if (mobileConnectStatus.getResponseType() == MobileConnectStatus.ResponseType.COMPLETE)
        {
            // Successfully authenticated, ParsedAuthenticationResponse and RequestTokenResponse are available
            try
            {
                final RequestTokenResponse response = mobileConnectStatus.getRequestTokenResponse();
                final RequestTokenResponseData responseData = response.getResponseData();
                final String token = responseData.getIdToken();

                Log.d(TAG, "Authorization has completed successfully");
                Log.d(TAG, "PCR is " + token);

                listener.tokenReceived(mobileConnectStatus.getRequestTokenResponse());
            }
            catch (final Exception e)
            {
                //This shouldn't happen so we will catch as exception to ensure something would return
                Log.d(TAG, "Part of the Auth response was incorrect", e);
                listener.authorizationFailed(MobileConnectStatus.startDiscovery());
            }
        }
        else
        {
            Log.d(TAG,
                  "The status is in an unknown state (not Complete, Error or Start Discovery - please restart " +
                  "discovery");
            listener.authorizationFailed(MobileConnectStatus.startDiscovery());
        }
    }
}