package security.crypto;

import java.io.Serializable;

import javax.crypto.SecretKey;

public class ComponentSymmetricKeys implements Serializable {
  private final SecretKey encryptionKey, hashKey;

  public ComponentSymmetricKeys() {
    this.encryptionKey = CryptoProcessor.generateKey();
    this.hashKey = CryptoProcessor.generateKey();
  }

  public ComponentSymmetricKeys(
    SecretKey encryptionKey, SecretKey hashKey
  ) {
    this.encryptionKey = encryptionKey;
    this.hashKey = hashKey;
  }

  @Override
  public String toString() {
    String encryptionString = new String(
      encryptionKey.getEncoded()
    );
    String hashString = new String(
      hashKey.getEncoded()
    );

    return "EncryptionKey: " + encryptionString +
      "\nHashKey: " + hashString;
  }

  public SecretKey getEncryptionKey() {
    return encryptionKey;
  }

  public SecretKey getHashKey() {
    return hashKey;
  }
}
