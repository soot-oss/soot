public class SwitchStmts {
    public static void main (String [] args) {
        for (int i = 0 ; i < 10; i++) {
            switch (i) {
            
                case 7 : {
                             System.out.println("Hello");
                             break;
                             }
                case 2 : {
                               System.out.println("Hi");
                               break;
                             }
                default: {
                            System.out.println("Smile");
                            break;
                         }
            }
                
        }
        for (int i = 0 ; i < 7; i++) {
            switch (i) {
                case 0 : {System.out.println("Hello");
                          break;}
                case 1 : {System.out.println("Hello");
                          break;}
                case 4 : {System.out.println("Hello");
                          break;}
                case 2 : {System.out.println("Hello");
                          break;}
                case 5 : {System.out.println("Hello");
                          break;}
                case 3 : {System.out.println("Hello");
                          break;}
                default : {System.out.println("Hello");
                          break;}
            }
        }
    }
}
