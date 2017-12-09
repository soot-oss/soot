public class AnonInField {

    private int x = 99;
    
    Runnable r = new Runnable(){
        public void run(){
            System.out.println(x);        
        }
    };

    public static void main(String [] args){
        AnonInField aif = new AnonInField();
        aif.r.run();
    }
}
