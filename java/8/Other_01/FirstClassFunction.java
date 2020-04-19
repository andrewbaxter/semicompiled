// Title: First class function

public class FirstClassFunction {
  @FunctionalInterface
  public interface MyInterface {
    public int myMethod(int intArg, String strArg);
  }

  public static int impl1(int intArg, String strArg) {
    return 3;
  }

  public FirstClassFunction() {
    MyInterface x = FirstClassFunction::impl1;
    x.myMethod(333, "hi");
  }
}
