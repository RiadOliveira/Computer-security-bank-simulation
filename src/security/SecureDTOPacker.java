package security;

import javax.crypto.SecretKey;

import dtos.DTO;
import error.AppException;
import security.CryptoProcessor.EncryptionAlgorithm;
import utils.BytesUtils;
import utils.Serializer;

public class SecureDTOPacker {
  public static String packDTO(
    DTO dto, SecretKey authKey, SecretKey encryptionKey,
    EncryptionAlgorithm encryptionAlgorithm
  ) throws Exception {
    byte[] serializedDTO = Serializer.serializeObject(dto);

    SecretKey parsedAuthKey = authKey == null ? encryptionKey : authKey;
    byte[] hmac = CryptoProcessor.generateHMAC(serializedDTO, parsedAuthKey);

    byte[] dataWithHmac = BytesUtils.concatenateByteArrays(hmac, serializedDTO);
    byte[] encryptedBytes = CryptoProcessor.encryptData(
      encryptionAlgorithm, dataWithHmac, encryptionKey
    );
    
    return CryptoProcessor.encodeBase64(encryptedBytes);
  }

  public static<T> T unpackDTO(
    String encodedData, SecretKey authKey,
    SecretKey encryptionKey, EncryptionAlgorithm encryptionAlgorithm
  ) throws Exception {
    byte[] decodedBytes = CryptoProcessor.decodeBase64(encodedData);
    byte[] decryptedBytes = CryptoProcessor.decryptData(
      encryptionAlgorithm, decodedBytes, encryptionKey
    );

    byte[] receivedHmac = BytesUtils.getByteSubArray(
      decryptedBytes, 0, CryptoProcessor.HMAC_BYTE_SIZE
    );
    byte[] serializedDTO = BytesUtils.getByteSubArray(
      decryptedBytes, CryptoProcessor.HMAC_BYTE_SIZE, 
      decryptedBytes.length
    );

    SecretKey parsedAuthKey = authKey == null ? encryptionKey : authKey;
    byte[] calculatedHmac = CryptoProcessor.generateHMAC(
      serializedDTO, parsedAuthKey
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

    return Serializer.deserializeObject(serializedDTO);
  }
}