public class ReturnCond {

    public static void main(String [] args){
        ReturnCond rc = new ReturnCond();
        boolean b = rc.run();
        System.out.println(b);
    }

    public boolean run(){
    
        int x = 0;
        int y = 8;

        return x > y;
    }
    
}
