package org.techtown.biopass;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.sf.ntru.encrypt.EncryptionKeyPair;
import net.sf.ntru.encrypt.EncryptionParameters;
import net.sf.ntru.encrypt.NtruEncrypt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        new IntentIntegrator(this).initiateScan();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                byte[] msg = result.getContents().getBytes(); // 스캔의 결과값을 받음
                NtruEncrypt ntru = new NtruEncrypt(EncryptionParameters.APR2011_439_FAST);
                EncryptionKeyPair kp = ntru.generateKeyPair();
                Log.e("result", result.getContents());
                byte[] decode = android.util.Base64.decode(msg, Base64.NO_WRAP); // base64 디코딩
                //Log.e("decode", new String(decode));
                //byte[] dec = ntru.decrypt(decode, kp); // 복호화 부분 오류발생
                Toast.makeText(this, new String(decode), Toast.LENGTH_LONG).show();

            }
            else{
                Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show();
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}