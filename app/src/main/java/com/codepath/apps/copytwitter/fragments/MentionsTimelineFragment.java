package com.codepath.apps.copytwitter.fragments;

import android.support.design.widget.Snackbar;

import com.codepath.apps.copytwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by charlie_zhou on 6/2/16.
 */
public class MentionsTimelineFragment extends TweetListFragment {
    @Override
    protected String getTimelineType() {
        return "mentions";
    }
}
