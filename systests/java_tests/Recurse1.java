public class Recurse1 {

    public static void main(String [] args){
        Recurse1 r = new Recurse1();
        System.out.println(r.fact(9));
    }

    public int fact(int n){
        if (n > 1) {
            return n * fact(n-1);
        }
        else {
            return 1;
        }
    }
}
