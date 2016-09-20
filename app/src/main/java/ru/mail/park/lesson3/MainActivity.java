package ru.mail.park.lesson3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefs";

    private static final String URL_1 = "https://gist.githubusercontent.com/anonymous/66e735b3894c5e534f2cf381c8e3165e/raw/8c16d9ec5de0632b2b5dc3e5c114d92f3128561a/gistfile1.txt";
    private static final String URL_2 = "https://gist.githubusercontent.com/anonymous/be76b41ddf012b761c15a56d92affeb6/raw/bb1d4f849cb79264b53a9760fe428bbe26851849/gistfile1.txt";
    private static final String textKey1 = "text1";
    private static final String textKey2 = "text2";

    static {
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .penaltyLog()
                .penaltyDeath()
                .build()
        );
    }

    private TextView text1;
    private TextView text2;

    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = getSharedPreferences(PREFS_NAME, 0);

        findViewById(R.id.open_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AnotherActivity.class));
            }
        });

        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);

        text1.setText(getString(R.string.click_me));
        text2.setText(getString(R.string.click_me));

        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFromUrl(URL_1);
            }
        });

        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFromUrl(URL_2);
            }
        });

        UrlDownloader.getInstance().setCallback(new UrlDownloader.Callback() {
            @Override
            public void onLoaded(String key, String value) {
                onTextLoaded(key, value);
            }
        });
    }

    private void saveString(String key, String value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String getString(String key) {
        return settings.getString(key, null);
    }

    private void loadFromUrl(String url) {
        textViewForUrl(url).setText(getString(R.string.loading));
        UrlDownloader.getInstance().load(url);
    }

    private void onTextLoaded(String url, String stringFromUrl) {
        if (stringFromUrl == null) {
            stringFromUrl = getString(R.string.data_unavailable);
        }

        Toast.makeText(getApplicationContext(), stringFromUrl, Toast.LENGTH_SHORT).show();
        textViewForUrl(url).setText(stringFromUrl);

        saveString(textKey1, text1.getText().toString());
        saveString(textKey2, text2.getText().toString());
    }

    private TextView textViewForUrl(String url) {
        if (URL_1.equals(url)) {
            return text1;
        } else if (URL_2.equals(url)) {
            return text2;
        }
        throw new IllegalArgumentException(getString(R.string.unknown_url) + url);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveString(textKey1, text1.getText().toString());
        saveString(textKey2, text2.getText().toString());
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        text1.setText(getString(textKey1));
        text2.setText(getString(textKey2));
    }
}
