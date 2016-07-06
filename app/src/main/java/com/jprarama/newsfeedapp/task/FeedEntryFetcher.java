package com.jprarama.newsfeedapp.task;

import android.content.Context;
import android.os.AsyncTask;

import com.jprarama.newsfeedapp.R;
import com.jprarama.newsfeedapp.consumer.ListConsumer;
import com.jprarama.newsfeedapp.exception.StatusNotOkException;
import com.jprarama.newsfeedapp.model.FeedEntry;
import com.jprarama.newsfeedapp.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joshua on 5/7/16.
 */
public class FeedEntryFetcher extends AsyncTask<String, Void, ArrayList<FeedEntry>> {
    private static final String BASE_URL = "http://content.guardianapis.com/search";
    private static final String API_KEY = "api-key";

    private static final String RESPONSE_DATA_KEY = "response";
    private static final String RESPONSE_STATUS_KEY = "status";
    private static final String ENTRIES_KEY = "results";
    private static final String URL_KEY = "webUrl";
    private static final String TITLE_KEY = "webTitle";
    private static final String PUBLICATION_KEY = "webPublicationDate";

    private static final String STATUS_OK = "ok";;

    private ListConsumer<FeedEntry> consumer;
    private Context context;
    private Exception catchedException;

    public FeedEntryFetcher(ListConsumer<FeedEntry> consumer, Context context) {
        this.consumer = consumer;
        this.context = context;
    }

    @Override
    protected ArrayList<FeedEntry> doInBackground(String... strings) {
        Map<String, String> params = new HashMap<>();
        params.put("q", strings[0]);
        params.put(API_KEY, context.getString(R.string.api_key));

        try {
            catchedException = null;
            String json = HttpUtil.getRequest(BASE_URL, params);
            JSONObject resultObj = new JSONObject(json);
            JSONObject data = resultObj.getJSONObject(RESPONSE_DATA_KEY);

            String status = data.getString(RESPONSE_STATUS_KEY);
            if (!STATUS_OK.equals(status)) {
                throw new StatusNotOkException();
            }

            JSONArray entries = data.getJSONArray(ENTRIES_KEY);
            ArrayList<FeedEntry> items = new ArrayList<>();

            for (int i = 0, len = entries.length(); i < len; i++) {
                JSONObject entry = entries.getJSONObject(i);
                String url = entry.getString(URL_KEY);
                String title = entry.getString(TITLE_KEY);
                String updated = entry.getString(PUBLICATION_KEY);
                        
                items.add(new FeedEntry(title, url, updated));
            }

            return items;
        } catch (Exception e) {
            catchedException = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<FeedEntry> strings) {
        super.onPostExecute(strings);

        if (catchedException == null) {
            consumer.consume(strings);
        } else  {
            consumer.consumeException(catchedException);
        }
    }
}
