package com.codepath.apps.copytwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.copytwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by charlie_zhou on 6/5/16.
 */
public class SearchTweetFragment extends TweetListFragment {
    String query;

    public static SearchTweetFragment newInstance(String query) {
        Bundle args = new Bundle();
        args.putString("query", query);
        SearchTweetFragment fragment = new SearchTweetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        query = getArguments().getString("query");
    }

    private JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                List<Tweet> loadedTweets = Tweet.fromJSONArray(response.getJSONArray("statuses"), "search");
                addAll(loadedTweets, NEWER);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Snackbar.make(rvTweets, "Search failed", Snackbar.LENGTH_LONG)
                    .show();
        }
    };

    @Override
    public void loadNew() {
        ((LoadingListener) getActivity()).onLoadingStart();
        client.search(query, handler);
    }
}
