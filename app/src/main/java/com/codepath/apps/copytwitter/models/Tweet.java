package com.codepath.apps.copytwitter.models;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.codepath.apps.copytwitter.helpers.RelativeTimeHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by charlie_zhou on 6/1/16.
 */
@Table(name = "tweets")
@Parcel(analyze={Tweet.class})
public class Tweet extends Model {

    @Column(name = "body")
    private String body;

    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;

    @Column(name = "type")
    private String type;

    private Tweet retweetedStatus;
    private boolean retweeted;

    private Entities entities;

    private int favoriteCount;

    private int retweetCount;

    private boolean favorited;

    public Tweet getRetweetedStatus() {
        return retweetedStatus;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public Entities getEntities() {
        return entities;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getCreatedSince() {
        return RelativeTimeHelper.fromString(getCreatedAt());
    }

    public User getUser() {
        return user;
    }

    public static Tweet fromJSON(JSONObject json, String type) throws JSONException {
        Tweet t = new Tweet();
        t.body = json.getString("text");
        t.createdAt = json.getString("created_at");
        t.uid = json.getLong("id");
        t.user = User.fromJSON(json.getJSONObject("user"));
        t.type = type;
        t.favoriteCount = json.getInt("favorite_count");
        t.retweetCount = json.getInt("retweet_count");
        t.entities = Entities.fromJSON(json.getJSONObject("entities"));
        t.retweeted = json.getBoolean("retweeted");
        t.favorited = json.getBoolean("favorited");
        if (json.has("retweeted_status")) {
            t.retweetedStatus = Tweet.fromJSON(json.getJSONObject("retweeted_status"), "retweeted");
        }
        return t;
    }

    public static List<Tweet> fromJSONArray(JSONArray jsonArray, String type) {
        List<Tweet> tweetList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                tweetList.add(Tweet.fromJSON(jsonArray.getJSONObject(i), type));
            } catch (JSONException e) {
                Log.d("jsonParse", "Cannot parse Tweet");
            }
        }
        return tweetList;
    }

    public static List<Tweet> loadNewerFromDB(String tweetType, long lastUID) {
        return new Select().from(Tweet.class)
                .where("type = ?", tweetType)
                .where("uid > ?", lastUID)
                .orderBy("uid desc")
                .limit(25)
                .execute();
    }

    public static List<Tweet> loadOlderFromDB(String tweetType, long lastUID) {
        return new Select().from(Tweet.class)
                .where("type = ?", tweetType)
                .where("uid < ?", lastUID)
                .orderBy("uid desc")
                .limit(25)
                .execute();
    }
}
