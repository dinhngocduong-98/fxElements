package com.framevault.shared;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public final class Tokens {

  private static final SecureRandom RANDOM = new SecureRandom();

  public static String create() {
    byte[] b = new byte[32];
    RANDOM.nextBytes(b);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
  }

  public static String hash(String s) {
    try {
      return hex(MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  public static String hex(byte[] b) {
    StringBuilder s = new StringBuilder();
    for (byte v : b) s.append(String.format("%02x", v & 255));
    return s.toString();
  }
}
