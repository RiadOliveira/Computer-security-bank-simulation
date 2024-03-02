package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import dtos.DTO;

public class Serializer {
  public static byte[] serializeObject(Object object) {
    try {
      ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream();
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(
        bytesOutputStream
      );

      objectOutputStream.writeObject(object);
      return bytesOutputStream.toByteArray();
    } catch (Exception exception) {
      ConsolePrinter.println("Falha ao serializar objeto!");
      return null;
    }
  }

  public static DTO deserializeObject(byte[] serializedData) {
    try {
      ByteArrayInputStream inputStream = new ByteArrayInputStream(serializedData);
      ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
      
      return (DTO) objectInputStream.readObject();
    } catch (Exception exception) {
      ConsolePrinter.println("Falha ao deserializar objeto!");
      return null;
    }
  }
}
