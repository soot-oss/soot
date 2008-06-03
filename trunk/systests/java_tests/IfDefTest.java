public class IfDefTest {
    public static void main(String [] args){
        IfDefTest i = new IfDefTest();
        i.run(9);
    }
    
    public void run(int i){
        int x;
        
        if (i < 10){
            x = 6;    
        }
        else if (i < 20){
            x = 9;
        }
        else {
            x = 8;
        }
        System.out.println(x);
    }
}
