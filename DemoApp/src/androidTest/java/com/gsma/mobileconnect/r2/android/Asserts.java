package com.gsma.mobileconnect.r2.android;

import com.google.gson.Gson;
import com.gsma.mobileconnect.r2.ErrorResponse;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.json.DiscoveryResponseData;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

import static junit.framework.Assert.assertEquals;

public class Asserts {

    public static void AssertResponse(ErrorResponse exp, ErrorResponse act) {
        AssertResponseByObjectType(ErrorResponse.class, exp, act);
    }

    public static void AssertResponse(DiscoveryResponse exp, DiscoveryResponse act) {
        AssertResponseByObjectType(DiscoveryResponse.class, exp, act);
    }

    public static void AssertResponse(DiscoveryResponseData exp, DiscoveryResponseData act) {

        Gson gson = new Gson();
        String jsonAct = gson.toJson(act, DiscoveryResponseData.class);
        System.out.println("String jsonAct = gson.toJson(act, typeOfSrc" + jsonAct);
        try {
            JSONObject json = new JSONObject(jsonAct);
            json.remove("subscriberId");
            jsonAct = json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonExp = gson.toJson(exp, DiscoveryResponseData.class);
        if(act.getSubscriberId() != null) {
            jsonExp = jsonExp.replace("\"}}", "\"},\"subscriberId\":\"" + act.getSubscriberId() + "\"}");
        }
        try {
            JSONObject json = new JSONObject(jsonExp);
            json.remove("subscriberId");
            jsonExp = json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("String jsonExp = gson.toJson(exp, typeOfSrc" + jsonExp);
        assertEquals(jsonExp, jsonAct);
    }

    private static void AssertResponseByObjectType(Type typeOfSrc, Object exp, Object act)
    {
        Gson gson = new Gson();
        String jsonAct = gson.toJson(act, typeOfSrc);
        System.out.println("String jsonAct = gson.toJson(act, typeOfSrc" + jsonAct);

        String jsonExp = gson.toJson(exp, typeOfSrc);
        System.out.println("String jsonExp = gson.toJson(exp, typeOfSrc" + jsonExp);
        assertEquals(jsonExp, jsonAct);
    }

    public static void AssertTrue(boolean value)
    {
        assertEquals(true, value);
    }

    public static void AssertTrueWithSmartMessage(boolean value, String message)
    {
        if (!value)
            System.out.println(message);
        assertEquals(true, value);
    }


    public static void AssertNotEquals(int exp, int act)
    {
        if (exp == act)
            System.out.println("Expected and actual values are match, but shouldn't match. exp = " + exp + "; act = " + act);
        assertEquals(exp == act, false);
    }

    public static void AssertResponse(String exp, String act) {
        System.out.println("exp = " + exp + ", act = " + act);
        assertEquals("Values aren't match: exp = " + exp + ", act = " + act, exp, act);
    }


    public static void AssertWithSmartMessage(String exp, String act, String message) {
        if (exp == act)
            System.out.println(message);
        System.out.println("exp = " + exp + ", act = " + act);
        assertEquals("Values aren't match: exp = " + exp + ", act = " + act, exp, act);
    }

    public static void AssertJwtValidation(MobileConnectStatus status)
    {   AssertResponse(null, status.getErrorCode());
        AssertResponse(null, status.getErrorMessage());
        AssertTrueWithSmartMessage(status.getException()==null, "Check Exception in MobileConnectStatus");

        System.out.println("MobileConnectStatus.ResponseType.COMPLETE = " + MobileConnectStatus.ResponseType.COMPLETE);
        System.out.println("status.getResponseType() = " + status.getResponseType());
        AssertWithSmartMessage(MobileConnectStatus.ResponseType.COMPLETE.toString(), status.getResponseType().toString(), "Check ResponseType in MobileConnectStatus");

        /*System.out.println("getAccessToken() = " + status.getRequestTokenResponse().getResponseData().getAccessToken());
        System.out.println("getIdToken() = " + status.getRequestTokenResponse().getResponseData().getIdToken());
        System.out.println("getRefreshToken() = " + status.getRequestTokenResponse().getResponseData().getRefreshToken());
        System.out.println("getTokenType() = " + status.getRequestTokenResponse().getResponseData().getTokenType());
        System.out.println("getExpiresIn() = " + status.getRequestTokenResponse().getResponseData().getExpiresIn());
        System.out.println("getExpiry() = " + status.getRequestTokenResponse().getResponseData().getExpiry());
        System.out.println("getTimeReceived() = " + status.getRequestTokenResponse().getResponseData().getTimeReceived());*/

        AssertTrueWithSmartMessage(status.getRequestTokenResponse().getResponseData().getAccessToken() != null, "Check AccessToken in MobileConnectStatus");
        AssertTrueWithSmartMessage(status.getRequestTokenResponse().getResponseData().getIdToken() != null, "Check AccessToken in MobileConnectStatus");
        //AssertTrueWithSmartMessage(status.getRequestTokenResponse().getResponseData().getRefreshToken() != null, "Check RefreshToken in MobileConnectStatus");
        AssertTrueWithSmartMessage(status.getRequestTokenResponse().getResponseData().getTokenType() != null, "Check TokenType in MobileConnectStatus");
        AssertTrueWithSmartMessage(status.getRequestTokenResponse().getResponseData().getExpiresIn() != null, "Check ExpiresIn in MobileConnectStatus");
        AssertTrueWithSmartMessage(status.getRequestTokenResponse().getResponseData().getExpiry() != null, "Check Expiry in MobileConnectStatus");
        AssertTrueWithSmartMessage(status.getRequestTokenResponse().getResponseData().getTimeReceived() != null, "Check TimeReceived in MobileConnectStatus");

    }

    public static void jsonsAreEqual(JSONObject exp, JSONObject act) throws JSONException {
        ArrayList<String> list = new ArrayList<>();
        Iterator it = exp.keys();
        while(it.hasNext()){
            String key = (String) it.next();
            if(!act.has(key))
                list.add("Can't find key "+key+" in actual response");
            else {
                Object exp_value = exp.get(key);
                Object act_value = act.get(key);
                if (!exp_value.equals(act_value))
                    list.add("Difference: actual: " + key + " = " + act_value + ", but expected " + key + " = " + exp_value);
            }
        }
       assertEquals(list.toString(), list.size(), 0);
    }

    public static void assertRequestTokenResponse(JSONObject exp, JSONObject act) throws JSONException {
        act.remove("iat");
        act.remove("exp");
        act.remove("auth_time");
        jsonsAreEqual(exp, act);
    }

    public static void assertRequestTokenAuthnResponse(JSONObject exp, JSONObject act) throws JSONException {
        act.remove("iat");
        act.remove("exp");
        act.remove("auth_time");
        act.remove("displayed_data");
        jsonsAreEqual(exp, act);
    }

    public static void assertIdentityResponse(JSONObject exp, JSONObject act) throws JSONException {
        act.remove("updated_at");
        jsonsAreEqual(exp, act);
    }

    public static void assertUserinfoAddress(JSONObject exp, JSONObject act) throws JSONException{
        assertEquals(exp.getString("sub"),act.getString("sub"));
        JSONObject expAddress = exp.getJSONObject("address");
        JSONObject actAddress = act.getJSONObject("address");
        jsonsAreEqual(expAddress, actAddress);
    }


}
