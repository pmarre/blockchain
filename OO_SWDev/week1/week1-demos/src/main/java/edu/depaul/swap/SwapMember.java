import StringObject;

public class SwapMember {
  public static void main(String[] args) {
    StringObject x = new StringObject("hello");
    StringObject y = new StringObject("world");
    swap(x, y);
    System.out.println(x);
    System.out.println(y);
  }
  public static void swap(StringObject a, StringObject b) {
    String t = a.str; a.str = b.str; b.str = t;
  }

}
