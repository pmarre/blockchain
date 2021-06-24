package OO_SWDev.*;

public class SwapPrimitive {

  static public void main (String[] args) {
    int x = 42;
    int y = 27;
    swap(x,y);
    System.out.println(x);
    System.out.println(y);
  }
  private static void swap (int a, int b) {
    int t = a;
    a = b;
    b = t;
  }
}
