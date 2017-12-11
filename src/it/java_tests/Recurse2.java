public class Recurse2 {

    public static void main(String [] args){
        Recurse2 r = new Recurse2();
        System.out.println(r.fact(9));
    }

    public int fact(int n){
        if (n > 1) {
            return n * next(n);
        }
        else {
            return 1;
        }
    }

    public int next(int n){
        return fact(n-1);
    }
}
