package security.crypto;

import java.math.BigInteger;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import utils.BytesUtils;
import utils.ConsolePrinter;

public class CryptoProcessor {
  public static final int AES_KEY_BYTE_SIZE = 16;
  public static final int ENCRYPTED_HMAC_BYTE_SIZE = 256;

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

  public static AsymmetricKeyPair generateAsymmetricKeyPair() {
    return AsymmetricKeyPairGenerator.generate();
  }

  public static SecretKey getKeyFromBytes(byte[] keyBytes) {
    return new SecretKeySpec(keyBytes, "AES");
  }

  public static byte[] handleAsymmetricEncryption(byte[] data, AsymmetricKey key) {
    byte[] parsedData = getParsedDataForAsymmetricEncryption(data);
    
    BigInteger resultValue = new BigInteger(parsedData).
      modPow(key.getExponent(), key.getModulus());
    byte[] resultBytes = resultValue.toByteArray();

    if(resultBytes[0] != 0) return resultBytes;
    return BytesUtils.getByteSubArray(resultBytes, 1, resultBytes.length);
  }

  private static byte[] getParsedDataForAsymmetricEncryption(byte[] data) {
    boolean needsNegativeHandling = data[0] < 0;
    if(!needsNegativeHandling) return data;

    byte[] parsedData = new byte[data.length + 1];
    System.arraycopy(data, 0, parsedData, 1, data.length);
    return parsedData;
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
