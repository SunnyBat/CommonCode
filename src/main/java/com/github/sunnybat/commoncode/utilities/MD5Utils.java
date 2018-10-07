package com.github.sunnybat.commoncode.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A utility class for doing operations with MD5 hashes.
 *
 * @author SunnyBat
 */
public class MD5Utils {

  private static final int DEFAULT_BUFFER_SIZE = 1024;

  /**
   * Converts a given byte array to a readable hex String. Note that this is generally
   *
   * @param bytes The bytes to convert into a hex String
   * @return The hex String representation
   * @throws IllegalArgumentException if bytes is null
   */
  public static final String toHexString(byte[] bytes) { // CHECK: Move this to a separate class, since it's applicable to more than just MD5?
    if (bytes == null) {
      throw new IllegalArgumentException("byte array must not be null");
    }
    StringBuilder hex = new StringBuilder(bytes.length * 2);
    for (int i = 0; i < bytes.length; i++) {
      hex.append(Character.toUpperCase(Character.forDigit((bytes[i] & 0XF0) >> 4, 16)));
      hex.append(Character.toUpperCase(Character.forDigit((bytes[i] & 0X0F), 16)));
    }
    return hex.toString();
  }

  /**
   * Converts the given hex String to a byte array. Note that this does not check character values, and will basically return garbage if anything
   * outside of base16 character representations (0-9 and a-f) are used.
   *
   * @param hexString The hex String to convert
   * @return A byte array representing the given hex String
   * @throws IllegalArgumentException if hexString is null or has an odd amount of characters in it
   */
  public static final byte[] toByteArray(String hexString) { // CHECK: Move this to a separate class, since it's applicable to more than just MD5?
    if (hexString == null) {
      throw new IllegalArgumentException("hexString must not be null");
    } else if (hexString.length() % 2 == 1) {
      throw new IllegalArgumentException("hexString must contain an even amount of characters");
    }
    byte[] array = new byte[hexString.length() / 2];
    for (int i = 0; i < hexString.length(); i += 2) {
      array[i / 2] += convert(hexString.charAt(i)) << 4;
      array[i / 2] += convert(hexString.charAt(i + 1));
    }
    return array;
  }

  private static byte convert(char one) {
    if (one >= 'A' && one <= 'F') {
      return (byte) (one - 'A' + 10);
    } else {
      return (byte) (one - '0');
    }
  }

  /**
   * Gets the MD5 of the given InputStream. Each byte is a representation of two hex values, with the first four bits of each byte being earlier in
   * the MD5 result than the last four bits.
   *
   * @param readFrom The InputStream to run an MD5 calculation on
   * @return The MD5 digest in a byte array
   * @throws NoSuchAlgorithmException If the MD5 algorithm is not found
   * @throws IOException If an IOException occurs while reading from the given InputStream
   * @throws IllegalArgumentException if readFrom is null or inputBufferSize is less than 1
   */
  public static byte[] getByteMD5(InputStream readFrom)
      throws NoSuchAlgorithmException, IOException {
    return MD5Utils.getByteMD5(readFrom, DEFAULT_BUFFER_SIZE);
  }

  /**
   * Gets the MD5 of the given InputStream. Each byte is a representation of two hex values, with the first four bits of each byte being earlier in
   * the MD5 result than the last four bits.
   *
   * @param readFrom The InputStream to run an MD5 calculation on
   * @param inputBufferSize The size of the buffer to use when reading the given InputStream
   * @return The MD5 digest in a byte array
   * @throws NoSuchAlgorithmException If the MD5 algorithm is not found
   * @throws IOException If an IOException occurs while reading from the given InputStream
   * @throws IllegalArgumentException if readFrom is null or inputBufferSize is less than 1
   */
  public static byte[] getByteMD5(InputStream readFrom, int inputBufferSize)
      throws NoSuchAlgorithmException, IOException {
    if (readFrom == null) {
      throw new IllegalArgumentException("readFrom cannot be null");
    } else if (inputBufferSize < 1) {
      throw new IllegalArgumentException("inputBufferSize cannot be less than 1");
    }
    MessageDigest mDigest = MessageDigest.getInstance("MD5");
    mDigest.reset();
    byte[] bytes = new byte[inputBufferSize];
    int numBytes;
    while ((numBytes = readFrom.read(bytes)) != -1) {
      mDigest.update(bytes, 0, numBytes);
    }
    byte[] digest = mDigest.digest();
    return digest;
  }

  /**
   * Gets the MD5 of the given InputStream. Each byte is a value from 0-15 representing one character in the MD5 sequence. Note that this cannot be
   * properly used with toHexString().
   *
   * @param readFrom The InputStream to run an MD5 calculation on
   * @param inputBufferSize The size of the buffer to use when reading the given InputStream
   * @return The MD5 digest in a byte array
   * @throws NoSuchAlgorithmException If the MD5 algorithm is not found
   * @throws IOException If an IOException occurs while reading from the given InputStream
   * @throws IllegalArgumentException if readFrom is null or inputBufferSize is less than 1
   * @deprecated Because it's a terrible method with no value
   */
  public static byte[] getIndividualByteMD5(InputStream readFrom, int inputBufferSize)
      throws NoSuchAlgorithmException, IOException {
    byte[] digest = MD5Utils.getByteMD5(readFrom, inputBufferSize);
    byte[] individual = new byte[digest.length * 2];
    for (int i = 0; i < individual.length; i++) {
      individual[i] = (byte) (digest[i / 2] & 240 >> 4); // Get first 4 bits (11110000 mask)
      individual[i] = (byte) (digest[i / 2] & 15); // Get last 4 bits (00001111 mask)
    }
    return individual;
  }

  /**
   * Gets the MD5 of the given InputStream. The result is a String representation of each hex value of the MD5 result.
   *
   * @param is The InputStream to run an MD5 calculation on
   * @return A String representation of the MD5 calculation
   * @throws NoSuchAlgorithmException If the MD5 algorithm is not found
   * @throws IOException If an IOException occurs while reading from the given InputStream
   * @throws IllegalArgumentException if readFrom is null or inputBufferSize is less than 1
   */
  public static String getStringMD5(InputStream is)
      throws NoSuchAlgorithmException, IOException {
    String result = getStringMD5(is, DEFAULT_BUFFER_SIZE);
    return result;
  }

  /**
   * Gets the MD5 of the given InputStream. The result is a String representation of each hex value of the MD5 result.
   *
   * @param is The InputStream to run an MD5 calculation on
   * @param byteArraySize The size of the buffer to use when reading the given InputStream
   * @return A String representation of the MD5 calculation
   * @throws NoSuchAlgorithmException If the MD5 algorithm is not found
   * @throws IOException If an IOException occurs while reading from the given InputStream
   * @throws IllegalArgumentException if readFrom is null or inputBufferSize is less than 1
   */
  public static String getStringMD5(InputStream is, int byteArraySize)
      throws NoSuchAlgorithmException, IOException {
    String result = toHexString(MD5Utils.getByteMD5(is, byteArraySize));
    return result;
  }

}
