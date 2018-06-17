package com.github.sunnybat.commoncode.utilities;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

/**
 * Credit goes to Johannes Brodwall on StackOverflow for providing this class. It has been modified quite a bit, but the basic encryption/decryption
 * code remains the same.
 *
 * @author Johannes Brodwall
 */
public class Encryption {

    private static class Base64Util {
        private Base64.Encoder javaEncoder;
        private Base64.Decoder javaDecoder;
        private BASE64Encoder sunEncoder;
        private BASE64Decoder sunDecoder;

        public Base64Util() {
            try {
                javaEncoder = Base64.getEncoder();
                javaDecoder = Base64.getDecoder();
            } catch (NoClassDefFoundError cnfe) {
            }
            try {
                sunEncoder = new BASE64Encoder();
                sunDecoder = new BASE64Decoder();
            } catch (NoClassDefFoundError cnfe) {
            }
        }

        public String base64Encode(byte[] bytes) {
            if (javaEncoder != null) {
                return javaEncoder.encodeToString(bytes);
            }
            else if (sunEncoder != null) {
                return sunEncoder.encode(bytes);
            }
            else {
                return null;
            }
        }
        
        public byte[] base64Decode(String str) {
            if (javaDecoder != null) {
                return javaDecoder.decode(str);
            } else if (sunDecoder != null) {
                try {
                    return sunDecoder.decodeBuffer(str);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        }
    }

  private static Encryption defaultEncryption;
  private static final byte[] DEFAULT_SALT = {
    (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,};

  public static Encryption getDefaultEncryption() {
    if (defaultEncryption == null) {
      defaultEncryption = new Encryption(DEFAULT_SALT);
    }
    return defaultEncryption;
  }

  private static final byte[] SALT = {
    (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,};
  private static final Base64Util utils = new Base64Util();

  /**
   * Encrypts the given String. Should be used in conjunction with {@link #decrypt(java.lang.String)}.
   *
   * @param property The String to encrypt
   * @return The encrypted String, or null if unable to encrypt it
   */
  public static String encrypt(String property) {
    if (property == null) {
      return null;
    }
    try {
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
      SecretKey key = keyFactory.generateSecret(new PBEKeySpec(System.getProperty("os.name").toCharArray()));
      Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
      pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
      return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
    } catch (IOException | GeneralSecurityException badPaddingException) {
      return null;
    }
  }

  private static String base64Encode(byte[] bytes) {
    return utils.base64Encode(bytes);
  }

  /**
   * Decrypts the given String. Should be used in conjunction with {@link #encrypt(java.lang.String)}.
   *
   * @param property The encrypted String to decrypt
   * @return The decrypted String, or null if unable to decrypt it
   */
  public static String decrypt(String property) {
    if (property == null) {
      return null;
    }
    try {
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
      SecretKey key = keyFactory.generateSecret(new PBEKeySpec(System.getProperty("os.name").toCharArray()));
      Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
      pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
      return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    } catch (IOException | GeneralSecurityException badPaddingException) {
      return null;
    }
  }

  private static byte[] base64Decode(String property) throws IOException {
    return utils.base64Decode(property);
  }

  public Encryption(byte[] salt) {

  }

}
