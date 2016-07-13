/*
 *                                   SOFTWARE USE PERMISSION
 *
 *  By downloading and accessing this software and associated documentation files ("Software") you are granted the
 *  unrestricted right to deal in the Software, including, without limitation the right to use, copy, modify, publish,
 *  sublicense and grant such rights to third parties, subject to the following conditions:
 *
 *  The following copyright notice and this permission notice shall be included in all copies, modifications or
 *  substantial portions of this Software: Copyright © 2016 GSM Association.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS," WITHOUT WARRANTY OF ANY KIND, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 *  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. YOU
 *  AGREE TO INDEMNIFY AND HOLD HARMLESS THE AUTHORS AND COPYRIGHT HOLDERS FROM AND AGAINST ANY SUCH LIABILITY.
 */
package com.gsma.mobileconnect.helpers;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.gsma.android.mobileconnect.R;
import com.gsma.mobileconnect.discovery.CompleteSelectedOperatorDiscoveryOptions;
import com.gsma.mobileconnect.discovery.DiscoveryException;
import com.gsma.mobileconnect.discovery.DiscoveryOptions;
import com.gsma.mobileconnect.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.discovery.IDiscovery;
import com.gsma.mobileconnect.discovery.ParsedDiscoveryRedirect;
import com.gsma.mobileconnect.impl.AndroidDiscoveryImpl;
import com.gsma.mobileconnect.model.DiscoveryModel;
import com.gsma.mobileconnect.utils.AndroidRestClient;
import com.gsma.mobileconnect.utils.ErrorResponse;
import com.gsma.mobileconnect.utils.RestClient;
import com.gsma.mobileconnect.utils.StringUtils;
import com.gsma.mobileconnect.view.DiscoveryAuthenticationDialog;
import com.gsma.mobileconnect.view.MobileConnectWebView;

import java.net.URISyntaxException;

/**
 * Class to wrap the Discovery related calls to the Mobile Connect SDK.
 */
public class DiscoveryService extends BaseService
{
    private IDiscovery discovery;

    public DiscoveryService()
    {
        final RestClient client = new AndroidRestClient();
        this.discovery = new AndroidDiscoveryImpl(null, client);
    }

    private static final String INTERNAL_ERROR_CODE = "internal error";

    /**
     * This method is called to initiate the Mobile Connect process.
     * <p/>
     * The return is either an 'error', 'operator selection is required' or 'authorization can start' (the operator
     * has been identified).
     *
     * @param config Mobile Connect Configuration instance
     * @return A status object
     */
    public MobileConnectStatus callMobileConnectForStartDiscovery(final MobileConnectConfig config)
    {
        final DiscoveryResponse discoveryResponse;
        try
        {
            final DiscoveryOptions options = config.getDiscoveryOptions("Mobile");

            final CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
            this.discovery.startAutomatedOperatorDiscovery(config,
                                                           config.getDiscoveryRedirectURL(),
                                                           options,
                                                           null,
                                                           captureDiscoveryResponse);
            discoveryResponse = captureDiscoveryResponse.getDiscoveryResponse();
        }
        catch (final DiscoveryException ex)
        {
            return MobileConnectStatus.error(INTERNAL_ERROR_CODE, "Failed to obtain operator details.", ex);
        }

        if (!discoveryResponse.isCached())
        {
            if (!isSuccessResponseCode(discoveryResponse.getResponseCode()))
            {
                final ErrorResponse errorResponse = getErrorResponse(discoveryResponse);
                return MobileConnectStatus.error(errorResponse.get_error(),
                                                 errorResponse.get_error_description(),
                                                 discoveryResponse);
            }
        }

        // The DiscoveryResponse may contain the operator endpoints in which case we can proceed to authorization
        // with an operator.
        final String operatorSelectionURL = this.discovery.extractOperatorSelectionURL(discoveryResponse);
        if (!StringUtils.isNullOrEmpty(operatorSelectionURL))
        {
            return MobileConnectStatus.operatorSelection(operatorSelectionURL);
        }
        else
        {
            return MobileConnectStatus.startAuthorization(discoveryResponse);
        }
    }

