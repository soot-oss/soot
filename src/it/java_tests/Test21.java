class HelperWithParams {

    public static void main(String [] args){
    }
    public HelperWithParams(int x, int y){
        
    }
    
    public HelperWithParams(){
        
    }
    
    public void action(){
        System.out.println("Helper");
    }
}
public class Test21 {

    public static void main (String [] args){
        Test21 t21 = new Test21();
        t21.run();
    }

    public void run(){
        new HelperWithParams (2, 3) {
            public void action(){
                System.out.println("Smile Anon");
            }
        }.action();
    }
}
