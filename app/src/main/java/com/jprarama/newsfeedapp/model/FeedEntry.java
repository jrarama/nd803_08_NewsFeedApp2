package com.jprarama.newsfeedapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by joshua on 6/7/16.
 */
public class FeedEntry implements Parcelable {

    private String title;

    private String url;

    private String updated;

    public FeedEntry(String title, String url, String updated) {
        this.title = title;
        this.url = url;
        this.updated = updated;
    }

    protected FeedEntry(Parcel in) {
        title = in.readString();
        url = in.readString();
        updated = in.readString();
    }

    public static final Creator<FeedEntry> CREATOR = new Creator<FeedEntry>() {
        @Override
        public FeedEntry createFromParcel(Parcel in) {
            return new FeedEntry(in);
        }

        @Override
        public FeedEntry[] newArray(int size) {
            return new FeedEntry[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(url);
        parcel.writeString(updated);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

}