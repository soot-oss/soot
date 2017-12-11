public class AssertErrorMsg {

    public static void main(String [] args){
        int x = 10;
        int y = 11;
        assert x == 10 : x > y;
    }
}
