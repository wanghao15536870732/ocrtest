package com.example.lab.android.nuc.ocrtest.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.example.lab.android.nuc.ocrtest.util.FileUtil;
import com.example.lab.android.nuc.ocrtest.R;
import com.example.lab.android.nuc.ocrtest.service.RecognizeService;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_GENERAL_WEBIMAGE = 110;
    private static final int REQUEST_CODE_BANKCARD = 111;
    private static final int REQUEST_CODE_VEHICLE_LICENSE = 120;
    private static final int REQUEST_CODE_DRIVING_LICENSE = 121;
    private static final int REQUEST_CODE_LICENSE_PLATE = 122;
    private static final int REQUEST_CODE_BUSINESS_LICENSE = 123;
    private static final int REQUEST_CODE_RECEIPT = 124;

    private boolean hasGotToken = false;

    private AlertDialog.Builder alertDialog;

    private Button generallyBtn,accurate_basic_Btn,handwritting_Btn,
            general_enhance_Btn,idcard_Btn,bankcard_Btn, general_webimage_Btn,
            vehicle_license_Btn,driving_license_Btn,license_plate_Btn,
            business_license_Btn,receipt_Btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        /**
         * 请选择您的初始化方式
         */

        //以license文件方式初始化
        initAccessToken();
        //用明文ak，sk初始化
