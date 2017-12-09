interface MyListener {
    public void action();
}

public class Test16 {

    public static void main (String [] args){
        Test16 t16 = new Test16();
        t16.run();
    }

    public void run(){
        class MyListener1 implements MyListener{
            public void action(){
                System.out.println("Smile");
            }
        };
        new MyListener1().action();
    }
}
