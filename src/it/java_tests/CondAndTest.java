import java.util.*;

public class CondAndTest {

    private Vector charsSet = new Vector();
    private Vector charsProb = new Vector();
    
    public static void main(String [] args) {
    
        boolean x = true;
        boolean y = false;

        if (x && y) {
            System.out.println("Both True");
        }

        CondAndTest cdt = new CondAndTest();
        cdt.addChar('i', 0.9);
    }

    private boolean addChar(char c, double d) {
        boolean result = false;

        if (d >= 0) {
            result &= this.charsSet.add(new Character(c)) && this.charsProb.add(new Double(d));
        }

        return result;
    }
}
