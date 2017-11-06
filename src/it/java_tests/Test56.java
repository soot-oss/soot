public class Test56{

    public static void main(String [] args){
        run(4);
    }
    
    public static void run(final int x){
        class Helper{
        
            public void action(){
                System.out.println(x);
            }
        };
    }
}
