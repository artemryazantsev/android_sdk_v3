package com.gsma.mobileconnect.r2.android.demo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.gsma.mobileconnect.r2.android.demo.R;

public class HelpActivity extends BaseActivity {

    private Toolbar toolbar;
    private Button btnTutorialToast;
    private Button btnTutorialDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        configureToolbar(toolbar, getString(R.string.action_help));
        init();

        btnTutorialToast.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                makeToast(getString(R.string.help_toast_content), true);
                return true;
            }
        });

        btnTutorialDialog.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                makeAlert(getString(R.string.help_dialog), getString(R.string.help_dialog_content));
                return true;
            }
        });
    }

    @Override
    protected void init() {
        btnTutorialDialog = (Button)findViewById(R.id.btnTutorialDialog);
        btnTutorialToast = (Button)findViewById(R.id.btnTutorialToast);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
