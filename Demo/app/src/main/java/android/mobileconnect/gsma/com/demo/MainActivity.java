package android.mobileconnect.gsma.com.demo;

import android.mobileconnect.gsma.com.demo.fragments.AuthFragmentsAdapter;
import android.mobileconnect.gsma.com.demo.fragments.AuthenticationFragment;
import android.mobileconnect.gsma.com.demo.fragments.AuthorizationFragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * An Activity which contains two Fragments, {@link AuthenticationFragment} and {@link AuthorizationFragment}
 */
public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        final AuthenticationFragment authenticationFragment = AuthenticationFragment.newInstance();
        final AuthorizationFragment authorizationFragment = AuthorizationFragment.newInstance();

        List<Fragment> authFragments = new ArrayList<>();
        authFragments.add(authenticationFragment);
        authFragments.add(authorizationFragment);

        final AuthFragmentsAdapter authFragmentsAdapter = new AuthFragmentsAdapter(authFragments,
                                                                                   getSupportFragmentManager());

        viewPager.setAdapter(authFragmentsAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.md_red_A200));
    }
}