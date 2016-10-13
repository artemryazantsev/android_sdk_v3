package android.mobileconnect.gsma.com.library.main;

import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;

/**
 * Created by usmaan.dad on 13/10/2016.
 */

public class DiscoveryResponseEvent
{
    private DiscoveryResponse discoveryResponse;

    public DiscoveryResponseEvent(final DiscoveryResponse discoveryResponse)
    {
        this.discoveryResponse = discoveryResponse;
    }

    public DiscoveryResponse getDiscoveryResponse()
    {
        return discoveryResponse;
    }

    public void setDiscoveryResponse(DiscoveryResponse discoveryResponse)
    {
        this.discoveryResponse = discoveryResponse;
    }
}
