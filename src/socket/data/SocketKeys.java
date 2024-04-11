package socket.data;

import security.crypto.AsymmetricKey;
import security.crypto.ComponentSymmetricKeys;

public class SocketKeys {
  private ComponentSymmetricKeys symmetricKeys;
  private AsymmetricKey publicKey;

  public SocketKeys() {
    super();
  }

  public SocketKeys(
    ComponentSymmetricKeys symmetricKeys, AsymmetricKey publicKey
  ) {
    this.symmetricKeys = symmetricKeys;
    this.publicKey = publicKey;
  }

  public ComponentSymmetricKeys getSymmetricKeys() {
    return symmetricKeys;
  }

  public void setSymmetricKeys(ComponentSymmetricKeys symmetricKeys) {
    this.symmetricKeys = symmetricKeys;
  }

  public AsymmetricKey getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(AsymmetricKey publicKey) {
    this.publicKey = publicKey;
  }
}
