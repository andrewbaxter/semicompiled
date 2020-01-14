// Category: Minimal
// Title: Multiple return statements
// Order: 800

public class MultipleReturns {
  public static boolean bool = 4 == 7;
  public static boolean bool2 = 4 == 11;
  public static int method() {
    if (bool) {
      return 5;
    } else {
      if (bool) {
        return 12;
      }
    }
    return 1;
  }
}
