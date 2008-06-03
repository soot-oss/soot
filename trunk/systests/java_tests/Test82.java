public class Test82 {

    public static void main (String [] args){
        Test82 t82 = new Test82();
        t82.run();
    }

    public void run(){
        final String atMe = getString();
        class MyListener1 {
            public int x = 9;
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
