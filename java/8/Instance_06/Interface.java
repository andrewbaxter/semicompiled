package Instance_06;

// Title: Interface definition
public interface Interface {
  void method(int x);

  default void defMethod(int x) {
    System.out.println("hi");
  }
}
