public class SimpleThis {

    public static void main (String [] args) {
        SimpleThis st = new SimpleThis();
        st.run();
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            this.go();
        }
    }

    public void go(){
        System.out.println("going");
    }
}
