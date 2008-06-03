public class Inter2 {

    public static void main(String [] args){
        Inter2 i2 = new Inter2();
        i2.run();
        i2.go();
    }

    public void run(){
        Object o = new I3.C();
        Class c = o.getClass();
        System.out.println(c.getModifiers());

        o = new C2.C3();
        c = o.getClass();
        System.out.println(c.getModifiers());
    }

    public void go(){
        
        Object o = new C2.C3();
        Class c = o.getClass();
        System.out.println(c.getModifiers());
    }
}

interface I3 {

    class C{}
}

class C2 {

    static class C3{}
}
