// Title: Interface method
public class InterfaceMethod {
  public static interface MyInterface {
    public void method(int x);
  }

  public static class MyClass implements MyInterface {
    public void method(int x) {
      System.out.println("hi");
    }
  }

  public static void main(String[] args) {
    MyInterface instance = new MyClass();
    instance.method(4);
  }
}
