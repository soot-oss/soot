public class FieldArrayInit {

    int [] a = {1 , 2 ,2};

    public static void main(String [] args) {
        FieldArrayInit fai = new FieldArrayInit();
        fai.go();
    }

    private void go(){
        a = new int [] { 1, 2, 3};
    }
}
