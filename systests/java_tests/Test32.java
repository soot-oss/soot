public class Test32 {

    public static void main(String [] args){
        Test32 t = new Test32();
        t.run();
    }

    public void run (){
        Inner in = new Inner();
        in.run();
    }

    class Inner {
        
        public void run(){
            class Helper2 {
                public void action(){
                    System.out.println("Smile");
                }
            };
            new Helper2().action();
        }
    }
}
