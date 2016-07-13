/*
 *                                   SOFTWARE USE PERMISSION
 *
 *  By downloading and accessing this software and associated documentation files ("Software") you are granted the
 *  unrestricted right to deal in the Software, including, without limitation the right to use, copy, modify, publish,
 *  sublicense and grant such rights to third parties, subject to the following conditions:
 *
 *  The following copyright notice and this permission notice shall be included in all copies, modifications or
 *  substantial portions of this Software: Copyright © 2016 GSM Association.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS," WITHOUT WARRANTY OF ANY KIND, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 *  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. YOU
 *  AGREE TO INDEMNIFY AND HOLD HARMLESS THE AUTHORS AND COPYRIGHT HOLDERS FROM AND AGAINST ANY SUCH LIABILITY.
 */

package com.gsma.mobileconnect.helpers;

import com.gsma.mobileconnect.discovery.CompleteSelectedOperatorDiscoveryOptions;
import com.gsma.mobileconnect.discovery.DiscoveryOptions;
import com.gsma.mobileconnect.oidc.AuthenticationOptions;
import com.gsma.mobileconnect.oidc.TokenOptions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class MobileConnectConfigTest
{
    @Test
    public void getDiscoveryOptions_allValuesSetInConfig_allValuesShouldBePopulated()
    {
        // GIVEN
        final String localClientIP = "10.0.0.1";
        final String specifiedClientIP = "127.0.0.1";
        final String identifiedClientIP = "127.0.0.2";
        final String identifiedMCC = "901";
        final String identifiedMNC = "01";
        final int timeout = 40;
        final boolean manuallySelect = true;
        final boolean cookiesEnabled = false;
        final boolean usingMobileData = true;
        final boolean shouldClientIPBeAddedToDiscoveryRequest = true;

        final MobileConnectConfig config = new MobileConnectConfig();
        config.setLocalClientIP(localClientIP);
        config.setClientIP(specifiedClientIP);
        config.setIdentifiedMCC(identifiedMCC);
        config.setIdentifiedMNC(identifiedMNC);
        config.setNetworkTimeout(timeout);
        config.setManuallySelect(manuallySelect);
        config.setCookiesEnabled(cookiesEnabled);
        config.setUsingMobileData(usingMobileData);
        config.setShouldClientIPBeAddedToDiscoveryRequest(shouldClientIPBeAddedToDiscoveryRequest);

        // WHEN
        final DiscoveryOptions discoveryOptions = config.getDiscoveryOptions(identifiedClientIP);

        //THEN
        assertEquals(localClientIP, discoveryOptions.getLocalClientIP());
        assertEquals(specifiedClientIP, discoveryOptions.getClientIP());
        assertEquals(identifiedMCC, discoveryOptions.getIdentifiedMCC());
        assertEquals(identifiedMNC, discoveryOptions.getIdentifiedMNC());
        assertEquals(timeout, discoveryOptions.getTimeout());
        assertEquals(manuallySelect, discoveryOptions.isManuallySelect());
        assertEquals(cookiesEnabled, discoveryOptions.isCookiesEnabled());
        assertEquals(usingMobileData, discoveryOptions.isUsingMobileData());
    }

    @Test
    public void getDiscoveryOptions_noValuesSetInConfig_onlyDefaultValuesSet()
    {
        // GIVEN
        final String identifiedClientIP = "127.0.0.2";
        final int defaultTimeout = 30000;

        final MobileConnectConfig config = new MobileConnectConfig();
        // WHEN
        final DiscoveryOptions discoveryOptions = config.getDiscoveryOptions(identifiedClientIP);

        //THEN
        assertNull(discoveryOptions.getLocalClientIP());
        assertNull(discoveryOptions.getClientIP());
        assertNull(discoveryOptions.getIdentifiedMCC());
        assertNull(discoveryOptions.getIdentifiedMNC());
        assertEquals(defaultTimeout, discoveryOptions.getTimeout());
        assertFalse(discoveryOptions.isManuallySelect());
        assertTrue(discoveryOptions.isCookiesEnabled());
        assertNull(discoveryOptions.isUsingMobileData());
    }

    @Test
    public void getDiscoveryOptions_setClientIPShouldBeAddedToDiscoveryRequest_shouldAddIdentifiedClientIP()
    {
        // GIVEN
        final String identifiedClientIP = "127.0.0.2";
        final boolean shouldClientIPBeAddedToDiscoveryRequest = true;

        final MobileConnectConfig config = new MobileConnectConfig();
        config.setShouldClientIPBeAddedToDiscoveryRequest(shouldClientIPBeAddedToDiscoveryRequest);

        // WHEN
        final DiscoveryOptions discoveryOptions = config.getDiscoveryOptions(identifiedClientIP);

        //THEN
        assertEquals(identifiedClientIP, discoveryOptions.getClientIP());
    }

    @Test
    public void shouldProvideIPreferences()
    {
        // GIVEN
        final String clientId = "CLIENT_ID";
        final String clientSecret = "CLIENT_SECRET";
        final String discoveryURL = "DISCOVERY_URL";

        final MobileConnectConfig config = new MobileConnectConfig();
        config.setClientId(clientId);
        config.setClientSecret(clientSecret);
        config.setDiscoveryURL(discoveryURL);

        // THEN
        assertEquals(clientId, config.getClientId());
        assertEquals(clientSecret, config.getClientSecret());
        assertEquals(discoveryURL, config.getDiscoveryURL());
    }

    @Test
    public void getCompleteSelectedOperatorDiscoveryOptions_allValuesSetInConfig_allValuesShouldBePopulated()
    {
        // GIVEN
        final boolean cookiesEnabled = false;
        final int timeout = 50;
        final MobileConnectConfig config = new MobileConnectConfig();
        config.setCookiesEnabled(cookiesEnabled);
        config.setNetworkTimeout(timeout);

        // WHEN
        final CompleteSelectedOperatorDiscoveryOptions options = config.getCompleteSelectedOperatorDiscoveryOptions();

        // THEN
        assertEquals(cookiesEnabled, options.isCookiesEnabled());
        assertEquals(timeout, options.getTimeout());
    }

    @Test
    public void getCompleteSelectedOperatorDiscoveryOptions_noValuesSetInConfig_onlyDefaultValuesSet()
    {
        // GIVEN
        final int defaultTimeout = 30000;
        final MobileConnectConfig config = new MobileConnectConfig();

        // WHEN
        final CompleteSelectedOperatorDiscoveryOptions options = config.getCompleteSelectedOperatorDiscoveryOptions();

        // THEN
        assertTrue(options.isCookiesEnabled());
        assertEquals(defaultTimeout, options.getTimeout());
    }

    @Test
    public void getAuthenticationOptions_withAllValuesSetInConfig_allValuesShouldBePopulated()
    {
        // GIVEN
        final int timeout = 50;
        final String claimsLocales = "CLAIMS-LOCALES";
        final String display = "DISPLAY";
        final String dtbs = "DTBS";
        final String idTokenHint = "ID-TOKEN-HINT";
        final String loginHint = "LOGIN-HINT";
        final String prompt = "PROMPT";
        final String screenMode = "SCREEN-MODE";
        final String uiLocales = "UI-LOCALES";
        final MobileConnectConfig config = new MobileConnectConfig();
        config.setAuthorizationTimeout(timeout);
        config.setClaimsLocales(claimsLocales);
        config.setDisplay(display);
        config.setDtbs(dtbs);
        config.setIdTokenHint(idTokenHint);
        config.setLoginHint(loginHint);
        config.setPrompt(prompt);
        config.setScreenMode(screenMode);
        config.setUiLocales(uiLocales);

        // WHEN
        final AuthenticationOptions options = config.getAuthenticationOptions();

        // THEN
        assertEquals(timeout, options.getTimeout());
        assertEquals(claimsLocales, options.getClaimsLocales());
        assertEquals(display, options.getDisplay());
        assertEquals(dtbs, options.getDtbs());
        assertEquals(idTokenHint, options.getIdTokenHint());
        assertEquals(loginHint, options.getLoginHint());
        assertEquals(prompt, options.getPrompt());
        assertEquals(screenMode, options.getScreenMode());
        assertEquals(uiLocales, options.getUiLocales());
    }

    @Test
    public void getAuthenticationOptions_withNoValuesSetInConfig_onlyDefaultValuesSet()
    {
        // GIVEN
        final int defaultTimeout = 300000;
        final String defaultDisplay = "page";
        final String defaultScreenMode = "overlay";
        final MobileConnectConfig config = new MobileConnectConfig();

        // WHEN
        final AuthenticationOptions options = config.getAuthenticationOptions();

        // THEN
        assertEquals(defaultTimeout, options.getTimeout());
        assertNull(options.getClaimsLocales());
        assertEquals(defaultDisplay, options.getDisplay());
        assertNull(options.getDtbs());
        assertNull(options.getIdTokenHint());
        assertNull(options.getLoginHint());
        assertNull(options.getPrompt());
        assertEquals(defaultScreenMode, options.getScreenMode());
        assertNull(options.getUiLocales());
    }

    @Test
    public void getTokenOptions_withAllValuesSetInConfig_allValuesShouldBePopulated()
    {
        // GIVEN
        final int timeout = 50;
        final boolean idTokenValidationRequired = false;
        final MobileConnectConfig config = new MobileConnectConfig();
        config.setNetworkTimeout(timeout);
        config.setIdTokenValidationRequired(idTokenValidationRequired);

        // WHEN
        final TokenOptions options = config.getTokenOptions();

        // THEN
        assertEquals(timeout, options.getTimeout());
        assertEquals(idTokenValidationRequired, options.isCheckIdTokenSignature());
    }

    @Test
    public void getTokenOptions_withNoValuesSetInConfig_onlyDefaultValuesSet()
    {
        // GIVEN
        final int defaultTimeout = 30000;
        final boolean defaultCheckIdTokenSignature = true;
        final MobileConnectConfig config = new MobileConnectConfig();

        // WHEN
        final TokenOptions options = config.getTokenOptions();

        // THEN
        assertEquals(defaultTimeout, options.getTimeout());
        assertEquals(defaultCheckIdTokenSignature, options.isCheckIdTokenSignature());
    }

}
