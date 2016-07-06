package com.jprarama.newsfeedapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jprarama.newsfeedapp.R;
import com.jprarama.newsfeedapp.model.FeedEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by joshua on 6/7/16.
 */
public class FeedEntryAdapter extends ArrayAdapter<FeedEntry> {

    private static final String DATE_FORMAT_FROM = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String TARGET_DATE_FORMAT = "EEE, MMM d, yyyy HH:mm";
    private static final String TAG = FeedEntryAdapter.class.getName();

    private SimpleDateFormat formatter;
    private SimpleDateFormat formatterTo;

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvPublished;
        TextView tvLink;
    }

    public FeedEntryAdapter(Context context, int resource) {
        super(context, resource);
        formatter = new SimpleDateFormat(DATE_FORMAT_FROM);
        formatterTo = new SimpleDateFormat(TARGET_DATE_FORMAT);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FeedEntry entry = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.feed_entry_item, parent, false);

            viewHolder.tvLink = (TextView) convertView.findViewById(R.id.tvLink);
            viewHolder.tvPublished = (TextView) convertView.findViewById(R.id.tvPublished);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvLink.setText(entry.getUrl());
        String updated = entry.getUpdated();
        try {
            Date date = formatter.parse(updated);
            updated = formatterTo.format(date);
        } catch (ParseException e) {
            Log.w(TAG, e.toString());
        }

        viewHolder.tvPublished.setText(updated);
        viewHolder.tvTitle.setText(entry.getTitle());

        return convertView;
    }
}
