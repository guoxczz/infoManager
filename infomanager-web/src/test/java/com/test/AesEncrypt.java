package com.test;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.NullCipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AesEncrypt {
    private static final Cipher ENCRYPT = getCipher("AES/CBC/PKCS5Padding");
    private static final Cipher DECRYPT = getCipher("AES/CBC/PKCS5Padding");
    private static final MessageDigest DIGEST = getSha256();

    private static MessageDigest getSha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String encrypt(String text, String key) {
        if (text == null) {
            return text;
        }
        // JDK6 用"UTF-8"
        return encrypt(text, digestBytes(key.getBytes(UTF_8)));
    }

    public static String decrypt(String text, String key) {
        if (text == null) {
            return text;
        }
        // JDK6 用"UTF-8"
        return decrypt(text, digestBytes(key.getBytes(UTF_8)));
    }

    private static Cipher getCipher(String transformation) {
        try {
            return Cipher.getInstance(transformation);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return new NullCipher();
    }

    /**
     * aes加密  每次生成不同的密文
     *
     * @param text
     * @param key
     * @return
     */
    public static String encrypt(String text, byte[] key) {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        byte[] iv = new byte[16];
        RANDOM.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        byte[] result = null;
        try {
            byte[] arr = null;
            synchronized (ENCRYPT) {
                ENCRYPT.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
                // JDK6 用"UTF-8"
                arr = ENCRYPT.doFinal(text.getBytes(UTF_8));
            }
            result = new byte[16 + arr.length];
            System.arraycopy(iv, 0, result, 0, 16);
            System.arraycopy(arr, 0, result, 16, arr.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodeBase64(result);
    }

    /**
     * aes解密
     *
     * @param text
     * @param key
     * @return
     */
    private static String decrypt(String text, byte[] key) {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        byte[] ivCiphertext = decodeBase64(text);
        byte[] iv = Arrays.copyOfRange(ivCiphertext, 0, 16);
        byte[] ciphertext = Arrays.copyOfRange(ivCiphertext, 16, ivCiphertext.length);
        byte[] dhSharedSecret = new byte[32];
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        byte[] result = new byte[0];
        try {
            synchronized (DECRYPT) {
                DECRYPT.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
                result = DECRYPT.doFinal(ciphertext);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // JDK6 用"UTF-8"
        return new String(result, UTF_8);
    }

    private static synchronized byte[] digestBytes(byte[] key) {
        return DIGEST.digest(key);
    }

    private static String encodeBase64(byte[] bytes) {
        return Base64.encode(bytes);
        //        return Base64.getEncoder().encodeToString(bytes);
        // 以下是JDK6 和JDK7的
        //                BASE64Encoder encoder = new BASE64Encoder();
        //                return encoder.encode(bytes);
    }

    private static byte[] decodeBase64(String key) {
        try {
            return Base64.decode(key);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
        //        return Base64.getDecoder().decode(key);
        // 以下是JDK6 和JDK7的
        //        BASE64Decoder decoder=new BASE64Decoder();
        //        try {
        //            return decoder.decodeBuffer(key);
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }
        //        return null;

    }

    public static void main(String[] args) {
        String text = "这是一串明文数据";
        final String secret = encrypt(text, "这是密钥");
        System.out.println(secret);
        text = decrypt(secret, "这是密钥");
        // IntStream.range(0,100).parallel().forEach(e->encrypt("这是一串明文数据","这是密钥"));
        // IntStream.range(0,100).parallel().forEach(e-> System.out.println(decrypt(secret,"这是密钥")));
        System.out.println(text);

    }
}