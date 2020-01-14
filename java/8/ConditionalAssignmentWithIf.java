// Category: Flow
// Title: Conditional assignment with if
// Order: 400

public class ConditionalAssignmentWithIf {
  public static boolean bool = 4 == 7;
  public ConditionalAssignmentWithIf() {
    final int x;
    if (bool) {
      x = 1;
    } else {
      x = 7;
    }
  }
}
