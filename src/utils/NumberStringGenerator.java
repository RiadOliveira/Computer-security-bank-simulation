package utils;

import java.util.Random;

public class NumberStringGenerator {
  private static final Random random = new Random();
  
  public static String generate(int length) {
    String generatedString = "";
    for(int ind=0 ; ind<length ; ind++) {
      generatedString += random.nextInt(10);
    }

    return generatedString;
  }
}
