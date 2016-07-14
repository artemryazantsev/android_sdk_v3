package com.gsma.android;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.gsma.android.xoperatorapidemo.utils.PhoneState;
import com.gsma.android.xoperatorapidemo.utils.PhoneUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP, manifest = "src/main/AndroidManifest.xml")
public class PhoneStateTest
{

    TelephonyManager telephonyManager;

    ConnectivityManager connectivityManager;

    NetworkInfo networkInfo;

    PhoneState state;

    PhoneState nullPhoneState = new PhoneState(null, null, null, null, false, false, false, null);

    final String usernumber = "07111111111";

    final String simSerialNumber = "xxxx";

    final String validSimOperator = "123456";

    final String inValidSimOperator = "11";

    final String negativeSimOperator = "-123456";

    final boolean networkIsConnected = true;

    final boolean roamingIsOn = true;

    @Before
    public void setup()
    {
        this.telephonyManager = mock(TelephonyManager.class);
        this.connectivityManager = mock(ConnectivityManager.class);
        this.networkInfo = mock(NetworkInfo.class);
    }

    /*Tests*/

    @Test
    public void getMcc_ShouldReturnCorrectMCC_WhenValidSimOperator()
    {
        //set up connectivity manager
        networkInfoSetUpForConnectivityManager(this.networkIsConnected, this.roamingIsOn, ConnectivityManager.TYPE_MOBILE);
        //mock SIM serial number
        when(this.telephonyManager.getSimSerialNumber()).thenReturn(this.simSerialNumber);
        //mock SIM operator
        when(this.telephonyManager.getSimOperator()).thenReturn(this.validSimOperator);
        this.state = PhoneUtils.getPhoneState(this.telephonyManager, this.connectivityManager);
        assertEquals("123", this.state.getMcc());
    }

    @Test
    public void getMcc_ShouldReturnNull_WhenInValidSimOperator()
    {
        //set up connectivity manager
        networkInfoSetUpForConnectivityManager(this.networkIsConnected, this.roamingIsOn, ConnectivityManager.TYPE_MOBILE);
        //mock SIM operator
        when(this.telephonyManager.getSimOperator()).thenReturn(this.inValidSimOperator);
        this.state = PhoneUtils.getPhoneState(this.telephonyManager, this.connectivityManager);
        assertNull(this.state.getMcc());
    }

    @Test
    public void getMcc_ShouldReturnNull_WhenNegativeSimOperator()
    {
        //set up connectivity manager
        networkInfoSetUpForConnectivityManager(this.networkIsConnected, this.roamingIsOn, ConnectivityManager.TYPE_MOBILE);
        //mock SIM operator
        when(this.telephonyManager.getSimOperator()).thenReturn(this.negativeSimOperator);
        this.state = PhoneUtils.getPhoneState(this.telephonyManager, this.connectivityManager);
        assertNull(this.state.getMcc());
    }

    @Test
    public void getMnc_ShouldReturnCorrectMNC_WhenValidSimOperator()
    {
        //set up connectivity manager
        networkInfoSetUpForConnectivityManager(this.networkIsConnected, this.roamingIsOn, ConnectivityManager.TYPE_MOBILE);
        //mock SIM serial number
        when(this.telephonyManager.getSimSerialNumber()).thenReturn(this.simSerialNumber);
        //mock SIM operator
        when(this.telephonyManager.getSimOperator()).thenReturn(this.validSimOperator);
        this.state = PhoneUtils.getPhoneState(this.telephonyManager, this.connectivityManager);
        assertEquals("456", this.state.getMnc());
    }

    @Test
    public void getMnc_ShouldReturnNull_WhenInValidSimOperator()
    {
        //set up connectivity manager
        networkInfoSetUpForConnectivityManager(this.networkIsConnected, this.roamingIsOn, ConnectivityManager.TYPE_MOBILE);
        //mock SIM operator
        when(this.telephonyManager.getSimOperator()).thenReturn(this.inValidSimOperator);
        this.state = PhoneUtils.getPhoneState(this.telephonyManager, this.connectivityManager);
        assertNull(this.state.getMnc());
    }

