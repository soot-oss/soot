public class PrivateAddAssign {
    private int x = 9;
    private int [] a = new int [] {3, 4, 5};
    
    class Inner {
        public void run(){
            PrivateAddAssign bar = new PrivateAddAssign();
            bar.x += 1;
            System.out.println("bar.x: "+bar.x);
            int i = 0;
            a[i++] += 1;
            System.out.println("a[0]: "+a[0]);
            System.out.println("a[1]: "+a[1]);
            System.out.println("a[2]: "+a[2]);
        }
    }

    public static void main(String [] args){
        PrivateAddAssign a = new PrivateAddAssign();
        a.run();
    }

    private void run(){
        Inner i = new Inner();
        i.run();
    }
}
