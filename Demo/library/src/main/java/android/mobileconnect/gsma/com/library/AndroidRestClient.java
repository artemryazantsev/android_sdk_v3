package android.mobileconnect.gsma.com.library;

/**
 * An Android compliant RestClient
 * Created by usmaan.dad on 30/09/2016.
 */
public class AndroidRestClient //implements IRestClient
{
    //    @Override
    //public RestResponse get(URI uri,
    //                            RestAuthentication authentication,
    //                            String sourceIp,
    //                            List<KeyValuePair> queryParams,
    //                            Iterable<KeyValuePair> iterable) throws RequestFailedException
    //    {
    //        URIBuilder uriBuilder = new URIBuilder(uri);
    //        if (queryParams != null)
    //        {
    //            uriBuilder.addParameters(new ArrayList(queryParams));
    //        }
    //
    //        try
    //        {
    //            HttpUriRequest use = this.createRequest(HttpUtils.HttpMethod.GET,
    //                                                    uriBuilder.build(),
    //                                                    authentication,
    //                                                    sourceIp,
    //                                                    cookies).build();
    //            return submitRequest(use);
    //        }
    //        catch (URISyntaxException var8)
    //        {
    //            throw new RequestFailedException(HttpUtils.HttpMethod.GET, uri, var8);
    //        }
    //    }
    //
    //    @Override
    //    public RestResponse postFormData(URI uri,
    //                                     RestAuthentication restAuthentication,
    //                                     List<KeyValuePair> list,
    //                                     String s,
    //                                     Iterable<KeyValuePair> iterable) throws RequestFailedException
    //    {
    //        return null;
    //    }
    //
    //    @Override
    //    public RestResponse postJsonContent(URI uri,
    //                                        RestAuthentication restAuthentication,
    //                                        Object o,
    //                                        String s,
    //                                        Iterable<KeyValuePair> iterable) throws RequestFailedException
    //    {
    //        return null;
    //    }
    //
    //    @Override
    //    public RestResponse postStringContent(URI uri,
    //                                          RestAuthentication restAuthentication,
    //                                          String s,
    //                                          ContentType contentType,
    //                                          String s1,
    //                                          Iterable<KeyValuePair> iterable) throws RequestFailedException
    //    {
    //        return null;
    //    }
    //
    //    @Override
    //    public RestResponse postContent(URI uri,
    //                                    RestAuthentication restAuthentication,
    //                                    HttpEntity httpEntity,
    //                                    String s,
    //                                    Iterable<KeyValuePair> iterable) throws RequestFailedException
    //    {
    //        return null;
    //    }
    //
    //    private RequestBuilder createRequest(HttpUtils.HttpMethod method, URI uri, RestAuthentication
    // authentication, String sourceIp, Iterable<KeyValuePair> cookies) {
    //        RequestBuilder builder = RequestBuilder.create(((HttpUtils.HttpMethod) ObjectUtils.requireNonNull
    // (method, "method")).name()).setUri((URI)ObjectUtils.requireNonNull(uri, "uri")).setConfig(this.requestConfig);
    //        if(cookies != null) {
    //            StringBuilder cookieBuilder = new StringBuilder();
    //            Iterator var8 = cookies.iterator();
    //
    //            while(var8.hasNext()) {
    //                KeyValuePair cookie = (KeyValuePair)var8.next();
    //                cookieBuilder.append(cookie.getKey()).append('=').append(cookie.getValue()).append(';');
    //            }
    //
    //            builder.addHeader("Cookie", cookieBuilder.toString());
    //        }
    //
    //        if(!StringUtils.isNullOrEmpty(sourceIp)) {
    //            builder.addHeader("X-Source-IP", sourceIp);
    //        }
    //
    //        if(authentication != null) {
    //            builder.addHeader("Authorization", authentication.getScheme() + " " + authentication.getParameter());
    //        }
    //
    //        return builder;
    //    }
    //
    //    private RestResponse submitRequest(final HttpUriRequest request) throws RequestFailedException {
    //
    //        ObjectUtils.requireNonNull(request, "request");
    //        ScheduledFuture abortFuture = this.scheduledExecutorService.schedule(new Runnable() {
    //            public void run() {
    //                RestClient.LOGGER.debug("Aborting httpMethod={} request to uri={} as request timed out,
    // timeout={} ms", new Object[]{request.getMethod(), LogUtils.maskUri(request.getURI(), RestClient.LOGGER, Level
    // .DEBUG), Long.valueOf(RestClient.this.timeout)});
    //                request.abort();
    //            }
    //        }, this.timeout, TimeUnit.MILLISECONDS);
    //
    //        try {
    //            request.addHeader("Accept", ContentType.APPLICATION_JSON.withCharset("UTF-8").getMimeType());
    //            LOGGER.debug("Issuing httpMethod={} request to uri={}", request.getMethod(), LogUtils.maskUri
    // (request.getURI(), LOGGER, Level.DEBUG));
    //            return (RestResponse)this.httpClient.execute(request, new RestClient.RestResponseHandler(request
    // .getMethod(), request.getURI(), abortFuture));
    //        } catch (InterruptedIOException var4) {
    //            if(request.isAborted()) {
    //                LOGGER.warn("Failed to perform httpMethod={} to uri={}; timed out, timeout={} ms", new
    // Object[]{request.getMethod(), LogUtils.maskUri(request.getURI(), LOGGER, Level.WARN), Long.valueOf(this
    // .timeout), var4});
    //                throw new RequestFailedException(request.getMethod(), request.getURI(), new TimeoutException
    // (String.format("HTTP %s request was aborted after %s ms", new Object[]{request.getMethod(), Long.valueOf(this
    // .timeout)})));
    //            } else {
    //                LOGGER.warn("Failed to perform httpMethod={} to uri={}; interrupted IO", new Object[]{request
    // .getMethod(), LogUtils.maskUri(request.getURI(), LOGGER, Level.WARN), var4});
    //                throw new RequestFailedException(request.getMethod(), request.getURI(), var4);
    //            }
    //        } catch (Exception var5) {
    //            LOGGER.warn("Failed to perform httpMethod={} to uri={}", new Object[]{request.getMethod(), LogUtils
    // .maskUri(request.getURI(), LOGGER, Level.WARN), var5});
    //            throw new RequestFailedException(request.getMethod(), request.getURI(), var5);
    //        }
}
