package com.gsma.mobileconnect.r2.android.bus;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public enum BusManager
{
    INSTANCE;

    private final Bus bus = new Bus(ThreadEnforcer.ANY);

    public static void post(final Object event)
    {
        INSTANCE.bus.post(event);
    }

    public static void register(final Object target)
    {
        INSTANCE.bus.register(target);
    }

    public static void unregister(final Object target)
    {
        INSTANCE.bus.unregister(target);
    }

}