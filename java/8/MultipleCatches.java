// Category: Flow
// Title: Multiple catches
// Order: 600

import java.io.FileNotFoundException;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class MultipleCatches {
  public MultipleCatches() {
    try {
      System.out.println("try");
    } catch (NoSuchElementException e) {
      System.out.println("catch 1");
    } catch (ConcurrentModificationException e) {
      System.out.println("catch 2");
    } finally {
      System.out.println("finally");
    }
  }
}
