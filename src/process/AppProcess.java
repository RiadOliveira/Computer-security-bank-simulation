package process;

import javax.crypto.SecretKey;

import security.crypto.CryptoProcessor;

public abstract class AppProcess {
  protected static SecretKey key;

  protected static void initKey(String keyBase64) {
    AppProcess.key = CryptoProcessor.getKeyFromBase64String(keyBase64);
  }

  public static SecretKey getKey() {
    return key;
  }
}
