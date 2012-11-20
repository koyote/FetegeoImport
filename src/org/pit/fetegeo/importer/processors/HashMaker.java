package org.pit.fetegeo.importer.processors;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Author: Pit Apps
 * Date: 19/11/12
 * Time: 23:52
 */
public class HashMaker {
  private static MessageDigest md;
  private static StringBuilder sb;

  static {
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
    }
  }


  public static String getMD5Hash(String input) {
    sb = new StringBuilder();

    for (byte b : md.digest(input.getBytes())) {
      sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
    }

    return sb.toString();
  }
}
