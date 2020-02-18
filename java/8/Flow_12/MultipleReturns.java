// Title: Multiple return statements

public class MultipleReturns {
  public static boolean bool = 4 == 7;
  public static boolean bool2 = 4 == 11;
  public static int method() {
    if (bool) {
      return 5;
    } else {
      if (bool2) {
        return 12;
      }
    }
    return 1;
  }
}
