interface MyListener {
    public void action();
}
public class Test9 {

    private int x = 9;
    
    public static void main (String [] args){
        Test9 t = new Test9();
        t.run();
    }

    public void run(){
        class MyListener1 implements MyListener {
            public void action(){
                System.out.println("Smile: "+x);
                go();
            }
        };
        MyListener ml = new MyListener1();
        ml.action();
    }

    private void go(){
        System.out.println("today");
    }
}
