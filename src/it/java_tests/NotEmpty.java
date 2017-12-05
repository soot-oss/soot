import java.util.*;
public class NotEmpty {
    
    public static void main (String [] args) {
        NotEmpty ne = new NotEmpty();
        ne.run();
    }
    
    private void run(){
        Stack s = new Stack();

        s.push("J");
        s.push("E");
        s.push("N");
        s.push("N");
        s.push("N");
        s.push("N");
        s.push("N");
        s.push("N");

        while (!s.isEmpty()) {
            System.out.println(s.pop());
        }
    }    
}
