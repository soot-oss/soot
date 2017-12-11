public class Test71 {

    public static void main(String [] args) {
        Test71 t71 = new Test71();
        t71.run();
    }

    private void run() {
        class MyClass {
            public void run(){
                System.out.println(MyClass.class.getName());
            }
        };

        MyClass mc = new MyClass();
        mc.run();
        
        System.out.println(MyClass.class.getName());
    }
}
