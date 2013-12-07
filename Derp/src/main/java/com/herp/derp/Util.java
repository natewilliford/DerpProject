package com.herp.derp;

import android.util.Log;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Util {

    public static String encodeSignature(String key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        Log.v("derp", "data: " + data);
        return new String(Hex.encodeHex(sha256_HMAC.doFinal(data.getBytes()))).toUpperCase();
    }
}
