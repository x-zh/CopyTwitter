package com.codepath.apps.copytwitter.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codepath.apps.copytwitter.R;
import com.codepath.apps.copytwitter.TwitterApplication;
import com.codepath.apps.copytwitter.activities.UserActivity;
import com.codepath.apps.copytwitter.fragments.ComposeDialogFragment;
import com.codepath.apps.copytwitter.models.Tweet;
import com.codepath.apps.copytwitter.models.Url;
import com.codepath.apps.copytwitter.models.User;
import com.codepath.apps.copytwitter.models.UserMention;
import com.codepath.apps.copytwitter.rest.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by charlie_zhou on 6/1/16.
 */
public class TweetArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final int USER_VIEW_TYPE = 0;
    public final int TWEET_VIEW_TYPE = 1;
    public final int highlightColor = Color.parseColor("#55ACEE");
    public final int mutedColor = Color.parseColor("#DDDDDD");

    private TwitterClient client;
    private User user;
    private Context context;
    private List<Tweet> tweetList;

    public TweetArrayAdapter(Context context, List<Tweet> tweetList) {
        this.client = TwitterApplication.getRestClient();
        this.context = context;
        this.tweetList = tweetList;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case USER_VIEW_TYPE:
                viewHolder = new UserViewHolder(
                        inflater.inflate(R.layout.item_user, parent, false));
                break;
            case TWEET_VIEW_TYPE:
                viewHolder = new TweetViewHolder(
                        inflater.inflate(R.layout.item_tweet, parent, false));
                break;
            default:
                viewHolder = new TweetViewHolder(
                        inflater.inflate(R.layout.item_tweet, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return (user != null && position == 0) ? USER_VIEW_TYPE : TWEET_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {
        return tweetList.size() + (user == null ? 0 : 1);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (user != null && position == 0){
            ((UserViewHolder) holder).bind(user);
        } else {
            int adjustedPosition = position - (user == null ? 0 : 1);
            ((TweetViewHolder) holder).bind(tweetList.get(adjustedPosition));
        }
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;

        @BindView(R.id.ivProfileBg)
        ImageView ivProfileBg;

        @BindView(R.id.tvName)
        TextView tvName;

        @BindView(R.id.tvScreenName)
        TextView tvScreenName;

        @BindView(R.id.tvDescription)
        TextView tvDescription;

        @BindView(R.id.tvFollowersCount)
        TextView tvFollowersCount;

        @BindView(R.id.tvFollowingCount)
        TextView tvFollowingCount;

        public UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(User user) {
            ivProfileImage.setImageResource(0);
            Picasso.with(ivProfileBg.getContext())
                    .load(user.getProfileImageUrl())
                    .fit().centerCrop()
                    .into(ivProfileImage);
            tvName.setText(user.getName());
            tvScreenName.setText(user.getScreenName());
            ivProfileBg.setImageResource(0);
            Picasso.with(ivProfileBg.getContext())
                    .load(user.getProfileBackgroundImageUrl())
                    .fit().centerCrop()
                    .into(ivProfileBg);
            tvDescription.setText(user.getDescription());
            tvFollowingCount.setText(String.valueOf(user.getFriendsCount()));
            tvFollowersCount.setText(String.valueOf(user.getFollowersCount()));
        }
    }

    public class TweetViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;

        @BindView(R.id.tvBody)
        TextView tvBody;

        @BindView(R.id.tvUserName)
        TextView tvUserName;

        @BindView(R.id.tvScreenName)
        TextView tvScreenName;

        @BindView(R.id.tvCreatedSince)
        TextView tvCreatedSince;

        @BindView(R.id.ivMedia)
        ImageView ivMedia;

        @BindView(R.id.tvRetweetCount)
        TextView tvRetweetCount;

        @BindView(R.id.tvLikeCount)
        TextView tvLikeCount;

        @BindView(R.id.ivReply)
        ImageView ivReply;

        @BindView(R.id.llRetweeted)
        LinearLayout llRetweeted;

        @BindView(R.id.tvRetweeted)
        TextView tvRetweeted;

        @BindView(R.id.ivRetweeted)
        ImageView ivRetweeted;

        @BindView(R.id.ivLike)
        ImageView ivLike;

        public TweetViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void showAsRetweeted(boolean b, int count) {
            if (b) {
                ivRetweeted.setColorFilter(highlightColor);
                tvRetweetCount.setTextColor(highlightColor);
            } else {
                ivRetweeted.setColorFilter(mutedColor);
                tvRetweetCount.setTextColor(mutedColor);
            }
            tvRetweetCount.setText(String.valueOf(count));
        }

        public void showAsLiked(boolean b, int count) {
            if (b) {
                ivLike.setColorFilter(highlightColor);
                tvLikeCount.setTextColor(highlightColor);
            } else {
                ivLike.setColorFilter(mutedColor);
                tvLikeCount.setTextColor(mutedColor);
            }
            tvLikeCount.setText(String.valueOf(count));
        }

        public void bind(Tweet tweet) {
            Tweet displayedTweet = tweet;
            if (tweet.getRetweetedStatus() != null) {
                llRetweeted.setVisibility(LinearLayout.VISIBLE);
                tvRetweeted.setText(String.format("%s Retweeted", displayedTweet.getUser().getName()));
                displayedTweet = tweet.getRetweetedStatus();
            } else {
                llRetweeted.setVisibility(LinearLayout.GONE);
            }
            ivProfileImage.setImageResource(0);
            ivMedia.setImageResource(0);
            ivMedia.setVisibility(ImageView.GONE);
            Picasso.with(ivProfileImage.getContext()).load(displayedTweet.getUser().getProfileImageUrl())
                    .fit().centerCrop().into(ivProfileImage);
            tvUserName.setText(displayedTweet.getUser().getName());

            SpannableStringBuilder ssb = new SpannableStringBuilder(displayedTweet.getBody());
            if (displayedTweet.getEntities().getUrls() != null) {
                for (Url url : displayedTweet.getEntities().getUrls()) {
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.getUrl()));
                            widget.getContext().startActivity(intent);
                        }
                    };
                    ssb.setSpan(clickableSpan,
                            url.getIndices().get(0),
                            url.getIndices().get(1),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            if (displayedTweet.getEntities().getUserMentions() != null) {
                for (UserMention userMention : displayedTweet.getEntities().getUserMentions()) {
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            Intent intent = new Intent(widget.getContext(), UserActivity.class);
                            intent.putExtra("user_id", userMention.getUserID());
                            widget.getContext().startActivity(intent);
                        }
                    };
                    ssb.setSpan(clickableSpan,
                            userMention.getIndices().get(0),
                            userMention.getIndices().get(1),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            tvBody.setText(ssb, TextView.BufferType.SPANNABLE);
            tvBody.setMovementMethod(LinkMovementMethod.getInstance());

            tvScreenName.setText(String.format("@%s", displayedTweet.getUser().getScreenName()));
            tvCreatedSince.setText(displayedTweet.getCreatedSince());

            showAsRetweeted(displayedTweet.isRetweeted(), displayedTweet.getRetweetCount());
            showAsLiked(displayedTweet.isFavorited(), displayedTweet.getFavoriteCount());

            final Tweet finalDisplayedTweet = displayedTweet;
            ivProfileImage.setOnClickListener((view) -> {
                Intent intent = new Intent(view.getContext(), UserActivity.class);
                intent.putExtra("user", Parcels.wrap(finalDisplayedTweet.getUser()));
                view.getContext().startActivity(intent);
            });
            ivReply.setOnClickListener((view) -> {
                ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance(finalDisplayedTweet);
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                composeDialogFragment.show(fragmentManager, "reply");
            });
            ivRetweeted.setOnClickListener((view) -> {
                if(!finalDisplayedTweet.isRetweeted()) {
                    client.retweet(finalDisplayedTweet.getUid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            finalDisplayedTweet.setRetweeted(true);
                            finalDisplayedTweet.setRetweetCount(finalDisplayedTweet.getRetweetCount() + 1);
                            showAsRetweeted(
                                    finalDisplayedTweet.isRetweeted(), finalDisplayedTweet.getRetweetCount());
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.d("response", responseString);
                            throwable.printStackTrace();
                        }
                    });
                } else {
                    client.unretweet(finalDisplayedTweet.getUid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            finalDisplayedTweet.setRetweeted(false);
                            finalDisplayedTweet.setRetweetCount(finalDisplayedTweet.getRetweetCount() - 1);
                            showAsRetweeted(
                                    finalDisplayedTweet.isRetweeted(), finalDisplayedTweet.getRetweetCount());
                        }


                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.d("response", responseString);
                            throwable.printStackTrace();
                        }
                    });
                }
            });

            if (displayedTweet.getEntities().getMedia() != null) {
                if (displayedTweet.getEntities().getMedia().get(0).getType().equalsIgnoreCase("photo")) {
                    ivMedia.setVisibility(ImageView.VISIBLE);
                    Picasso.with(ivMedia.getContext())
                            .load(displayedTweet.getEntities().getMedia().get(0).getMediaUrl())
                            .fit().centerCrop().into(ivMedia);
                }
            }
        }
    }
}
