package utils;

import java.util.Arrays;

public class BytesUtils {
  public static byte[] concatenateByteArrays(
    byte[] first, byte[] second
  ) {
    int firstLength = first.length;
    int secondLength = second.length;

    byte[] result = new byte[firstLength + secondLength];
    System.arraycopy(first, 0, result, 0, firstLength);
    System.arraycopy(second, 0, result, firstLength, secondLength);

    return result;
  }

  public static byte[] getByteSubArray(
    byte[] data, int start, int end
  ) {
    start = Math.abs(start);
    end = Math.abs(end);

    if(start > end) {
      int temp = start;
      start = end;
      end = temp;
    }

    if (start >= data.length || end > data.length) {
      throw new IllegalArgumentException("Índices de início/fim inválidos!");
    }
    return Arrays.copyOfRange(data, start, end);
  }

  public static boolean byteArraysAreEqual(byte[] first, byte[] second) {
    if (first.length != second.length) return false;
    
    for (int ind = 0; ind < first.length; ind++) {
      if (first[ind] != second[ind]) return false;
    }
    
    return true;
  }
}
