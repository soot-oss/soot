class Helper {

    public static void main(String [] args){
    }

    public void action(){
        System.out.println("Helper");
    }
}
public class Test55{

    public static void main(String [] args){
        run(4);
    }
    
    public static void run(final int x){
        new Helper(){
        
            public void action(){
                System.out.println(x);
            }
        };
    }
}