    @Test
    public void getMnc_ShouldReturnNull_WhenNegativeSimOperator()
    {
        //set up connectivity manager
        networkInfoSetUpForConnectivityManager(this.networkIsConnected, this.roamingIsOn, ConnectivityManager.TYPE_MOBILE);
        //mock SIM operator
        when(this.telephonyManager.getSimOperator()).thenReturn(this.negativeSimOperator);
        this.state = PhoneUtils.getPhoneState(this.telephonyManager, this.connectivityManager);
        assertNull(this.state.getMnc());
    }

    @Test
    public void getMsisdn_ShouldReturnCorrectMsisdn_WhenValidNumber()
    {
        //set up connectivity manager
        networkInfoSetUpForConnectivityManager(this.networkIsConnected, this.roamingIsOn, ConnectivityManager.TYPE_MOBILE);
        //mock mobile telephone number of user
        when(this.telephonyManager.getLine1Number()).thenReturn(this.usernumber);
        this.state = PhoneUtils.getPhoneState(this.telephonyManager, this.connectivityManager);
        assertEquals(this.usernumber, this.state.getMsisdn());
    }

    @Test
    public void getSimOperator_ShouldReturnCorrectSimOperator_WhenValidSimOperator()
    {
        //set up connectivity manager
        networkInfoSetUpForConnectivityManager(this.networkIsConnected, this.roamingIsOn, ConnectivityManager.TYPE_MOBILE);
        //mock SIM operator
        when(this.telephonyManager.getSimOperator()).thenReturn(this.validSimOperator);
        this.state = PhoneUtils.getPhoneState(this.telephonyManager, this.connectivityManager);
        assertEquals(this.validSimOperator, this.state.getSimOperator());
    }

    @Test
    public void getSimSerialNumber_ShouldReturnCorrectSimSerialNumber_WhenValidSimSerialNumber()
    {
        //set up connectivity manager
        networkInfoSetUpForConnectivityManager(this.networkIsConnected, this.roamingIsOn, ConnectivityManager.TYPE_MOBILE);
        //mock SIM serial number
        when(this.telephonyManager.getSimSerialNumber()).thenReturn(this.simSerialNumber);
        this.state = PhoneUtils.getPhoneState(this.telephonyManager, this.connectivityManager);
        assertEquals(this.simSerialNumber, this.state.getSimSerialNumber());
    }

    @Test
    public void isConnected_ShouldReturnConnectionStatusAsTrue_WhenStatusConnectionIsTrue()
    {
        //mock Active Network Info, mock NetworkInfo first
        networkInfoSetUpForConnectivityManager(this.networkIsConnected, this.roamingIsOn, ConnectivityManager.TYPE_MOBILE);
        this.state = PhoneUtils.getPhoneState(this.telephonyManager, this.connectivityManager);
        assertEquals(this.networkIsConnected, this.state.isConnected());
    }

    @Test
    public void isRoaming_ShouldReturnRoamingStatusAsTrue_WhenStatusConnectionIsFalse()
    {

        networkInfoSetUpForConnectivityManager(this.networkIsConnected, this.roamingIsOn, ConnectivityManager.TYPE_MOBILE);
        this.state = PhoneUtils.getPhoneState(this.telephonyManager, this.connectivityManager);
        assertEquals(this.roamingIsOn, this.state.isRoaming());
    }

    @Test
    public void isUsingMobileData_ShouldReturnTrue_WhenValidNetworkInfo()
    {
        //mock Active Network Info, mock NetworkInfo first
        networkInfoSetUpForConnectivityManager(this.networkIsConnected, this.roamingIsOn, ConnectivityManager.TYPE_MOBILE);
        this.state = PhoneUtils.getPhoneState(this.telephonyManager, this.connectivityManager);
        assertEquals(true, this.state.isUsingMobileData());
    }

    /**
     * This method sets up the Connectivity Manager and mocks the methods associated to it according to the method
     * parameters
     *
     * @param isNetworkConnected
     * @param isRoaming
     * @param type
     */
    public void networkInfoSetUpForConnectivityManager(final boolean isNetworkConnected, final boolean isRoaming, final int type)
    {
        //mock Active Network Info, mock NetworkInfo first
        when(this.networkInfo.isConnected()).thenReturn(isNetworkConnected);
        when(this.networkInfo.isRoaming()).thenReturn(isRoaming);
        when(this.networkInfo.getType()).thenReturn(type);

        when(this.connectivityManager.getActiveNetworkInfo()).thenReturn(this.networkInfo);
    }
}