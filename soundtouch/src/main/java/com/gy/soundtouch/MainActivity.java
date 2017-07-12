package com.gy.soundtouch;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import net.surina.ExampleActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_hello).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExampleActivity.startSelf(MainActivity.this);
            }
        });
    }
}
