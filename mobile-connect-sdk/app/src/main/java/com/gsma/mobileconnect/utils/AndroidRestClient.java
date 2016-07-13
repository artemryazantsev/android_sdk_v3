package com.gsma.mobileconnect.utils;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An Android specific version which overrides the incompatible aspects of the Java API.
 * Created by nick.copley on 26/02/2016.
 */
public class AndroidRestClient extends RestClient
{

    @Override
    public HttpClientContext getHttpClientContext(final String username, final String password, final URI uriForRealm)
    {
        final String host = uriForRealm.getHost();
        int port = uriForRealm.getPort();
        if (port == -1)
        {
            if ("http".equalsIgnoreCase(uriForRealm.getScheme()))
            {
                port = 80;
            }
            else if ("https".equalsIgnoreCase(uriForRealm.getScheme()))
            {
                port = 443;
            }
        }

        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(host, port),
                                           new UsernamePasswordCredentials(username, password));
        final BasicAuthCache authCache = new BasicAuthCache();
        authCache.put(new HttpHost(host, port, uriForRealm.getScheme()), new BasicScheme());
        final HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credentialsProvider);
        context.setAuthCache(authCache);
        return context;
    }

    @Override
    public RestResponse callRestEndPoint(final HttpRequestBase httpRequest,
                                         final HttpClientContext context,
                                         final int timeout,
                                         final List<KeyValuePair> cookiesToProxy) throws RestException, IOException
    {
        final CookieStore cookieStore = this.buildCookieStore(httpRequest.getURI().getHost(), cookiesToProxy);

        RestResponse restResponse;
        CloseableHttpClient closeableHttpClient = null;
        try
        {
            closeableHttpClient = this.getHttpClient(cookieStore);
            final Timer timer = new Timer();
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    httpRequest.abort();
                }
            }, (long) timeout);
            final RequestConfig localConfig = RequestConfig.custom()
                                                           .setConnectionRequestTimeout(timeout)
                                                           .setConnectTimeout(timeout)
                                                           .setSocketTimeout(timeout)
                                                           .setCookieSpec("standard")
                                                           .build();
            context.setRequestConfig(localConfig);

            final CloseableHttpResponse httpResponse = closeableHttpClient.execute(httpRequest, context);
            restResponse = this.buildRestResponse(httpRequest, httpResponse);
            checkRestResponse(restResponse);
        }
        catch (final IOException ioe)
        {
            final String requestUri = httpRequest.getURI().toString();
            if (httpRequest.isAborted())
            {
                throw new RestException("Rest end point did not respond", requestUri);
            }
            else
            {
                throw new RestException("Rest call failed", requestUri, ioe);
            }
        }
        finally
        {
            if (closeableHttpClient != null)
            {
                closeableHttpClient.close();
            }
        }

        return restResponse;
    }

    private CookieStore buildCookieStore(final String host, final List<KeyValuePair> cookiesToProxy)
    {
        final BasicCookieStore cookieStore = new BasicCookieStore();
        if (cookiesToProxy == null)
        {
            return cookieStore;
        }
        else
        {

            for (final KeyValuePair cookieToProxy : cookiesToProxy)
            {
                final BasicClientCookie cookie = new BasicClientCookie(cookieToProxy.getKey(),
                                                                       cookieToProxy.getValue());
                cookie.setDomain(host);
                cookieStore.addCookie(cookie);
            }

            return cookieStore;
        }
    }

    private CloseableHttpClient getHttpClient(final CookieStore cookieStore)
    {
        return HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }

    private RestResponse buildRestResponse(final HttpRequestBase request,
                                           final CloseableHttpResponse closeableHttpResponse) throws IOException
    {
        final String requestUri = request.getURI().toString();
        final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
        final HeaderIterator headers = closeableHttpResponse.headerIterator();
        final List<KeyValuePair> headerList = new ArrayList<>(3);

        while (headers.hasNext())
        {
            final Header httpEntity = headers.nextHeader();
            headerList.add(new KeyValuePair(httpEntity.getName(), httpEntity.getValue()));
        }

        final HttpEntity entity = closeableHttpResponse.getEntity();
        final String responseData = EntityUtils.toString(entity);
        return new RestResponse(requestUri, statusCode, headerList, responseData);
    }

    private static void checkRestResponse(final RestResponse response) throws RestException
    {
        if (!response.isJsonContent())
        {
            throw new RestException("Invalid response", response);
        }
    }
}