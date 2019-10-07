package com.example.testmixpanelapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final String MIXPANEL_TOKEN = "2cafa09994191a7684c38d5354967728";

    MixpanelAPI mixpanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mixpanel =
                MixpanelAPI.getDefaultInstance(getApplicationContext());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("s", 1);
            mixpanel.track("test", jsonObject);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        mixpanel.flush();
        super.onDestroy();
    }
}
