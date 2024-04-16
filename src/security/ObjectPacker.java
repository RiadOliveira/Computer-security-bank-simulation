package security;

import javax.crypto.SecretKey;

import errors.SecurityException;
import security.crypto.AsymmetricKey;
import security.crypto.ComponentSymmetricKeys;
import security.crypto.CryptoProcessor;
import utils.BytesUtils;
import utils.Serializer;

public class ObjectPacker {
  public static<T> String encodeObject(T object) {
    byte[] serializedObject = Serializer.serializeObject(object);
    return CryptoProcessor.encodeBase64(serializedObject);
  }

  public static<T> T decodeObject(String encodedData) {
    byte[] decodedBytes = CryptoProcessor.decodeBase64(encodedData);
    return Serializer.deserializeObject(decodedBytes);
  }

  public static String packSymmetricKeys(
    ComponentSymmetricKeys symmetricKeys,
    AsymmetricKey connectedComponentPublicKey
  ) {
    byte[] encryptionKeyBytes = symmetricKeys.getEncryptionKey().getEncoded();
    byte[] hashKeyBytes = symmetricKeys.getHashKey().getEncoded();
    byte[] serializedKeys = BytesUtils.concatenateByteArrays(
      encryptionKeyBytes, hashKeyBytes
    );

    byte[] encryptedKey = CryptoProcessor.handleAsymmetricEncryption(
      serializedKeys, connectedComponentPublicKey
    );
    return CryptoProcessor.encodeBase64(encryptedKey);
  }

  public static ComponentSymmetricKeys unpackSymmetricKeys(
    String packedKeys, AsymmetricKey privateKey
  ) {
    byte[] decodedBytes = CryptoProcessor.decodeBase64(packedKeys);
    byte[] decryptedBytes = CryptoProcessor.handleAsymmetricEncryption(
      decodedBytes, privateKey
    );

    SecretKey encryptionKey = extractEncryptionKeyForKeysUnpacking(
      decryptedBytes
    );
    SecretKey hashKey = extractHashKeyForKeysUnpacking(decryptedBytes);

    return new ComponentSymmetricKeys(encryptionKey, hashKey);
  }

  private static SecretKey extractEncryptionKeyForKeysUnpacking(
    byte[] decryptedBytes
  ) {
    byte[] encryptionKeyBytes = BytesUtils.getByteSubArray(
      decryptedBytes, 0, CryptoProcessor.AES_KEY_BYTE_SIZE
    );
    return CryptoProcessor.getKeyFromBytes(encryptionKeyBytes);
  }

  private static SecretKey extractHashKeyForKeysUnpacking(
    byte[] decryptedBytes
  ) {
    byte[] hashKeyBytes = BytesUtils.getByteSubArray(
      decryptedBytes, CryptoProcessor.AES_KEY_BYTE_SIZE,
      decryptedBytes.length
    );
    return CryptoProcessor.getKeyFromBytes(hashKeyBytes);
  }
  
  public static<T> String packObject(
    T object, ComponentSymmetricKeys symmetricKeys,
    AsymmetricKey privateKey
  ) throws SecurityException {
    try {
      return handleObjectPacking(
        object, symmetricKeys, privateKey
      );
    } catch (Exception exception) {
      if(exception instanceof SecurityException) {
        throw (SecurityException) exception;
      }
      throw new SecurityException(
        "Falha ao garantir segurança no empacotamento dos dados!"
      );
    }
  }

  private static<T> String handleObjectPacking(
    T object, ComponentSymmetricKeys symmetricKeys,
    AsymmetricKey privateKey
  ) throws Exception {
    byte[] serializedObject = Serializer.serializeObject(object);

    byte[] encryptedObject = CryptoProcessor.encryptSymmetrically(
      serializedObject, symmetricKeys.getEncryptionKey()
    );
    byte[] encryptedHmac = generateAsymmetricallyEncryptedHmac(
      serializedObject, symmetricKeys.getHashKey(), privateKey
    );

    byte[] encryptedObjectWithHmac = BytesUtils.
      concatenateByteArrays(encryptedHmac, encryptedObject);
    return CryptoProcessor.encodeBase64(encryptedObjectWithHmac);
  }

  private static byte[] generateAsymmetricallyEncryptedHmac(
    byte[] serializedObject, SecretKey hashKey,
    AsymmetricKey privateKey
  ) throws Exception {
    byte[] hmac = CryptoProcessor.generateHMAC(serializedObject, hashKey);
    return CryptoProcessor.handleAsymmetricEncryption(
      hmac, privateKey
    );
  }

  public static<T> T unpackObject(
    String packedObject, ComponentSymmetricKeys symmetricKeys,
    AsymmetricKey publicKey
  ) throws SecurityException {
    try {
      return handleObjectUnpacking(
        packedObject, symmetricKeys, publicKey
      );
      
    } catch (Exception exception) {
      if(exception instanceof SecurityException) {
        throw (SecurityException) exception;
      }
      throw new SecurityException(
        "Falha ao garantir segurança no desempacotamento dos dados!"
      );
    }
  }

  private static<T> T handleObjectUnpacking(
    String packedObject, ComponentSymmetricKeys symmetricKeys,
    AsymmetricKey publicKey
  ) throws Exception {
    byte[] decodedBytes = CryptoProcessor.decodeBase64(packedObject);

    byte[] receivedHmac = extractHmacForObjectUnpacking(
      decodedBytes, publicKey
    );
    byte[] serializedObject = extractSerializedObjectForObjectUnpacking(
      decodedBytes, symmetricKeys.getEncryptionKey()
    );
    
    validateHmac(
      serializedObject, receivedHmac, symmetricKeys.getHashKey()
    );
    return Serializer.deserializeObject(serializedObject);
  }

  private static byte[] extractHmacForObjectUnpacking(
    byte[] decodedBytes, AsymmetricKey publicKey
  ) {
    byte[] asymmetricallyEncryptedHmac = BytesUtils.getByteSubArray(
      decodedBytes, 0, CryptoProcessor.ENCRYPTED_HMAC_BYTE_SIZE
    );
    return CryptoProcessor.handleAsymmetricEncryption(
      asymmetricallyEncryptedHmac, publicKey
    );
  }

  private static byte[] extractSerializedObjectForObjectUnpacking(
    byte[] decodedBytes, SecretKey encryptionKey
  ) throws Exception {
    byte[] symmetricallyEncryptedObject = BytesUtils.getByteSubArray(
      decodedBytes, CryptoProcessor.ENCRYPTED_HMAC_BYTE_SIZE, 
      decodedBytes.length
    );

    return CryptoProcessor.decryptSymmetrically(
      symmetricallyEncryptedObject, encryptionKey
    );
  }

  private static void validateHmac(
    byte[] serializedObject, byte[] receivedHmac,
    SecretKey hashKey
  ) throws Exception {
    byte[] calculatedHmac = CryptoProcessor.generateHMAC(
      serializedObject, hashKey
    );
    boolean hmacsAreEqual = BytesUtils.byteArraysAreEqual(
      receivedHmac, calculatedHmac
    );

    if(!hmacsAreEqual) {
      throw new SecurityException(
        "O HMAC calculado não corresponde " +
        "ao recebido, mensagem descartada!"
      );
    }
  }
}
