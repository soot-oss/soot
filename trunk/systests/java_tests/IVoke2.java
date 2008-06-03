interface I { 
    public void run();
}

class A implements I { 
    public void run(){
        System.out.println("smile");
    }
}

public class IVoke2 {

    public static void test(I i) {
        // the use of i.getClass() leads to problem
        System.out.println(i.getClass());
    }
   
    static public void main (String[] args) {
        I a = new A();
        a.run();
        test(a);
    }
}                                     
