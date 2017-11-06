public class Test88 {

    public static void main(String [] args){
        Test88 t88 = new Test88();
        t88.run();
    }

    public void run(){
        Inner in = new Inner();
        in.run();
    }

    public class Inner {
        public void run(){
            go("Ondrej");
        }
    }

    private static void go(String name){
        System.out.println("Hello "+name+"!");
    }
}
