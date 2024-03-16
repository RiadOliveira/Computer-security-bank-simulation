package security.crypto.rsa.key;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSAKeyPairGenerator {
  private static final SecureRandom secureRandom = new SecureRandom();

  private static final int PRIME_BIT_LENGTH = 2048;
  private static final BigInteger INITIAL_PUBLIC_EXPONENT_VALUE = 
    BigInteger.valueOf(65537l);
  
  public static RSAKeyPair generate() {
    BigInteger firstPrime = generatePrime();
    BigInteger secondPrime = generatePrime();

    BigInteger modulus = firstPrime.multiply(secondPrime);
    BigInteger totient = (firstPrime.subtract(BigInteger.ONE)).
      multiply(secondPrime.subtract(BigInteger.ONE));

    BigInteger publicExponent = getPublicExponent(totient);
    BigInteger privateExponent = publicExponent.modInverse(totient);
    
    return new RSAKeyPair(publicExponent, privateExponent, modulus);
  }

  private static BigInteger generatePrime() {
    BigInteger primeNumber;
    do {
      primeNumber = BigInteger.probablePrime(
        PRIME_BIT_LENGTH, secureRandom
      );
    } while(!isPrime(primeNumber));

    return primeNumber;
  }

  private static boolean isPrime(BigInteger numberToVerify) {
    return numberToVerify.isProbablePrime(100);
  }

  private static BigInteger getPublicExponent(BigInteger totient) {
    BigInteger publicExponent = INITIAL_PUBLIC_EXPONENT_VALUE;
    while(!validPublicExponent(publicExponent, totient)) {
      publicExponent = publicExponent.add(BigInteger.TWO);
    }

    return publicExponent;
  }

  private static boolean validPublicExponent(
    BigInteger publicExponent, BigInteger totient
  ) {
    return publicExponent.gcd(totient).equals(BigInteger.ONE);
  }
}
