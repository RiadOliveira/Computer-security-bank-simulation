package utils;

import java.text.DecimalFormat;

public class ValueFormatter {
  private static final DecimalFormat BRAZILIAN_FORMAT =
    new DecimalFormat("R$ #,##0.00");

  public static String formatToBrazilianCurrency(
    double value
  ) {
    return BRAZILIAN_FORMAT.format(value);
  }
}
