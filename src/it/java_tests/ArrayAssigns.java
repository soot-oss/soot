public class ArrayAssigns {

    public static void main(String [] args){
        int [] arr = new int [] {1, 2, 3};
        int i = 0;
        
        D d = new D(9);
        arr[i++] += d.height;
        arr[i++] += d.height;
        //S s = new S(8);
        //D d = new D(9);

        //s.height += d.height;

    }
}
class S {
    int height;
    public S(int i){
        height = i;
    }
}
class D {
    int height;
    public D(int i){
        height = i;
    }
}
