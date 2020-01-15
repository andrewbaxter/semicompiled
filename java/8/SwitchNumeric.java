// Category: Flow
// Title: Numeric switch
// Order: 1000

public class SwitchNumeric {
  public static int method(int q) {
    switch (q) {
      case 1:
        return 1;
      case 2:
        return 2;
      case 3:
        System.out.println("case 3");
        break;
    }
    return 4;
  }
}
