// Category: Basic
// Title: Instance field
// Order: 710

public class InstanceField {
  public String a;

  public InstanceField() {
    this.a = "hi";
  }

  public static void main(String[] args) {
    InstanceField instance = new InstanceField();
    System.out.println(instance.a);
  }
}
