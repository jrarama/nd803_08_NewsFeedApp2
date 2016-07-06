package com.jprarama.newsfeedapp.task;

import android.os.AsyncTask;
import android.util.Log;

import com.jprarama.newsfeedapp.consumer.ListConsumer;
import com.jprarama.newsfeedapp.model.FeedEntry;
import com.jprarama.newsfeedapp.util.FeedXmlParser;
import com.jprarama.newsfeedapp.util.HttpUtil;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by joshua on 6/7/16.
 */
public class FeedItemsFetcher extends AsyncTask<String, Void, ArrayList<FeedEntry>> {
    private static final String TAG = FeedItemsFetcher.class.getName();

    private ListConsumer<FeedEntry> consumer;
    private Exception catchedException;

    public FeedItemsFetcher(ListConsumer<FeedEntry> consumer) {
        this.consumer = consumer;
    }

    @Override
    protected ArrayList<FeedEntry> doInBackground(String... strings) {
        Log.w(TAG, "Getting " + strings[0]);

        InputStream in;
        try {
            in = HttpUtil.performRequest("GET", strings[0], null, null);
            return FeedXmlParser.parse(in);
        } catch (Exception e) {
            catchedException = e;
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<FeedEntry> feedEntries) {
        if (catchedException == null) {
            consumer.consume(feedEntries);
        } else {
            consumer.consumeException(catchedException);
        }
    }
}
