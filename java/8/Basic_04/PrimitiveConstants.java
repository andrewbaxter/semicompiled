// Title: Primitive constants

public class PrimitiveConstants {
  public PrimitiveConstants() {
    {
      int x;
      x = -1;
      x = 0;
      x = 1;
      x = 5;
      x = 6;
      x = -128;
      x = 127;
      x = -32768;
      x = 32767;
      x = 0x80000000;
      x = 0x7fffffff;
    }
    {
      long x;
      x = -1;
      x = 0;
      x = 1;
      x = 2;
      x = 3;
      x = 0x8000000000000000L;
      x = 0x7fffffffffffffffL;
    }
    {
      float x;
      x = -1;
      x = 0;
      x = 1;
      x = 2;
      x = 3;
      x = 0.5f;
      x = 0.1f;
      x = 0x1.fffffeP+127f;
      x = 0x0.000002P-126f;
      x = 1.0f / 0.0f; // Pos inf
      x = -1.0f / 0.0f; // Neg inf
      x = 0.0f / 0.0f; // NaN
    }
    {
      double x;
      x = -1;
      x = 0;
      x = 1;
      x = 2;
    }
    {
      boolean x;
      x = true;
      x = false;
    }
    {
      byte x;
      x = -1;
      x = 0;
      x = 1;
      x = 2;
    }
    {
      short x;
      x = -1;
      x = 0;
      x = 1;
      x = 2;
    }
  }
}
