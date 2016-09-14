package ru.mail.park.lesson3;

import android.content.Loader;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static {
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .penaltyLog()
                .penaltyDeath()
                .build()
        );
    }

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

        UrlDownloader.getInstance().setCallback(new UrlDownloader.Callback() {
            @Override
            public void onLoaded(String value) {
                onTextLoaded(value);
            }
        });
    }

    private void loadFromUrl() {
        text.setText("Loading...");
        UrlDownloader.getInstance().load();
    }

    private void onTextLoaded(String stringFromUrl) {
        if (stringFromUrl == null) {
            stringFromUrl = "Data unavailable";
        }
        Toast.makeText(MainActivity.this, stringFromUrl, Toast.LENGTH_SHORT).show();
        text.setText(stringFromUrl);
    }
}
