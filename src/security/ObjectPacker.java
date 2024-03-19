package security;

import javax.crypto.SecretKey;

import error.SecureException;
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
    byte[] serializedKeys = Serializer.serializeObject(symmetricKeys);
    byte[] encryptedKeys = CryptoProcessor.handleAsymmetricEncryption(
      serializedKeys, connectedComponentPublicKey
    );
    return CryptoProcessor.encodeBase64(encryptedKeys);
  }

  public static ComponentSymmetricKeys unpackSymmetricKeys(
    String packedKeys, AsymmetricKey privateKey
  ) {
    byte[] decodedBytes = CryptoProcessor.decodeBase64(packedKeys);
    byte[] decryptedBytes = CryptoProcessor.handleAsymmetricEncryption(
      decodedBytes, privateKey
    );
    return Serializer.deserializeObject(decryptedBytes);
  }
  
  public static<T> String packObject(
    T object, ComponentSymmetricKeys symmetricKeys,
    AsymmetricKey privateKey
  ) throws SecureException {
    try {
      return handleObjectPacking(
        object, symmetricKeys, privateKey
      );
    } catch (Exception exception) {
      if(exception instanceof SecureException) {
        throw (SecureException) exception;
      }
      throw new SecureException(
        "Falha ao garantir segurança no empacotamento dos dados!"
      );
    }
  }

  private static<T> String handleObjectPacking(
    T object, ComponentSymmetricKeys symmetricKeys,
    AsymmetricKey privateKey
  ) throws Exception {
    byte[] serializedObject = Serializer.serializeObject(object);

    byte[] encryptedDTO = CryptoProcessor.encryptSymmetrically(
      serializedObject, symmetricKeys.getEncryptionKey()
    );
    byte[] encryptedHmac = generateAsymmetricallyEncryptedHmac(
      serializedObject, symmetricKeys.getHashKey(), privateKey
    );

    byte[] encryptedDTOWithHmac = BytesUtils.
      concatenateByteArrays(encryptedHmac, encryptedDTO);
    return CryptoProcessor.encodeBase64(encryptedDTOWithHmac);
  }

  private static byte[] generateAsymmetricallyEncryptedHmac(
    byte[] serializedObject, SecretKey authKey,
    AsymmetricKey asymmetricKey
  ) throws Exception {
    byte[] hmac = CryptoProcessor.generateHMAC(serializedObject, authKey);
    return CryptoProcessor.handleAsymmetricEncryption(
      hmac, asymmetricKey
    );
  }

  public static<T> T unpackObject(
    String packedObject, ComponentSymmetricKeys symmetricKeys,
    AsymmetricKey publicKey
  ) throws SecureException {
    try {
      return handleObjectUnpacking(
        packedObject, symmetricKeys, publicKey
      );
    } catch (Exception exception) {
      if(exception instanceof SecureException) {
        throw (SecureException) exception;
      }
      throw new SecureException(
        "Falha ao garantir segurança no desempacotamento dos dados!"
      );
    }
  }

  private static<T> T handleObjectUnpacking(
    String packedObject, ComponentSymmetricKeys symmetricKeys,
    AsymmetricKey publicKey
  ) throws Exception {
    byte[] decodedBytes = CryptoProcessor.decodeBase64(packedObject);

    byte[] receivedHmac = extractHmacFromDecodedBytes(
      decodedBytes, publicKey
    );
    byte[] serializedObject = extractSerializedObjectFromDecodedBytes(
      decodedBytes, symmetricKeys.getEncryptionKey()
    );
    
    validateHmac(
      serializedObject, receivedHmac, symmetricKeys.getHashKey()
    );
    return Serializer.deserializeObject(serializedObject);
  }

  private static byte[] extractHmacFromDecodedBytes(
    byte[] decodedBytes, AsymmetricKey publicKey
  ) {
    byte[] asymmetricallyEncryptedHmac = BytesUtils.getByteSubArray(
      decodedBytes, 0, CryptoProcessor.ENCRYPTED_HMAC_BYTE_SIZE
    );

    return CryptoProcessor.handleAsymmetricEncryption(
      asymmetricallyEncryptedHmac, publicKey
    );
  }

  private static byte[] extractSerializedObjectFromDecodedBytes(
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
      throw new SecureException(
        "O HMAC calculado não corresponde " +
        "ao recebido, mensagem descartada!"
      );
    }
  }
}
