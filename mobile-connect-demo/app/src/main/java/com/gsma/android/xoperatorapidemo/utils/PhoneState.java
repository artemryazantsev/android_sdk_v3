package com.gsma.android.xoperatorapidemo.utils;

/**
 * object holding the phone state that is useful to discovery
 */
public class PhoneState
{
    String msisdn = null; // mobile telephone number of the user

    String simOperator = null; // field comprising Mobile Country Code and

    // Mobile Network Code
    String mcc = null; // Mobile Country Code

    String mnc = null; // Mobile Network Code

    boolean connected = false; // is the device connected to the Internet

    boolean usingMobileData = false; // is the device connecting using

    // mobile/cellular data
    boolean roaming = false; // is the device roaming (international)

    String simSerialNumber = null; // the SIM serial number

    public PhoneState(final String msisdn,
                      final String simOperator,
                      final String mcc,
                      final String mnc,
                      final boolean connected,
                      final boolean usingMobileData,
                      final boolean roaming,
                      final String simSerialNumber)
    {
        this.msisdn = msisdn;
        this.simOperator = simOperator;
        this.mcc = mcc;
        this.mnc = mnc;
        this.connected = connected;
        this.usingMobileData = usingMobileData;
        this.roaming = roaming;
        this.simSerialNumber = simSerialNumber;
    }

    /**
     * @return the msisdn
     */
    public String getMsisdn()
    {
        return this.msisdn;
    }

    /**
     * @param msisdn the msisdn to set
     */
    public void setMsisdn(final String msisdn)
    {
        this.msisdn = msisdn;
    }

    /**
     * @return the simOperator
     */
    public String getSimOperator()
    {
        return this.simOperator;
    }

    /**
     * @param simOperator the simOperator to set
     */
    public void setSimOperator(final String simOperator)
    {
        this.simOperator = simOperator;
    }

    /**
     * @return the mcc
     */
    public String getMcc()
    {
        return this.mcc;
    }

    /**
     * @param mcc the mcc to set
     */
    public void setMcc(final String mcc)
    {
        this.mcc = mcc;
    }

    /**
     * @return the mnc
     */
    public String getMnc()
    {
        return this.mnc;
    }

    /**
     * @param mnc the mnc to set
     */
    public void setMnc(final String mnc)
    {
        this.mnc = mnc;
    }

    /**
     * @return the connected
     */
    public boolean isConnected()
    {
        return this.connected;
    }

    /**
     * @param connected the connected to set
     */
    public void setConnected(final boolean connected)
    {
        this.connected = connected;
    }

    /**
     * @return the usingMobileData
     */
    public boolean isUsingMobileData()
    {
        return this.usingMobileData;
    }

    /**
     * @param usingMobileData the usingMobileData to set
     */
    public void setUsingMobileData(final boolean usingMobileData)
    {
        this.usingMobileData = usingMobileData;
    }

    /**
     * @return the roaming
     */
    public boolean isRoaming()
    {
        return this.roaming;
    }

    /**
     * @param roaming the roaming to set
     */
    public void setRoaming(final boolean roaming)
    {
        this.roaming = roaming;
    }

    /**
     * @return the simSerialNumber
     */
    public String getSimSerialNumber()
    {
        return this.simSerialNumber;
    }

    /**
     * @param simSerialNumber the simSerialNumber to set
     */
    public void setSimSerialNumber(final String simSerialNumber)
    {
        this.simSerialNumber = simSerialNumber;
    }

}
