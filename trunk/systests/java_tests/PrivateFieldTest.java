public class PrivateFieldTest {
    private int a = 9;
    private int b = 9;
    private int c = 9;
    private int d = 9;
    private int e = 9;
    private int f = 9;
    private int g = 9;
    private int h = 9;
    private int i = 9;
    private int j = 9;
    private int k = 9;
    private int l = 9;
    private int m = 9;
    private int n = 9;
    private int o = 9;
    private int p = 9;
    private int q = 9;
    private int r = 9;
    private int s = 9;
    
    public static void main(String [] args){
        PrivateFieldTest u = new PrivateFieldTest();
        u.run();
    }

    public void run(){
        Inner i = new Inner();
        i.run();
    }
    
    public class Inner {
        
        public void run(){
            a += 9;
            b += 0;
            c += 5;
            d -= 8;
            e += 4;
            f *= 3;
            g -= 2;
            h += 44;
            i += h;
            j -= g;
            h++;
            k--;
            l++;
            System.out.println(l);
        }
    }
}
