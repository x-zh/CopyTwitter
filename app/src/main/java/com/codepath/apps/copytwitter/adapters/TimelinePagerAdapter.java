package com.codepath.apps.copytwitter.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.copytwitter.fragments.HomeTimelineFragment;
import com.codepath.apps.copytwitter.fragments.MentionsTimelineFragment;

/**
 * Created by charlie_zhou on 6/2/16.
 */
public class TimelinePagerAdapter extends FragmentPagerAdapter {

    public TimelinePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new HomeTimelineFragment();
        }
        return new MentionsTimelineFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Home";
        }
        return "Mentions";
    }
}
