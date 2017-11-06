public class Test45 {

    public static void main(String [] args){
        Test45 t45 = new Test45();
        t45.run();
    }

    public void run(){
        class MyClass {
            public int x;
            public void run(){
                System.out.println("x: "+x);
            }
        }
        MyClass m = new MyClass();
        System.out.println("m.x: "+m.x); 
    }
}
