package ru.mail.park.lesson3;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class UrlDownloader {

    private static final UrlDownloader DOWNLOADER = new UrlDownloader();

    public static UrlDownloader getInstance() {
        return DOWNLOADER;
    }

    public interface Callback {
        void onLoaded(String value);
    }

    private String cachedResult;

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void load() {
        if (cachedResult != null) {
            callback.onLoaded(cachedResult);
            return;
        }

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    return load(params[0]);
                } catch (IOException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String content) {
                cachedResult = content;
                if (callback != null) {
                    callback.onLoaded(content);
                }
            }
        }.execute("https://gist.githubusercontent.com/anonymous/66e735b3894c5e534f2cf381c8e3165e/raw/8c16d9ec5de0632b2b5dc3e5c114d92f3128561a/gistfile1.txt");
    }

    private String load(String url) throws IOException {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        StringBuilder result = new StringBuilder();

        URL oracle = new URL(url);
        InputStream in = oracle.openStream();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

            for (;;) {
                String inputLine = bufferedReader.readLine();
                if (inputLine == null) {
                    break;
                }

                result.append(inputLine).append('\n');
            }
        } finally {
            in.close();
        }

        return result.toString();
    }

}
