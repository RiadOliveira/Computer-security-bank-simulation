package security.crypto.rsa;

import security.crypto.rsa.key.RSAKey;
import security.crypto.rsa.key.RSAKeyPair;
import security.crypto.rsa.key.RSAKeyPairGenerator;

public class RSAProcessor {
  public static RSAKeyPair generateKey() {
    return RSAKeyPairGenerator.generate();
  }

  public static byte[] handleEncryption(
    byte[] data, RSAKey key
  ) {
    return null;
  }
}
