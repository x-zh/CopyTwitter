package com.codepath.apps.copytwitter.rest;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.apps.copytwitter.fragments.TweetListFragment;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "mwjjQg7lYUgaoh8h46vhNuzA5";       // Change this
	public static final String REST_CONSUMER_SECRET = "HOrP94IqrBUgUfKDACmac7eWC38oMbJYtNsTvpN721n6E4SIyH"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://copytwitter"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

    public void getTimeline(long userID,
                            int fetchingDirection,
                            long lastID,
                            AsyncHttpResponseHandler asyncHttpResponseHandler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", "25");
        params.put("user_id", userID);
        if (lastID > 0) {
            params.put(fetchingDirection == TweetListFragment.NEWER ? "max_id" : "since_id", lastID);
        }
        client.get(apiUrl, params, asyncHttpResponseHandler);
    }

    public void getTimeline(String tweetType,
                            int fetchingDirection,
                            long lastID,
                            AsyncHttpResponseHandler asyncHttpResponseHandler) {
        String apiUrl = getApiUrl(String.format("statuses/%s_timeline.json", tweetType));
        RequestParams params = new RequestParams();
        params.put("count", "200");
        if (lastID > 0) {
            params.put(fetchingDirection == TweetListFragment.NEWER ? "max_id" : "since_id", lastID);
        }
        client.get(apiUrl, params, asyncHttpResponseHandler);
    }

    public void update(String status, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", status);
        client.post(apiUrl, params, asyncHttpResponseHandler);
    }

    public void reply(String status, long replyToTweetID, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", status);
        params.put("in_reply_to_status_id", replyToTweetID);
        client.post(apiUrl, params, asyncHttpResponseHandler);
    }

    public void getUser(long uid, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        String apiUrl = getApiUrl("users/show.json");
        RequestParams params = new RequestParams();
        params.put("user_id", uid);
        client.get(apiUrl, params, asyncHttpResponseHandler);
    }

    public void search(String query, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        String apiUrl = getApiUrl("search/tweets.json");
        RequestParams params = new RequestParams();
        params.put("q", query);
        client.get(apiUrl, params, asyncHttpResponseHandler);
    }

    public void getAccount(AsyncHttpResponseHandler asyncHttpResponseHandler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        client.get(apiUrl, asyncHttpResponseHandler);
    }

    public void retweet(long uid, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl(String.format("statuses/retweet/%d.json", uid));
        client.post(apiUrl, jsonHttpResponseHandler);
    }

    public void unretweet(long uid, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl(String.format("statuses/unretweet/%d.json", uid));
        client.post(apiUrl, jsonHttpResponseHandler);
    }
}