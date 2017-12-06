import java.util.*;

public class FinalFieldTest {

    private final LinkedList attributes = new LinkedList(new MyAttributeColl());

    public static void main(String [] args){
        System.out.println("Hi");
        FinalFieldTest fft = new FinalFieldTest();
        fft.run();
    }

    public void run() {
        System.out.println(attributes.toString());
    }

    private class MyAttributeColl extends ArrayList {
    
        public String toString(){
            return "Jennifer";
        }
    }
}
