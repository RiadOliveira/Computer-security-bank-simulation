package utils;

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
    int length = end - start;
    byte[] subArray = new byte[length];

    System.arraycopy(data, start, subArray, 0, length);
    return subArray;
  }

  public static boolean byteArraysAreEqual(byte[] first, byte[] second) {
    if (first.length != second.length) return false;
    
    for (int ind = 0; ind < first.length; ind++) {
      if (first[ind] != second[ind]) return false;
    }
    
    return true;
  }
}
