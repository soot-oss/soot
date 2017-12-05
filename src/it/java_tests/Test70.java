public class Test70 {
    public static void main (String [] args){
        Test70 t70 = new Test70();
        t70.run();
    }

    public void run(){
        class MyClass {
            public void run(){
                System.out.println("local inner");
            }
        };
            
        MyClass mc = new MyClass();
        mc.run();

        Object obj = mc;

        if (obj instanceof MyClass){
            MyClass mc2 = (MyClass)obj;
            mc2.run();
        }
    }

}
