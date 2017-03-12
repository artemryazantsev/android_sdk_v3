package com.gsma.mobileconnect.r2.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gsma.mobileconnect.r2.android.demo.R;

public class EndpointsConfigure extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endpoints_configure);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button saveButton = (Button) findViewById(R.id.save_configs_button);
        final EditText provider_metadata_field = (EditText) findViewById((R.id.provider_metadata_field));
        final EditText authorization_url_field = (EditText) findViewById((R.id.authorization_url_field));
        final EditText requestToken_url_field = (EditText) findViewById((R.id.requestToken_url_field));
        final EditText userInfo_url_field = (EditText) findViewById((R.id.userInfo_url_field));
        final EditText revokeToken_url_field = (EditText) findViewById((R.id.revokeToken_url_field));


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("provider_metadata", provider_metadata_field.getText().toString());
                intent.putExtra("authorization_url", authorization_url_field.getText().toString());
                intent.putExtra("requestToken_url", requestToken_url_field.getText().toString());
                intent.putExtra("userInfo_url", userInfo_url_field.getText().toString());
                intent.putExtra("revokeToken_url", revokeToken_url_field.getText().toString());
                startActivity(intent);
            }
        });
    }

}
