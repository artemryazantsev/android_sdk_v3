package com.gsma.mobileconnect.r2.android.compatibility;

import android.util.Base64;

import com.gsma.mobileconnect.r2.encoding.IMobileConnectEncodeDecoder;

/**
 * An implementation of Encoding and Decoding from an array of {@link byte} to {@link String}
 * Created by usmaan.dad on 26/08/2016.
 */
public class AndroidMobileConnectEncodeDecoder implements IMobileConnectEncodeDecoder
{
    @Override
    public String encodeToBase64(final byte[] value)
    {
        return Base64.encodeToString(value, Base64.NO_WRAP);
    }

    @Override
    public byte[] decodeFromBase64(final String value)
    {
        return Base64.decode(value.replaceAll("=",""), Base64.URL_SAFE);
    }
}