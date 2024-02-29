package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {
  private static final ByteArrayOutputStream bytesOutputStream =
    new ByteArrayOutputStream();
  private static ObjectOutputStream objectOutputStream;

  static {
    try {
      objectOutputStream = new ObjectOutputStream(
        bytesOutputStream
      );
    } catch (Exception exception) {
      ConsolePrinter.println("Falha ao iniciar serializador!");
    }
  }

  public static byte[] serializeObject(Object object) {
    try {
      objectOutputStream.writeObject(object);
      byte[] serializedData = bytesOutputStream.toByteArray();

      return serializedData;
    } catch (Exception exception) {
      ConsolePrinter.println("Falha ao serializar objeto!");
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public static<T> T deserializeObject(byte[] serializedData) {
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
