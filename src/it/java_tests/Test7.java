interface MyListener {
    public void action();
}
public class Test7 {

    public static void main (String [] args){
        Test7 t7 = new Test7();
        t7.run();
    }

    public void run(){
        final String atMe = getString();
        class MyListener1 implements MyListener{
            public void action(){
                System.out.println("Smile: "+atMe);
            }
        };
        new MyListener1().action();
    }

    public String getString(){
        return "atMe";
    }
}
