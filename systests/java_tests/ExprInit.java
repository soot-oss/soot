public class ExprInit {
    int x = 3;
    boolean ok = (x != 5);

    public static void main(String [] args){
        ExprInit e = new ExprInit();
        e.run();
    }

    public void run(){
        System.out.println(x);
        System.out.println(ok);
    }
}

