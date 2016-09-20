package ru.mail.park.lesson3;

import android.support.v4.util.LruCache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.Request;
import okhttp3.Response;

public class UrlDownloader {

    private static final UrlDownloader DOWNLOADER = new UrlDownloader();

    public static UrlDownloader getInstance() {
        return DOWNLOADER;
    }

    public interface Callback {
        void onLoaded(String request, String value);
    }

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ArrayList<Future> futures = new ArrayList<>();

    private final LruCache<String, String> cache = new LruCache<>(32);

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void load(final String url) {
        final String cachedResult = cache.get(url);
        if (cachedResult != null) {
            callback.onLoaded(url, cachedResult);
            return;
        }

        futures.add(executor.submit(new Runnable() {
            @Override
            public void run() {
                String result;
                try {
                    result = loadInternal(url);
                } catch (IOException ignored) {
                    result = null;
                } catch (InterruptedException ignored) {
                    return;
                }
                notifyLoaded(url, result);
            }
        }));
    }

    public void clearCache() {
        cache.evictAll();
    }

    public void cancelAll() {
        for (Future f : futures)
            f.cancel(true);
        futures.clear();
    }

    private void cleanupFutures() {
        for (Iterator<Future> it = futures.iterator(); it.hasNext();) {
            Future f = it.next();
            if (f.isDone() || f.isCancelled())
                it.remove();
        }
    }

    private void notifyLoaded(final String url, final String result) {
        Ui.run(new Runnable() {
            @Override
            public void run() {
                cleanupFutures();

                if (result != null) {
                    cache.put(url, result);
                }
                if (callback != null) {
                    callback.onLoaded(url, result);
                }
            }
        });
    }

    private String loadInternal(String url) throws IOException, InterruptedException {
        Thread.sleep(5000);

        Response response = Http.getClient().newCall(
                new Request.Builder()
                        .url(url)
                        .build()
        ).execute();

        try {
            return response.body().string();
        } finally {
            response.close();
        }
    }

}
