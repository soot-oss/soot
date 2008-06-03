public class Test20 {

    public static void main (String [] args){
        Test20 t20 = new Test20();
        t20.run();
    }

    public void run(){
        final String atMe = getString();
        class MyListener1 {
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
