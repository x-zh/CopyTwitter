package com.codepath.apps.copytwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.copytwitter.TwitterApplication;
import com.codepath.apps.copytwitter.models.Tweet;
import com.codepath.apps.copytwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by charlie_zhou on 6/4/16.
 */
public class UserTimelineFragment extends TweetListFragment {
    private User user;

    public static UserTimelineFragment newInstance(long userID) {

        Bundle args = new Bundle();
        args.putLong("user_id", userID);

        UserTimelineFragment fragment = new UserTimelineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static UserTimelineFragment newInstance(User user) {

        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));

        UserTimelineFragment fragment = new UserTimelineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getArguments().getParcelable("user") != null) {
                user = Parcels.unwrap(getArguments().getParcelable("user"));
            } else {
                user = User.createOrFind(getArguments().getLong("user_id"));
            }
            tweetArrayAdapter.setUser(user);
        }
    }

    @Override
    public void loadNew() {
        ((LoadingListener) getActivity()).onLoadingStart();
        client.getUser(user.getUid(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    user = User.fromJSON(response);
                    tweetArrayAdapter.setUser(user);
                    tweetArrayAdapter.notifyItemChanged(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
        super.loadNew();
    }

    public void populateTweets(int fetchingDirection) {
        client.getTimeline(
                user.getUid(),
                fetchingDirection,
                lastTweetUid(),
                getJsonHttpResponseHandler(
                        String.format("user_%d", user.getUid()),
                        fetchingDirection));
    }
}
