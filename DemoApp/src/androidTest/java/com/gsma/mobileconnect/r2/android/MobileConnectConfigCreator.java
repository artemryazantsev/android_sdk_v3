package com.gsma.mobileconnect.r2.android;

import android.content.Context;

import com.gsma.mobileconnect.r2.MobileConnectConfig;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by n.rusak on 10/14/16.
 */

public class MobileConnectConfigCreator {

    Context context;
    GsmaDataReader dataReader;
    String defaultClientId;
    String defaultClientSecret;
    String defaultServiceUri;
    String defaultRedirectUri;
    boolean defaultCaching;

    public MobileConnectConfigCreator(Context context){
        this.context = context;
        this.dataReader = new GsmaDataReader(context, GsmaDataReader.Environment.SandboxR2);
        try {
            defaultClientId = dataReader.getConsumerKey();
            defaultClientSecret = dataReader.getConsumerSecret();
            defaultServiceUri = dataReader.getServiceUri();
            defaultRedirectUri = dataReader.getRedirectUri();
            defaultCaching = false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public MobileConnectConfigCreator(Context context, GsmaDataReader.Environment environment){
        this.context = context;
        this.dataReader = new GsmaDataReader(context, environment);
        try {
            defaultClientId = dataReader.getConsumerKey();
            defaultClientSecret = dataReader.getConsumerSecret();
            defaultServiceUri = dataReader.getServiceUri();
            defaultRedirectUri = dataReader.getRedirectUri();
            defaultCaching = false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public MobileConnectConfig createConfig(String clientId, String clientSecret, String serviceUri, String redirectUri, boolean caching){

        URI discoveryUri = null;

        try
        {
            discoveryUri = new URI(serviceUri);
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        URI redirect = null;
        try
        {
            redirect = new URI(redirectUri);
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
        return new MobileConnectConfig.Builder().withClientId(clientId)
                .withClientSecret(
                        clientSecret)
                .withDiscoveryUrl(discoveryUri)
                .withRedirectUrl(redirect)
                .withCacheResponsesWithSessionId(
                        caching)
                .build();
    }

    public MobileConnectConfig createConfig(String clientId, String clientSecret, String serviceUri, String redirectUri){
        return createConfig(clientId, clientSecret, serviceUri, redirectUri, defaultCaching);
    }

    public MobileConnectConfig createConfigClientId(String clientId) throws FileNotFoundException {
        return createConfig(clientId, defaultClientSecret, defaultServiceUri, defaultRedirectUri);
    }

    public MobileConnectConfig createConfigClientSecret(String clientSecret) throws FileNotFoundException {
        return createConfig(defaultClientId, clientSecret, defaultServiceUri, defaultRedirectUri);
    }

    public MobileConnectConfig createConfigServiceUri(String serviceUri) throws FileNotFoundException {
        return createConfig(defaultClientId, defaultClientSecret, serviceUri, defaultRedirectUri);
    }

    public MobileConnectConfig createConfigRedirectUri(String redirectUri) throws FileNotFoundException {
        return createConfig(defaultClientId, defaultClientSecret, defaultServiceUri, redirectUri);
    }

    public MobileConnectConfig createConfigCaching(boolean caching) throws FileNotFoundException {
        return createConfig(defaultClientId, defaultClientSecret, defaultServiceUri, defaultRedirectUri, caching);
    }

    public MobileConnectConfig createConfig() throws FileNotFoundException {
        return createConfig(defaultClientId, defaultClientSecret, defaultServiceUri, defaultRedirectUri);
    }

    public MobileConnectConfig createConfig(boolean caching) throws FileNotFoundException {
        return createConfig(defaultClientId, defaultClientSecret, defaultServiceUri, defaultRedirectUri, caching);
    }
}
