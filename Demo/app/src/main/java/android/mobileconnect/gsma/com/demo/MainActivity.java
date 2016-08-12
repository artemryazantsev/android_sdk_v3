package android.mobileconnect.gsma.com.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.DiscoveryService;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.rest.RestClient;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private Button performDiscoveryButton;

    private Button performAuthorizationButton;

    private DiscoveryService.Builder discoveryService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        performDiscoveryButton = (Button) findViewById(R.id.button_perform_discovery);
        performAuthorizationButton = (Button) findViewById(R.id.button_perform_authorisation);

        performDiscoveryButton.setOnClickListener(this);
        performAuthorizationButton.setOnClickListener(this);

        JacksonJsonService jacksonJsonService = new JacksonJsonService();

        RestClient restClient = new RestClient.Builder().withJsonService(jacksonJsonService).build();

        discoveryService = new DiscoveryService.Builder().withExecutorService(Executors.newSingleThreadExecutor())
                                                         .withJsonService(jacksonJsonService)
                                                         .withRestClient();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.button_perform_discovery:
            {
                break;
            }
            case R.id.button_perform_authorisation:
            {
                break;
            }
        }
    }
}
