package com.test;

import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.cipher.CryptoCipherFactory;
import org.apache.commons.crypto.utils.Utils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author 王澄
 * @create 2020-05-22 19:45
 **/
public class AesEncryptApache {
    private static final CryptoCipher ENCRYPT = getCipher("AES/CBC/PKCS5Padding");
    private static final CryptoCipher DECRYPT = getCipher("AES/CBC/PKCS5Padding");
    private static final MessageDigest DIGEST = getSha256();
    private static final SecureRandom RANDOM = new SecureRandom();

    private static CryptoCipher getCipher(String transformation) {
        final Properties properties = new Properties();
        properties.setProperty(CryptoCipherFactory.CLASSES_KEY, CryptoCipherFactory.CipherProvider.OPENSSL.getClassName());
        try {
            return Utils.getCipherInstance(transformation, properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static MessageDigest getSha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String text, String key) {
        if (text == null) {
            return text;
        }
        return encrypt(text, digestBytes(key.getBytes(UTF_8)));
    }

    public static String decrypt(String text, String key) {
        if (text == null) {
            return text;
        }
        return decrypt(text, digestBytes(key.getBytes(UTF_8)));
    }

    private static synchronized byte[] digestBytes(byte[] key) {
        return DIGEST.digest(key);
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
        final int outBytesLength;
        try {
            byte[] arr = text.getBytes(UTF_8);
            final ByteBuffer inBuffer = ByteBuffer.allocateDirect(arr.length);
            inBuffer.put(arr);
            inBuffer.flip();
            ByteBuffer outBuffer = ByteBuffer.allocateDirect(arr.length * 2 + 16);
            synchronized (ENCRYPT) {
                ENCRYPT.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
                outBytesLength = ENCRYPT.doFinal(inBuffer, outBuffer);
            }
            outBuffer.flip(); // ready for use as decrypt
            arr = new byte[outBytesLength];
            outBuffer.duplicate().get(arr);
            result = new byte[16 + arr.length];
            System.arraycopy(iv, 0, result, 0, 16);
            System.arraycopy(arr, 0, result, 16, arr.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodeBase64(result).replace("\r\n", "");
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
        final int outBytesLength;
        try {
            final ByteBuffer inBuffer = ByteBuffer.allocateDirect(ciphertext.length);
            final ByteBuffer outBuffer = ByteBuffer.allocateDirect(ciphertext.length * 2 + 16);
            inBuffer.put(ciphertext);
            inBuffer.flip();
            synchronized (DECRYPT) {
                DECRYPT.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
                outBytesLength = DECRYPT.doFinal(inBuffer, outBuffer);
            }
            outBuffer.flip();
            result = new byte[outBytesLength];
            outBuffer.duplicate().get(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(result, UTF_8);
    }

    private static String asString(final ByteBuffer buffer) {
        final ByteBuffer copy = buffer.duplicate();
        final byte[] bytes = new byte[copy.remaining()];
        copy.get(bytes);
        return new String(bytes, UTF_8);
    }

    private static String encodeBase64(byte[] bytes) {
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(bytes);
    }

    private static byte[] decodeBase64(String key) {
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            return decoder.decodeBuffer(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String text = "这是一串明文";
        final String secret = encrypt(text, "这是密钥");
        text = decrypt(secret, "这是密钥");
        System.out.println(text);
    }
}
