package org.techtown.biopass;

import android.provider.Settings;

import net.sf.ntru.encrypt.EncryptionKeyPair;
import net.sf.ntru.encrypt.EncryptionParameters;
import net.sf.ntru.encrypt.NtruEncrypt;

import java.nio.charset.StandardCharsets;

public class NTRU {
    private String Android_ID;
    private EncryptionParameters encryptionParameters;
    private NtruEncrypt ntru;
    private EncryptionKeyPair kp;

    public NTRU(String Android_ID, EncryptionParameters encryptionParameters){
        this.Android_ID =  Android_ID;
        this.encryptionParameters = encryptionParameters;
        this.ntru = new NtruEncrypt(this.encryptionParameters);
        this.kp = this.ntru.generateKeyPair();
    }

    byte[] Enc(){
        return this.ntru.encrypt(Android_ID.getBytes(StandardCharsets.UTF_8), kp.getPublic());
    }

    byte[] Dec(byte[] Enc){
        return this.ntru.decrypt(Enc, kp);
    }

}