package com.codepath.apps.copytwitter.models;

import com.activeandroid.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by charlie_zhou on 6/5/16.
 */
@Parcel
public class Media {

    private long uid;
    private String mediaUrl;
    private String type;
    private String displayUrl;

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getType() {
        return type;
    }

    public static Media fromJSON(JSONObject json) throws JSONException {
        Media media = new Media();
        media.uid = json.getLong("id");
        media.mediaUrl = json.getString("media_url");
        media.type = json.getString("type");
        media.displayUrl = json.getString("display_url");
        return media;
    }

    public static List<Media> fromJSONArray(JSONArray media) {
        List<Media> arr = new ArrayList<>();
        for (int i = 0; i < media.length(); i++) {
            try {
                arr.add(Media.fromJSON(media.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arr;
    }
}
