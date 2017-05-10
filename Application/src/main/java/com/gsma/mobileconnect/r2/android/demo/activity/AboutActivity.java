package com.gsma.mobileconnect.r2.android.demo.activity;

import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.gsma.mobileconnect.r2.android.demo.R;

public class AboutActivity extends BaseActivity {

    private Toolbar toolbar;

    private TextView tvHelpNavigation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        configureToolbar(toolbar, getString(R.string.action_about));

        init();

        tvHelpNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HelpActivity.class));
            }
        });
    }


    @Override
    protected void init() {
        tvHelpNavigation = (TextView)findViewById(R.id.tvHelp);
    }
}
