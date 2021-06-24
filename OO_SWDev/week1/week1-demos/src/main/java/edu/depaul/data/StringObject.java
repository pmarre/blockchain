public class StringObject{
  public String str;
  public StringObject(String str){ this.str = str; }
  public String toString(){ return str; }

  @Override
  public boolean equals(Object o) {
    if (o == null) return false;
    if (! (o instanceof StringObject)) return false;
    StringObject other = (StringObject) o;
    return str.equals(other.str);
  }
}
