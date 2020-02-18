// Category: Instance
// Title: Field read/write
// Order: 900

public class InstanceField {
  public String a;

  public InstanceField() {
    this.a = "hi";
  }

  public static void myMethod() {
    InstanceField instance = new InstanceField();
    System.out.println(instance.a);
  }
}
