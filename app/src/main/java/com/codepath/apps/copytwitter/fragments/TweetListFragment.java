package com.codepath.apps.copytwitter.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.copytwitter.R;
import com.codepath.apps.copytwitter.TwitterApplication;
import com.codepath.apps.copytwitter.adapters.TweetArrayAdapter;
import com.codepath.apps.copytwitter.helpers.EndlessScrollListener;
import com.codepath.apps.copytwitter.models.Tweet;
import com.codepath.apps.copytwitter.rest.TwitterClient;
import com.codepath.apps.copytwitter.services.SaveTweetsService;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by charlie_zhou on 6/1/16.
 */
public class TweetListFragment extends Fragment {
    final public static int NEWER = 0;
    final public static int OLDER = 1;

    protected TwitterClient client;
    protected TweetArrayAdapter tweetArrayAdapter;
    protected List<Tweet> tweetList;
    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        client = TwitterApplication.getRestClient();
        super.onCreate(savedInstanceState);

        tweetList = new ArrayList<>();
        tweetArrayAdapter = new TweetArrayAdapter(getContext(), tweetList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_list, container, false);
        ButterKnife.bind(this, view);

        if (savedInstanceState == null) {
            rvTweets.setAdapter(tweetArrayAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
            rvTweets.setLayoutManager(linearLayoutManager);
            rvTweets.addOnScrollListener(new EndlessScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    loadMore();
                }
            });

            swipeRefreshLayout.setOnRefreshListener(() -> {
                loadNew();
            });

            loadNew();
        }
        return view;
    }

    public void loadMore() {
        ((LoadingListener) getActivity()).onLoadingStart();
        populateTweets(OLDER);
    }

    public void loadNew() {
        ((LoadingListener) getActivity()).onLoadingStart();
        populateTweets(NEWER);
    }

    public void addAll(List<Tweet> tweets, int fetchDirection) {
        if (fetchDirection == NEWER) {
            tweetList.addAll(0, tweets);
            tweetArrayAdapter.notifyItemRangeInserted(0, tweets.size());
            swipeRefreshLayout.setRefreshing(false);
            rvTweets.scrollToPosition(0);
        } else {
            int currentSize = tweetArrayAdapter.getItemCount();
            tweetList.addAll(tweets);
            tweetArrayAdapter.notifyItemRangeInserted(currentSize,
                    tweetList.size() - currentSize);
        }
        ((LoadingListener) getActivity()).onLoadingFinished();
    }

    protected JsonHttpResponseHandler getJsonHttpResponseHandler(final String tweetType, final int fetchingDirection) {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Tweet> loadedTweets = Tweet.fromJSONArray(response, tweetType);
                Intent intent = new Intent(getContext(), SaveTweetsService.class);
                ArrayList<Parcelable> parcelables = new ArrayList<>();
                for (Tweet tweet : loadedTweets) {
                    parcelables.add(Parcels.wrap(tweet));
                }
                intent.putParcelableArrayListExtra("tweets", parcelables);
                getContext().startService(intent);
                addAll(loadedTweets, fetchingDirection);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                List<Tweet> tweets;
                if (fetchingDirection == NEWER) {
                    tweets = Tweet.loadNewerFromDB(getTimelineType(), firstTweetUid());
                } else {
                    tweets = Tweet.loadOlderFromDB(getTimelineType(), lastTweetUid());
                }
                addAll(tweets, fetchingDirection);
            }
        };
    }

    protected long firstTweetUid() {
        return tweetList.size() > 0 ? tweetList.get(0).getUid() : 0;
    }

    protected long lastTweetUid() {
        return tweetList.size() > 0 ? tweetList.get(tweetList.size() - 1).getUid() : 0;
    }

    protected String getTimelineType() {
        throw new UnsupportedOperationException("Unknown timeline");
    }

    public void populateTweets(int fetchingDirection) {
        ((LoadingListener) getActivity()).onLoadingStart();
        client.getTimeline(getTimelineType(), fetchingDirection,
                fetchingDirection == NEWER ? firstTweetUid() : lastTweetUid(),
                getJsonHttpResponseHandler(getTimelineType(), fetchingDirection));
    }

    public interface LoadingListener {
        // These methods are the different events and
        // need to pass relevant arguments related to the event triggered
        public void onLoadingStart();
        // or when data has been loaded
        public void onLoadingFinished();
    }
}
