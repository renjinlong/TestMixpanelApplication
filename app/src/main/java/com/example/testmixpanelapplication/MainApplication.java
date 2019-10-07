package com.example.testmixpanelapplication;

import android.app.Application;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

public class MainApplication extends Application {

    private final String MIXPANEL_TOKEN = "YOUR_TOKEN";

    @Override
    public void onCreate() {
        super.onCreate();
        MixpanelAPI mixpanel =
                MixpanelAPI.getDefaultInstance(getApplicationContext());
    }


}
