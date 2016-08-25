package com.gsma.mobileconnect.helpers;

import android.os.AsyncTask;
import android.util.Log;

import com.gsma.mobileconnect.utils.AndroidJsonUtils;
import com.gsma.mobileconnect.utils.AndroidRestClient;
import com.gsma.mobileconnect.utils.KeyValuePair;
import com.gsma.mobileconnect.utils.RestException;
import com.gsma.mobileconnect.utils.RestResponse;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

/**
 * At the moment User Info is not yet fully supported. This Android Async class attempts to exercise some of its
 * functionality.
 * Created by nick.copley on 03/03/2016.
 */
public class RetrieveUserinfoTask extends AsyncTask<Void, Void, UserInfo>
{
    private static final String TAG = "RetrieveUserinfoTask";

    String userinfoUri;

    String accessToken;

    //	String redirectUri;
    UserInfoListener listener;

    AndroidRestClient restClient;

    String clientId;

    String secret;

    /**
     * Constructor.
     *
     * @param userinfoUri URI for the service
     * @param accessToken Access token
     * @param clientId    Application client Id
     * @param secret      Application client secret
     * @param listener    Listener for callbacks
     */
    public RetrieveUserinfoTask(final String userinfoUri,
                                final String accessToken,
                                final String clientId,
                                final String secret,
                                final UserInfoListener listener)
    {
        this.userinfoUri = userinfoUri;
        this.accessToken = accessToken;
        this.listener = listener;
        this.restClient = new AndroidRestClient();
        this.clientId = clientId;
        this.secret = secret;

    }

    @Override
    protected UserInfo doInBackground(final Void... params)
    {
        JSONObject json = null;
        UserInfo userInfo = null;

        final HttpGet httpRequest = new HttpGet(this.userinfoUri);

        httpRequest.addHeader("Accept", "application/json");
        httpRequest.addHeader("Authorization", "Bearer " + this.accessToken);

        final URI uri = URI.create(this.userinfoUri);

        final HttpParams httpParams = httpRequest.getParams();
        httpParams.setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.TRUE);
        httpRequest.setParams(httpParams);

        try
        {
            final HttpClientContext context = this.restClient.getHttpClientContext(this.clientId, this.secret, uri);
            final RestResponse restResponse = this.restClient.callRestEndPoint(httpRequest,
                                                                               context,
                                                                               3000,
                                                                               new ArrayList<KeyValuePair>());
            if (200 == restResponse.getStatusCode())
            {
                final String response = restResponse.getResponse();
                Log.d(TAG, response);
                userInfo = AndroidJsonUtils.parseUserInfo(response);
            }
            else
            {
                Log.d(TAG, "Non successful response code [" + restResponse.getStatusCode() + "] " +
                           restResponse.getResponse());
            }

        }
        catch (IOException | RestException e)
        {
            Log.d(TAG, "Error calling User service", e);
        }
        return userInfo;
    }

    @Override
    protected void onPostExecute(final UserInfo userInfo)
    {
        Log.d(TAG, "onPostExecute for " + userInfo);
        this.listener.userReceived(userInfo);
    }

}
