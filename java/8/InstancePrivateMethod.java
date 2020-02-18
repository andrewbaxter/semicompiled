// Category: Instance
// Title: Private method
// Order: 800

public class InstancePrivateMethod {
  private void method() {
    System.out.println("hi");
  }

  public static void main(String[] args) {
    InstancePrivateMethod instance = new InstancePrivateMethod();
    instance.method();
  }
}
