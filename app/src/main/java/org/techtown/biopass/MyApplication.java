package org.techtown.biopass;

import android.app.Application;

import net.sf.ntru.encrypt.EncryptionKeyPair;
import net.sf.ntru.encrypt.EncryptionParameters;
import net.sf.ntru.encrypt.NtruEncrypt;

public class MyApplication extends Application {
    NtruEncrypt ntru = new NtruEncrypt(EncryptionParameters.APR2011_439_FAST);
    EncryptionKeyPair kp = ntru.generateKeyPair();

    public void init(){

    }
}
