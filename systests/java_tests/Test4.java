interface MyListener {
    public void action();
}
public class Test4 {

    public static void main (String [] args){
        Test4 t4 = new Test4();
        MyListener ml = t4.run();
        ml.action();
        MyListener ml2 = t4.runAgain();
        ml2.action();
    }

    public MyListener run(){
        class MyListener1 implements MyListener {
            public void action(){
                System.out.println("Smile");
            }
        };
        return new MyListener1();
    }
    
    public MyListener runAgain(){
        class MyListener2 implements MyListener {
            public void action(){
                System.out.println("Smile 2");
            }
        };
        return new MyListener2();
    }
}
