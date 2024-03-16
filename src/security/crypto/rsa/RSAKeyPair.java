package security.crypto.rsa;

import java.math.BigInteger;

public class RSAKeyPair {
  private final RSAKey publicKey, privateKey;

  public RSAKeyPair(
    BigInteger publicExponent, BigInteger privateExponent,
    BigInteger modulus
  ) {
    this.publicKey = new RSAKey(publicExponent, modulus);
    this.privateKey = new RSAKey(privateExponent, modulus);
  }

  public RSAKey getPublicKey() {
    return publicKey;
  }

  public RSAKey getPrivateKey() {
    return privateKey;
  }
}