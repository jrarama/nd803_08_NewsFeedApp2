package com.jprarama.newsfeedapp.activity;

import android.app.Activity;
import android.content.Intent;
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
import com.jprarama.newsfeedapp.adapter.FeedUrlAdapter;
import com.jprarama.newsfeedapp.consumer.ListConsumer;
import com.jprarama.newsfeedapp.model.FeedUrl;
import com.jprarama.newsfeedapp.task.FeedUrlFetcher;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final String DEFAULT_QUERY = "programming";

    private TextView tvNoQuery;
    private ListView listView;

    private FeedUrlAdapter adapter;
    private ArrayList<FeedUrl> feedUrls;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvNoQuery = (TextView) findViewById(R.id.tvNoQuery);
        listView = (ListView) findViewById(R.id.listView);

        adapter = new FeedUrlAdapter(this, R.layout.feed_url_item);
        adapter.setNotifyOnChange(false);
        listView.setAdapter(adapter);

        final Activity activity = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                FeedUrl item = feedUrls.get(position);
                Intent intent = new Intent(activity, FeedDetailActivity.class);
                intent.setAction(FeedDetailActivity.VIEW_FEEDS_ACTION);
                intent.putExtra(FeedDetailActivity.FEED_URL_KEY, item.getUrl());

                startActivity(intent);
            }
        });

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.w(TAG, "Refreshing");
                loadFeedUrls();
            }
        });

        loadFeedUrls();
    }

    private void loadFeedUrls() {
        String query = DEFAULT_QUERY;
        final Activity activity = this;
        new FeedUrlFetcher(new ListConsumer<FeedUrl>() {
            @Override
            public void consume(ArrayList<FeedUrl> list) {
                feedUrls = list;
                listItems();

                refreshLayout.setRefreshing(false);
            }

            @Override
            public void consumeException(Exception e) {
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();

                refreshLayout.setRefreshing(false);
            }
        }).execute(query);
    }

    private void listItems() {
        if (feedUrls == null) {
            Log.w(TAG, "Feed Url List is null");
            return;
        }

        if (feedUrls.isEmpty()) {
            tvNoQuery.setText(getString(R.string.no_results));
            tvNoQuery.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }

        tvNoQuery.setVisibility(View.GONE);

        adapter.clear();
        adapter.addAll(feedUrls);
        adapter.notifyDataSetChanged();
        listView.setVisibility(View.VISIBLE);
    }

}
