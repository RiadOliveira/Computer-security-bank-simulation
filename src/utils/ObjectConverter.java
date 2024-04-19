package utils;

import errors.AppException;

public class ObjectConverter {
  @SuppressWarnings("unchecked")
  public static <T> T convert(Object objectToConvert) throws AppException {
    try {
      Class<T> targetClass = (Class<T>) objectToConvert.getClass();
      return targetClass.cast(objectToConvert);
    } catch(ClassCastException e) {
      throw new AppException("Instância de objeto inválida!");
    }
  }
}
