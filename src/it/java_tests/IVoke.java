interface I { }

class A implements I { }

public class IVoke {

    public static void test(I i) {
        // the use of i.getClass() leads to problem
        System.out.println(i.getClass());
    }
   
    static public void main (String[] args) {
        I a = new A();
        test(a);
    }
}                                     
