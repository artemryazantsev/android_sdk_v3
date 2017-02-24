package com.gsma.mobileconnect.r2.android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.gsma.mobileconnect.r2.android.demo.R;
import com.gsma.mobileconnect.r2.android.fragments.BaseAuthFragment;

public class ResultsActivity extends AppCompatActivity {

    TextView response_token_id;
    TextView message_field;
    TextView access_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        response_token_id = (TextView) findViewById(R.id.response_token_id);
        message_field = (TextView) findViewById(R.id.response_message);
        access_token = (TextView) findViewById(R.id.response_access_token);

        response_token_id.setText(BaseAuthFragment.mobileConnectStatus.getRequestTokenResponse().getResponseData().getIdToken());
        access_token.setText(BaseAuthFragment.mobileConnectStatus.getRequestTokenResponse().getResponseData().getAccessToken());

        if (BaseAuthFragment.mobileConnectStatus.getRequestTokenResponse() != null) {
            message_field.setText(getResources().getString(R.string.successful_request_message));
        } else {
            message_field.setText(BaseAuthFragment.mobileConnectStatus.getRequestTokenResponse().getErrorResponse().toString());
        }
    }
}
