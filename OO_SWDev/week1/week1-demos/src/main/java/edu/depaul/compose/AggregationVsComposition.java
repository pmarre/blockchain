import StringObject;

public class AggregationVsComposition {
	public static void main(String[] args) {
		StringObject so = new StringObject("Hello");
		A a = new A(so);
		
		B b = new B("World");
		
		System.out.println(a.toString());
		System.out.println(b.toString());
		
		so.str = "SE 450";
		
		System.out.println(a.toString());
		System.out.println(b.toString());
	}
}

class A {
	public StringObject so;
	
	public A(StringObject so) {
		this.so = so;
	}
	
	public String toString() {
		return so.str;
	}
}

class B {
	private StringObject so;

	public B(String str){
		so = new StringObject(str);
	}
	
	public String toString() {
		return so.str;
	}
}

