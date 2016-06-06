package com.codepath.apps.copytwitter.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.codepath.apps.copytwitter.R;
import com.codepath.apps.copytwitter.adapters.TimelinePagerAdapter;
import com.codepath.apps.copytwitter.fragments.ComposeDialogFragment;
import com.codepath.apps.copytwitter.fragments.HomeTimelineFragment;
import com.codepath.apps.copytwitter.fragments.TweetListFragment;
import com.codepath.apps.copytwitter.models.Tweet;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements
        ComposeDialogFragment.ComposeDialogListener, TweetListFragment.LoadingListener {
    private TimelinePagerAdapter timelinePagerAdapter;
    MenuItem miActionProgressItem;

    @BindView(R.id.vpPager)
    ViewPager vpPager;

    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    @BindView(R.id.tbHome)
    Toolbar tbHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        // action bar
        setSupportActionBar(tbHome);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // home and mentions time_lines
        timelinePagerAdapter = new TimelinePagerAdapter(this.getSupportFragmentManager());
        vpPager.setAdapter(timelinePagerAdapter);
        tabLayout.setupWithViewPager(vpPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(true);
        }
    }

    public void hideProgressBar() {
        // Hide progress item
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miCompose:
                FragmentManager fm = getSupportFragmentManager();
                ComposeDialogFragment composeDialogFragment = new ComposeDialogFragment();
                composeDialogFragment.show(fm, "compose");
                break;
            case R.id.miProfile:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                long uid = sharedPreferences.getLong("uid", 0);
                Intent intent = new Intent(HomeActivity.this, UserActivity.class);
                intent.putExtra("user_id", uid);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishComposeDialog(Tweet tweet) {
        HomeTimelineFragment homeTimelineFragment = (HomeTimelineFragment) timelinePagerAdapter.instantiateItem(vpPager, 0);
        homeTimelineFragment.addAll(Arrays.asList(new Tweet[]{tweet}), HomeTimelineFragment.NEWER);
    }

    @Override
    public void onLoadingStart() {
        showProgressBar();
    }

    @Override
    public void onLoadingFinished() {
        hideProgressBar();
    }
}
