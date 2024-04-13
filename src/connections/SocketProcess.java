package connections;

import java.lang.reflect.Constructor;

import security.crypto.AsymmetricKey;
import security.crypto.AsymmetricKeyPair;
import security.crypto.CryptoProcessor;

public abstract class SocketProcess implements Runnable {
  private static final AsymmetricKeyPair asymmetricKeyPair = CryptoProcessor.
    generateAsymmetricKeyPair();
  
  protected final Constructor<? extends SocketThread> socketThreadConstructor;

  @SuppressWarnings("unchecked")
  public SocketProcess(Class<? extends SocketThread> socketThreadClass) {
    this.socketThreadConstructor = (Constructor<? extends SocketThread>) socketThreadClass.
      getDeclaredConstructors()[0];
  }

  public static AsymmetricKey getPublicKey() {
    return asymmetricKeyPair.getPublicKey();
  }

  public static AsymmetricKey getPrivateKey() {
    return asymmetricKeyPair.getPrivateKey();
  }
}
