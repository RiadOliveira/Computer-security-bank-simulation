package utils;

import javax.crypto.SecretKey;

import dtos.DTO;
import error.AppException;
import utils.CryptoUtils.EncryptionAlgorithm;

public class SecureDTOPacker {
  public static String packDTO(
    DTO dto, SecretKey key,
    EncryptionAlgorithm encryptionAlgorithm
  ) throws Exception {
    byte[] serializedDTO = SerializationUtils.serializeObject(dto);
    byte[] hmac = CryptoUtils.generateHMAC(serializedDTO, key);
    byte[] dataWithHmac = BytesUtils.concatenateByteArrays(hmac, serializedDTO);

    byte[] encryptedBytes = CryptoUtils.encryptData(
      encryptionAlgorithm, dataWithHmac, key
    );
    return CryptoUtils.encodeBase64(encryptedBytes);
  }

  public static<T> T unpackDTO(
    String encodedData, SecretKey key,
    EncryptionAlgorithm encryptionAlgorithm
  ) throws Exception {
    byte[] decodedBytes = CryptoUtils.decodeBase64(encodedData);
    byte[] decryptedBytes = CryptoUtils.decryptData(
      encryptionAlgorithm, decodedBytes, key
    );

    byte[] receivedHmac = BytesUtils.getByteSubArray(
      decryptedBytes, 0, CryptoUtils.HMAC_BYTE_SIZE
    );
    byte[] serializedDTO = BytesUtils.getByteSubArray(
      decryptedBytes, CryptoUtils.HMAC_BYTE_SIZE, 
      decryptedBytes.length
    );

    byte[] calculatedHmac = CryptoUtils.generateHMAC(
      serializedDTO, key
    );
    boolean hmacsAreEqual = BytesUtils.byteArraysAreEqual(
      receivedHmac, calculatedHmac
    );
    if(!hmacsAreEqual) {
      throw new AppException(
        "O HMAC calculado não corresponde ao recebido, " +
        "mensagem descartada!"
      );
    }

    return SerializationUtils.deserializeObject(serializedDTO);
  }
}
