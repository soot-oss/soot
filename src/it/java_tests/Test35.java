class Test60 {

    public static void main(String [] args){
        Inner in = new Inner();
        in.run();
    }

    public static class Inner {
        public void run(){
            System.out.println("Hello");
        }
    }
}

public class Test35 {

    public static void main(String [] args){
        Test35 t = new Test35();
        t.run();
    }

    public void run (){
        new Test60.Inner();
    }

}
