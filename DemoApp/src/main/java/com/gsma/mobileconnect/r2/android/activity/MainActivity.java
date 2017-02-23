package com.gsma.mobileconnect.r2.android.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.gsma.mobileconnect.r2.android.adapter.ApiFragmentsAdapter;
import com.gsma.mobileconnect.r2.android.demo.R;
import com.gsma.mobileconnect.r2.android.fragments.ManualDiscoveryFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        final ManualDiscoveryFragment manualDiscoveryFragment = ManualDiscoveryFragment.newInstance();
        final List<Fragment> apiFragments = new ArrayList<>();
        apiFragments.add(manualDiscoveryFragment);
        final ApiFragmentsAdapter apiFragmentsAdapter = new ApiFragmentsAdapter(apiFragments,
                getSupportFragmentManager());
        viewPager.setAdapter(apiFragmentsAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.md_cyan_800));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.md_red_A700));
    }
}