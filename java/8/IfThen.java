// Category: Flow
// Title: If, then
// Order: 100

public class IfThen {
  public static boolean bool = 4 == 7;
  public IfThen() {
    System.out.println("before");
    if (bool) {
      System.out.println("true");
    } else {
      System.out.println("false");
    }
    System.out.println("after");
  }
}
