public class Test58{

    public static void main(String [] args){
        Test58 t58 = new Test58();
        t58.run();
    }

    public void run(){
        class MyClass {
            public void run(){
                class MyClass2{
                    public void run(){
                        System.out.println("MyClass2");
                    }
                };
                MyClass2 mc2 = new MyClass2();
                mc2.run();
                System.out.println("MyClass");
            }
        };
        MyClass mc = new MyClass();
        mc.run();
    }
}
