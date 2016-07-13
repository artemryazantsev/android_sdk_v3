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

package com.gsma.mobileconnect.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gsma.mobileconnect.oidc.ParsedIdToken;
import com.gsma.mobileconnect.oidc.RequestTokenResponse;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class JsonUtilsTest
{
    @Rule
    final public ExpectedException thrown = ExpectedException.none();

    @Test
    public void extractUrl_withMissingLinksNode_shouldReturnNull()
    {
        // GIVEN
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode root = mapper.createObjectNode();

        // WHEN
        final String url = JsonUtils.extractUrl(root, "MISSING");

        // THEN
        assertNull(url);
    }

    @Test
    public void extractUrl_withMissingUrl_shouldReturnNull()
    {
        // GIVEN
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode root = mapper.createObjectNode();
        final ArrayNode links = mapper.createArrayNode();
        root.set("links", links);

        // WHEN
        final String url = JsonUtils.extractUrl(root, "MISSING");

        // THEN
        assertNull(url);
    }

    @Test
    public void extractUrl_withUrl_shouldReturnUrl()
    {
        // GIVEN
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode root = mapper.createObjectNode();
        final ArrayNode links = mapper.createArrayNode();
        root.set("links", links);

        final ObjectNode link = mapper.createObjectNode();
        final String relToFind = "relToFind";
        link.put("rel", relToFind);
        final String expectedHref = "hrefToFind";
        link.put("href", expectedHref);

        links.add(link);

        // WHEN
        final String url = JsonUtils.extractUrl(root, relToFind);

        // THEN
        assertEquals(expectedHref, url);
    }

    @Test
    public void extractUrl_withNoRelField_shouldReturnUrl()
    {
        // GIVEN
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode root = mapper.createObjectNode();
        final ArrayNode links = mapper.createArrayNode();
        root.set("links", links);

        final ObjectNode link = mapper.createObjectNode();
        link.put("href", "href");

        links.add(link);

        // WHEN
        final String url = JsonUtils.extractUrl(root, "relToFind");

        // THEN
        assertNull(url);
    }

    @Test
    public void getErrorResponse_withErrorFields_shouldReturnErrorResponse() throws IOException
    {
        // GIVEN
        final String expectedError = "EXPECTED ERROR";
        final String expectedErrorDescription = "EXPECTED ERROR_DESCRIPTION";
        final String expectedDescription = "EXPECTED DESCRIPTION";
        final String expectedErrorUri = "EXPECTED ERROR_URI";
        final String json =
                "{ \"error\": \"" + expectedError + "\", " + "\"error_description\": \"" + expectedErrorDescription +
                "\", " + "\"description\": \"" + expectedDescription + "\", " + "\"error_uri\": \"" + expectedErrorUri +
                "\" }";
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode root = mapper.readTree(json);

        // WHEN
        final ErrorResponse errorResponse = JsonUtils.getErrorResponse(root);

        // THEN
        assertNotNull(errorResponse);
        assertEquals(expectedError, errorResponse.get_error());
        assertEquals(expectedErrorDescription + " " + expectedDescription, errorResponse.get_error_description());
        assertEquals(expectedErrorUri, errorResponse.get_error_uri());
    }

    @Test
    public void getErrorResponse_withNoError_shouldReturnNull() throws IOException
    {
        // GIVEN
        final String json = "{ \"no-error\": \"value\", " + "\"error_description\": \"error_description\", " +
                            "\"description\": \"description\", " + "\"error_uri\": \"error_uri\" }";
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode root = mapper.readTree(json);

        // WHEN
        final ErrorResponse errorResponse = JsonUtils.getErrorResponse(root);

        // THEN
        assertNull(errorResponse);
    }

    @Test
    public void getErrorResponse_withNoErrorDescription_shouldReturnDescription() throws IOException
    {
        // GIVEN
        final String expectedError = "EXPECTED ERROR";
        final String expectedDescription = "EXPECTED DESCRIPTION";
        final String expectedErrorUri = "EXPECTED ERROR_URI";
        final String json =
                "{ \"error\": \"" + expectedError + "\", " + "\"description\": \"" + expectedDescription + "\", " +
                "\"error_uri\": \"" + expectedErrorUri + "\" }";
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode root = mapper.readTree(json);

        // WHEN
        final ErrorResponse errorResponse = JsonUtils.getErrorResponse(root);

        // THEN
        assertNotNull(errorResponse);
        assertEquals(expectedError, errorResponse.get_error());
        assertEquals(expectedDescription, errorResponse.get_error_description());
        assertEquals(expectedErrorUri, errorResponse.get_error_uri());
    }

    @Test
    public void getErrorResponse_withNoDescription_shouldReturnErrorDescription() throws IOException
    {
        // GIVEN
        final String expectedError = "EXPECTED ERROR";
        final String expectedErrorDescription = "EXPECTED ERROR_DESCRIPTION";
        final String expectedErrorUri = "EXPECTED ERROR_URI";
        final String json =
                "{ \"error\": \"" + expectedError + "\", " + "\"error_description\": \"" + expectedErrorDescription +
                "\", " + "\"error_uri\": \"" + expectedErrorUri + "\" }";
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode root = mapper.readTree(json);

        // WHEN
        final ErrorResponse errorResponse = JsonUtils.getErrorResponse(root);

        // THEN
        assertNotNull(errorResponse);
        assertEquals(expectedError, errorResponse.get_error());
        assertEquals(expectedErrorDescription, errorResponse.get_error_description());
        assertEquals(expectedErrorUri, errorResponse.get_error_uri());
    }

    @Test
    public void parseJson_withValidJson_shouldSucceed() throws IOException
    {
        // GIVEN
        final String json = "{ \"field\": \"value\" }";

        // WHEN
        final JsonNode root = JsonUtils.parseJson(json);

        // THEN
        assertNotNull(root);
        assertFalse(root.isMissingNode());
    }

    @Test
    public void parseJson_withEmptyString_shouldReturnMissingNode() throws IOException
    {
        // GIVEN
        final String json = "";

        // WHEN
        final JsonNode root = JsonUtils.parseJson(json);

        // THEN
        assertNotNull(root);
        assertTrue(root.isMissingNode());
    }

    @Test
    public void parseJson_withInvalidJson_shouldThrowIOException() throws IOException
    {
        // GIVEN
        final String json = "{ \"field\" }";

        // THEN
        this.thrown.expect(IOException.class);

        // WHEN
        JsonUtils.parseJson(json);
    }

    @Test
    public void parseOperatorIdentifiedDiscoveryResult_withValidOperatorIdentifiedDiscoveryResult_shouldSucceed() throws
                                                                                                                  IOException
    {
        // GIVEN
        final String expectedClientId = "EXPECTED CLIENT_ID";
        final String expectedClientSecret = "EXPECTED CLIENT_SECRET";
        final String expectedAuthorizationHref = "EXPECTED AUTHORIZATION_HREF";
        final String expectedTokenHref = "EXPECTED TOKEN_HREF";
        final String expectedUserInfoHref = "EXPECTED USER_INFO_HREF";
        final String expectedPremiumInfoHref = "EXPECTED PREMIUM_INFO_HREF";
        final String jsonStr =
                "{ \"response\": {" + " \"client_id\": \"" + expectedClientId + "\", " + " \"client_secret\": \"" +
                expectedClientSecret + "\", " + " \"apis\": {" + " \"operatorid\": {" + " \"link\": [" +
                " { \"rel\": \"authorization\", " + " \"href\": \"" + expectedAuthorizationHref + "\"}, " +
                " { \"rel\": \"token\", " + " \"href\": \"" + expectedTokenHref + "\"}, " +
                " { \"rel\": \"userinfo\", " + " \"href\": \"" + expectedUserInfoHref + "\"}, " +
                " { \"rel\": \"premiuminfo\", " + " \"href\": \"" + expectedPremiumInfoHref + "\"} " + "]}}}}";
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode root = mapper.readTree(jsonStr);

        // WHEN
        final ParsedOperatorIdentifiedDiscoveryResult parsedOperatorIdentifiedDiscoveryResult = JsonUtils
                .parseOperatorIdentifiedDiscoveryResult(
                root);

        // THEN
        assertNotNull(parsedOperatorIdentifiedDiscoveryResult);
        assertEquals(expectedClientId, parsedOperatorIdentifiedDiscoveryResult.getClientId());
        assertEquals(expectedClientSecret, parsedOperatorIdentifiedDiscoveryResult.getClientSecret());
        assertEquals(expectedAuthorizationHref, parsedOperatorIdentifiedDiscoveryResult.getAuthorizationHref());
        assertEquals(expectedTokenHref, parsedOperatorIdentifiedDiscoveryResult.getTokenHref());
        assertEquals(expectedUserInfoHref, parsedOperatorIdentifiedDiscoveryResult.getUserInfoHref());
        assertEquals(expectedPremiumInfoHref, parsedOperatorIdentifiedDiscoveryResult.getPremiumInfoHref());
    }

    @Test
    public void parseOperatorIdentifiedDiscoveryResult_withInvalidOperatorIdentifiedDiscoveryResult_shouldReturnNull
            () throws
                                                                                                                       IOException
    {
        // GIVEN
        final String jsonStr =
                "{ \"response\": {" + " \"client_id\": \"XXX\", " + " \"client_secret\": \"XXX\", " + " \"apis\": {" +
                " \"operatorid\": {" + " \"not-link\": [" + " { \"rel\": \"authorization\", " +
                " \"href\": \"XXX\"}, " + " { \"rel\": \"token\", " + " \"href\": \"XXX\"}, " +
                " { \"rel\": \"userinfo\", " + " \"href\": \"XXX\"}, " + " { \"rel\": \"premiuminfo\", " +
                " \"href\": \"XXX\"} " + "]}}}}";
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode root = mapper.readTree(jsonStr);

        // WHEN
        final ParsedOperatorIdentifiedDiscoveryResult parsedOperatorIdentifiedDiscoveryResult = JsonUtils
                .parseOperatorIdentifiedDiscoveryResult(
                root);

        // THEN
        assertNull(parsedOperatorIdentifiedDiscoveryResult);
    }

    @Test
    public void parseRequestTokenResponse_withValidResponse_shouldReturnResponseData() throws IOException
    {
        // GIVEN
        final String expectedAccessToken = "EXPECTED ACCESS_TOKEN";
        final String expectedTokenType = "EXPECTED TOKEN_TYPE";
        final String expectedRefreshToken = "EXPECTED REFRESH_TOKEN";
        final Integer expectedExpiresIn = Integer.valueOf("3600");
        final String expectedIdToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
                                       ".eyJub25jZSI6Im5vbmNlXzAucDNvd3pxbmR5dyIsInN1YiI6ImMwY2Q3NjFmZDA3ZTVlMTk3NDk3NmZiMzVkYzA2MmRlIiwiYW1yIjoiU01TX1VSTCIsImF1dGhfdGltZSI6MTQ1MDg4NDczNCwiYWNyIjoiMiIsImF6cCI6IjBjOWRmMjE5IiwiaWF0IjoxNDUwODg0NzMzLCJleHAiOjE0NTA4ODgzMzMsImF1ZCI6WyIwYzlkZjIxOSJdLCJpc3MiOiJodHRwOi8vb3BlcmF0b3JfYS5zYW5kYm94Lm1vYmlsZWNvbm5lY3QuaW8vb2lkYy9hY2Nlc3N0b2tlbiJ9.wlkZgNtN8ezAia6dZ8l2dYQBryB9skcIVN_6XzZn2mI";
        final String expectedJsonStr =
                "{ \"access_token\": \"" + expectedAccessToken + "\", " + " \"token_type\": \"" + expectedTokenType +
                "\", " + " \"expires_in\": \"" + expectedExpiresIn + "\", " + " \"refresh_token\": \"" +
                expectedRefreshToken + "\", " + " \"id_token\": \"" + expectedIdToken + "\"}";

        final Calendar expectedTime = Calendar.getInstance();
        final Calendar expectedExpires = (Calendar) expectedTime.clone();
        expectedExpires.add(Calendar.SECOND, expectedExpiresIn);

        // WHEN
        final RequestTokenResponse requestTokenResponse = JsonUtils.parseRequestTokenResponse(expectedTime, expectedJsonStr);

        // THEN
        assertFalse(requestTokenResponse.hasErrorResponse());
        assertTrue(requestTokenResponse.hasResponseData());
        assertEquals(expectedTime, requestTokenResponse.getResponseData().getTimeReceived());
        assertEquals(expectedAccessToken, requestTokenResponse.getResponseData().get_access_token());
        assertEquals(expectedTokenType, requestTokenResponse.getResponseData().get_token_type());
        assertEquals(expectedRefreshToken, requestTokenResponse.getResponseData().get_refresh_token());
        assertEquals(expectedJsonStr, requestTokenResponse.getResponseData().getOriginalResponse());
        assertEquals(expectedExpiresIn, requestTokenResponse.getResponseData().get_expires_in());
        assertEquals(expectedExpires, requestTokenResponse.getResponseData().getExpires());
        assertNotNull(requestTokenResponse.getResponseData().getParsedIdToken());
    }

    @Test
    public void parseRequestTokenResponse_withErrorResponse_shouldReturnErrorResponse() throws IOException
    {
        // GIVEN
        final String expectedError = "EXPECTED ERROR";
        final String expectedErrorDescription = "EXPECTED ERROR_DESCRIPTION";
        final String expectedJsonStr =
                "{ \"error\": \"" + expectedError + "\", " + " \"error_description\": \"" + expectedErrorDescription +
                "\"}";

        final Calendar expectedTime = Calendar.getInstance();

        // WHEN
        final RequestTokenResponse requestTokenResponse = JsonUtils.parseRequestTokenResponse(expectedTime, expectedJsonStr);

        // THEN
        assertFalse(requestTokenResponse.hasResponseData());
        assertTrue(requestTokenResponse.hasErrorResponse());
        assertEquals(expectedError, requestTokenResponse.getErrorResponse().get_error());
        assertEquals(expectedErrorDescription, requestTokenResponse.getErrorResponse().get_error_description());
    }

    @Test
    public void createParsedIdToken_withValidToken_shouldSucceed() throws IOException
    {
        // GIVEN
        final String expectedIdToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
                                       ".eyJub25jZSI6Im5vbmNlXzAucDNvd3pxbmR5dyIsInN1YiI6ImMwY2Q3NjFmZDA3ZTVlMTk3NDk3NmZiMzVkYzA2MmRlIiwiYW1yIjoiU01TX1VSTCIsImF1dGhfdGltZSI6MTQ1MDg4NDczNCwiYWNyIjoiMiIsImF6cCI6IjBjOWRmMjE5IiwiaWF0IjoxNDUwODg0NzMzLCJleHAiOjE0NTA4ODgzMzMsImF1ZCI6WyIwYzlkZjIxOSJdLCJpc3MiOiJodHRwOi8vb3BlcmF0b3JfYS5zYW5kYm94Lm1vYmlsZWNvbm5lY3QuaW8vb2lkYy9hY2Nlc3N0b2tlbiJ9.wlkZgNtN8ezAia6dZ8l2dYQBryB9skcIVN_6XzZn2mI";
        final String expectedPcr = "c0cd761fd07e5e1974976fb35dc062de";
        final String expectedNonce = "nonce_0.p3owzqndyw";

        // WHEN
        final ParsedIdToken parsedIdToken = JsonUtils.createParsedIdToken(expectedIdToken);

        // THEN
        assertEquals(expectedIdToken, parsedIdToken.get_id_token());
        assertEquals(expectedPcr, parsedIdToken.get_pcr());
        assertEquals(expectedNonce, parsedIdToken.get_nonce());
        assertNotNull(parsedIdToken.get_id_token_claims());
    }

    @Test
    public void createParsedIdToken_withInvalidToken_shouldThrowException() throws IOException
    {
        // GIVEN
        final String expectedIdToken =
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9eyJub25jZSI6Im5vbmNlXzAucDNvd3pxbmR5dyIsInN1YiI6ImMwY2Q3NjFmZDA3ZTVlMTk3NDk3NmZiMzVkYzA2MmRlIiwiYW1yIjoiU01TX1VSTCIsImF1dGhfdGltZSI6MTQ1MDg4NDczNCwiYWNyIjoiMiIsImF6cCI6IjBjOWRmMjE5IiwiaWF0IjoxNDUwODg0NzMzLCJleHAiOjE0NTA4ODgzMzMsImF1ZCI6WyIwYzlkZjIxOSJdLCJpc3MiOiJodHRwOi8vb3BlcmF0b3JfYS5zYW5kYm94Lm1vYmlsZWNvbm5lY3QuaW8vb2lkYy9hY2Nlc3N0b2tlbiJ9.wlkZgNtN8ezAia6dZ8l2dYQBryB9skcIVN_6XzZn2mI";

        // THEN
        this.thrown.expect(IllegalArgumentException.class);

        // WHEN
        JsonUtils.createParsedIdToken(expectedIdToken);
    }

    @Test
    public void createParsedIdToken_withInvalidPayload_shouldThrowException() throws IOException
    {
        // GIVEN
        final String expectedIdToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
                                 ".XXXeyJub25jZSI6Im5vbmNlXzAucDNvd3pxbmR5dyIsInN1YiI6ImMwY2Q3NjFmZDA3ZTVlMTk3NDk3NmZiMzVkYzA2MmRlIiwiYW1yIjoiU01TX1VSTCIsImF1dGhfdGltZSI6MTQ1MDg4NDczNCwiYWNyIjoiMiIsImF6cCI6IjBjOWRmMjE5IiwiaWF0IjoxNDUwODg0NzMzLCJleHAiOjE0NTA4ODgzMzMsImF1ZCI6WyIwYzlkZjIxOSJdLCJpc3MiOiJodHRwOi8vb3BlcmF0b3JfYS5zYW5kYm94Lm1vYmlsZWNvbm5lY3QuaW8vb2lkYy9hY2Nlc3N0b2tlbiJ9.wlkZgNtN8ezAia6dZ8l2dYQBryB9skcIVN_6XzZn2mI";

        // THEN
        this.thrown.expect(IOException.class);

        // WHEN
        JsonUtils.createParsedIdToken(expectedIdToken);
    }
}
