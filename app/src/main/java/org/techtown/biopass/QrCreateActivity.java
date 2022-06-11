package org.techtown.biopass;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


import org.bouncycastle.asn1.mozilla.PublicKeyAndChallenge;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCipher;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceKeysToParams;
import org.bouncycastle.pqc.jcajce.spec.McElieceKeyGenParameterSpec;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

public class QrCreateActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_create);

        Security.addProvider(new BouncyCastlePQCProvider());

        ImageView qr = findViewById(R.id.QRview);
        Intent intent = getIntent();
        String ID = intent.getStringExtra("ID");
        String name = intent.getStringExtra("name");

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            PublicKey pk = getPublicKey(getApplicationContext().getFilesDir().getPath()+"public.pem");
            McElieceCipher EnCipheredText = new McElieceCipher();
            McEliecePublicKeyParameters GPKP = (McEliecePublicKeyParameters) McElieceKeysToParams.generatePublicKeyParameter((PublicKey) pk);
            EnCipheredText.init(true, GPKP);
            byte[] ciphertextBytes = EnCipheredText.messageEncrypt(ID.getBytes());
            String enc = Base64.encodeToString(ciphertextBytes, Base64.DEFAULT);
            enc = URLEncoder.encode(enc, "EUC-KR");
            Log.e("QR", name+","+enc );
            BitMatrix bitMatrix = multiFormatWriter.encode(name+","+enc, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qr.setImageBitmap(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
        /*
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                finish();
            }
        }, 5000);
        */
         Toast.makeText(this, "5초 뒤 QR코드가 종료됩니다.", Toast.LENGTH_LONG).show();
    }

    public PublicKey getPublicKey(String filename) throws Exception {
        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();

        String temp = new String(keyBytes);
        String publicKeyPEM = temp.replace("-----BEGIN publicKey-----", "");
        publicKeyPEM = publicKeyPEM.replace(System.lineSeparator(), "");
        publicKeyPEM = publicKeyPEM.replace("-----END publicKey-----", "");
        Log.e("key", publicKeyPEM);
        byte[] encoded = Base64.decode(publicKeyPEM, Base64.DEFAULT);
        KeyFactory keyFactory = KeyFactory.getInstance("McEliece");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return keyFactory.generatePublic(keySpec);
    }

    public String Enc(String msg, PublicKey key) throws InvalidKeyException {
        McElieceCipher EnCipheredText = new McElieceCipher();
        McEliecePublicKeyParameters GPKP = (McEliecePublicKeyParameters) McElieceKeysToParams.generatePublicKeyParameter(key);
        EnCipheredText.init(true, GPKP);
        byte[] ciphertextBytes = EnCipheredText.messageEncrypt(msg.getBytes());
        //Log.e("ciphertextBytes", new String(ciphertextBytes) );
        return new String(ciphertextBytes);
    }
}
