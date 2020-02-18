// Title: Try, catch, finally

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
