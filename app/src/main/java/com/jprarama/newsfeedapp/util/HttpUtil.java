package com.jprarama.newsfeedapp.util;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by joshua on 5/7/16.
 */
public class HttpUtil {

    public static InputStream performRequest(String type, String baseUrl, Map<String, String> params, Map<String, String> headers) throws IOException {
        Uri.Builder uriBuilder = Uri.parse(baseUrl).buildUpon();

        if (params != null) {
            for (Map.Entry<String, String> param: params.entrySet()) {
                uriBuilder.appendQueryParameter(param.getKey(), param.getValue());
            }
        }

        String mainUrl = uriBuilder.build().toString();
        System.out.println(mainUrl);
        URL url = new URL(mainUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(type);

        if (headers != null) {
            for (Map.Entry<String, String> header: headers.entrySet()) {
                connection.addRequestProperty(header.getKey(), header.getValue());
            }
        }

        connection.connect();

        return connection.getInputStream();
    }

    public static String getRequest(String baseUrl, Map<String, String> params) throws IOException {
        InputStream stream = performRequest("GET", baseUrl, params, null);
        return readStream(stream);
    }

    private static String readStream(InputStream stream) throws IOException {
        InputStreamReader isr = new InputStreamReader(stream);
        StringBuffer sb = new StringBuffer();

        BufferedReader br = new BufferedReader(isr);
        try {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
        } finally {
            br.close();
        }
        return sb.toString();
    }
}
