public class EmptyAfterMethod {

    public static void main(String [] args) {
        EmptyAfterMethod eam = new EmptyAfterMethod();
        ;; 
        eam.init();
    }

    protected void init(){};
}
