interface MyListener {
    public void action();
}
public class Test8 {

    public static void main (String [] args){
        Test8 t8 = new Test8();
        run();
    }

    public static void run(){
        class MyListener1 implements MyListener {
            public void action(){
                System.out.println("Smile");
            }
        };
        new MyListener1().action();
    }
}
