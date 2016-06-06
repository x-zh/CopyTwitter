package com.codepath.apps.copytwitter.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.copytwitter.R;
import com.codepath.apps.copytwitter.TwitterApplication;
import com.codepath.apps.copytwitter.activities.HomeActivity;
import com.codepath.apps.copytwitter.models.Tweet;
import com.codepath.apps.copytwitter.rest.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cz.msebera.android.httpclient.Header;

/**
 * Created by charlie_zhou on 6/4/16.
 */
public class ComposeDialogFragment extends DialogFragment {
    private TwitterClient client;
    private Tweet replyToTweet;
    private SharedPreferences sharedPreferences;

    @BindView(R.id.etTweet)
    EditText etTweet;

    @BindView(R.id.tvSize)
    TextView tvSize;

    @BindView(R.id.ivProfilePic)
    ImageView ivProfilePic;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvScreenName)
    TextView tvScreenName;

    public static ComposeDialogFragment newInstance(Tweet replyToTweet) {

        Bundle args = new Bundle();
        args.putParcelable("reply_to_tweet", Parcels.wrap(replyToTweet));

        ComposeDialogFragment fragment = new ComposeDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getParcelable("reply_to_tweet") != null) {
            replyToTweet = Parcels.unwrap(getArguments().getParcelable("reply_to_tweet"));
        }
        client = TwitterApplication.getRestClient();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose_dialog, container, false);
        ButterKnife.bind(this, view);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // init with @sbd if it's reply
        if (replyToTweet != null) {
            etTweet.setText(String.format("@%s: ", replyToTweet.getUser().getScreenName()));
        }
        updateSize();
        fetchAccount();
        return view;
    }

    public void fetchAccount() {
        long uid = sharedPreferences.getLong("uid", 0);
        if (uid > 0) {
            updateUser();
        } else {
            client.getAccount(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    try {
                        editor.putLong("uid", response.getLong("id"));
                        editor.commit();
                        updateUser();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void updateUser() {
        ivProfilePic.setVisibility(ImageView.GONE);
        tvName.setVisibility(TextView.GONE);
        tvScreenName.setVisibility(TextView.GONE);
        String userName = sharedPreferences.getString("user_name", null);
        if (userName == null) {
            client.getUser(sharedPreferences.getLong("uid", 0), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    try {
                        editor.putString("user_name", response.getString("name"));
                        editor.putString("screen_name", response.getString("screen_name"));
                        editor.putString("profile_image_url", response.getString("profile_image_url"));
                        editor.commit();
                        loadCurrentUser();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            loadCurrentUser();
        }
    }

    public void loadCurrentUser() {
        ivProfilePic.setImageResource(0);
        String profileImageUrl = sharedPreferences.getString("profile_image_url", "");
        String userName = sharedPreferences.getString("user_name", "");
        String screenName = sharedPreferences.getString("screen_name", "");
        Picasso.with(getContext()).load(profileImageUrl)
                .fit().centerCrop().into(ivProfilePic);
        tvName.setText(userName);
        tvScreenName.setText(String.format("@%s", screenName));
        ivProfilePic.setVisibility(ImageView.VISIBLE);
        tvName.setVisibility(TextView.VISIBLE);
        tvScreenName.setVisibility(TextView.VISIBLE);
    }

    @OnTextChanged(R.id.etTweet)
    public void updateSize() {
        tvSize.setText(String.valueOf(140 - etTweet.getText().length()));
    }

    private JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                Tweet tweet = Tweet.fromJSON(response, "home");
                ((ComposeDialogListener) getActivity()).onFinishComposeDialog(tweet);
                dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d("tweeting", errorResponse.toString());
            dismiss();
            super.onFailure(statusCode, headers, throwable, errorResponse);
        }
    };

    @OnClick(R.id.btnTweet)
    public void tweet() {
        if (replyToTweet != null) {
            client.reply(etTweet.getText().toString(), replyToTweet.getUid(), jsonHttpResponseHandler);
        } else {
            client.update(etTweet.getText().toString(), jsonHttpResponseHandler);
        }
    }

    @OnClick(R.id.ivClose)
    public void close() {
        dismiss();
    }

    public interface ComposeDialogListener {
        void onFinishComposeDialog(Tweet tweet);
    }
}
