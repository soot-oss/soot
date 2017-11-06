public class PrivateFieldArray {

    private int [] x = new int[] {3, 5, 4};
    
    public static void main(String [] args) {
        PrivateFieldArray pfa = new PrivateFieldArray();
        pfa.run();
    }

    private void run() {
        Inner i = new Inner();
        i.run();
    }

    class Inner {
        public void run(){
            int i = 0;
            System.out.println(foo().x[i++]+=4);
            foo().x = new int [] {6, 7, 8};
        }
    }

    private PrivateFieldArray foo(){
        return new PrivateFieldArray();
    }
}
