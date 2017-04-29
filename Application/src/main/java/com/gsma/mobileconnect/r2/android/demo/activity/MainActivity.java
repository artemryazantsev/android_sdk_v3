package com.gsma.mobileconnect.r2.android.demo.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.gsma.mobileconnect.r2.android.demo.R;

import com.gsma.mobileconnect.r2.android.demo.fragments.DemoAppFragment;
import com.gsma.mobileconnect.r2.android.demo.fragments.WithoutDiscoveryAppFragment;
import com.gsma.mobileconnect.r2.android.demo.interfaces.OnBackPressedListener;

public class MainActivity extends BaseActivity {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private boolean isDemoApp = true;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        fragmentManager = getSupportFragmentManager();

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        nvDrawer = (NavigationView) findViewById(R.id.nav_view);

        Fragment fragment = null;
        try {
            fragment = (Fragment) DemoAppFragment.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        setupDrawerContent(nvDrawer);
        setupToolbarContent(toolbar);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupToolbarContent(Toolbar toolbar) {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                selectToolbarItem(item);
                return false;
            }
        });

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public void selectToolbarItem (MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_switch:
                selectApp();
                break;
            case R.id.action_help:
                openActivity(HelpActivity.class);
                break;
            case R.id.action_about:
                openActivity(AboutActivity.class);
                break;
            default:
                break;

        }
    }

    private void selectApp () {
        Fragment fragment = null;
        Class fragmentClass;
        if (!isDemoApp) {
            fragmentClass = DemoAppFragment.class;
            isDemoApp = true;
            setTitle(getResources().getString(R.string.demo_app));
        } else {
            fragmentClass = WithoutDiscoveryAppFragment.class;
            isDemoApp = false;
            setTitle(getResources().getString(R.string.without_discovery_app));
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.flContent, fragment);
        transaction.commit();
    }

    public void selectDrawerItem(MenuItem menuItem) {

        Fragment fragment = null;
        switch(menuItem.getItemId()) {
            case R.id.nav_demo_app:
                openFragment(fragment, DemoAppFragment.class, menuItem);
                isDemoApp = true;
                break;
            case R.id.nav_without_discovery_app:
                openFragment(fragment, WithoutDiscoveryAppFragment.class, menuItem);
                isDemoApp = false;
                break;
            case R.id.nav_help:
                openActivity(HelpActivity.class);
                mDrawer.closeDrawers();
                break;
            case R.id.nav_about:
                openActivity(AboutActivity.class);
                mDrawer.closeDrawers();
                break;
            default:
                break;
        }
    }

    /**
     * Opens activity without putting extra data
     *
     * @param activityClass
     */
    private void openActivity (Class activityClass) {
        startActivity(new Intent(getApplicationContext(), activityClass));
    }

    private void openFragment(Fragment fragment, Class fragmentClass, MenuItem menuItem) {
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    @Override
    protected void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        OnBackPressedListener backPressedListener = null;
        for (Fragment fragment: fm.getFragments()) {
            if (fragment instanceof  OnBackPressedListener) {
                backPressedListener = (OnBackPressedListener) fragment;
                break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (backPressedListener != null) {
                backPressedListener.onBackPressed();
            } else {
                super.onBackPressed();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
