package org.pit.fetegeo.importer.processors;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;

/**
 * Author: Pit Apps
 * Date: 19/11/12
 * Time: 23:52
 * <p/>
 * Creates an MD5 hash digest of a given String.
 */
public class HashMaker {
  private static MessageDigest md;

  static {
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      System.err.println("Could not get MD5 instance. Something must be wrong with Java!");
      System.err.println(e);
    }
  }

  /*
    Returns the MD5 hash of a String, returns null if string empty or null
   */
  public static String getMD5Hash(String input) {
    if (input == null || input.isEmpty()) {
      return null;
    }

    input = input.trim().toLowerCase(); // The input is trimmed and set to lowercase as fetegeo only handles lowercase

    // We will try and save a hash of a normalised string. I.e. without accents.
    // This enables us to search for places in the roman alphabet without caring about special accents.
    // The normaliser will not work on names not based on the roman alphabet.
    String normalised = Normalizer.normalize(input, Normalizer.Form.NFD);
    normalised = normalised.replaceAll("[^\\p{ASCII}]", "");
    if (!normalised.isEmpty()) {
      input = normalised;
    }

    StringBuilder sb = new StringBuilder();

    for (byte b : md.digest(input.getBytes())) {
      sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
    }

    return sb.toString();
  }
}
