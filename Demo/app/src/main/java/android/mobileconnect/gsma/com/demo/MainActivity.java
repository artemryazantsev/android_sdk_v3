package android.mobileconnect.gsma.com.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button performDiscoveryButton;

    private Button performAuthorizationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        performDiscoveryButton = (Button) findViewById(R.id.button_perform_discovery);
        performAuthorizationButton = (Button) findViewById(R.id.button_perform_authorisation);

        performDiscoveryButton.setOnClickListener(this);
        performAuthorizationButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_perform_discovery: {
                break;
            }
            case R.id.button_perform_authorisation: {
                break;
            }
        }
    }
}
