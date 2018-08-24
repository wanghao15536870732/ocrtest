package com.example.lab.android.nuc.ocrtest.activity;

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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.GeneralParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.example.lab.android.nuc.ocrtest.BaseEnum.DictationResult;
import com.example.lab.android.nuc.ocrtest.imageview.RotationalTriangleImageView;
import com.example.lab.android.nuc.ocrtest.util.FileUtil;
import com.example.lab.android.nuc.ocrtest.R;
import com.example.lab.android.nuc.ocrtest.util.SynthesisManager;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class GeneralActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 101;
    private static final int REQUEST_CODE_CAMERA = 102;
    private TextView infoTextView;
    private ImageView imageView;
    private String filePath;
    private RotationalTriangleImageView mImageView;

    /*与悬浮按钮相关*/
    private FloatingActionsMenu mFloatingActionsMenu;
    private FloatingActionButton text_to_voice;
    private FloatingActionButton voice_to_text;
    private String TAG = "GeneralActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_general );
        initFloatButton();
        infoTextView = (TextView) findViewById(R.id.info_text_view);
        infoTextView.setTextIsSelectable(true);
        imageView = (ImageView) findViewById( R.id.image_view );
//        mImageView = (RotationalTriangleImageView) findViewById( R.id.iv );
//        mImageView.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mImageView.getParameter() == 0) {
//                    mImageView.start();
//                }
//            }
//        } );

        findViewById(R.id.gallery_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int ret = ActivityCompat.checkSelfPermission(GeneralActivity.this, Manifest.permission
                            .READ_EXTERNAL_STORAGE);
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(GeneralActivity.this,
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
                Intent intent = new Intent( GeneralActivity.this,ImageBrowsweActivity.class );
                intent.putExtra( "file_path",filePath );
                startActivity( intent );
            }
        } );

        findViewById(R.id.camera_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GeneralActivity.this, CameraActivity.class);
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
                ActivityCompat.requestPermissions(GeneralActivity.this,
                        new String[] {Manifest.permission.READ_PHONE_STATE},
                        100);
            }
        }

        //添加返回按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle( "通用文字识别" );
        }
    }

    //悬浮按钮的一些点击事件
    private void initFloatButton(){
        mFloatingActionsMenu = (FloatingActionsMenu) findViewById( R.id.main_actions_menu );
        text_to_voice = (FloatingActionButton) findViewById( R.id.action_text_to_voice );
        text_to_voice.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFloatingActionsMenu.toggle();
            }
        } );
        voice_to_text = (FloatingActionButton) findViewById( R.id.action_voice_to_text );
        voice_to_text.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFloatingActionsMenu.toggle();
            }
        } );
    }

    private void recGeneral(String filePath) {
        GeneralParams param = new GeneralParams();
        param.setDetectDirection(true);
        param.setImageFile(new File(filePath));
        OCR.getInstance(this).recognizeGeneral(param, new OnResultListener<GeneralResult>() {
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
                Bitmap bitmap = BitmapFactory.decodeStream( getContentResolver().openInputStream(  uri) );
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

    /*-------------------------------带窗口的语音识别--------------------------*/
    private void showRecognizerDialog() {
        //有动画效果
        RecognizerDialog iatDialog;
        // 初始化有交互动画的语音识别器
        iatDialog = new RecognizerDialog(GeneralActivity.this, mInitListener);
        //设置监听，实现听写结果的回调
        iatDialog.setListener(new RecognizerDialogListener() {
            String resultJson = "[";//放置在外边做类的变量则报错，会造成json格式不对
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                System.out.println("-----------------   onResult   -----------------");
                if (!isLast) {
                    resultJson += recognizerResult.getResultString() + ",";
                } else {
                    resultJson += recognizerResult.getResultString() + "]";
                }

                if (isLast) {
                    //解析语音识别后返回的json格式的结果
                    Gson gson = new Gson();
                    List<DictationResult> resultList = gson.fromJson(resultJson,
                            new TypeToken<List<DictationResult>>() {
                            }.getType());
                    String result = "";
                    for (int i = 0; i < resultList.size() - 1; i++) {
                        result += resultList.get(i).toString();
                    }
                    Log.d(TAG,"识别结果"+result);
                    SynthesisManager.getSingleton().startSpeaking(result);
                }
            }
            @Override
            public void onError(SpeechError speechError) {
                //自动生成的方法存根
                speechError.getPlainDescription(true);
            }
        });
        //开始听写，需将sdk中的assets文件下的文件夹拷入项目的assets文件夹下（没有的话自己新建）
        iatDialog.show();
    }
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(GeneralActivity.this, "初始化失败，错误码：" + code, Toast.LENGTH_SHORT).show();
            }
        }
    };

}
