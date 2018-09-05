package com.example.lab.android.nuc.ocrtest.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.cocosw.bottomsheet.BottomSheet;
import com.example.lab.android.nuc.ocrtest.BaseEnum.DictationResult;
import com.example.lab.android.nuc.ocrtest.util.FileUtil;
import com.example.lab.android.nuc.ocrtest.R;
import com.example.lab.android.nuc.ocrtest.util.RecognitionManager;
import com.example.lab.android.nuc.ocrtest.util.SynthesisManager;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class GeneralActivity extends AppCompatActivity {

    private String VOICE = null;
    private static final int REQUEST_CODE_PICK_IMAGE = 101;
    private static final int REQUEST_CODE_CAMERA = 102;
    private TextView infoTextView;
    private ImageView imageView;
    private String filePath;
    private StringBuilder sb = new StringBuilder(  );

    private Button btnRecognizerDialog; //带窗口的语音识别
    private Button btnSynthesizer; //语音合成

    List<String> permissionList = new ArrayList<>(  );
    //有动画效果
    private RecognizerDialog iatDialog;

    /*与悬浮按钮相关*/
    private FloatingActionsMenu mFloatingActionsMenu;
    private FloatingActionButton text_to_voice, voice_to_text,change_voice,huxue;

    private String TAG = "GeneralActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_general );
        initFloatButton();
        infoTextView = (TextView) findViewById(R.id.info_text_view);
        infoTextView.setMovementMethod( ScrollingMovementMethod.getInstance());
//        infoTextView.setMovementMethod( ScrollingMovementMethod.getInstance());
        imageView = (ImageView) findViewById( R.id.image_view );

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

        //获取手机录音机使用权限，听写、识别、语义理解需要用到此权限
        if(ContextCompat.checkSelfPermission( GeneralActivity.this, Manifest.
                permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            permissionList.add( Manifest.permission.RECORD_AUDIO );
        }
        //读取手机信息权限
        if(ContextCompat.checkSelfPermission( GeneralActivity.this, Manifest.
                permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add( Manifest.permission.READ_PHONE_STATE );
        }
        //SD卡读写的权限（如果需要保存音频文件到本地的话）
        if(ContextCompat.checkSelfPermission( GeneralActivity.this, Manifest.
                permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add( Manifest.permission.READ_EXTERNAL_STORAGE );
        }
        if (! permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions( GeneralActivity.this,permissions,1 );
        }else {
            setRecognitionManager();
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

        change_voice = (FloatingActionButton) findViewById( R.id.change_voice );
        change_voice.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SpeechSynthesizer.getSynthesizer() != null && SpeechSynthesizer.getSynthesizer().isSpeaking()){
                    SpeechSynthesizer.getSynthesizer().pauseSpeaking();
                    text_to_voice.setIcon( R.drawable.ic_play );
                    text_to_voice.setTitle( "文字播报" );
                }
                mFloatingActionsMenu.toggle();
                new BottomSheet.Builder(GeneralActivity.this)
                        .title( "选择声音种类" )
                        .sheet( R.menu.change_voice )
                        .listener( new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case R.id.xiaoyan:
                                        VOICE = "xiaoyan";
                                        break;
                                    case R.id.xiaoyu:
                                        VOICE = "xiaoyu";
                                        break;
                                    case R.id.henry:
                                        VOICE = "henry";
                                        break;
                                    case R.id.vimary:
                                        VOICE = "vimary";
                                        break;
                                    case R.id.xiaomei:
                                        VOICE = "xiaomei";
                                        break;
                                    case R.id.vixl:
                                        VOICE = "vixl";
                                        break;
                                    case R.id.xiaorong:
                                        VOICE = "xiaorong";
                                        break;
                                    case R.id.xiaokun:
                                        VOICE = "xiaokun";
                                        break;
                                    case R.id.xiaoqiang:
                                        VOICE = "xiaoqiang";
                                        break;
                                    case R.id.vixying:
                                        VOICE = "vixying";
                                        break;
                                    case R.id.nannan:
                                        VOICE = "nannan";
                                        break;
                                    case R.id.vils:
                                        VOICE = "vils";
                                        break;
                                    case R.id.xiaoxin:
                                        VOICE = "xiaoxin";
                                        break;
                                    case R.id.cancel_chose:
                                        break;

                                }
                            }
                        } ).show();
            }
        } );

        text_to_voice = (FloatingActionButton) findViewById( R.id.action_text_to_voice );
        text_to_voice.setIcon( R.drawable.ic_play  );
        text_to_voice.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFloatingActionsMenu.toggle();
                if (infoTextView.getText() != null && text_to_voice.getTitle().equals("文字播报")) {
                    SpeechSynthesizer( infoTextView.getText().toString() );
                    SpeechSynthesizer.getSynthesizer().resumeSpeaking();
                    text_to_voice.setIcon( R.drawable.ic_pause);
                    text_to_voice.setTitle( "结束播报" );
                }else if (infoTextView.getText() != null && text_to_voice.getTitle().equals("结束播报")){
                    if (SpeechSynthesizer.getSynthesizer().isSpeaking())
                        SpeechSynthesizer.getSynthesizer().pauseSpeaking();
                    text_to_voice.setIcon( R.drawable.ic_play );
                    text_to_voice.setTitle( "文字播报" );
                }
            }
        } );
        voice_to_text = (FloatingActionButton) findViewById( R.id.action_voice_to_text );
        voice_to_text.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFloatingActionsMenu.toggle();
                voice_text();
            }
        } );
        huxue = (FloatingActionButton) findViewById( R.id.huxue );
        huxue.setOnClickListener( new View.OnClickListener() {
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
        if (cursor == null) {
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

    private void setRecognitionManager(){
        RecognitionManager.getSingleton().startRecognitionWithDialog(this, new RecognitionManager.onRecognitionListen() {
            @Override
            public void result(String msg) {
                SynthesisManager.getSingleton().startSpeaking(msg);
            }
            @Override
            public void error(String errorMsg) {

            }

            @Override
            public void onBeginOfSpeech() {

            }

            @Override
            public void onVolumeChanged(int volume, byte[] data) {

            }

            @Override
            public void onEndOfSpeech() {

            }
        });

    }


    /*-------------------------------语音转文字--------------------------*/
    private void voice_text(){
        // 有交互动画的语音识别器
        iatDialog = new RecognizerDialog(GeneralActivity.this, mInitListener);

        iatDialog.setListener(new RecognizerDialogListener() {
            String resultJson = "[";//放置在外边做类的变量则报错，会造成json格式不对（？）

            @SuppressLint("SetTextI18n")
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

                    if (infoTextView.getText() != null) {
                        infoTextView.setText( infoTextView.getText().toString() + result.toString() );
                    }

                    int offset = infoTextView.getLineCount() * infoTextView.getLineHeight();
                    if(offset > (infoTextView.getHeight() - infoTextView.getLineHeight() - 20)){
                        infoTextView.scrollTo(0,offset - infoTextView.getHeight() + infoTextView.getLineHeight() + 20);
                    }
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

    /**
     * 用于SpeechRecognizer（无交互动画）对象的监听回调
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            Log.i(TAG, recognizerResult.toString());
        }

        @Override
        public void onError(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(GeneralActivity.this, "初始化失败，错误码：" + code, Toast.LENGTH_SHORT).show();
            }
        }
    };

    /*-------------------------------语音合成--------------------------*/
    public void SpeechSynthesizer(String text){
        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(GeneralActivity.this, null);

        /**
         2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
         *
         */
        // 清空参数
        mTts.setParameter( SpeechConstant.PARAMS, null);
        mTts.setParameter( SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        if (VOICE != null){
            mTts.setParameter( SpeechConstant.VOICE_NAME, VOICE );//设置发音人
        }else {
            mTts.setParameter( SpeechConstant.VOICE_NAME, "xiaoyan" );//设置发音人
        }
        mTts.setParameter( SpeechConstant.SPEED, "50");//设置语速
        //设置合成音调
        mTts.setParameter( SpeechConstant.PITCH, "50");
        mTts.setParameter( SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter( SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter( SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        boolean isSuccess = mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts2.wav");
//        Toast.makeText(MainActivity.this, "语音合成 保存音频到本地：\n" + isSuccess, Toast.LENGTH_LONG).show();
        //3.开始合成
        int code = mTts.startSpeaking(text, mSynListener);

        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                //上面的语音配置对象为初始化时：
                Toast.makeText(GeneralActivity.this, "语音组件未安装", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(GeneralActivity.this, "语音合成失败,错误码: " + code, Toast.LENGTH_LONG).show();
            }
        }
    }
    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {

        }
        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }
        //开始播放
        public void onSpeakBegin() {
        }
        //暂停播放
        public void onSpeakPaused() {
        }
        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }
        //恢复播放回调接口
        public void onSpeakResumed() {
        }
        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0){
                    for (int result : grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText( this, "必须同意所有的权限才能使用本程序", Toast.LENGTH_SHORT ).show();
                            return;
                        }
                    }
                    setRecognitionManager();
                }else {
                    Toast.makeText( this,"发生未知错误", Toast.LENGTH_SHORT ).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (SpeechSynthesizer.getSynthesizer() != null && SpeechSynthesizer.getSynthesizer().isSpeaking()){
            SpeechSynthesizer.getSynthesizer().stopSpeaking();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (SpeechSynthesizer.getSynthesizer() != null) {
            SpeechSynthesizer.getSynthesizer().destroy();
        }
    }
}
