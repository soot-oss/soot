public class Test91 {

    public static void main(String [] args){
        Test91 t91 = new Test91();
        t91.run();
    }

    public void run(){
        class MyClass {
            public MyClass(int x){
                System.out.println("x="+x);
            }
            public MyClass(int x, int y){
                System.out.println("x*y="+x*y);
            }
            public void run(){
                System.out.println("running");
            }
        }
        MyClass m1 = new MyClass(3);
        MyClass m2 = new MyClass(3, 4);
        m1.run();
        m2.run();
    }
}
