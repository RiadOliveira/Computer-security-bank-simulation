package security.crypto;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import security.crypto.rsa.RSAKey;
import security.crypto.rsa.RSAKeyPair;
import security.crypto.rsa.RSAProcessor;
import utils.ConsolePrinter;

public class CryptoProcessor {
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
      ConsolePrinter.println("Falha ao iniciar cifra simétrica!");
    }
  }

  public static SecretKey generateKey() {
    return aesKeyGenerator.generateKey();
  }

  public static RSAKeyPair generateAsymmetricKeys() {
    return RSAProcessor.generateKey();
  }

  public static SecretKey getKeyFromBase64String(String keyBase64) {
    byte[] decodedBytes = decodeBase64(keyBase64);
    return new SecretKeySpec(decodedBytes, "AES");
  }

  public static byte[] handleAsymmetricEncryption(
    byte[] data, RSAKey key
  ) {
    return RSAProcessor.handleEncryption(data, key);
  }

  public static byte[] encryptSymmetrically(
    byte[] data, SecretKey key
  ) throws Exception {
    aesCipher.init(Cipher.ENCRYPT_MODE, key);
    return aesCipher.doFinal(data);
  }

  public static byte[] decryptSymmetrically(
    byte[] data, SecretKey key
  ) throws Exception {
    aesCipher.init(Cipher.DECRYPT_MODE, key);
    return aesCipher.doFinal(data);
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
