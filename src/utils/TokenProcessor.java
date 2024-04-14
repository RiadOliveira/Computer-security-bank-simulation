package utils;

import java.util.UUID;

public class TokenProcessor {
  public static String generate(
    String secretKey, UUID userId
  ) {
    return Hasher.hashAndEncode(secretKey + userId);
  }

  public static boolean isValid(
    String token, String secretKey, UUID userId
  ) {
    return Hasher.compare(token, secretKey + userId);
  }
}
