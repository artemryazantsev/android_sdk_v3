package com.gsma.mobileconnect.r2.android;

import android.content.Context;

import com.gsma.mobileconnect.r2.ErrorResponse;
import com.gsma.mobileconnect.r2.android.demo.BuildConfig;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.json.DiscoveryResponseData;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.rest.RestResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;


public class GsmaDataReader {

    private final String stringForReplaceSubscriberName = "\"subscriberId\": ";
    private final String stringForReplaceSubscriberValue = "\"\"";
    private final String stringForReplaceTtlName = "\"ttl\": ";
    private final String stringForReplaceTtlValue = "1";

    public enum Environment{
        Production("Production"), Integration("Integration"), SandboxR2("SandboxR2"), SandboxR1("SandboxR1");

        String prefix;
        Environment(String prefix){
            this.prefix = prefix;
        }

        public String getPrefix(){
            return prefix;
        }
    }

    public enum DiscoveryItems{
        Spain("Spain"),SpainNoSecret("SpainNoSecret"),Myanmar("Myanmar"), OperatorG("OperatorG");

        String prefix;
        DiscoveryItems(String prefix){this.prefix = prefix;}

        public String getPrefix(){return prefix;}
    }

    public enum ErrorList{
        NullResponse("responseTypeError"),NoScope("noScopeError"),NonePrompt("nonePromptError"),InvalidScope("invalidScopeError"),
        invalidCodeError("invalidCodeError"),ClientID("clientIDError"),TokenClientID("tokenClientIDError"),TokenClientSecret("tokenClientSecretError"),
        InvalidRequest("invalid_request"), invalidExpectedStateError("invalidExpectedStateError");

        String prefix;
        ErrorList(String prefix){this.prefix = prefix;}

        public String getPrefix(){return prefix;}
    }


    private final XmlUtils xmlUtils;
    private final Environment environment;
    private final String envPath;
    private Environment defaultEnv = Environment.SandboxR2;
    private Context context;

    public GsmaDataReader(Context context){
        this(context, getEnvByPrefix(BuildConfig.env));
    }

    public GsmaDataReader(Context context, Environment environment){
        if(environment==null)
            environment = defaultEnv;
        this.context = context;
        xmlUtils = new XmlUtils(context);
        this.environment = environment;
        envPath = String.format("//env[@name='%1$s']", environment.getPrefix());
    }

    private static Environment getEnvByPrefix(String prefix){
        Environment envList[] = Environment.values();
        for(int i=0;i<envList.length;i++){
            if(envList[i].getPrefix().equals(prefix))
                return envList[i];
        }
        return null;
    }

    private Environment getRunnerEnvironment() throws FileNotFoundException {
        return Environment.valueOf(xmlUtils.getNodeText("/config/runner/environment"));
    }

    public String getInvalidParam() throws FileNotFoundException {
        return xmlUtils.getNodeText(envPath + "/invalidParam");
    }

    public String getAuthRedirect() throws FileNotFoundException {
        return xmlUtils.getNodeText(envPath + "/authRedirect");
    }

    public String getSourceIp() throws FileNotFoundException {
        return xmlUtils.getNodeText(envPath + "/sourceIp");
    }

    public String getAppShortName() throws FileNotFoundException {
        return xmlUtils.getNodeText(envPath + "/ApplicationShortName");
    }
    public String getServiceUri() throws FileNotFoundException {
        return xmlUtils.getNodeText(envPath + "/serviceUri");
    }

    public String getConsumerKey() throws FileNotFoundException {
        return xmlUtils.getNodeText(envPath + "/consumerKey");
    }

    public String getConsumerSecret() throws FileNotFoundException {
        return xmlUtils.getNodeText(envPath + "/consumerSecret");
    }

    public String getConsentUrl() throws FileNotFoundException {
        return xmlUtils.getNodeText(envPath + "/consentUrl");    }

    public String getMsisdn() throws FileNotFoundException {
        return xmlUtils.getNodeText(envPath + "/msisdn");
    }

    public String getEncrMsisdn() throws FileNotFoundException {
        return xmlUtils.getNodeText(envPath+"/encryptedMsisdn");
    }

    public String getRedirectUri() throws FileNotFoundException {
        return xmlUtils.getNodeText(envPath + "/redirectUri");
    }

    public String getMcc() throws FileNotFoundException {
        return xmlUtils.getNodeText(envPath + "/mcc");
    }

    public String getMnc() throws FileNotFoundException {
        return xmlUtils.getNodeText(envPath + "/mnc");
    }

    public enum UserInfoDataPrefixes{
        userinfo_openid_profile_email_address_phone_offline_access("userinfo_openid_profile_email_address_phone_offline_access",
                "openid profile email address phone offline access"),
        userinfo_openid("userinfo_openid", "openid"),
        userinfo_openid_profile("userinfo_openid_profile", "openid profile"),
        userinfo_openid_email("userinfo_openid_email", "openid email"),
        userinfo_openid_address("userinfo_openid_address", "openid address"),
        userinfo_openid_phone("userinfo_openid_phone", "openid phone"),
        userinfo_openid_offline_access("userinfo_openid_offline_access", "openid offline_access");

