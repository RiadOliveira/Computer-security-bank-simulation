package constants;

public class Constants {
  private static boolean ALLOW_BACKDOOR_ACCESS = true;

  public static boolean BACKDOOR_ACCESS_ALLOWED() {
    return ALLOW_BACKDOOR_ACCESS;
  }
}