    /**
     * This method is called to extract the response from the operator selection process and then determine what to
     * do next.
     * <p/>
     *
     * @param config Mobile Connect Configuration instance
     * @return A status object
     */
    public MobileConnectStatus callMobileConnectOnDiscoveryRedirect(final MobileConnectConfig config)
    {
        final CaptureParsedDiscoveryRedirect captureParsedDiscoveryRedirect = new CaptureParsedDiscoveryRedirect();
        try
        {
            final String url = DiscoveryModel.getInstance().getDiscoveryServiceRedirectedURL();
            this.discovery.parseDiscoveryRedirect(url, captureParsedDiscoveryRedirect);
        }
        catch (final URISyntaxException ex)
        {
            return MobileConnectStatus.error(INTERNAL_ERROR_CODE, "Cannot parse the redirect parameters.", ex);
        }

        final ParsedDiscoveryRedirect parsedDiscoveryRedirect = captureParsedDiscoveryRedirect.getParsedDiscoveryRedirect();
        if (parsedDiscoveryRedirect == null || !parsedDiscoveryRedirect.hasMCCAndMNC())
        {
            // The operator has not been identified, need to start again.
            return MobileConnectStatus.startDiscovery();
        }

        final DiscoveryResponse discoveryResponse;
        try
        {
            final CompleteSelectedOperatorDiscoveryOptions options = config.getCompleteSelectedOperatorDiscoveryOptions();
            final CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();

            // Obtain the discovery information for the selected operator
            this.discovery.completeSelectedOperatorDiscovery(config,
                                                             config.getDiscoveryRedirectURL(),
                                                             parsedDiscoveryRedirect.getSelectedMCC(),
                                                             parsedDiscoveryRedirect.getSelectedMNC(),
                                                             options,
                                                             null,
                                                             captureDiscoveryResponse);
            discoveryResponse = captureDiscoveryResponse.getDiscoveryResponse();

            //move to models
            DiscoveryModel.getInstance().setMcc(parsedDiscoveryRedirect.getSelectedMCC());
            DiscoveryModel.getInstance().setMnc(parsedDiscoveryRedirect.getSelectedMNC());
            DiscoveryModel.getInstance().setEncryptedMSISDN(parsedDiscoveryRedirect.getEncryptedMSISDN());
        }
        catch (final DiscoveryException ex)
        {
            return MobileConnectStatus.error(INTERNAL_ERROR_CODE, "Failed to obtain operator details.", ex);
        }

        if (!discoveryResponse.isCached())
        {
            if (!isSuccessResponseCode(discoveryResponse.getResponseCode()))
            {
                final ErrorResponse errorResponse = getErrorResponse(discoveryResponse);
                return MobileConnectStatus.error(errorResponse.get_error(),
                                                 errorResponse.get_error_description(),
                                                 discoveryResponse);
            }
        }

        if (this.discovery.isOperatorSelectionRequired(discoveryResponse))
        {
            return MobileConnectStatus.startDiscovery();
        }

        return MobileConnectStatus.startAuthorization(discoveryResponse);
    }

    /**
     * Create an Android WebView to display the MNO Discovery page. The {@link android.webkit.WebView} then captures the redirect on success
     * containing
     * the MMC and MNC values. The handler is then called with these values.
     *
     * @param config            Mobile Connect Configuration instance
     * @param context           The Android context
     * @param discoveryListener The implementation of {@link DiscoveryListener} which will receive Success/Failure
     *                          callbacks
     * @param operatorUrl       The MNO discovery URI.
     */
    @SuppressWarnings("unused")
    public void doDiscoveryWithWebView(final MobileConnectConfig config,
                                       final Context context,
                                       final DiscoveryListener discoveryListener,
                                       final String operatorUrl)
    {

        final ViewGroup nullParent = null;

        final RelativeLayout webViewLayout = (RelativeLayout) LayoutInflater.from(context)
                                                                            .inflate(R.layout.layout_web_view,
                                                                               nullParent,
                                                                               false);

        final MobileConnectWebView webView = (MobileConnectWebView) webViewLayout.findViewById(R.id.web_view);
        final ProgressBar progressBar = (ProgressBar) webViewLayout.findViewById(R.id.progressBar);

        final DiscoveryAuthenticationDialog dialog = DiscoveryAuthenticationDialog.getInstance(context);

        dialog.setContentView(webViewLayout);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new DiscoveryWebViewClient(dialog,
                                                            progressBar,
                                                            config.getDiscoveryRedirectURL(),
                                                            discoveryListener,
                                                            config));

        webView.loadUrl(operatorUrl);
        dialog.show();
    }

    /**
     * Extract an error response from a discovery response, create a generic error if the discovery response does not
     * contain an error response.
     *
     * @param discoveryResponse The discovery response to check.
     * @return The extracted error response, or a generic error.
     */
    private ErrorResponse getErrorResponse(final DiscoveryResponse discoveryResponse)
    {
        ErrorResponse errorResponse = this.discovery.getErrorResponse(discoveryResponse);
        if (null == errorResponse)
        {
            errorResponse = new ErrorResponse();
            errorResponse.set_error(INTERNAL_ERROR_CODE);
            errorResponse.set_error_description("End point failed.");
        }
        return errorResponse;
    }

    public void setDiscovery(final IDiscovery discovery)
    {
        this.discovery = discovery;
    }

    private class DiscoveryWebViewClient extends MobileConnectWebViewClient
    {
        private final DiscoveryListener listener;

        private final MobileConnectConfig config;

        public DiscoveryWebViewClient(final Dialog dialog,
                                      final ProgressBar progressBar,
                                      final String redirectUrl,
                                      final DiscoveryListener listener,
                                      final MobileConnectConfig config)
        {
            super(dialog, progressBar, redirectUrl);
            this.listener = listener;
            this.config = config;
        }

        @Override
        protected boolean qualifyUrl(final String url)
        {
            return url.contains("mcc_mnc=");
        }

        @Override
        protected void handleError(final MobileConnectStatus status)
        {
            this.listener.discoveryFailed(status);
        }

        @Override
        protected void handleResult(final String url)
        {
            DiscoveryModel.getInstance().setDiscoveryServiceRedirectedURL(url);

            if (DiscoveryModel.getInstance().getDiscoveryServiceRedirectedURL() != null)
            {
                final MobileConnectStatus status = callMobileConnectOnDiscoveryRedirect(this.config);

                if (!status.isError() && !status.isStartDiscovery())
                {
                    this.listener.discoveryComplete(status);
                }
            }
        }
    }
}