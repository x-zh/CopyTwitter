package com.codepath.apps.copytwitter.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.copytwitter.R;
import com.codepath.apps.copytwitter.TwitterApplication;
import com.codepath.apps.copytwitter.fragments.TweetListFragment;
import com.codepath.apps.copytwitter.fragments.UserTimelineFragment;
import com.codepath.apps.copytwitter.models.User;
import com.codepath.apps.copytwitter.rest.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class UserActivity extends AppCompatActivity
        implements TweetListFragment.LoadingListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (getIntent().getParcelableExtra("user") != null) {
                User user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
                ft.replace(R.id.fmUserTimeline, UserTimelineFragment.newInstance(user));
            } else {
                long userId = getIntent().getLongExtra("user_id", 0);
                ft.replace(R.id.fmUserTimeline, UserTimelineFragment.newInstance(userId));
            }
            ft.commit();
        }
    }

    @Override
    public void onLoadingStart() {

    }

    @Override
    public void onLoadingFinished() {

    }
}
