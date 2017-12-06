import java.io.*;
public class BranchTests {

    public static void main (String [] args) {
        BranchTests bt = new BranchTests();
        bt.runContinues();
        bt.runBreaks();
    }

    public void runBreaks() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            StringBuffer word = new StringBuffer();
            while (true) {
                String in = (String)br.readLine();
                if (in.equals( "done")) break;
                word.append(in);
            }
            System.out.println(word);

            int i = 0;
            outer: while(i < 5){
                int j = 0;
                inner: while (j < 3) {
                    String in = (String)br.readLine();
                    if (in.equals("outer")) break outer;
                    if (in.equals("inner")) break inner;
                    System.out.println(j);
                    j++;
                }
                System.out.println(i);
                i++;
                
            }

            
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    
        
    }
    
    public void runContinues() {
        
    }
}
