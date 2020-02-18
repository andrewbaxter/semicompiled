// Title: Mixed-scope locals

public class MixedScopes {
  public MixedScopes() {
    int x = 4;
    {
      int y = 7;
    }
    String z = "nog";
  }
}