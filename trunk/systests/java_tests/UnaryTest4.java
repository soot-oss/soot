public class UnaryTest4 {
    
    private int [] y = new int [] {2,3,4,2,3};
    private int x = 9;
    
    public static void main (String [] args){
        UnaryTest4 u = new UnaryTest4();
        u.run();
    }

    public void run(){
        Inner i = new Inner();
        i.run();
    }

    public class Inner {
        public void run(){
            int i = 0;
            y[i++]++;

            int j = 0;
            System.out.println(y[j++] += 1);

            System.out.println(y[0]);
            
            
            System.out.println(x++);
            x--;
            System.out.println(++x);
        }
    }
}
