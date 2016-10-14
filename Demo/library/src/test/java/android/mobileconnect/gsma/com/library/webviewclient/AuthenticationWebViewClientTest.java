package android.mobileconnect.gsma.com.library.webviewclient;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by usmaan.dad on 14/10/2016.
 */

public class AuthenticationWebViewClientTest
{
    private AuthenticationWebViewClient authenticationWebViewClient;

    @Before
    public void setUp() throws Exception
    {
        authenticationWebViewClient = new AuthenticationWebViewClient(null, null, null, null, null);
    }

    @Test
    public void testQualifyUrlTrue()
    {
        // Given
        String url = "www.google.com/?code=901";

        // When
        boolean containsMNCMCC = authenticationWebViewClient.qualifyUrl(url);

        // Then
        assertEquals(containsMNCMCC, true);
    }

    @Test
    public void testQualifyUrlFalse()
    {
        // Given
        String url = "www.google.com/";

        // When
        boolean containsMNCMCC = authenticationWebViewClient.qualifyUrl(url);

        // Then
        assertEquals(containsMNCMCC, false);
    }
}
