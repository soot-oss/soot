interface MyListener {
    public void action();
}

public class Test13 {

    public static void main (String [] args){
        Test13 t13 = new Test13();
        MyListener ml = t13.run();
        ml.action();
        MyListener ml2 = t13.runAgain();
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
        class MyListener1 implements MyListener {
            public void action(){
                System.out.println("Smile 2");
            }
        };
        return new MyListener1();
    }
}
