package security.crypto;

import java.math.BigInteger;

public class AsymmetricKeyPair {
  private final AsymmetricKey publicKey, privateKey;

  public AsymmetricKeyPair(
    BigInteger publicExponent, BigInteger privateExponent,
    BigInteger modulus
  ) {
    this.publicKey = new AsymmetricKey(publicExponent, modulus);
    this.privateKey = new AsymmetricKey(privateExponent, modulus);
  }

  public AsymmetricKey getPublicKey() {
    return publicKey;
  }

  public AsymmetricKey getPrivateKey() {
    return privateKey;
  }
}