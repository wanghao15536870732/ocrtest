package com.example.lab.android.nuc.ocrtest.application;

import android.app.Application;

import com.example.lab.android.nuc.ocrtest.util.RecognitionManager;
import com.example.lab.android.nuc.ocrtest.util.SynthesisManager;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * 功能:
 * 修改时间：
 * 修改备注：
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5b5ec33a");
        RecognitionManager.getSingleton().init(this,"5b5ec33a");
        SynthesisManager.getSingleton().init(this,"5b5ec33a");
    }
}
