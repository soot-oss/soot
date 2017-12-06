public class UnaryTest3 {
    
    //private int [] x = new int [] {2,3,4,2,3};
    private int x = 9;
    
    public static void main (String [] args){
        UnaryTest3 u = new UnaryTest3();
        u.run();
    }

    public void run(){
        Inner i = new Inner();
        i.run();
    }

    public class Inner {
        public void run(){
            //int i = 0;
            //x[i++]++;

            /*int j = 0;
            System.out.println(x[j++] += 1);

            System.out.println(x[0]);*/
            
            
            System.out.println(x++);
            x--;
            System.out.println(++x);
        }
    }
}
