public class Reverse {

    public static void main (String [] args) {
        System.out.println(toHexString(34));
    }
    
    public static String toHexString(int i) {
        StringBuffer buf = new StringBuffer(8);
        do {
            buf.append(Character.forDigit(i & 0xF, 16));
            i >>>= 4;
        } while (i != 0);
        return buf.reverse().toString();
    }
}
