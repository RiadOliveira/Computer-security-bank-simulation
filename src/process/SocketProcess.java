package process;

import security.crypto.AsymmetricKey;
import security.crypto.AsymmetricKeyPair;
import security.crypto.CryptoProcessor;

public abstract class SocketProcess implements Runnable {
  protected final AsymmetricKeyPair asymmetricKeyPair;
    
  public SocketProcess() {
    this.asymmetricKeyPair = CryptoProcessor.generateAsymmetricKeyPair();
  }

  public AsymmetricKeyPair getAsymmetricKeyPair() {
    return asymmetricKeyPair;
  }

  public AsymmetricKey getPublicKey() {
    return asymmetricKeyPair.getPublicKey();
  }

  public AsymmetricKey getPrivateKey() {
    return asymmetricKeyPair.getPrivateKey();
  }
}
