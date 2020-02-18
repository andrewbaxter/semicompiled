// Title: While loop, continue

public class WhileLoopContinue {
  public WhileLoopContinue() {
    int i = 4;
    System.out.println("before");
    while (i < 7) {
      ++i;
      System.out.println("before continue");
      if (i == 7) continue;
      System.out.println("after continue");
    }
    System.out.println("after");
  }
}
