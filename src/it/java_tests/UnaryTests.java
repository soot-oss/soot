public class UnaryTests {

    
    public static void main (String [] args) {
    
        UnaryTests ut = new UnaryTests();
        ut.solve();
    }

    void solve(){
    
        long l = 10;
        
        
        int i = 1;
        
        l >>= 1;

        if (l != 0) {
        }

        i++;

        
        for (i = 1; (l >>= 1) != 0; i++);
    }
    
}