        String xmlTag;
        String scope;
        UserInfoDataPrefixes(String xmlTag, String scope){
            this.xmlTag = xmlTag;
            this.scope = scope;
        }

        public String getXmlTag(){
            return xmlTag;
        }

        public String getScope(){
            return scope;
        }
    }

    public DiscoveryResponse getDiscoveryResponseData(DiscoveryItems item) throws IOException, JsonDeserializationException {
             RestResponse restResp = new RestResponse.Builder().withContent(xmlUtils.getNodeText(envPath + "/discoveryItemType" + item.getPrefix())).build();

        return DiscoveryResponse.fromRestResponse(restResp, new JacksonJsonService());
    }

    public String getDiscoveryProviderMetadataResponse() throws FileNotFoundException {
        return xmlUtils.getNodeText(envPath + "/discoveryProviderMetadata");
    }

    public String getdiscoveryProviderMetadataCachedResponse() throws FileNotFoundException {
        return xmlUtils.getNodeText(envPath + "/discoveryProviderMetadataCached");
    }

    public DiscoveryResponse getDiscoveryResponseDataWithExpectedParameters(DiscoveryItems item, String ClientIdEncode, String ClientSecretEncode, String SubscriberId, long ttl) throws IOException, JsonDeserializationException {

        String jsonString = xmlUtils.getNodeText(envPath + "/discoveryItemType" + item.getPrefix());
        RestResponse restResp = new RestResponse.Builder().withContent(jsonString).build();
        DiscoveryResponse expWithoutExpParameters = DiscoveryResponse.fromRestResponse(restResp, new JacksonJsonService());

        String target = stringForReplaceSubscriberName + stringForReplaceSubscriberValue;
        String replacement;
        if (SubscriberId == null) replacement = stringForReplaceSubscriberName + "null";
        else replacement = stringForReplaceSubscriberName + "\"" + SubscriberId + "\"";

        String targetTtl = stringForReplaceTtlName + stringForReplaceTtlValue;
        String replacementTtl;
        replacementTtl = stringForReplaceTtlName + String.valueOf(ttl);

        String jsonStringWithExpParameters = jsonString.
                replace(target, replacement).replace(targetTtl, replacementTtl).
                replace(expWithoutExpParameters.getResponseData().getResponse().getClientId(), ClientIdEncode).
                replace(expWithoutExpParameters.getResponseData().getResponse().getClientSecret(), ClientSecretEncode);

        RestResponse restRespWithExpParameters = new RestResponse.Builder().withContent(jsonStringWithExpParameters).build();

        System.out.println("restRespWithExpParameters.getContent-----" + restRespWithExpParameters.getContent());
        DiscoveryResponse response = DiscoveryResponse.fromRestResponse(restRespWithExpParameters, new JacksonJsonService());
        System.out.println("response.getResponseData().getSubscriberId()----" + response.getResponseData().getSubscriberId());
        return response;
    }

