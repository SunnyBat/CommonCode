package com.github.sunnybat.commoncode.encryption;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import sun.misc.BASE64Decoder; // Java 8 now has a Base64 class! But we're still using Java 7...
import sun.misc.BASE64Encoder;

/**
 * Credit goes to Johannes Brodwall on StackOverflow for providing this class.
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
   * @return The encrypted String
   * @throws GeneralSecurityException
   * @throws UnsupportedEncodingException
   */
  public static String encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
    SecretKey key = keyFactory.generateSecret(new PBEKeySpec(System.getProperty("java.version").toCharArray()));
    Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
    pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
    return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
  }

  private static String base64Encode(byte[] bytes) {
    // NB: This class is internal, and you probably should use another impl
    return encoder.encode(bytes); // Need to replace with a different encoder
  }

  /**
   * Decrypts the given String. Should be used in conjunction with {@link #encrypt(java.lang.String)}.
   *
   * @param property The encrypted String to decrypt
   * @return The decrypted String
   * @throws GeneralSecurityException
   * @throws IOException
   */
  public static String decrypt(String property) throws GeneralSecurityException, IOException {
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
    SecretKey key = keyFactory.generateSecret(new PBEKeySpec(System.getProperty("java.version").toCharArray()));
    Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
    pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
    return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
  }

  private static byte[] base64Decode(String property) throws IOException {
    // NB: This class is internal, and you probably should use another impl
    return decoder.decodeBuffer(property); // Need to replace with a different encoder
  }

}
