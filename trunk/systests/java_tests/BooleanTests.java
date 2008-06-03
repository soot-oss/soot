public class BooleanTests {

    public static void main(String [] args){

        boolean b = false;
        boolean c = true;
        int i = 0;
            
        while (c){
            System.out.println(true);
            i++;
            if (i > 10) break;
        }
        
        if (false || false){
            System.out.println("both false");
        }
        if (true || false) {
            System.out.println("at least one true");
        }
        
    }
}
