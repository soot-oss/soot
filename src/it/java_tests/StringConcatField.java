public class StringConcatField {

    public static final String s = 1 + 2 + "jennifer"+ 3 + 4;
    public static String s1 = 1 + 2 + "jennifer"+ 3 + 4;

    public static void main(String [] args){
        System.out.println(s);
        System.out.println(s1);
        System.out.println(1+2+"jennifer"+3+4);
    }
}
