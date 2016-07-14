package com.gsma.android.xoperatorapidemo.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * extract useful phone state information and return in the form of a PhoneState
 * object
 */
public final class PhoneUtils
{
    private static final String TAG = "PhoneUtils";

    /**
     * convert information which can be obtained from the Android OS into
     * PhoneState information necessary for discovery
     *
     * @param telephonyMgr
     * @param connectivityMgr
     * @return
     */
    public static PhoneState getPhoneState(final TelephonyManager telephonyMgr, final ConnectivityManager connectivityMgr)
    {
		/*
         * the users' phone number is obtained - this is not always available/
		 * valid
		 */
        final String msisdn = telephonyMgr.getLine1Number();

		/*
         * get the active network
		 */
        final NetworkInfo activeNetwork = connectivityMgr.getActiveNetworkInfo();

		/*
		 * check if the device is currently connected
		 */
        final boolean connected = activeNetwork != null && activeNetwork.isConnected();

		/*
		 * check if the device is currently roaming
		 */
        final boolean roaming = activeNetwork != null && activeNetwork.isRoaming();

		/*
		 * check if the device is using mobile/cellular data
		 */
        final boolean usingMobileData = activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;




		/*
		 * get the SIM serial number
		 */
        final String simSerialNumber = telephonyMgr.getSimSerialNumber();

		/*
		 * the simOperator indicates the registered network MCC/MNC the
		 * networkOperator indicates the current network MCC/MNC
		 */
        final String simOperator = telephonyMgr.getSimOperator();
		/*
		 * Mobile Country Code is obtained from the first three digits of
		 * simOperator, Mobile Network Code is any remaining digits
		 */
        String mcc = null;
        String mnc = null;
        if (simOperator != null && simOperator.length() > 3)
        {
            if (Integer.parseInt(simOperator) > 0)
            {
                mcc = simOperator.substring(0, 3);
                mnc = simOperator.substring(3);
            }

        }

		/*
		 * return a new PhoneState object from the parameters used in discovery
		 */
        return new PhoneState(msisdn, simOperator, mcc, mnc, connected, usingMobileData, roaming, simSerialNumber);

    }
}
