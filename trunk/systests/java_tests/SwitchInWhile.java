public class SwitchInWhile {

    public static void main (String [] args) {
    
        int i = 0;
        while (i < 10) {
            switch(i % 2) {
                case 0 : {
                             System.out.println("even");
                             break;
                         }
                case 1: {
                            System.out.println("odd");
                        }
                
            
            }

            i++;
        }
    }
}
