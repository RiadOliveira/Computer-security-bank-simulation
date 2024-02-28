package utils;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class CryptoUtils {
  private static Cipher aesCipher;
  private static KeyGenerator aesKeyGenerator;

  static {
    try {
      aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      aesKeyGenerator = KeyGenerator.getInstance("AES");
    } catch (Exception exception) {
      ConsolePrinter.println("Falha ao iniciar cifra AES!");
    }
  }

  public static SecretKey generateSecretKey() {
    return aesKeyGenerator.generateKey();
  }

  public static String encryptAES(String content, SecretKey key) {
    try {
      aesCipher.init(Cipher.ENCRYPT_MODE, key);

      byte[] encryptedBytes = aesCipher.doFinal(content.getBytes());
      return encodeBase64(encryptedBytes);
    } catch (Exception exception) {
      ConsolePrinter.println("Falha ao cifrar com AES!");
      return null;
    }
  }

  public static String decryptAES(String content, SecretKey key) {
    try {
      aesCipher.init(Cipher.DECRYPT_MODE, key);

      byte[] decryptedBytes = aesCipher.doFinal(decodeBase64(content));
      return new String(decryptedBytes);
    } catch (Exception exception) {
      ConsolePrinter.println("Falha ao decifrar com AES!");
      return null;
    }
  }

  public static String encryptVernamMauborgne(
    String content, SecretKey key
  ) {
    byte[] encryptedBytes = getVernamMauborgneUpdatedBytes(
      content, key.toString()
    );
    return encodeBase64(encryptedBytes);
  }

  public static String decryptVernamMauborgne(
    String content, SecretKey key
  ) {
    byte[] decryptedBytes = getVernamMauborgneUpdatedBytes(
      decodeBase64(content), key.toString().getBytes()
    );
    return new String(decryptedBytes);
  }

  private static byte[] getVernamMauborgneUpdatedBytes(
    String content, String key
  ) {
    return getVernamMauborgneUpdatedBytes(
      content.getBytes(), key.getBytes()
    );
  }

  private static byte[] getVernamMauborgneUpdatedBytes(
    byte contentBytes[], byte keyBytes[]
  ) {
    int contentLength = contentBytes.length;
    int keyLength = keyBytes.length;

    for(int ind=0 ; ind<contentLength ; ind++) {
      contentBytes[ind] ^= keyBytes[ind % keyLength];
    }

    return contentBytes;
  }

  private static String encodeBase64(byte[] contentBytes) {
    return Base64.getEncoder().encodeToString(
      contentBytes
    );
  }

  private static byte[] decodeBase64(String content) {
    return Base64.getDecoder().decode(content);
  }
}
