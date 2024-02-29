package process;

import javax.crypto.SecretKey;

import security.CryptoProcessor;

public abstract class AppProcess {
  private static SecretKey key;

  public AppProcess(String keyBase64) {
    AppProcess.key = CryptoProcessor.getKeyFromBase64String(keyBase64);
  }

  public abstract void run();

  public static SecretKey getKey() {
    return key;
  }
}
