package com.codepath.apps.copytwitter.models;

import android.support.v4.util.Pair;

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
public class Url {
    private List<Integer> indices;
    private String url;

    public String getUrl() {
        return url;
    }

    public List<Integer> getIndices() {
        return indices;
    }

    public static Url fromJSON(JSONObject json) throws JSONException {
        Url url = new Url();
        url.indices = new ArrayList<>(Arrays.asList(
            json.getJSONArray("indices").getInt(0), json.getJSONArray("indices").getInt(1)
        ));
        url.url = json.getString("url");
        return url;
    }

    public static List<Url> fromJSONArray(JSONArray urls) {
        List<Url> arr = new ArrayList<>();
        for(int i=0; i<urls.length(); i++) {
            try {
                arr.add(Url.fromJSON(urls.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arr;
    }
}
