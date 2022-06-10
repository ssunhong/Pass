package org.techtown.biopass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCipher;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceKeysToParams;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.ExecutionException;

public class StartActivity extends AppCompatActivity {

    File pemFile;
    EditText inputName;
    EditText inputSerial;
    Button submitButton;
    client c;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        pemFile = new File(getApplicationContext().getFilesDir().getPath()+"public.pem");
        inputName = findViewById(R.id.name);
        inputSerial = findViewById(R.id.serial);
        submitButton = findViewById(R.id.submitButton);
        preferences = getSharedPreferences("name", MODE_PRIVATE);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = new client();
                String name = inputName.getText().toString();
                String serial = inputSerial.getText().toString();
                String response = null;
                try {
                    response = c.execute("newbi", serial, name).get();
                    String[]buffer = response.split(",");
                    if(response.equals("NotEnroll")){
                        Toast.makeText(StartActivity.this, "등록되지 않았습니다.", Toast.LENGTH_LONG).show();
                    }else if(buffer[0].equals("requestDeviceID")){
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("name", name);
                        editor.apply();

                        PublicKey publicKey = getPublicKey(buffer[1]);
                        writePemFile(publicKey);
                        Log.e("writeFile", "success" );
                        c = new client();
                        String enc = Enc(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID),publicKey);
                        response = c.execute("responseDeviceID", enc, name).get();
                        if(response.equals("Enroll"))
                            startActivity(new Intent(StartActivity.this, MainActivity.class));
                    }
                } catch (ExecutionException | InterruptedException | NoSuchAlgorithmException | InvalidKeySpecException | IOException | InvalidKeyException | InvalidCipherTextException e) {
                    e.printStackTrace();
                }
            }
        });

        if(checkPemFile(pemFile)) {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
        }
    }

    boolean checkPemFile(File pemFile){
        return pemFile.exists();
    }

    private void writePemFile(Key key) throws IOException {
        Pem pemFile = new Pem(key, "publicKey");
        pemFile.write(getApplicationContext().getFilesDir().getPath()+"public.pem");
    }
    private PublicKey getPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Security.addProvider(new BouncyCastlePQCProvider());
        byte[] b = Base64.decode(key, Base64.DEFAULT);
        KeyFactory keyFactory = KeyFactory.getInstance("McEliece");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(b);
        return keyFactory.generatePublic(keySpec);
    }
    private String Enc(String msg, PublicKey key) throws InvalidKeyException, UnsupportedEncodingException, InvalidCipherTextException {
        McElieceCipher EnCipheredText = new McElieceCipher();
        McEliecePublicKeyParameters GPKP = (McEliecePublicKeyParameters) McElieceKeysToParams.generatePublicKeyParameter(key);
        EnCipheredText.init(true, GPKP);
        byte[] ciphertextBytes = EnCipheredText.messageEncrypt(msg.getBytes());
        String enc = Base64.encodeToString(ciphertextBytes, Base64.DEFAULT);

        Log.e("URL encoding before", enc );
        enc = URLEncoder.encode(enc, "EUC-KR");
        Log.e("URL encoding after", enc );
        return enc;
    }
}