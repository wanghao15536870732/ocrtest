package com.example.lab.android.nuc.ocrtest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.baidu.ocr.ui.camera.CameraActivity;

import java.io.File;
import java.io.FileNotFoundException;

public class AccurateActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 101;
    private static final int REQUEST_CODE_CAMERA = 102;
    private TextView infoTextView;
    private ImageView imageView;
    private String filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_accurate );
        //jsjsjsjsjk的骄傲理解的
        infoTextView = (TextView) findViewById(R.id.info_text_view);
        infoTextView.setTextIsSelectable(true);
        imageView = (ImageView) findViewById( R.id.image_view );
        findViewById(R.id.gallery_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int ret = ActivityCompat.checkSelfPermission(AccurateActivity.this, Manifest.permission
                            .READ_EXTERNAL_STORAGE);
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AccurateActivity.this,
                                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                1000);
                        return;
                    }
                }
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
            }
        });
        imageView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( AccurateActivity.this,ImageBrowsweActivity.class );
                intent.putExtra( "file_path",filePath );
                startActivity( intent );
            }
        } );

        findViewById(R.id.camera_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AccurateActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int ret = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
            if (ret == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(AccurateActivity.this,
                        new String[] {Manifest.permission.READ_PHONE_STATE},
                        100);
            }
        }
        //添加返回按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle( "通用文字识别 (高精度版)" );
        }
    }

    private void recGeneral(String filePath) {
        GeneralParams param = new GeneralParams();
        param.setDetectDirection(true);
        param.setImageFile(new File(filePath));
        OCR.getInstance(this).recognizeAccurate(param, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult result) {
                StringBuilder sb = new StringBuilder();
                for (WordSimple word : result.getWordList()) {
                    sb.append(word.getWords());
                    sb.append("\n");
                }
                infoTextView.setText(sb);
            }

            @Override
            public void onError(OCRError error) {
                infoTextView.setText(error.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (imageView.getVisibility() == View.GONE) {
                imageView.setVisibility( View.VISIBLE );
            }
            filePath = getRealPathFromURI(uri);
            try {
                Bitmap bitmap = BitmapFactory.decodeStream( getContentResolver().openInputStream(uri) );
                imageView.setImageBitmap( bitmap );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            recGeneral(filePath);
        }

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            filePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
            if (imageView.getVisibility() == View.GONE) {
                imageView.setVisibility( View.VISIBLE );
            }
            Bitmap bitmap = BitmapFactory.decodeFile( filePath );
            imageView.setImageBitmap( bitmap );
            recGeneral(filePath);

        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected( item );
    }
}
