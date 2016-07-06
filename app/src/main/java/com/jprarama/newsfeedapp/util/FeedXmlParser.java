package com.jprarama.newsfeedapp.util;

import android.text.Html;
import android.util.Xml;

import com.jprarama.newsfeedapp.model.FeedEntry;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by joshua on 6/7/16.
 */
public class FeedXmlParser {
    private static final String TAG = FeedXmlParser.class.getName();
    // We don't use namespaces
    private static final String ns = null;
    private static final String TITLE_TAG = "title";
    private static final String LINK_TAG = "link";
    private static final String SUMMARY_TAG = "summary";
    private static final String DESCRIPTION_TAG = "description";
    private static final String PUBDATE_TAG = "pubDate";
    private static final String UPDATED_TAG = "updated";
    private static final int MAX_SUMMARY = 150;


    public static ArrayList<FeedEntry> parse(InputStream xmlIn) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(xmlIn, null);
            parser.nextTag();
            return readFeed2(parser);
        } finally {
            xmlIn.close();
        }
    }

    private static ArrayList<FeedEntry> readFeed2(XmlPullParser parser) throws IOException, XmlPullParserException {
        ArrayList<FeedEntry> entries = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("channel")) {
                return readChannel(parser);
            } else if (name.equals("entry")) {
                FeedEntry entry = readEntry(parser);
                entries.add(entry);
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private static ArrayList<FeedEntry> readFeed(XmlPullParser parser) throws IOException, XmlPullParserException {
        ArrayList<FeedEntry> entries = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("entry")) {
                FeedEntry entry = readEntry(parser);
                entries.add(entry);
            } else if (name.equals("channel")) {
                ArrayList<FeedEntry> channelItems = readAtom(parser);
                entries.addAll(channelItems);
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private static ArrayList<FeedEntry> readAtom(XmlPullParser parser) throws IOException, XmlPullParserException {
        ArrayList<FeedEntry> entries = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, ns, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("channel")) {
                ArrayList<FeedEntry> items = readChannel(parser);
                entries.addAll(items);
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private static ArrayList<FeedEntry> readChannel(XmlPullParser parser) throws IOException, XmlPullParserException {
        ArrayList<FeedEntry> entries = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, ns, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("item")) {
                FeedEntry entry = readItem(parser);
                entries.add(entry);
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private static FeedEntry readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String title = null;
        String summary = null;
        String link = null;
        String updated = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(TITLE_TAG)) {
                title = readBasicTag(parser, TITLE_TAG);
            } else if (name.equals(DESCRIPTION_TAG)) {
                summary = readBasicTag(parser, DESCRIPTION_TAG);
            } else if (name.equals(PUBDATE_TAG)) {
                updated = readBasicTag(parser, PUBDATE_TAG);
            } else if (name.equals(LINK_TAG)) {
                link = readBasicTag(parser, LINK_TAG);
            } else {
                skip(parser);
            }
        }
        return new FeedEntry(title, link, updated, summary);
    }

    private static FeedEntry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String title = null;
        String summary = null;
        String link = null;
        String updated = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(TITLE_TAG)) {
                title = readBasicTag(parser, TITLE_TAG);
            } else if (name.equals(SUMMARY_TAG)) {
                summary = readBasicTag(parser, SUMMARY_TAG);
            } else if (name.equals(UPDATED_TAG)) {
                updated = readBasicTag(parser, UPDATED_TAG);
            } else if (name.equals(LINK_TAG)) {
                link = readLink(parser);
            } else {
                skip(parser);
            }
        }
        return new FeedEntry(title, link, updated, summary);
    }


    // For the tags title and summary, extracts their text values.
    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static String readBasicTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String text = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return text;
    }

    // Processes link tags in the feed.
    private static String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, LINK_TAG);
        String tag = parser.getName();
        if (tag.equals(LINK_TAG)) {
            link = parser.getAttributeValue(null, "href");
            parser.nextTag();
        }

        parser.require(XmlPullParser.END_TAG, ns, LINK_TAG);
        return link;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
