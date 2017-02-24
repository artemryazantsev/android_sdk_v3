package com.gsma.mobileconnect.r2.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gsma.mobileconnect.r2.android.interfaces.ITitle;

import java.util.List;

public class ApiFragmentsAdapter extends FragmentStatePagerAdapter
{
    List<Fragment> fragments;

    public ApiFragmentsAdapter(final List<Fragment> fragments, final FragmentManager fm)
    {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(final int i)
    {
        return fragments.get(i);
    }

    @Override
    public int getCount()
    {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(final int position)
    {
        return ((ITitle) fragments.get(position)).getTitle();
    }
}