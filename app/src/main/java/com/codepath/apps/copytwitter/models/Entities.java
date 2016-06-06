package com.codepath.apps.copytwitter.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.List;

/**
 * Created by charlie_zhou on 6/5/16.
 */
@Parcel
public class Entities {

    private List<Url> urls;
    private List<UserMention> userMentions;
    private List<Media> media;

    public List<Media> getMedia() {
        return media;
    }

    public List<Url> getUrls() {
        return urls;
    }

    public List<UserMention> getUserMentions() {
        return userMentions;
    }

    public static Entities fromJSON(JSONObject json) throws JSONException {
        Entities entities = new Entities();
        if (json.has("media")) {
            entities.media = Media.fromJSONArray(json.getJSONArray("media"));
        }
        if (json.has("urls")) {
            entities.urls = Url.fromJSONArray(json.getJSONArray("urls"));
        }
        if (json.has("user_mentions")) {
            entities.userMentions = UserMention.fromJSONArray(json.getJSONArray("user_mentions"));
        }
        return entities;
    }
}
