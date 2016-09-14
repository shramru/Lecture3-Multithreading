package ru.mail.park.lesson3;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView text;

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
    }

    private void loadFromUrl() {
        new AsyncTask<String, Void, String>() {

            @Override
            protected void onPreExecute() {
                text.setText("Loading...");
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    return readStringFromUrl(params[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                onTextLoaded(result);
            }

        }.execute("https://gist.githubusercontent.com/anonymous/66e735b3894c5e534f2cf381c8e3165e/raw/8c16d9ec5de0632b2b5dc3e5c114d92f3128561a/gistfile1.txt");
    }

    private void onTextLoaded(final String stringFromUrl) {
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
