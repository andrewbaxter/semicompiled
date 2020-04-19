// Title: Fields

import java.util.List;

public class InstanceField {
  public int prim;
  public String obj;
  public List<Boolean> generic;

  public InstanceField() {
    this.obj = "hi";
  }

  public static void myMethod() {
    InstanceField instance = new InstanceField();
    System.out.println(instance.obj);
  }
}
