// Category: Flow
// Title: String switch statement
// Order: 1100

public class SwitchString {
  public static int method(String q) {
    switch (q) {
      case "hi":
        return 1;
      case "bye":
        return 2;
      case "case 3":
        System.out.println("case 3");
        break;
    }
    return 4;
  }
}
