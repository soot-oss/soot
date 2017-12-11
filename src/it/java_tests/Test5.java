interface MyListener {
    public void action();
}
public class Test5 {

    public static void main (String [] args){
        Test5 t5 = new Test5();
        t5.run();
    }

    public void run(){
        final String atMe = getString();
        new MyListener () {
            public void action(){
                System.out.println("Smile: "+atMe);
            }
        }.action();
    }

    public String getString(){
        return "atMe";
    }
}
