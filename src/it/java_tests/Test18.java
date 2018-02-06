interface MyListener {
    public void action();
}

public class Test18 {

    public static void main (String [] args){
        Test18 t18 = new Test18();
        t18.run();
    }

    public void run(){
        final String atMe = getString();
        new MyListener () {
            public void action(){
                System.out.println("Smile: ");
            }
        }.action();
    }

    public String getString(){
        return "atMe";
    }
}
