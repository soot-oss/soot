class Helper {

    public static void main(String [] args){
    }

    public void action(){
        System.out.println("Helper");
    }
}

public class Test19 {

    public static void main (String [] args){
        Test19 t19 = new Test19();
        t19.run();
    }

    public void run(){
        final String atMe = getString();
        new Helper () {
            public void action(){
                System.out.println("Smile: "+atMe);
            }
        }.action();
    }

    public String getString(){
        return "atMe";
    }
}
