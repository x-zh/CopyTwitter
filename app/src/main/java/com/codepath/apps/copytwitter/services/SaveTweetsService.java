package com.codepath.apps.copytwitter.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Parcelable;

import com.codepath.apps.copytwitter.models.Tweet;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by charlie_zhou on 6/5/16.
 */
public class SaveTweetsService extends IntentService {

    public SaveTweetsService() {
        super("sql-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ArrayList<Parcelable> parcelables = intent.getParcelableArrayListExtra("tweets");
        if (parcelables == null) return;
        for(Parcelable parcelable : parcelables) {
            Tweet tweet = Parcels.unwrap(parcelable);
            tweet.getUser().save();
            tweet.save();
        }
    }
}
