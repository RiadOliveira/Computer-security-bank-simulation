package socket;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import security.crypto.AsymmetricKey;
import security.crypto.AsymmetricKeyPair;
import security.crypto.CryptoProcessor;
import socket.components.SocketComponent;
import socket.data.SocketKeys;

public abstract class SocketProcess implements Runnable {
  private static final Map<
    SocketComponent, List<SocketKeys>
  > connectedSocketsKeys = new HashMap<>();
  private static final AsymmetricKeyPair asymmetricKeyPair = CryptoProcessor.generateAsymmetricKeyPair();
  
  protected final Constructor<SocketThread> socketThreadConstructor;

  @SuppressWarnings("unchecked")
  public SocketProcess(Class<SocketThread> socketThreadClass) {
    this.socketThreadConstructor = (Constructor<SocketThread>) socketThreadClass.
      getDeclaredConstructors()[0];
  }

  public static SocketKeys getConnectedSocketKeys(
    SocketComponent component, int socketIndex
  ) {
    var componentKeys = connectedSocketsKeys.get(component);
    if(componentKeys == null) return null;

    return componentKeys.get(socketIndex);
  }

  public static void addSocketComponentKeys(
    SocketComponent component, SocketKeys keys
  ) {
    var componentKeys = connectedSocketsKeys.get(component);

    if(componentKeys != null) componentKeys.add(keys);
    else connectedSocketsKeys.put(component, Arrays.asList(keys));
  }

  public static AsymmetricKey getPublicKey() {
    return asymmetricKeyPair.getPublicKey();
  }

  public static AsymmetricKey getPrivateKey() {
    return asymmetricKeyPair.getPrivateKey();
  }
}
