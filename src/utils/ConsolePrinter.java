package utils;

public class ConsolePrinter {
  public static synchronized void print(Object content) {
    System.out.print(content);
  }

  public static synchronized void println(Object content) {
    System.out.println(content);
  }
}
