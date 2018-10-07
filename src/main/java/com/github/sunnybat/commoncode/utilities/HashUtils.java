package com.github.sunnybat.commoncode.utilities;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author SunnyBat
 */
public class HashUtils {

  private byte[] salt;

  /**
   * Creates a new HashUtils object. This is used for hashing Strings.
   *
   * @param salt The salt to use when hashing
   */
  public HashUtils(byte[] salt) {
    this.salt = salt;
  }

  /**
   * Hashes the given String using PBKDF2. This uses 65536 iterations and generates a 128-bit hash.
   *
   * @param toHash The String to hash
   * @return The hashed String, or null if an error occurs
   * @throws NullPointerException if toHash is null
   */
  public String PBKDF2(String toHash) {
    return PBKDF2(toHash, 65536, 128);
  }

  /**
   * Hashes the given String using PBKDF2.
   *
   * @param toHash The String to hash
   * @param iterations The amount of iterations to use
   * @param resultLength The amount of bits for the resulting hash to have
   * @return The hashed String, or null if an error occurs
   * @throws NullPointerException if toHash is null
   */
  public String PBKDF2(String toHash, int iterations, int resultLength) {
    if (toHash == null) {
      throw new NullPointerException("toHash is null");
    }
    try {
      KeySpec spec = new PBEKeySpec(toHash.toCharArray(), salt, iterations, resultLength);
      SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      byte[] hash = f.generateSecret(spec).getEncoded();
      Base64.Encoder enc = Base64.getEncoder();
      return enc.encodeToString(hash);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      return null;
    }
  }

}
