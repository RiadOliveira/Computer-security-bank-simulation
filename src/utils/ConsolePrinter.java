package utils;

import dtos.DTO;

public class ConsolePrinter {
  public static synchronized void print(Object content) {
    if(content instanceof DTO) ((DTO)content).print();
    else System.out.print(content);
  }

  public static synchronized void println(Object content) {
    if(content instanceof DTO) ((DTO)content).print();
    else System.out.println(content);
  }
}
