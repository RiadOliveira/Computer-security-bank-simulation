package security.crypto;

import java.io.Serializable;
import java.math.BigInteger;

public class AsymmetricKey implements Serializable {
  private final BigInteger exponent, modulus;

  public AsymmetricKey(BigInteger exponent, BigInteger modulus) {
    this.exponent = exponent;
    this.modulus = modulus;
  }

  @Override
  public String toString() {
    return "Exponent: " + exponent + "\nModulus: " + modulus;
  }

  public BigInteger getExponent() {
    return exponent;
  }

  public BigInteger getModulus() {
    return modulus;
  }
}
