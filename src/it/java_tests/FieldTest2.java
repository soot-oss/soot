public class FieldTest2 {

    private int x = 9;
    public static int SMILE = 0;
    public String hi = "HI";
    
    public static void main(String [] args) {
    
        FieldTest2 ft = new FieldTest2();
        ft.run();
    }

    private void run() {
        System.out.println(hi);
        x = 10; 
    }
}
