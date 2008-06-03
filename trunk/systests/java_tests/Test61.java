public class Test61{

    public static void main(String [] args){
        run(4);
    }
    
    public static void run(final int x){
        class Helper{
        
            public void action(){
                
                class Helper1 {
                    public void action(){
                        System.out.println(x);
                    }
                };
            }
        };
    }
}
