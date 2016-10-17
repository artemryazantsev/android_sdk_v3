package com.gsma.mobileconnect.r2.android.exampleapps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.gsma.mobileconnect.r2.constants.Scopes;

/**
 * The Main Activity which contains the list of Authorizations and the Authentication triggers.
 * <p/>
 * Selecting any of the list items on the UI will result navigating to {@link MSISDNActivity} where the user may
 * select an MSISDN.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    // UI Views
    private TextView authorizationTextView;

    private TextView authenticationTextView;

    private TextView signUpTextView;

    private TextView nationalIdTextView;

    private TextView phoneNumberTextView;

    private String scope;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind all of the views
        authorizationTextView = (TextView) findViewById(R.id.text_view_authorization);
        authenticationTextView = (TextView) findViewById(R.id.text_view_authentication);
        signUpTextView = (TextView) findViewById(R.id.text_view_sign_up);
        nationalIdTextView = (TextView) findViewById(R.id.text_view_national_id);
        phoneNumberTextView = (TextView) findViewById(R.id.text_view_phone_number);

        // Assign each view a click event to this Activity
        authorizationTextView.setOnClickListener(this);
        authenticationTextView.setOnClickListener(this);
        signUpTextView.setOnClickListener(this);
        nationalIdTextView.setOnClickListener(this);
        phoneNumberTextView.setOnClickListener(this);
    }

    /**
     * A scope must be built based on the type of Authorization. For Authentication, the only scope is
     * {@link Scopes#MOBILECONNECTAUTHENTICATION}
     *
     * @param view The view which was clicked on the UI
     */
    @Override
    public void onClick(final View view)
    {
        Intent intent = null;
        StringBuilder scopeBuilder = new StringBuilder();
        String title = null;

        switch (view.getId())
        {
            case R.id.text_view_authentication:
            {
                intent = new Intent(this, AuthenticationActivity.class);
                scopeBuilder = scopeBuilder.append(Scopes.MOBILECONNECTAUTHENTICATION);
                title = "Authentication";
                break;
            }
            case R.id.text_view_authorization:
            {
                intent = new Intent(this, AuthorizationActivity.class);
                scopeBuilder = scopeBuilder.append(Scopes.MOBILECONNECTAUTHORIZATION);
                title = "Authorization";
                break;
            }
            case R.id.text_view_sign_up:
            {
                intent = new Intent(this, SignupActivity.class);
                scopeBuilder = scopeBuilder.append(Scopes.MOBILECONNECTAUTHORIZATION);
                scopeBuilder = scopeBuilder.append(" " + Scopes.MOBILECONNECTIDENTITYSIGNUP);
                title = "Sign Up";
                break;
            }
            case R.id.text_view_national_id:
            {
                intent = new Intent(this, NationalIDActivity.class);
                scopeBuilder = scopeBuilder.append(Scopes.MOBILECONNECTAUTHORIZATION);
                scopeBuilder = scopeBuilder.append(" " + Scopes.MOBILECONNECTIDENTITYNATIONALID);
                title = "National ID";
                break;
            }
            case R.id.text_view_phone_number:
            {
                intent = new Intent(this, PhoneNumberActivity.class);
                scopeBuilder = scopeBuilder.append(Scopes.MOBILECONNECTAUTHORIZATION);
                scopeBuilder = scopeBuilder.append(" " + Scopes.MOBILECONNECTIDENTITYPHONE);
                title = "Phone Number";
                break;
            }
        }

        this.scope = scopeBuilder.toString();

        // Pass the scope to the MSISDN activity so that it may add it to it's AuthenticationOptions
        intent.putExtra("scope", scope);
        intent.putExtra("title", title);

        startActivity(intent);
    }
}