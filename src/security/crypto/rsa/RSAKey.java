package security.crypto.rsa;

import java.math.BigInteger;

public class RSAKey {
  private final BigInteger exponent, modulus;

  public RSAKey(BigInteger exponent, BigInteger modulus) {
    this.exponent = exponent;
    this.modulus = modulus;
  }

  public BigInteger getExponent() {
    return exponent;
  }

  public BigInteger getModulus() {
    return modulus;
  }
}