    public DiscoveryResponse getErrorJsonObject() throws IOException {
        RestResponse restResp = new RestResponse.Builder().withContent(xmlUtils.getNodeText(envPath + "/errorJsonObject")).build();
        try {
            return DiscoveryResponse.fromRestResponse(restResp, new JacksonJsonService());
        } catch (JsonDeserializationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ErrorResponse getErrorNoScope() throws FileNotFoundException, JsonDeserializationException {
        String jsonString = xmlUtils.getNodeText(envPath + "/noScopeError");
        RestResponse restResp = new RestResponse.Builder().withContent(jsonString).build();
        return DiscoveryResponse.fromRestResponse(restResp, new JacksonJsonService()).getErrorResponse();
    }

    public ErrorResponse getErrorUnsupportedScope() throws FileNotFoundException, JsonDeserializationException {
        String jsonString = xmlUtils.getNodeText(envPath + "/unsupportedScopeError");
        RestResponse restResp = new RestResponse.Builder().withContent(jsonString).build();
        return DiscoveryResponse.fromRestResponse(restResp, new JacksonJsonService()).getErrorResponse();
    }

    public ErrorResponse getErrorUnsupportedAcrValues() throws FileNotFoundException, JsonDeserializationException {
        String jsonString = xmlUtils.getNodeText(envPath + "/invalidAcrValuesError");
        RestResponse restResp = new RestResponse.Builder().withContent(jsonString).build();
        return DiscoveryResponse.fromRestResponse(restResp, new JacksonJsonService()).getErrorResponse();
    }

    public ErrorResponse getError(ErrorList errorType) throws IOException, JsonDeserializationException {

        System.out.println("errorType - " + errorType);
        String jsonString = xmlUtils.getNodeText(envPath + "/"+ errorType);
        RestResponse restResp = new RestResponse.Builder().withContent(jsonString).build();
        return DiscoveryResponse.fromRestResponse(restResp, new JacksonJsonService()).getErrorResponse();
    }

    public ErrorResponse getErrorNullRedirectUri() throws IOException, JsonDeserializationException {

        String jsonString = xmlUtils.getNodeText(envPath + "/noRedirectUrlError");
        RestResponse restResp = new RestResponse.Builder().withContent(jsonString).build();
        return DiscoveryResponse.fromRestResponse(restResp, new JacksonJsonService()).getErrorResponse();
    }

    public JSONObject getDecodedIdToken() throws FileNotFoundException, JSONException {
        String jsonString = xmlUtils.getNodeText(envPath + "/decodedIdToken");
        return new JSONObject(jsonString);
    }

    public JSONObject getIdentityDefaultResponse() throws FileNotFoundException, JSONException {
        String jsonString = xmlUtils.getNodeText(envPath + "/identityDefault");
        return new JSONObject(jsonString);
    }

    public JSONObject getIdentitySignupResponse() throws FileNotFoundException, JSONException {
        String jsonString = xmlUtils.getNodeText(envPath + "/identitySignup");
        return new JSONObject(jsonString);
    }

    public JSONObject getIdentityPhoneResponse() throws FileNotFoundException, JSONException {
        String jsonString = xmlUtils.getNodeText(envPath + "/identityPhone");
        return new JSONObject(jsonString);
    }

    public JSONObject getIdentityNationalIdResponse() throws FileNotFoundException, JSONException {
        String jsonString = xmlUtils.getNodeText(envPath + "/identityNationalId");
        return new JSONObject(jsonString);
    }

    public ErrorResponse getErrorInvalidMncMcc() throws IOException, JsonDeserializationException {

        String jsonString = xmlUtils.getNodeText(envPath + "/invalidMncMccError");
        RestResponse restResp = new RestResponse.Builder().withContent(jsonString).build();
        return DiscoveryResponse.fromRestResponse(restResp, new JacksonJsonService()).getErrorResponse();
    }

    public ErrorResponse getErrorInvalidScope() throws IOException, JsonDeserializationException {

        String jsonString = xmlUtils.getNodeText(envPath + "/invalidScopeError");
        RestResponse restResp = new RestResponse.Builder().withContent(jsonString).build();
        return DiscoveryResponse.fromRestResponse(restResp, new JacksonJsonService()).getErrorResponse();
    }

    public ErrorResponse getErrorInvalidStateError() throws IOException, JsonDeserializationException {

        String jsonString = xmlUtils.getNodeText(envPath + "/invalidExpectedStateError");
        RestResponse restResp = new RestResponse.Builder().withContent(jsonString).build();
        return DiscoveryResponse.fromRestResponse(restResp, new JacksonJsonService()).getErrorResponse();
    }

    public ErrorResponse getErrorInvalidAcrValue() throws IOException, JsonDeserializationException {

        String jsonString = xmlUtils.getNodeText(envPath + "/noneAcrValues");
        RestResponse restResp = new RestResponse.Builder().withContent(jsonString).build();
        return DiscoveryResponse.fromRestResponse(restResp, new JacksonJsonService()).getErrorResponse();
    }

    public ErrorResponse getErrorInvalidMsisdn() throws IOException, JsonDeserializationException {

        String jsonString = xmlUtils.getNodeText(envPath + "/invalidMsisdnError");
        RestResponse restResp = new RestResponse.Builder().withContent(jsonString).build();
        return DiscoveryResponse.fromRestResponse(restResp, new JacksonJsonService()).getErrorResponse();
    }


    public DiscoveryResponseData getRedirectJsonObject() throws IOException {
        RestResponse restResp = new RestResponse.Builder().withContent(xmlUtils.getNodeText(envPath + "/redirectItem")).build();
        try {
            //return new DiscoveryResponse(true,null,0,null, ParseUtils.stringToJsonNode(xmlUtils.getNodeText(envPath + "/redirectItem"))).getResponseData();
            return DiscoveryResponse.fromRestResponse(restResp, new JacksonJsonService()).getResponseData();
        } catch (JsonDeserializationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getUserinfo(String name) throws FileNotFoundException, JSONException {
        String jsonString = xmlUtils.getNodeText(envPath + "/userinfo/"+name);
        return new JSONObject(jsonString);
    }
/*
    public JsonNode getAuthJsonObject() throws IOException {
        try {
            return new DiscoveryResponse(true,null,0,null, ParseUtils.stringToJsonNode(xmlUtils.getNodeText(envPath + "/discoveryItemAuthorization"))).getResponseData();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

*/
    /** provide node text by xpath expression
     * @param xpathExpression like //serviceUri
     * @return text from the node
     */
    public String getNodeText(String xpathExpression) throws FileNotFoundException {
        return xmlUtils.getNodeText(xpathExpression);
    }
}