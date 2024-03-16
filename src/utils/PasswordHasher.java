package utils;

import java.security.MessageDigest;

import security.crypto.CryptoProcessor;

public class PasswordHasher {
  private static final String SHA3_INSTANCE_NAME = "SHA3-256";
  private static MessageDigest messageDigest;

  static {
    try {
      messageDigest = MessageDigest.getInstance(SHA3_INSTANCE_NAME);
    } catch (Exception exception) {
      ConsolePrinter.println(
        "Falha ao iniciar hasher SHA3-256!"
      );
    }
  }

  public static String hashAndEncode(String password) {
    byte[] hashedBytes = messageDigest.digest(password.getBytes());
    return CryptoProcessor.encodeBase64(hashedBytes);
  }

  public static boolean passwordsAreEqual(
    String hashedFirstPassword, String secondPassword
  ) {
    return hashedFirstPassword.equals(hashAndEncode(secondPassword));
  }
}
