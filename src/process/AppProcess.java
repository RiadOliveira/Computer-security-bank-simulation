package process;

import security.crypto.AsymmetricKey;
import security.crypto.AsymmetricKeyPair;
import security.crypto.CryptoProcessor;

public abstract class AppProcess {
  protected static AsymmetricKeyPair asymmetricKeyPair;

  protected static void initAsymmetricKeyPair() {
    asymmetricKeyPair = CryptoProcessor.generateAsymmetricKeyPair();
  }

  public static AsymmetricKey getPublicKey() {
    return asymmetricKeyPair.getPublicKey();
  }

  public static AsymmetricKey getPrivateKey() {
    return asymmetricKeyPair.getPrivateKey();
  }
}
