package me.dodeedoo.CoolermanRat;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class AES {

    private static SecretKeySpec secretKey;
    private static byte[] key;

    /***
     * Set the secret encryption key that will be used to encrypt the given string.
     *
     * @param myKey The key you wish to set as the secret key
     */
    private static void setKey(String myKey) {
        try {
            key = myKey.getBytes("UTF-8");
            key = MessageDigest.getInstance("SHA-1").digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * Encrypt a String using AES.
     *
     * @param strToEncrypt The target String to encrypt
     * @param secret The secret key you wish to encrypt
     *               the String with, this secret key
     *               will be used to decrypt the String
     * @return The encrypted String
     */
    public static String encrypt(String strToEncrypt, String secret) throws Exception {
        setKey(secret);
        final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
    }

    /***
     * Decrypt the AES Encrypted String.
     *
     * @param strToDecrypt The target String to decrypt
     * @param secret The secret key that was used to encrypt this String
     * @return The decrypted String
     */
    public static String decrypt(String strToDecrypt, String secret) throws Exception {
        setKey(secret);
        final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
    }

}