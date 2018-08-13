package com.example.lab.android.nuc.ocrtest;

import android.content.Context;

import java.io.File;

class FileUtil {
    public static File getSaveFile(Context context) {
        File file = new File(context.getFilesDir(), "pic.jpg");
        return file;
    }
}
