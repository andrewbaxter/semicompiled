// Category: Flow
// Title: Nested try
// Order: 700

public class NestedTry {
  public NestedTry() {
    try {
      System.out.println("try 1, before");
      try {
        System.out.println("try 2");
      } catch (RuntimeException e) {
        System.out.println("catch 2");
      }
      System.out.println("try 1, after");
    } catch (RuntimeException e) {
      System.out.println("catch");
    }
  }
}
