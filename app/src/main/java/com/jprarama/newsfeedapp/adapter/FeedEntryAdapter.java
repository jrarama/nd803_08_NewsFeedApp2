package com.jprarama.newsfeedapp.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jprarama.newsfeedapp.R;
import com.jprarama.newsfeedapp.model.FeedEntry;

/**
 * Created by joshua on 6/7/16.
 */
public class FeedEntryAdapter extends ArrayAdapter<FeedEntry> {
    private static class ViewHolder {
        TextView tvTitle;
        TextView tvPublished;
        TextView tvLink;
        TextView tvSummary;
    }
    public FeedEntryAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FeedEntry entry = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.feed_detail_item, parent, false);

            viewHolder.tvLink = (TextView) convertView.findViewById(R.id.tvLink);
            viewHolder.tvPublished = (TextView) convertView.findViewById(R.id.tvPublished);
            viewHolder.tvSummary = (TextView) convertView.findViewById(R.id.tvSummary);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvLink.setText(entry.getUrl());
        viewHolder.tvPublished.setText(entry.getUpdated());
        viewHolder.tvTitle.setText(entry.getTitle());

        String summary = entry.getSnippet();
        summary = summary == null ? getContext().getString(R.string.no_summary) :
                Html.fromHtml(entry.getSnippet().replaceAll("<img.+/(img)*>", "")).toString();

        if (summary.length() > 150) {
            summary = summary.substring(0, 150) + " ...";
        }
        viewHolder.tvSummary.setText(summary);

        return convertView;
    }
}
