public class LabeledStmtTest {

    public static void main(String [] args){
    
        int i = 0;
        int j = 0;
        outer:while (i < 100) {
        
            inner: while (j < 100) {
                j = j + 1;
                if (j * i < 50) continue;
                System.out.println(j);
                if (j == 75) break inner;
            }
             
            i = i + 1;

            if (i == 20) break outer;
        }
    }
}
