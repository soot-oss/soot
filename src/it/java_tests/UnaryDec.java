public class UnaryDec {

    private int x = 9;

    public static void main(String [] args){
        UnaryDec ud = new UnaryDec();
        ud.run();
    }

    public void run(){
        Inner i = new Inner();
        i.run();
    }

    class Inner {
        public void run(){
            x--;
            x-=1;
        }
    }
}
