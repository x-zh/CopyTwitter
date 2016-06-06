package com.codepath.apps.copytwitter.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by charlie_zhou on 6/5/16.
 */
@Parcel
public class UserMention {
    private List<Integer> indices;
    private long userID;

    public List<Integer> getIndices() {
        return indices;
    }

    public long getUserID() {
        return userID;
    }

    public static UserMention fromJSON(JSONObject json) throws JSONException {
        UserMention userMention = new UserMention();
        userMention.indices = new ArrayList<>(Arrays.asList(
                json.getJSONArray("indices").getInt(0), json.getJSONArray("indices").getInt(1)
        ));
        userMention.userID = json.getInt("id");
        return userMention;
    }

    public static List<UserMention> fromJSONArray(JSONArray userMentions) {
        List<UserMention> arr = new ArrayList<>();
        for(int i=0; i<userMentions.length(); i++) {
            try {
                arr.add(UserMention.fromJSON(userMentions.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arr;
    }
}
