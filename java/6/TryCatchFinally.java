// Category: Flow
// Title: Try, catch, finally
// Order: 500

public class TryCatchFinally {
  public TryCatchFinally() {
    try {
      System.out.println("try");
    } catch (RuntimeException e) {
      System.out.println("catch");
    } finally {
      System.out.println("finally");
    }
  }
}
