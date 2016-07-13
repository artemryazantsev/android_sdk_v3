package com.gsma.android.model;

/**
 * This is where the MMC etc will be stored.
 * Created by nick.copley on 19/02/2016.
 */
public class DeviceModel
{
    private static final DeviceModel ourInstance = new DeviceModel();

    private DeviceModel()
    {
    }

    public static DeviceModel getInstance()
    {
        return ourInstance;
    }
}
