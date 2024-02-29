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

  public static byte[] encryptAES(byte[] data, SecretKey key) {
    try {
      aesCipher.init(Cipher.ENCRYPT_MODE, key);
      return aesCipher.doFinal(data);
    } catch (Exception exception) {
      ConsolePrinter.println("Falha ao cifrar com AES!");
      return null;
    }
  }

  public static byte[] decryptAES(byte[] data, SecretKey key) {
    try {
      aesCipher.init(Cipher.DECRYPT_MODE, key);
      return aesCipher.doFinal(data);
    } catch (Exception exception) {
      ConsolePrinter.println("Falha ao decifrar com AES!");
      return null;
    }
  }

  public static byte[] encryptVernamMauborgne(
    byte[] data, SecretKey key
  ) {
    return getVernamMauborgneUpdatedBytes(
      data, key.toString().getBytes()
    );
  }

  public static byte[] decryptVernamMauborgne(
    byte[] data, SecretKey key
  ) {
    return getVernamMauborgneUpdatedBytes(
      data, key.toString().getBytes()
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

  public static String encodeBase64(byte[] contentBytes) {
    return Base64.getEncoder().encodeToString(
      contentBytes
    );
  }

  public static byte[] decodeBase64(String content) {
    return Base64.getDecoder().decode(content);
  }
}
