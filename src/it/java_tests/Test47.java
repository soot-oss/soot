public class Test47{

    public static void main(String [] args){
        Test47 t47 = new Test47();
        t47.run();
    }
    
    public void run(){
        class MyClass{
            public int x = 9;
            public void run(){
                System.out.println(x);
            }
        };
        MyClass mc = new MyClass();
        mc.run();
        new MyClass(){
            public void run(){
                System.out.println(x*x);
            }
        }.run();
    }
}
