package android.mobileconnect.gsma.com.demo.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by usmaan.dad on 24/08/2016.
 */
public class AuthFragmentsAdapter extends FragmentStatePagerAdapter
{
    List<Fragment> fragments;

    public AuthFragmentsAdapter(List<Fragment> fragments, FragmentManager fm)
    {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int i)
    {
        return fragments.get(i);
    }

    @Override
    public int getCount()
    {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return ((ITitle) fragments.get(position)).getTitle();
    }
}