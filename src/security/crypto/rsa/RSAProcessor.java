package security.crypto.rsa;

import java.math.BigInteger;

public class RSAProcessor {
  public static RSAKeyPair generateKey() {
    return RSAKeyPairGenerator.generate();
  }

  public static byte[] handleEncryption(
    byte[] data, RSAKey key
  ) {
    BigInteger parsedData = new BigInteger(1, data);
    BigInteger resultData = parsedData.modPow(
      key.getExponent(), key.getModulus()
    );

    return resultData.toByteArray();
  }
}
