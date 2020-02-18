// Title: Field read/write

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
