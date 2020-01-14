// Category: Minimal
// Title: Instance method
// Order: 700

public class InstanceMethod {
  public void method() {
    System.out.println("hi");
  }

  public static void main(String[] args) {
    InstanceMethod instance = new InstanceMethod();
    instance.method();
  }
}
