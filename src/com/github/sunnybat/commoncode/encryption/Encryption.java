package com.github.sunnybat.commoncode.encryption;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Credit goes to Johannes Brodwall on StackOverflow for providing this class. It has been modified quite a bit, but the basic encryption/decryption
 * code remains the same.
 *
 * @author Johannes Brodwall
 */
public class Encryption {

  private static final byte[] SALT = {
    (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,};
  private static final BASE64Encoder encoder = new BASE64Encoder();
  private static final BASE64Decoder decoder = new BASE64Decoder();

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
    // NB: This class is internal, and you probably should use another impl
    return encoder.encode(bytes); // Need to replace with a different encoder
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
    // NB: This class is internal, and you probably should use another impl
    return decoder.decodeBuffer(property); // Need to replace with a different encoder
  }

}
