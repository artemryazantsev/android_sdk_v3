package com.gsma.mobileconnect.r2.android.bus;

import junit.framework.Assert;
import junit.framework.TestCase;

public class BusManagerTests extends TestCase
{

    public void testValueOf()
    {
        try
        {
            @SuppressWarnings("UnusedAssignment") BusManager thing = BusManager.valueOf("");
            Assert.fail("Should have thrown");
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertNotNull(e);
        }
    }

    public void testValueOfInstance()
    {
        BusManager thing = BusManager.valueOf("INSTANCE");
        Assert.assertNotNull(thing);
    }

    public void testValues()
    {
        BusManager[] array = BusManager.values();
        Assert.assertTrue(array.length == 1);
        Assert.assertNotNull(array[0]);
    }
}
