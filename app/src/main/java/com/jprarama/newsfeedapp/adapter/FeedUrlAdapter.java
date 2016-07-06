package com.jprarama.newsfeedapp.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jprarama.newsfeedapp.R;
import com.jprarama.newsfeedapp.model.FeedUrl;

import java.util.List;

/**
 * Created by joshua on 5/7/16.
 */
public class FeedUrlAdapter extends ArrayAdapter<FeedUrl> {

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvUrl;
    }

    public FeedUrlAdapter(Context context, int resource) {
        super(context, resource);
    }

    public FeedUrlAdapter(Context context, int resource, List<FeedUrl> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FeedUrl item = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.feed_url_item, parent, false);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.tvUrl = (TextView) convertView.findViewById(R.id.tvUrl);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvTitle.setText(Html.fromHtml(item.getTitle()));
        viewHolder.tvUrl.setText(item.getUrl());

        return convertView;
    }
}
