package com.jprarama.newsfeedapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jprarama.newsfeedapp.R;
import com.jprarama.newsfeedapp.adapter.FeedEntryAdapter;
import com.jprarama.newsfeedapp.adapter.FeedUrlAdapter;
import com.jprarama.newsfeedapp.consumer.ListConsumer;
import com.jprarama.newsfeedapp.model.FeedEntry;
import com.jprarama.newsfeedapp.model.FeedUrl;
import com.jprarama.newsfeedapp.task.FeedItemsFetcher;

import java.util.ArrayList;

public class FeedDetailActivity extends AppCompatActivity {

    private static final String TAG = FeedDetailActivity.class.getName();
    public static final String VIEW_FEEDS_ACTION = "view_feeds";
    public static final String FEED_URL_KEY = "feed_url";

    private TextView tvNoResults;
    private ListView listView;
    private SwipeRefreshLayout refreshLayout;

    private FeedEntryAdapter adapter;
    private ArrayList<FeedEntry> feedEntries;
    private String feedUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvNoResults = (TextView) findViewById(R.id.tvNoResults);
        adapter = new FeedEntryAdapter(this, R.layout.feed_detail_item);
        adapter.setNotifyOnChange(false);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                FeedEntry entry = feedEntries.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getUrl()));
                startActivity(intent);
            }
        });

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.w(TAG, "Refreshing");
                loadFeedEntries(feedUrl);
            }
        });

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (VIEW_FEEDS_ACTION.equals(intent.getAction())) {
            feedUrl = intent.getStringExtra(FEED_URL_KEY);
            loadFeedEntries(feedUrl);
        }

        intent.setAction(null);
        setIntent(null);
    }

    private void loadFeedEntries(String url) {
        Log.d(TAG, "Fetching " + url);
        new FeedItemsFetcher(new ListConsumer<FeedEntry>() {
            @Override
            public void consume(ArrayList<FeedEntry> list) {
                feedEntries = list;

                listItems();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void consumeException(Exception e) {
                refreshLayout.setRefreshing(false);
                Log.w(TAG, "Error: " + e.getMessage());
                tvNoResults.setText(getString(R.string.error_fetching_data));
                tvNoResults.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
        }).execute(url);
    }

    private void listItems() {
        if (feedEntries == null || feedEntries.isEmpty()) {
            tvNoResults.setText(getString(R.string.no_results));
            tvNoResults.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }

        tvNoResults.setVisibility(View.GONE);

        adapter.clear();
        adapter.addAll(feedEntries);
        adapter.notifyDataSetChanged();
        listView.setVisibility(View.VISIBLE);
    }
}
