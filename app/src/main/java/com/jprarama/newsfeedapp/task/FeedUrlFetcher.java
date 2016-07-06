package com.jprarama.newsfeedapp.task;

import android.os.AsyncTask;

import com.jprarama.newsfeedapp.consumer.ListConsumer;
import com.jprarama.newsfeedapp.exception.StatusNotOkException;
import com.jprarama.newsfeedapp.model.FeedUrl;
import com.jprarama.newsfeedapp.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joshua on 5/7/16.
 */
public class FeedUrlFetcher extends AsyncTask<String, Void, ArrayList<FeedUrl>> {
    private static final String BASE_URL = "https://ajax.googleapis.com/ajax/services/feed/find";
    private static final String API_VERSION = "1.0";

    private static final String RESPONSE_DATA_KEY = "responseData";
    private static final String RESPONSE_STATUS_KEY = "responseStatus";
    private static final String ENTRIES_KEY = "entries";
    private static final String URL_KEY = "url";
    private static final String TITLE_KEY = "title";

    private static final int STATUS_OK = 200;

    private ListConsumer<FeedUrl> consumer;
    private Exception catchedException;

    public FeedUrlFetcher(ListConsumer<FeedUrl> consumer) {
        this.consumer = consumer;
    }

    @Override
    protected ArrayList<FeedUrl> doInBackground(String... strings) {
        Map<String, String> params = new HashMap<>();
        params.put("v", API_VERSION);
        params.put("q", strings[0]);

        try {
            catchedException = null;
            String json = HttpUtil.getRequest(BASE_URL, params);
            JSONObject resultObj = new JSONObject(json);
            int status = resultObj.getInt(RESPONSE_STATUS_KEY);
            if (status != STATUS_OK) {
                throw new StatusNotOkException();
            }
            JSONObject data = resultObj.getJSONObject(RESPONSE_DATA_KEY);
            JSONArray entries = data.getJSONArray(ENTRIES_KEY);
            ArrayList<FeedUrl> items = new ArrayList<>();

            for (int i = 0, len = entries.length(); i < len; i++) {
                JSONObject entry = entries.getJSONObject(i);
                String url = entry.getString(URL_KEY);
                String title = entry.getString(TITLE_KEY);

                items.add(new FeedUrl(title, url));
            }

            return items;
        } catch (Exception e) {
            catchedException = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<FeedUrl> strings) {
        super.onPostExecute(strings);

        if (catchedException == null) {
            consumer.consume(strings);
        } else  {
            consumer.consumeException(catchedException);
        }
    }
}
