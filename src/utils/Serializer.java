package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {
  public static <T> byte[] serializeObject(T object) {
    try {
      ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream();
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(
          bytesOutputStream);

      objectOutputStream.writeObject(object);

      var byteArray = bytesOutputStream.toByteArray();
      for (int i = 0; i < byteArray.length; i++) {
        System.out.println(byteArray[i]);
      }
      return bytesOutputStream.toByteArray();
    } catch (Exception exception) {
      ConsolePrinter.println("Falha ao serializar objeto!");
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T deserializeObject(byte[] serializedData) {
    try {
      ByteArrayInputStream inputStream = new ByteArrayInputStream(serializedData);
      ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

      return (T) objectInputStream.readObject();
    } catch (Exception exception) {
      ConsolePrinter.println("Falha ao deserializar objeto!");
      return null;
    }
  }
}
