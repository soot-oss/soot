public class SimpleSwitch {

    public static void main(String [] args) {
        SimpleSwitch ss = new SimpleSwitch();
        ss.run(7);
    }
    
    public void run(int i){
    
        switch(i){
            case 2: {
                System.out.println(2);
                break;
                    }
            case 7: {
                System.out.println(7);
                break;
                    }
             default: {
                System.out.println("default");
                break;
                      }
        
        }
    }
    
}
