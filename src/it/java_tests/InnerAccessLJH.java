import java.util.*;

public class InnerAccessLJH {
    public static void main(String[] args) {
      if (new C().getCount() == 3)
        System.out.println("correct");
      else
        System.out.println("incorrect");
    }
}


class C {
    protected int i = 2;
    private String s = "hi";

    Runnable r = new Runnable() {
	    public void run() {
		s += "s";       
		//s = s + "s";       
	    }
	};

    public int getCount() {
	return new Object() {
		public int m() {
		    r.run();
		    return s.length();
		}
	    }.m();
    }
}

class DI extends D.Inner {
}


class D implements Map.Entry {
    public Object getKey() { return null; }
    public Object getValue() { return null; }
    public Object setValue(Object o) { return o; }

    static class Inner {}
}


class Outer {
    class Middle {
	class Inner {
	    void m() {
		Inner.this.m1();
		Middle.this.m1();
		Outer.this.m1();
	    }

	    void m1() {}
	}
	void m1() {}
    }
    void m1() {}
}

