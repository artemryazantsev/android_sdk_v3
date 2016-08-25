package com.gsma.mobileconnect.impl;

import android.net.Uri;

import com.gsma.mobileconnect.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.oidc.AuthenticationOptions;
import com.gsma.mobileconnect.oidc.DiscoveryResponseExpiredException;
import com.gsma.mobileconnect.oidc.IOIDC;
import com.gsma.mobileconnect.oidc.IParseAuthenticationResponseCallback;
import com.gsma.mobileconnect.oidc.IParseIDTokenCallback;
import com.gsma.mobileconnect.oidc.IRequestTokenCallback;
import com.gsma.mobileconnect.oidc.IStartAuthenticationCallback;
import com.gsma.mobileconnect.oidc.OIDCException;
import com.gsma.mobileconnect.oidc.ParsedAuthorizationResponse;
import com.gsma.mobileconnect.oidc.ParsedIdToken;
import com.gsma.mobileconnect.oidc.RequestTokenResponse;
import com.gsma.mobileconnect.oidc.StartAuthenticationResponse;
import com.gsma.mobileconnect.oidc.TokenOptions;
import com.gsma.mobileconnect.utils.AndroidJsonUtils;
import com.gsma.mobileconnect.utils.AndroidRestClient;
import com.gsma.mobileconnect.utils.Constants;
import com.gsma.mobileconnect.utils.ParsedOperatorIdentifiedDiscoveryResult;
import com.gsma.mobileconnect.utils.RestClient;
import com.gsma.mobileconnect.utils.RestException;
import com.gsma.mobileconnect.utils.RestResponse;
import com.gsma.mobileconnect.utils.StringUtils;
import com.gsma.mobileconnect.utils.ValidationUtils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * An Android specific implementation of the OIDC Service. It extends the implementation from the Java SDK
 * but overrides code that is incompatible with Android.
 * Created by nick.copley on 26/02/2016.
 */
public class AndroidOIDCImpl extends OIDCImpl implements IOIDC
{
    private static final String CALLBACK = "callback";

    private static final String DISCOVERY_RESULT = "discoveryResult";

    private final RestClient restClient;

    public AndroidOIDCImpl(final AndroidRestClient restClient)
    {
        super(restClient);
        this.restClient = restClient;
    }

    @Override
    public void parseAuthenticationResponse(final String redirectURL, final IParseAuthenticationResponseCallback callback) throws
                                                                                                               OIDCException
    {
        ValidationUtils.validateParameter(redirectURL, Constants.REDIRECT_URL_PARAMETER_NAME);
        ValidationUtils.validateParameter(callback, CALLBACK);

        final Uri uri = Uri.parse(redirectURL);

        final ParsedAuthorizationResponse parsedAuthorizationResponse = new ParsedAuthorizationResponse();
        parsedAuthorizationResponse.set_error(uri.getQueryParameter(Constants.ERROR_NAME));
        parsedAuthorizationResponse.set_error_description(uri.getQueryParameter(Constants.ERROR_DESCRIPTION_NAME));
        parsedAuthorizationResponse.set_error_uri(uri.getQueryParameter(Constants.ERROR_URI_NAME));
        parsedAuthorizationResponse.set_state(uri.getQueryParameter(Constants.STATE_PARAMETER_NAME));
        parsedAuthorizationResponse.set_code(uri.getQueryParameter(Constants.CODE_PARAMETER_NAME));
        callback.complete(parsedAuthorizationResponse);
    }

    @Override
    public void parseIDToken(final DiscoveryResponse discoveryResult,
                             final String id_tokenStr,
                             final TokenOptions options,
                             final IParseIDTokenCallback callback) throws OIDCException, DiscoveryResponseExpiredException
    {
        validateParseIdTokenParameters(discoveryResult, id_tokenStr, callback);
        try
        {
            final ParsedIdToken parsedIdToken = AndroidJsonUtils.createParsedIdToken(id_tokenStr);
            callback.complete(parsedIdToken);
        }
        catch (final IOException ex)
        {
            throw new OIDCException("Not an id_token", ex);
        }
    }

    private void validateParseIdTokenParameters(final DiscoveryResponse discoveryResult,
                                                final String idTokenStr,
                                                final IParseIDTokenCallback callback) throws DiscoveryResponseExpiredException
    {
        ValidationUtils.validateParameter(discoveryResult, DISCOVERY_RESULT);
        if (discoveryResult.hasExpired())
        {
            throw new DiscoveryResponseExpiredException("discoveryResult has expired");
        }
        ValidationUtils.validateParameter(idTokenStr, Constants.ID_TOKEN_FIELD_NAME);
        ValidationUtils.validateParameter(callback, CALLBACK);
    }

