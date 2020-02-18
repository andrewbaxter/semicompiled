// Title: While loop, break

public class WhileLoopBreak {
  public WhileLoopBreak() {
    int i = 4;
    System.out.println("before");
    while (i < 7) {
      ++i;
      System.out.println("before break");
      if (i == 6)
        break;
      System.out.println("after break");
    }
    System.out.println("after");
  }
}
