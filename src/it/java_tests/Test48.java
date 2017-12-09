interface MyListener {
    public void action();
}
public class Test48 {

    public static void main (String [] args){
        Test48 t48 = new Test48();
        t48.run();
    }

    public void run(){
        final String atMe = getString();
        new MyListener () {
            public void action(){
                System.out.println("Smile: ");
            }
        }.action();
        System.out.println(atMe);
    }

    public String getString(){
        return "atMe";
    }
}
