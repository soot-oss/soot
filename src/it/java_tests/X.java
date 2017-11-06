public class X {

    public static void main(String [] args){
        X x = new X();
        x.m1();
        x.m4();
        x.m6();
        W w = new W();
        w.m9();
    }
    
    public void m1(){
        class Y{
            public void m2(){
                System.out.println("Y 1 m2");
            }
        }
        Y y = new Y();
        y.m2();
        class Z{
            public void m3(){
                System.out.println("Z 1 m3");
            }
        }
        Z z = new Z();
        z.m3();
    }

    public void m4(){
        class Y{
            public void m5(){
                System.out.println("Y 2 m5");
            }
        }
        Y y = new Y();
        y.m5();
    }

    public void m6(){
        new Object(){
            public void m7(){
                System.out.println("Anon 1 in X m7");
            }
        }.m7();
        new Object(){
            public void m8(){
                System.out.println("Anon 2 in X m8");
            }
        }.m8();
    }
}
class W {

    public void m9(){
        new Object(){
            public void m10(){
                System.out.println("Anon 1 in W m10");
            }
        }.m10();
    } 
}
