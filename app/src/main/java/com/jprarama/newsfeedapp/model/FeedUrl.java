package com.jprarama.newsfeedapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by joshua on 5/7/16.
 */
public class FeedUrl implements Parcelable {

    private String title;

    private String url;

    public FeedUrl(String title, String url) {
        this.title = title;
        this.url = url;
    }

    protected FeedUrl(Parcel in) {
        title = in.readString();
        url = in.readString();
    }

    public static final Creator<FeedUrl> CREATOR = new Creator<FeedUrl>() {
        @Override
        public FeedUrl createFromParcel(Parcel in) {
            return new FeedUrl(in);
        }

        @Override
        public FeedUrl[] newArray(int size) {
            return new FeedUrl[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(url);
    }
}
