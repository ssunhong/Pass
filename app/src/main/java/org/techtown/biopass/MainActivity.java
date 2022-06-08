package org.techtown.biopass;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_WEAK;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCipher;
import org.bouncycastle.pqc.crypto.mceliece.McElieceKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mceliece.McElieceParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private File pemFile;
    private client c = new client();

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("path", getApplicationContext().getFilesDir().getPath() );
        pemFile = new File(getApplicationContext().getFilesDir().getPath()+"public.pem");
        Security.addProvider(new BouncyCastlePQCProvider());
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        String key = null;
        if (checkPemFile()) {
            try {
                key = c.execute("getkey", ID).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            try {
                writePemFile(getPublicKey(key));
            } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("지문 인증")
                    .setSubtitle("기기에 등록된 지문을 이용하여 지문을 인증해주세요.")
                    .setNegativeButtonText("취소")
                    .setDeviceCredentialAllowed(false)
                    .build();

            Button biometricLoginButton = findViewById(R.id.buttonAuthWithFingerprint);
            biometricLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    biometricPrompt.authenticate(promptInfo);
                }
            });

            executor = ContextCompat.getMainExecutor(this);
            biometricPrompt = new BiometricPrompt(this,
                    executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode,
                                                  @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(getApplicationContext(),
                                    "ERROR", Toast.LENGTH_SHORT)
                            .show();
                }

                @Override
                public void onAuthenticationSucceeded(
                        @NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);

                    Date currentTime = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
                    String getTime = simpleDateFormat.format(currentTime);

                    Intent intent = new Intent(MainActivity.this, QrCreateActivity.class);
                    intent.putExtra("Enc", ID );
                    startActivity(new Intent(intent));
                }


                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            });

            Button scanButton = findViewById(R.id.scanButton);
            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new IntentIntegrator(MainActivity.this).initiateScan();
                }
            });
        }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public boolean checkPemFile(){
        if(pemFile.isFile())
            return false;
        else
            return true;
    }
    private void writePemFile(Key key) throws IOException {
        Pem pemFile = new Pem(key, "publicKey");
        pemFile.write(getApplicationContext().getFilesDir().getPath()+"public.pem");
    }
    private Key getPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] b = Base64.decode(key, Base64.DEFAULT);
        KeyFactory keyFactory = KeyFactory.getInstance("McEliece");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(b);
        return keyFactory.generatePublic(keySpec);
    }
}