package android.mobileconnect.gsma.com.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by usmaan.dad on 26/09/2016.
 */
public class ResultActivity extends AppCompatActivity
{
    private TextView nameTextView;

    private TextView idTokenTextView;

    private TextView accessTokenTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        nameTextView = (TextView) findViewById(R.id.text_view_name);
        idTokenTextView = (TextView) findViewById(R.id.text_view_id_token);
        accessTokenTextView = (TextView) findViewById(R.id.text_view_access_token);

        Intent intent = getIntent();

        if (intent != null)
        {
            String name = intent.getStringExtra("name");
            String idToken = intent.getStringExtra("idToken");
            String accessToken = intent.getStringExtra("accessToken");

            nameTextView.setText(name);
            idTokenTextView.setText(idToken);
            accessTokenTextView.setText(accessToken);
        }
    }
}