    /**
     * Request an access token.
     *
     * @param discoveryResult  The information found in discovery.
     * @param redirectURI      The URI to request the token.
     * @param code             The auth code
     * @param specifiedOptions options
     * @param callback         the callback
     * @throws OIDCException
     * @throws DiscoveryResponseExpiredException
     */
    @Override
    public void requestToken(final DiscoveryResponse discoveryResult,
                             final String redirectURI,
                             final String code,
                             final TokenOptions specifiedOptions,
                             final IRequestTokenCallback callback) throws OIDCException, DiscoveryResponseExpiredException
    {
        validateTokenParameters(discoveryResult, redirectURI, code, callback);

        final ParsedOperatorIdentifiedDiscoveryResult parsedOperatorIdentifiedDiscoveryResult = AndroidJsonUtils
                .parseOperatorIdentifiedDiscoveryResult(
                discoveryResult.getResponseData());
        if (null == parsedOperatorIdentifiedDiscoveryResult)
        {
            throw new OIDCException("Not a valid discovery result.");
        }

        final String tokenURL = parsedOperatorIdentifiedDiscoveryResult.getTokenHref();
        if (StringUtils.isNullOrEmpty(tokenURL))
        {
            throw new OIDCException("No token href");
        }
        final String clientId = parsedOperatorIdentifiedDiscoveryResult.getClientId();
        final String clientSecret = parsedOperatorIdentifiedDiscoveryResult.getClientSecret();

        final TokenOptions optionsToUse = getOptionsToBeUsed(specifiedOptions);

        RestResponse restResponse = null;
        try
        {
            final URI tokenURI = new URIBuilder(tokenURL).build();
            final HttpPost httpPost = buildHttpPostForAccessToken(tokenURI, redirectURI, code);

            final HttpClientContext context = this.restClient.getHttpClientContext(clientId, clientSecret, tokenURI);

            restResponse = this.restClient.callRestEndPoint(httpPost, context, optionsToUse.getTimeout(), null);

            final RequestTokenResponse requestTokenResponse = AndroidJsonUtils.parseRequestTokenResponse(Calendar.getInstance(),
                                                                                                         restResponse
                                                                                                           .getResponse());
            requestTokenResponse.setResponseCode(restResponse.getStatusCode());
            requestTokenResponse.setHeaders(restResponse.getHeaders());

            callback.complete(requestTokenResponse);
        }
        catch (final RestException ex)
        {
            throw newOIDCExceptionFromRestException("Call to Token end point failed", ex);
        }
        catch (final URISyntaxException ex)
        {
            throw newOIDCExceptionWithRestResponse("Failed to build discovery URI", null, ex);
        }
        catch (final IOException ex)
        {
            throw newOIDCExceptionWithRestResponse("Calling Discovery service failed", restResponse, ex);
        }
    }

    private void validateAuthenticationParameters(final DiscoveryResponse discoveryResult,
                                                  final String redirectURI,
                                                  final String nonce,
                                                  final IStartAuthenticationCallback callback) throws
                                                                                         DiscoveryResponseExpiredException
    {
        ValidationUtils.validateParameter(discoveryResult, DISCOVERY_RESULT);
        if (discoveryResult.hasExpired())
        {
            throw new DiscoveryResponseExpiredException("discoveryResult has expired");
        }
        ValidationUtils.validateParameter(redirectURI, Constants.REDIRECT_URI_PARAMETER_NAME);
        ValidationUtils.validateParameter(nonce, Constants.NONCE_PARAMETER_NAME);
        ValidationUtils.validateParameter(callback, CALLBACK);
    }

    private void validateTokenParameters(final DiscoveryResponse discoveryResult,
                                         final String redirectURI,
                                         final String code,
                                         final IRequestTokenCallback callback) throws DiscoveryResponseExpiredException
    {
        ValidationUtils.validateParameter(discoveryResult, DISCOVERY_RESULT);
        if (discoveryResult.hasExpired())
        {
            throw new DiscoveryResponseExpiredException("discoveryResult has expired");
        }
        ValidationUtils.validateParameter(redirectURI, Constants.REDIRECT_URI_PARAMETER_NAME);
        ValidationUtils.validateParameter(code, Constants.CODE_PARAMETER_NAME);
        ValidationUtils.validateParameter(callback, CALLBACK);
    }

