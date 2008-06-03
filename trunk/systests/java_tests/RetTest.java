public class RetTest {

    public static void main(String [] args){
        RetTest r = new RetTest();
        int c = r.run(9);
    }

    public int run(int i){
        if (i == 8) {
            return 7;
        }
        else {
            return 8;
        }
    }

    public void go(int i){
        if (i == 9){
            System.out.println("smile");
            return;
        }
        else {
            return;
        }
    } 
}
