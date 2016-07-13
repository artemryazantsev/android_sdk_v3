package com.gsma.mobileconnect.helpers;

import android.util.Log;

import com.gsma.mobileconnect.utils.KeyValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * Helper function developed to extract code, state and error values.
 */
public class ParameterList
{
    private final ArrayList<KeyValuePair> parameterList = new ArrayList<>();

    private static final String TAG = "ParameterList";

    private void put(final String key, final String value)
    {
        final KeyValuePair kv = new KeyValuePair(key, value);
        this.parameterList.add(kv);
    }

    public String getValue(final String key)
    {
        boolean found = false;
        String value = null;
        if (key != null)
        {
            for (int i = 0; i < this.parameterList.size() && !found; i++)
            {
                if (key.equals(this.parameterList.get(i).getKey()))
                {
                    found = true;
                    value = this.parameterList.get(i).getValue();
                }
            }
        }
        Log.d(TAG, "Parameter " + key + " = " + value);
        return value;
    }

    public static ParameterList getKeyValuesFromUrl(final String url, final int start)
    {
        final ParameterList parameterList = new ParameterList();
        parameterList.loadKeyValuesFromUrl(url, start);
        return parameterList;
    }

    private void loadKeyValuesFromUrl(final String url, final int start)
    {
        final String[] urlParts = url.split("[\\?&]");
        for (int i = start; i < urlParts.length; i++)
        {
            final String part = urlParts[i];
            final String[] kv = part.split("=", 2);
            if (kv.length == 2)
            {
                String key = kv[0];
                String value = kv[1];
                try
                {
                    key = URLDecoder.decode(key, "UTF-8");
                }
                catch (final UnsupportedEncodingException e)
                {
                    Log.e("loadKeyValuesFromUrl", e.getMessage());
                }
                try
                {
                    value = URLDecoder.decode(value, "UTF-8");
                }
                catch (final UnsupportedEncodingException e)
                {
                    Log.e("loadKeyValuesFromUrl", e.getMessage());
                }

                Log.d(TAG, "Returned " + key + " = " + value);
                put(key, value);
            }
            else if (kv.length == 1)
            {
                String key = kv[0];
                try
                {
                    key = URLDecoder.decode(key, "UTF-8");
                }
                catch (final UnsupportedEncodingException e)
                {
                    Log.e("loadKeyValuesFromUrl", e.getMessage());
                }
                put(key, null);
            }
        }
    }
}