    /**
     * See
     * {@link IOIDC#startAuthentication(DiscoveryResponse, String, String, String, String, Integer, String, String, AuthenticationOptions, IStartAuthenticationCallback)}.
     */
    @Override
    public void startAuthentication(final DiscoveryResponse discoveryResult,
                                    final String redirectURI,
                                    final String state,
                                    final String nonce,
                                    String scope,
                                    Integer maxAge,
                                    String acrValues,
                                    final String encryptedMSISDN,
                                    final AuthenticationOptions specifiedOptions,
                                    final IStartAuthenticationCallback callback) throws
                                                                           OIDCException,
                                                                           DiscoveryResponseExpiredException
    {
        validateAuthenticationParameters(discoveryResult, redirectURI, nonce, callback);
        scope = getScope(scope);
        maxAge = getMaxAge(maxAge);
        acrValues = getAcrValues(acrValues);

        final AuthenticationOptions optionsToBeUsed = getOptionsToBeUsed(specifiedOptions);

        final ParsedOperatorIdentifiedDiscoveryResult parsedOperatorIdentifiedDiscoveryResult = AndroidJsonUtils
                .parseOperatorIdentifiedDiscoveryResult(
                discoveryResult.getResponseData());
        if (null == parsedOperatorIdentifiedDiscoveryResult)
        {
            throw new OIDCException("Not a valid discovery result.");
        }

        final String authorizationHref = parsedOperatorIdentifiedDiscoveryResult.getAuthorizationHref();
        if (StringUtils.isNullOrEmpty(authorizationHref))
        {
            throw new OIDCException("No authorization href");
        }

        final URIBuilder builder = getUriBuilder(authorizationHref);

        builder.addParameter(Constants.CLIENT_ID_PARAMETER_NAME, parsedOperatorIdentifiedDiscoveryResult.getClientId());
        builder.addParameter(Constants.RESPONSE_TYPE_PARAMETER_NAME, Constants.RESPONSE_TYPE_PARAMETER_VALUE);
        builder.addParameter(Constants.SCOPE_PARAMETER_NAME, scope);
        builder.addParameter(Constants.REDIRECT_URI_PARAMETER_NAME, redirectURI);
        builder.addParameter(Constants.ACR_VALUES_PARAMETER_NAME, acrValues);
        if (!StringUtils.isNullOrEmpty(state))
        {
            builder.addParameter(Constants.STATE_PARAMETER_NAME, state);
        }
        builder.addParameter(Constants.NONCE_PARAMETER_NAME, nonce);
        builder.addParameter(Constants.DISPLAY_PARAMETER_NAME, optionsToBeUsed.getDisplay());
        if (!StringUtils.isNullOrEmpty(optionsToBeUsed.getPrompt()))
        {
            builder.addParameter(Constants.PROMPT_PARAMETER_NAME, optionsToBeUsed.getPrompt());
        }
        builder.addParameter(Constants.MAX_AGE_PARAMETER_NAME, maxAge.toString());
        if (!StringUtils.isNullOrEmpty(optionsToBeUsed.getUiLocales()))
        {
            builder.addParameter(Constants.UI_LOCALES_PARAMETER_NAME, optionsToBeUsed.getUiLocales());
        }
        if (!StringUtils.isNullOrEmpty(optionsToBeUsed.getClaimsLocales()))
        {
            builder.addParameter(Constants.CLAIMS_LOCALES_PARAMETER_NAME, optionsToBeUsed.getClaimsLocales());
        }
        if (!StringUtils.isNullOrEmpty(optionsToBeUsed.getIdTokenHint()))
        {
            builder.addParameter(Constants.ID_TOKEN_HINT_PARAMETER_NAME, optionsToBeUsed.getIdTokenHint());
        }
        if (!StringUtils.isNullOrEmpty(optionsToBeUsed.getLoginHint()))
        {
            builder.addParameter(Constants.LOGIN_HINT_PARAMETER_NAME, optionsToBeUsed.getLoginHint());
        }
        else if (!StringUtils.isNullOrEmpty(encryptedMSISDN))
        {
            builder.addParameter(Constants.LOGIN_HINT_PARAMETER_NAME,
                                 Constants.ENCRYPTED_MSISDN_PREFIX + encryptedMSISDN);
        }
        if (!StringUtils.isNullOrEmpty(optionsToBeUsed.getDtbs()))
        {
            builder.addParameter(Constants.DTBS_PARAMETER_NAME, optionsToBeUsed.getDtbs());
        }

        final StartAuthenticationResponse authenticationResponse = new StartAuthenticationResponse();
        authenticationResponse.setUrl(buildUrl(builder));
        authenticationResponse.setScreenMode(optionsToBeUsed.getScreenMode());

        callback.complete(authenticationResponse);
    }

    /**
     * Get the options to be used for the startAuthentication call.
     * <p/>
     * Use provided values or defaults.
     *
     * @param specifiedOptions Provided value, may be null.
     * @return Options to be used.
     */
    private AuthenticationOptions getOptionsToBeUsed(final AuthenticationOptions specifiedOptions)
    {
        AuthenticationOptions optionsToBeUsed = specifiedOptions;
        if (null == optionsToBeUsed)
        {
            optionsToBeUsed = new AuthenticationOptions();
        }
        return optionsToBeUsed;
    }

