public class PrivateGets {

    private int x = 0;
    private int y = 9;

    public static void main(String [] args){
        PrivateGets pg = new PrivateGets();
        pg.run();
    }

    public void run(){
        Inner i = new Inner();
        i.run();
    }

    public class Inner {
        
        public void run(){
            System.out.println("x: "+x);
            System.out.println("x: "+x);
        }
    }
}
