import java.io.*;
import java.util.*;

public class FileReaderTest {

    public static void main(String [] args) {
    
        for (int i = 0; i < args.length; i++) {
        
            try {
                BufferedReader br = new BufferedReader(new FileReader(args[i]));

                StringTokenizer st = new StringTokenizer(br.readLine());
                
                myMethod(StringToInt(st.nextToken()), StringToInt(st.nextToken()), st.nextToken());
            
                String line;
                while ((line = br.readLine()) != "done") {
                    StringTokenizer st2 = new StringTokenizer(line);
                    myOtherMethod(StringToInt(st2.nextToken()), StringToInt(st2.nextToken()));
                }
                
            }
            catch(IOException e) {
                System.out.println("Error: "+e);
            }
                
        }
    }

    private static void myOtherMethod(int i, int j) {
        System.out.println(i+j);
    }
    
    private static void myMethod(int i, int j, String s) {
        System.out.println(i+j+s);
    }
    
    private static int StringToInt(String s) {
    
        return (new Integer(s)).intValue();
    }
}
