package org.techtown.biopass;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import net.sf.ntru.encrypt.EncryptionKeyPair;
import net.sf.ntru.encrypt.EncryptionParameters;
import net.sf.ntru.encrypt.NtruEncrypt;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

public class QrCreateActivity extends AppCompatActivity {


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_create);
        ImageView qr = findViewById(R.id.QRview);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String ID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);


            Date currentTime = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
            String getTime = simpleDateFormat.format(currentTime);

            NtruEncrypt ntru = new NtruEncrypt(EncryptionParameters.APR2011_439_FAST);
            EncryptionKeyPair kp = ntru.generateKeyPair();

            byte[] enc = ntru.encrypt((getTime+ID).getBytes(), kp.getPublic());
            Log.e("enc", new String(enc) );
            String str1 = android.util.Base64.encodeToString(enc, android.util.Base64.NO_WRAP); // 일단 QR코드의 재료가 될 문자열
            Log.e("str1", str1 );
            byte[] decode = android.util.Base64.decode(str1, android.util.Base64.NO_WRAP); // base64디코딩 실험
            byte[] dec = ntru.decrypt(decode, kp); // ntru 복호화 실험
            Toast.makeText(this, new String(enc), Toast.LENGTH_SHORT).show(); // 복호화 성공을 알 수 있음


            BitMatrix bitMatrix = multiFormatWriter.encode(str1, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qr.setImageBitmap(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                finish();
            }
        }, 5000);
         Toast.makeText(this, "5초 뒤 QR코드가 종료됩니다.", Toast.LENGTH_LONG).show();
    }
}
