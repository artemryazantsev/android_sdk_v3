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

import android.util.Log;

import com.google.common.io.BaseEncoding;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gsma.mobileconnect.helpers.UserInfo;
import com.gsma.mobileconnect.oidc.ParsedIdToken;
import com.gsma.mobileconnect.oidc.RequestTokenResponse;
import com.gsma.mobileconnect.oidc.RequestTokenResponseData;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;

/**
 * Class to hold Json utility functions.
 */
public final class AndroidJsonUtils
{
    private static final String TAG = AndroidJsonUtils.class.getSimpleName();

    /**
     * Extract an error response from the discovery response if any.
     * <p>
     * A discovery response has an error if the error field is present.
     *
     * @param jsonDoc The discovery response to examine.
     * @return The error response if present, null otherwise.
     */
    private static ErrorResponse getErrorResponse(final JsonObject jsonDoc)
    {
        if (null == jsonDoc)
        {
            throw new IllegalArgumentException("Missing argument jsonDoc");
        }

        final String error = getOptionalStringValue(jsonDoc, Constants.ERROR_NAME);
        String errorDescription = getOptionalStringValue(jsonDoc, Constants.ERROR_DESCRIPTION_NAME);
        // Sometimes "description" rather than "error_description" is seen
        final String altErrorDescription = getOptionalStringValue(jsonDoc, Constants.ERROR_DESCRIPTION_ALT_NAME);
        final String errorUri = getOptionalStringValue(jsonDoc, Constants.ERROR_URI_NAME);

        if (StringUtils.isNullOrEmpty(error))
        {
            return null;
        }

        if (!StringUtils.isNullOrEmpty(altErrorDescription))
        {
            if (StringUtils.isNullOrEmpty(errorDescription))
            {
                errorDescription = altErrorDescription;
            }
            else
            {
                errorDescription += " " + altErrorDescription;
            }
        }

        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(error);
        errorResponse.setErrorDescription(errorDescription);
        errorResponse.setErrorUri(errorUri);
        return errorResponse;
    }

    /**
     * Return the long value of an optional child node.
     * <p>
     * Check the parent node for the named child, if found return the long contents of the child node, return null
     * otherwise.
     *
     * @param parentNode The node to check
     * @param name       The name of the optional child.
     * @return Long value of the child node if present, null otherwise.
     */
    private static Long getOptionalLongValue(final JsonNode parentNode, final String name)
    {
        final JsonNode childNode = parentNode.get(name);
        if (null == childNode)
        {
            return null;
        }
        else
        {
            return childNode.asLong();
        }
    }


    /**
     * Parse the UserInfo object.
     *
     * @param response
     * @return
     */
    public static UserInfo parseUserInfo(String response)
    {
        UserInfo userInfo = new UserInfo();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonDoc = null;
        try
        {
            jsonDoc = objectMapper.readTree(response);
            Log.d(TAG, jsonDoc.asText());

        }
        catch (IOException e)
        {
            Log.e("parseUserInfo", e.getMessage());
        }

        ErrorResponse errorResponse = getErrorResponse(jsonDoc);
        if (null != errorResponse)
        {

            return null;
        }
        return userInfo;
    }

    static String getOptionalStringValue(final JsonObject parentNode, final String name)
    {
        final JsonElement childNode = parentNode.get(name);
        return childNode != null && !childNode.isJsonNull() ? childNode.getAsString() : null;
    }
}