// Title: Mutual recursion

public class MutualRecursion {
  public static void first() {
    second();
  }

  public static void second() {
    first();
  }
}
