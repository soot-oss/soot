public class Test60 {

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
