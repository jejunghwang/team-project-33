package com.photoeditor.core;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.Key;

public class FileEncryptor {
    public static void encryptFile(File inputFile, File outputFile, String keyString) {
        doCrypto(Cipher.ENCRYPT_MODE, keyString, inputFile, outputFile);
    }

    public static void decryptFile(File inputFile, File outputFile, String keyString) {
        doCrypto(Cipher.DECRYPT_MODE, keyString, inputFile, outputFile);
    }

    private static void doCrypto(int cipherMode, String keyString, File inputFile, File outputFile) {
        try {
            byte[] keyBytes = new byte[16];
            byte[] givenKey = keyString.getBytes();
            System.arraycopy(givenKey, 0, keyBytes, 0, Math.min(givenKey.length, 16));

            Key secretKey = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(cipherMode, secretKey);

            FileInputStream fis = new FileInputStream(inputFile);
            byte[] inputBytes = fis.readAllBytes();
            fis.close();

            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(outputBytes);
            fos.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
