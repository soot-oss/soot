import java.io.*;

public class TryCatch2 {

    int x;
    
    public static void main(String [] args) {
        TryCatch2 tc = new TryCatch2();
        tc.run();
        tc.x = 8;
    }

    private void run(){
        try {
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            while (true){
            String temp = (String)br.readLine();
                if (temp.equals("done")) break;
                System.out.println(temp);
            }
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
            //System.out.println("Error");
        }
        catch(Exception e2) {
            System.out.println(e2.getMessage());
        }
    }
}
