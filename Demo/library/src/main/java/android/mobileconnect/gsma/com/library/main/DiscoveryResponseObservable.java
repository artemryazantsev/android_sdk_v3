package android.mobileconnect.gsma.com.library.main;

import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;

import java.util.Observable;

/**
 * An Observable to wrap the {@link DiscoveryResponse} to notify that it has been retrieved from the Mobile Connect API
 */
public class DiscoveryResponseObservable extends Observable
{
    private DiscoveryResponse discoveryResponse;

    public DiscoveryResponse getValue()
    {
        return discoveryResponse;
    }

    public void setValue(final DiscoveryResponse discoveryResponse)
    {
        this.discoveryResponse = discoveryResponse;
        setChanged();
        notifyObservers();
    }
}