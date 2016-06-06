package com.codepath.apps.copytwitter.models;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by charlie_zhou on 6/1/16.
 */
@Table(name = "users")
@Parcel(analyze={User.class})
public class User extends Model {
    @Column(name = "name")
    private String name;

    @Column(name = "screen_name")
    private String screenName;

    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private Long uid;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    private String profileBannerUrl;

    // @Column(name = "profile_background_image_url")
    private String profileBackgroundImageUrl;

    // @Column(name = "description")
    private String description;

    private int friendsCount;

    private int followersCount;

    public String getProfileBannerUrl() {
        return profileBannerUrl;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getDescription() {
        return description;
    }

    public Long getUid() {
        return uid;
    }
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getProfileBackgroundImageUrl() {
        return profileBackgroundImageUrl;
    }

    public static User createOrFind(long uid) {
        User findUser = new Select().from(User.class)
                .where("uid = ?", uid)
                .executeSingle();

        if (findUser != null) {
            return findUser;
        } else {
            User user = new User();
            user.uid  = uid;
            return user;
        }
    }

    public static User fromJSON(JSONObject json) throws JSONException {
        long uid = json.getLong("id");
        User u = User.createOrFind(uid);
        u.name = (json.getString("name"));
        u.profileImageUrl = (json.getString("profile_image_url"));
        u.screenName = (json.getString("screen_name"));

        if (json.has("profile_background_image_url")) {
            try {
                u.profileBannerUrl = json.getString("profile_banner_url");
                u.profileBackgroundImageUrl = json.getString("profile_background_image_url");
                u.description = json.getString("description");
                u.friendsCount = json.getInt("friends_count");
                u.followersCount = json.getInt("followers_count");
            } catch (JSONException e) {
                Log.d("jsonParse", "Cannot parse user " + u.toString());
            }
        }

        u.uid = uid;
        // u.save();
        return u;
    }
}
