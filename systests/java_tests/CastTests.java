import java.util.*;
public class CastTests {
    private Vector myList = new Vector(27);
    private Vector myCharacterList = new Vector(27);

    public static void main(String [] args) {
        CastTests ct = new CastTests();
        ct.run('p', 0.9);
    }

    private boolean run(char y, double x) {
        
        boolean result = false;
       
        if (x >= 0.8) {
            result &= this.myCharacterList.add(new Character(y)) && this.myList.add(new Double(x));
        }
        else if (x == 0.3) {
            result = true;
        }
        else if (x < 0.4) {
            result = true;
        }
        return result;
    }
}
