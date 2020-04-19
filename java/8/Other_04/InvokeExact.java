// Title: Invoke exact

import java.lang.invoke.MethodHandles;

public class InvokeExact {
  public static boolean first(int a, String b) {
    return false;
  }

  static {
    try {
      boolean result =
          (boolean)
              MethodHandles.lookup()
                  .unreflect(InvokeExact.class.getMethod("first", int.class, String.class))
                  .invokeExact(7, "hi");
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }
}
