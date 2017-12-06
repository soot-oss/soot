class Helper {

    public static void main(String [] args){
    }

    public void action(){
        System.out.println("Helper");
    }
}
public class Test31 {

    public static void main(String [] args){
        Test31 t = new Test31();
        t.run();
    }

    public void run (){
        Inner in = new Inner();
        in.run();
    }

    class Inner {
        
        public void run(){
            new Helper () {
                public void action(){
                    System.out.println("Smile");
                }
            }.action();
        }
    }
}
