public class IfTest {

    int x = 0;
    public static void main(String [] args){
    
        int i = 9;

        if (i > 10) {

            i = i - 1;
            System.out.println(i);
        }
        
        if (i < 10) {
            System.out.println("Smile");
        }
        else {
            System.out.println("Tomorrow");
        }

        if (i == 9) {
            System.out.println("i = 9");
        }

        boolean result;

        for (int j = 0; j < 10; j ++) {
            result = true;
            if (result) {
                System.out.println("true");
            }
            if (!result){
                System.out.println("false");
            }
        }
   
        IfTest it = new IfTest();
        it.run(7);
        it.go();
    }

    private void go() {
        int i = 0;
        int j = 9;
        if (i < j) {
            i = i + 1;
        }
    }

    private boolean run(int size) {
        boolean result = true;

        Integer t = new Integer(9);
        if (t instanceof Integer) {
            t = new Integer(100);
        }
        if (result) {
            System.out.println("Smile");
        }
       
        if (size > 1) {
            result = false;
        }
        else {
            x = size;
        }

        return result;
    }

    
}
