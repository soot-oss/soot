


class Aaa {

    public class Ccc {
        public int yy;

	public Ccc() {
	    yy = 4;
	}

	public void bar() {
	System.out.println(Aaa.this.x + this.yy);
	class BBB {
	    int u = 0;
	    public void bar2() {
		ddd.bar3();
		System.out.println(Aaa.this.x + this.u + Aaa.Ccc.this.yy);
	     }
	    class Ddd {
		int v = 0;
		public void bar3() {
		    System.out.println(BBB.this.u + this.v);		}
	    }
	    public Ddd ddd;
            public BBB() {
		ddd = new Ddd();
            }
	}
	BBB b = new BBB();
	b.bar2();
	}
    }

    public int zz;

    public int x;

    public Ccc ccc;
    public Aaa() {
       ccc = new Ccc();
       x = 3;
    }
}

public class Test78 { 


    public static void main(String[] args) { 
	Aaa aaa = new Aaa();
	System.out.println(aaa.x);
	aaa.ccc.bar();
    } 
 
} 
