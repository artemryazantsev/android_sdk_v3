package android.mobileconnect.gsma.com.demo.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class AuthFragmentsAdapter extends FragmentStatePagerAdapter
{
    List<Fragment> fragments;

    public AuthFragmentsAdapter(final List<Fragment> fragments, final FragmentManager fm)
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