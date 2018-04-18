public class CondTest {

    public static void main(String [] args){
    
        CondTest c = new CondTest();
        c.run(3);
    }

    public void run(int x){
        if (x < 10) {
            System.out.println(x+" is less then 10");
        }
        else if (x > 4) {
            System.out.println(x+" is greater then 4");
        }
        else if (x > 0) {
            System.out.println(x+" is greater then 0");
        }
        else if (x < 5) {
            System.out.println(x+" is less then 5");
        }
        else {
        
        }
        
    }
}
