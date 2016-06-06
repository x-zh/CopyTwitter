package com.codepath.apps.copytwitter.fragments;

import com.codepath.apps.copytwitter.models.Tweet;

import java.util.List;

/**
 * Created by charlie_zhou on 6/1/16.
 */
public class HomeTimelineFragment extends TweetListFragment {
    @Override
    protected String getTimelineType() {
        return "home";
    }
}
