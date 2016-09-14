package ru.mail.park.lesson3;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final int LOADER_ID = 1;

    static {
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .penaltyLog()
                .penaltyDeath()
                .build()
        );
    }

    private TextView text;

    private Loader<String> loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.text);

        text.setText("Click me");

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFromUrl();
            }
        });

        loader = getLoaderManager().initLoader(LOADER_ID, null,
                new LoaderManager.LoaderCallbacks<String>() {
            @Override
            public Loader<String> onCreateLoader(int id, final Bundle args) {
                return new AsyncTaskLoader<String>(MainActivity.this) {
                    @Override
                    public String loadInBackground() {
                        try {
                            return readStringFromUrl("https://gist.githubusercontent.com/anonymous/66e735b3894c5e534f2cf381c8e3165e/raw/8c16d9ec5de0632b2b5dc3e5c114d92f3128561a/gistfile1.txt");
                        } catch (IOException e) {
                            return null;
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<String> loader, String data) {
                onTextLoaded(data);
            }

            @Override
            public void onLoaderReset(Loader<String> loader) {
                Log.d("MainActivity", "OnLoaderReset");
            }
        });
    }

    private void loadFromUrl() {
        loader.forceLoad();
    }

    private void onTextLoaded(String stringFromUrl) {
        if (stringFromUrl == null) {
            stringFromUrl = "Data unavailable";
        }
        Toast.makeText(MainActivity.this, stringFromUrl, Toast.LENGTH_SHORT).show();
        text.setText(stringFromUrl);
    }

    private String readStringFromUrl(String url) throws IOException {
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
