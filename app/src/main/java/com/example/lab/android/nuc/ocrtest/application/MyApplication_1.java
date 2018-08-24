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

public class MyApplication_1 extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5ad97691");
        RecognitionManager.getSingleton().init(this,"5ad97691");
        SynthesisManager.getSingleton().init(this,"5ad97691");
    }
}
