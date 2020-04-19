// Title: Static read

public class StaticRead {
  final static int x;

  static {
    x = 5 + 7;
    int y = x;
  }
}