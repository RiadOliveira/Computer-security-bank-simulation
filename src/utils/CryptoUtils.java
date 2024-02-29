package utils;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {
  public enum EncryptionAlgorithm {AES, VERNAM_MAUBORGNE};

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

  public static SecretKey generateKey() {
    return aesKeyGenerator.generateKey();
  }

  public static SecretKey getKeyFromBase64String(String keyBase64) {
    byte[] decodedBytes = decodeBase64(keyBase64);
    return new SecretKeySpec(decodedBytes, "AES");
  }

  public static byte[] encryptData(
    EncryptionAlgorithm encryptionAlgorithm,
    byte[] data, SecretKey key
  ) {
    switch(encryptionAlgorithm) {
      case AES: return encryptAES(data, key);
      case VERNAM_MAUBORGNE: return encryptVernamMauborgne(data, key);
      default: return encryptAES(data, key);
    }
  }

  public static byte[] decryptData(
    EncryptionAlgorithm encryptionAlgorithm,
    byte[] data, SecretKey key
  ) {
    switch(encryptionAlgorithm) {
      case AES: return decryptAES(data, key);
      case VERNAM_MAUBORGNE: return decryptVernamMauborgne(data, key);
      default: return decryptAES(data, key);
    }
  }

  private static byte[] encryptAES(byte[] data, SecretKey key) {
    try {
      aesCipher.init(Cipher.ENCRYPT_MODE, key);
      return aesCipher.doFinal(data);
    } catch (Exception exception) {
      ConsolePrinter.println("Falha ao cifrar com AES!");
      return null;
    }
  }

  private static byte[] decryptAES(byte[] data, SecretKey key) {
    try {
      aesCipher.init(Cipher.DECRYPT_MODE, key);
      return aesCipher.doFinal(data);
    } catch (Exception exception) {
      ConsolePrinter.println("Falha ao decifrar com AES!");
      return null;
    }
  }

  private static byte[] encryptVernamMauborgne(
    byte[] data, SecretKey key
  ) {
    return getVernamMauborgneUpdatedBytes(
      data, key.getEncoded()
    );
  }

  private static byte[] decryptVernamMauborgne(
    byte[] data, SecretKey key
  ) {
    return getVernamMauborgneUpdatedBytes(
      data, key.getEncoded()
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
