package com.jprarama.newsfeedapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jprarama.newsfeedapp.R;
import com.jprarama.newsfeedapp.adapter.FeedEntryAdapter;
import com.jprarama.newsfeedapp.consumer.ListConsumer;
import com.jprarama.newsfeedapp.model.FeedEntry;
import com.jprarama.newsfeedapp.task.FeedEntryFetcher;
import com.jprarama.newsfeedapp.util.Utilities;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private TextView tvNoQuery;
    private ListView listView;

    private FeedEntryAdapter adapter;
    private ArrayList<FeedEntry> feedEntries;
    private SwipeRefreshLayout refreshLayout;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvNoQuery = (TextView) findViewById(R.id.tvNoQuery);

        adapter = new FeedEntryAdapter(this, R.layout.feed_entry_item);
        adapter.setNotifyOnChange(false);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                FeedEntry item = feedEntries.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl()));

                startActivity(intent);
            }
        });

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.w(TAG, "Refreshing");
                loadFeedEntries();
            }
        });

        loadFeedEntries();
    }

    private void loadFeedEntries() {
        query = Utilities.getPreference(this, getString(R.string.pref_feed_query_key),
                getString(R.string.default_feed_query));

        new FeedEntryFetcher(new ListConsumer<FeedEntry>() {
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
                tvNoQuery.setText(getString(R.string.error_fetching_data));
                tvNoQuery.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);

                refreshLayout.setRefreshing(false);
            }
        }, this).execute(query);
    }

    private void listItems() {
        if (feedEntries == null) {
            Log.w(TAG, "Feed Entry List is null");
            return;
        }

        if (feedEntries.isEmpty()) {
            tvNoQuery.setText(String.format(getString(R.string.no_results), query));
            tvNoQuery.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }

        tvNoQuery.setVisibility(View.GONE);

        adapter.clear();
        adapter.addAll(feedEntries);
        adapter.notifyDataSetChanged();
        listView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
