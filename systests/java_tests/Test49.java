public class Test49{
    
    public static void main(String [] args){
        Test49 t49 = new Test49();
        t49.run();
    }

    public void run(){
        final String s = getString();
        class MyClass {
            public void run(){
                System.out.println(s);
                class MyClass2 {
                    public void run(){
                        System.out.println(s);
                    }
                };
                MyClass2 mc2 = new MyClass2();
                mc2.run();
            }
        };

        MyClass m = new MyClass();
        m.run();
    }

    public String getString(){
        return "hello";
    }
}
