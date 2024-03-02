package security;

import javax.crypto.SecretKey;

import dtos.DTO;
import error.SecureException;
import utils.BytesUtils;
import utils.Serializer;

public class SecureDTOPacker {
  public static String packDTO(
    DTO dto, SecretKey authKey, SecretKey encryptionKey
  ) throws SecureException {
    try {
      return handleDTOPacking(dto, authKey, encryptionKey);
    } catch (Exception exception) {
      if(exception instanceof SecureException) {
        throw (SecureException) exception;
      }
      throw new SecureException(
        "Falha ao garantir segurança no empacotamento dos dados!"
      );
    }
  }

  private static String handleDTOPacking(
    DTO dto, SecretKey authKey, SecretKey encryptionKey
  ) throws Exception {
    byte[] serializedDTO = Serializer.serializeObject(dto);
  
    SecretKey parsedAuthKey = authKey == null ? encryptionKey : authKey;
    byte[] hmac = CryptoProcessor.generateHMAC(serializedDTO, parsedAuthKey);

    byte[] dataWithHmac = BytesUtils.concatenateByteArrays(hmac, serializedDTO);
    byte[] encryptedBytes = CryptoProcessor.encryptData(
      dataWithHmac, encryptionKey
    );
    
    return CryptoProcessor.encodeBase64(encryptedBytes);
  }

  public static DTO unpackDTO(
    String encodedData, SecretKey authKey, SecretKey encryptionKey
  ) throws SecureException {
    try {
      return handleDTOUnpacking(encodedData, authKey, encryptionKey);
    } catch (Exception exception) {
      if(exception instanceof SecureException) {
        throw (SecureException) exception;
      }
      throw new SecureException(
        "Falha ao garantir segurança no desempacotamento dos dados!"
      );
    }
  }

  public static DTO handleDTOUnpacking(
    String encodedData, SecretKey authKey, SecretKey encryptionKey
  ) throws Exception {
    byte[] decodedBytes = CryptoProcessor.decodeBase64(encodedData);
    byte[] decryptedBytes = CryptoProcessor.decryptData(
      decodedBytes, encryptionKey
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
      throw new SecureException(
        "O HMAC calculado não corresponde ao recebido, " +
        "mensagem descartada!"
      );
    }

    return Serializer.deserializeObject(serializedDTO);
  }
}