//        initAccessTokenWithAkSk();

        alertDialog = new AlertDialog.Builder(this);
        initBtn();
        initListener();
    }

    private void initBtn(){
        generallyBtn = (Button)findViewById( R.id.generally_button );
        accurate_basic_Btn = (Button) findViewById( R.id.accurate_basic_button );
        handwritting_Btn = (Button) findViewById( R.id.handwritting_button );
        general_enhance_Btn = (Button) findViewById( R.id.general_enhance_button );
        idcard_Btn = (Button) findViewById( R.id.idcard_button );
        bankcard_Btn = (Button) findViewById( R.id.bankcard_button );
        general_webimage_Btn = (Button) findViewById( R.id.general_webimage_button );
        vehicle_license_Btn = (Button) findViewById( R.id.vehicle_license_button );
        driving_license_Btn = (Button) findViewById( R.id.driving_license_button );
        license_plate_Btn = (Button) findViewById( R.id.license_plate_button );
        business_license_Btn = (Button) findViewById( R.id.business_license_button );
        receipt_Btn = (Button) findViewById( R.id.receipt_button );
    }

    private void initListener(){
        // 通用文字识别
        generallyBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this,GeneralActivity.class );
                startActivity( intent );
            }
        } );

        // 通用文字识别(高精度版)
        accurate_basic_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, AccurateActivity.class);
                startActivity( intent );

            }
        });

        // 手写识别
        handwritting_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, HandWritingActivity.class);
                startActivity(intent);
            }
        });


        // 通用文字识别（含生僻字版）//展示因为没有购买ops，暂时不能使用
        general_enhance_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, EnhanceActivity.class);
                startActivity( intent );
            }
        });

        // 身份证识别
        idcard_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, IDCardActivity.class);
                startActivity(intent);
            }
        });

        // 银行卡识别
        bankcard_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, BankCardActivity.class);
                startActivity(intent);
            }
        });

        // 网络图片识别
        general_webimage_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_GENERAL_WEBIMAGE);
            }
        });



        // 行驶证识别
        vehicle_license_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_VEHICLE_LICENSE);
            }
        });

        // 驾驶证识别
        driving_license_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_DRIVING_LICENSE);
            }
        });

        // 车牌识别
        license_plate_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_LICENSE_PLATE);
            }
        });

        // 营业执照识别
        business_license_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_BUSINESS_LICENSE);
            }
        });

        // 通用票据识别
        receipt_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_RECEIPT);
            }
        });
    }

    private boolean checkTokenStatus() {
        if (!hasGotToken) {
            Toast.makeText(getApplicationContext(), "token还未成功获取", Toast.LENGTH_LONG).show();
        }
        return hasGotToken;
    }

    /**
     * 以license文件方式初始化
     */
    private void initAccessToken() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("licence方式获取token失败", error.getMessage());
            }
        }, getApplicationContext());
    }

    /**
     * 用明文ak，sk初始化
     */
    private void initAccessTokenWithAkSk() {
        OCR.getInstance(this).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("AK，SK方式获取token失败", error.getMessage());
            }
        }, getApplicationContext(),  "vqwTKi6m7zQwGhc9I61Is0bu", "PUX9y6gW7QCHH7ARIIrbv2aG7o8XqCGG ");
    }

    /**
     * 自定义license的文件路径和文件名称，以license文件方式初始化
     */
    private void initAccessTokenLicenseFile() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("自定义文件路径licence方式获取token失败", error.getMessage());
            }
        }, "aip.license", getApplicationContext());
    }


    private void alertText(final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }

    private void infoPopText(final String result) {
        alertText("", result);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initAccessToken();
        } else {
            Toast.makeText(getApplicationContext(), "需要android.permission.READ_PHONE_STATE", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            // 识别成功回调，网络图片文字识别
            case REQUEST_CODE_GENERAL_WEBIMAGE:
                if (resultCode == Activity.RESULT_OK){
                    RecognizeService.recWebimage(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                            new RecognizeService.ServiceListener() {
                                @Override
                                public void onResult(String result) {
                                    infoPopText(result);
                                }
                            });
                }
                break;
            // 识别成功回调，银行卡识别
            case REQUEST_CODE_BANKCARD:
                if (resultCode == Activity.RESULT_OK){
                    RecognizeService.recBankCard(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                            new RecognizeService.ServiceListener() {
                                @Override
                                public void onResult(String result) {
                                    infoPopText(result);
                                }
                            });
                }
                break;
            // 识别成功回调，行驶证识别
            case REQUEST_CODE_VEHICLE_LICENSE:
                if (resultCode == Activity.RESULT_OK){
                    RecognizeService.recVehicleLicense(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                            new RecognizeService.ServiceListener() {
                                @Override
                                public void onResult(String result) {
                                    infoPopText(result);
                                }
                            });
                }
                break;
            // 识别成功回调，驾驶证识别
            case REQUEST_CODE_DRIVING_LICENSE:
                if (resultCode == Activity.RESULT_OK){
                    RecognizeService.recDrivingLicense(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                            new RecognizeService.ServiceListener() {
                                @Override
                                public void onResult(String result) {
                                    infoPopText(result);
                                }
                            });
                }
                break;
            // 识别成功回调，车牌识别
            case REQUEST_CODE_LICENSE_PLATE:
                if (resultCode == Activity.RESULT_OK){
                    RecognizeService.recLicensePlate(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                            new RecognizeService.ServiceListener() {
                                @Override
                                public void onResult(String result) {
                                    infoPopText(result);
                                }
                            });
                }
                break;
            // 识别成功回调，营业执照识别
            case REQUEST_CODE_BUSINESS_LICENSE:
                if (resultCode == Activity.RESULT_OK){
                    RecognizeService.recBusinessLicense(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                            new RecognizeService.ServiceListener() {
                                @Override
                                public void onResult(String result) {
                                    infoPopText(result);
                                }
                            });
                }
                break;
            // 识别成功回调，通用票据识别
            case REQUEST_CODE_RECEIPT:
                if (resultCode == Activity.RESULT_OK){
                    RecognizeService.recReceipt(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                            new RecognizeService.ServiceListener() {
                                @Override
                                public void onResult(String result) {
                                    infoPopText(result);
                                }
                            });
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放内存资源
        OCR.getInstance(this).release();
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
            }
            @Override
            public void onError(OCRError error) {
            }
        });
    }
}
