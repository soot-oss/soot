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
public class Test22 {

    public static void main (String [] args){
        Test22 t22 = new Test22();
        t22.run(3);
    }

    public void run(final int x){
        new HelperWithParams (2, 3) {
            public void action(){
                System.out.println("Smile Anon: "+x);
            }
        }.action();
    }
}
