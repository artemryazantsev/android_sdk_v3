package com.gsma.mobileconnect.model;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The Model object of the Discovery
 * <p/>
 * Created by nick.copley on 19/02/2016.
 */
public class DiscoveryModel
{
    private AtomicBoolean discoveryInProgress;

    private static final DiscoveryModel ourInstance = new DiscoveryModel();

    private String mcc;

    private String mnc;

    private String discoveryServiceRedirectedURL;

    private String encryptedMSISDN = null;

    public static DiscoveryModel getInstance()
    {
        return ourInstance;
    }

    private DiscoveryModel()
    {
    }

    public String getMcc()
    {
        return this.mcc;
    }

    public void setMcc(final String mcc)
    {
        this.mcc = mcc;
    }

    public String getMnc()
    {
        return this.mnc;
    }

    public void setMnc(final String mnc)
    {
        this.mnc = mnc;
    }

    public String getDiscoveryServiceRedirectedURL()
    {
        return this.discoveryServiceRedirectedURL;
    }

    public void setDiscoveryServiceRedirectedURL(final String discoveryServiceRedirectedURL)
    {
        this.discoveryServiceRedirectedURL = discoveryServiceRedirectedURL;
    }

    public String getEncryptedMSISDN()
    {
        return this.encryptedMSISDN;
    }

    public void setEncryptedMSISDN(final String encryptedMSISDN)
    {
        this.encryptedMSISDN = encryptedMSISDN;
    }

}
