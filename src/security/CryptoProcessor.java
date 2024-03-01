package security;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import utils.ConsolePrinter;

public class CryptoProcessor {
  public enum EncryptionAlgorithm {AES, VERNAM_MAUBORGNE};

  public static final EncryptionAlgorithm SELECTED_ENCRYPTION_ALGORITHM = 
    EncryptionAlgorithm.AES;

  public static final int HMAC_BYTE_SIZE = 32;
  private static final String AES_INSTANCE_NAME = "AES/ECB/PKCS5Padding";
  private static final String HMAC_INSTANCE_NAME = "HmacSHA256";

  private static Cipher aesCipher;
  private static KeyGenerator aesKeyGenerator;
  private static Mac shaHMAC;

  static {
    try {
      aesCipher = Cipher.getInstance(AES_INSTANCE_NAME);
      aesKeyGenerator = KeyGenerator.getInstance("AES");

      shaHMAC = Mac.getInstance(HMAC_INSTANCE_NAME);
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
    byte[] data, SecretKey key
  ) throws Exception {
    switch(SELECTED_ENCRYPTION_ALGORITHM) {
      case AES: return encryptAES(data, key);
      case VERNAM_MAUBORGNE: return encryptVernamMauborgne(data, key);
      default: return encryptAES(data, key);
    }
  }

  public static byte[] decryptData(
    byte[] data, SecretKey key
  ) throws Exception {
    switch(SELECTED_ENCRYPTION_ALGORITHM) {
      case AES: return decryptAES(data, key);
      case VERNAM_MAUBORGNE: return decryptVernamMauborgne(data, key);
      default: return decryptAES(data, key);
    }
  }

  private static byte[] encryptAES(
    byte[] data, SecretKey key
  ) throws Exception {
    aesCipher.init(Cipher.ENCRYPT_MODE, key);
    return aesCipher.doFinal(data);
  }

  private static byte[] decryptAES(
    byte[] data, SecretKey key
  ) throws Exception {
    aesCipher.init(Cipher.DECRYPT_MODE, key);
    return aesCipher.doFinal(data);
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

  public static byte[] generateHMAC(
    byte[] data, SecretKey key
  ) throws Exception {
    shaHMAC.init(key);
    return shaHMAC.doFinal(data);
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