    /**
     * Get the options to be used for the requestToken call.
     * <p/>
     * Use provided values or defaults.
     *
     * @param specifiedOptions Provided values, may be null.
     * @return Options to be used.
     */
    private TokenOptions getOptionsToBeUsed(final TokenOptions specifiedOptions)
    {
        TokenOptions optionsToUse = specifiedOptions;
        if (null == optionsToUse)
        {
            optionsToUse = new TokenOptions();
        }
        return optionsToUse;
    }

    /**
     * Utility method to create a URIBuilder or throw a OIDCException.
     *
     * @param authorizationHref The base URI
     * @return A URIBuilder
     * @throws OIDCException Thrown if the authorizationHref is invalid.
     */
    private URIBuilder getUriBuilder(final String authorizationHref) throws OIDCException
    {
        try
        {
            return new URIBuilder(authorizationHref);
        }
        catch (final URISyntaxException ex)
        {
            throw new OIDCException("Invalid URI", ex);
        }
    }

    /**
     * Utility method to get the string version of the URI or thrown an OIDCException.
     *
     * @param builder The URIBuilder.
     * @return The string uri.
     * @throws OIDCException Thrown if the uri is invalid.
     */
    private String buildUrl(final URIBuilder builder) throws OIDCException
    {
        try
        {
            return builder.build().toString();
        }
        catch (final URISyntaxException ex)
        {
            throw new OIDCException("Invalid URI", ex);
        }
    }

    /**
     * Return the acr values to be used.
     * <p/>
     * Either provided values or default value.
     *
     * @param acrValues To provided acr values.
     * @return The acr values to use.
     */
    private String getAcrValues(String acrValues)
    {
        if (StringUtils.isNullOrEmpty(acrValues))
        {
            acrValues = Constants.DEFAULT_ACRVALUES_VALUE;
        }
        return acrValues;
    }

    /**
     * Return the max age value to be used.
     * <p/>
     * Either the provided value or a default value.
     *
     * @param maxAge Provided max age value.
     * @return The max age value to be used.
     */
    private Integer getMaxAge(Integer maxAge)
    {
        if (null == maxAge)
        {
            maxAge = Constants.DEFAULT_MAXAGE_VALUE;
        }
        return maxAge;
    }

    /**
     * Return the scope to be used.
     * <p/>
     * Either the provided value or default value.
     *
     * @param scope The provided scope value.
     * @return The scope value to be used.
     */
    private String getScope(String scope)
    {
        if (StringUtils.isNullOrEmpty(scope))
        {
            scope = Constants.DEFAULT_SCOPE_VALUE;
        }
        return scope;
    }

    /**
     * Build a HttpPost for the requestToken call.
     *
     * @param uri         The URI of the token service.
     * @param redirectURL Redirect URL required by the token service.
     * @param code        The code obtained from the authorization service.
     * @return A HttpPost.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     */
    private HttpPost buildHttpPostForAccessToken(final URI uri, final String redirectURL, final String code) throws
                                                                                           URISyntaxException,
                                                                                           UnsupportedEncodingException
    {
        final URIBuilder uriBuilder = new URIBuilder(uri);

        final HttpPost httpPost = new HttpPost(uriBuilder.build());
        httpPost.setHeader(Constants.CONTENT_TYPE_HEADER_NAME, Constants.CONTENT_TYPE_HEADER_VALUE);
        httpPost.setHeader(Constants.ACCEPT_HEADER_NAME, Constants.ACCEPT_JSON_HEADER_VALUE);

        final List<NameValuePair> nameValuePairs = new ArrayList<>(3);
        nameValuePairs.add(new BasicNameValuePair(Constants.REDIRECT_URI_PARAMETER_NAME, redirectURL));
        nameValuePairs.add(new BasicNameValuePair(Constants.GRANT_TYPE_PARAMETER_NAME,
                                                  Constants.GRANT_TYPE_PARAMETER_VALUE));
        nameValuePairs.add(new BasicNameValuePair(Constants.CODE_PARAMETER_NAME, code));

        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        return httpPost;
    }

    private OIDCException newOIDCExceptionFromRestException(final String message, final RestException restException)
    {
        return new OIDCException(message,
                                 restException.getUri(),
                                 restException.getStatusCode(),
                                 restException.getHeaders(),
                                 restException.getContents(),
                                 restException);
    }

    private OIDCException newOIDCExceptionWithRestResponse(final String message, final RestResponse restResponse, final Throwable ex)
    {
        if (null == restResponse)
        {
            return new OIDCException(message, ex);
        }
        else
        {
            return new OIDCException(message,
                                     restResponse.getUri(),
                                     restResponse.getStatusCode(),
                                     restResponse.getHeaders(),
                                     restResponse.getResponse(),
                                     ex);
        }
    }
}
