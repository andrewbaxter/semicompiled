// Category: Flow
// Title: While loop
// Order: 150

public class WhileLoop {
  public WhileLoop() {
    int i = 4;
    System.out.println("before");
    while (i < 7) {
      System.out.println("loop start");
      ++i;
      System.out.println("loop end");
    }
    System.out.println("after");
  }
}
