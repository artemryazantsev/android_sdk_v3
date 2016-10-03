package android.mobileconnect.gsma.com.library;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by usmaan.dad on 12/08/2016.
 */
public class DiscoveryModel
{
    private AtomicBoolean discoveryInProgress;

    private static DiscoveryModel ourInstance = new DiscoveryModel();

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
        return mcc;
    }

    public void setMcc(String mcc)
    {
        this.mcc = mcc;
    }

    public String getMnc()
    {
        return mnc;
    }

    public void setMnc(String mnc)
    {
        this.mnc = mnc;
    }

    public String getDiscoveryServiceRedirectedURL()
    {
        return discoveryServiceRedirectedURL;
    }

    public void setDiscoveryServiceRedirectedURL(String discoveryServiceRedirectedURL)
    {
        this.discoveryServiceRedirectedURL = discoveryServiceRedirectedURL;
    }

    public String getEncryptedMSISDN()
    {
        return encryptedMSISDN;
    }

    public void setEncryptedMSISDN(String encryptedMSISDN)
    {
        this.encryptedMSISDN = encryptedMSISDN;
    }
}
