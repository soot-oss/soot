public class FinallyRet {

    public static void main(String [] args){
        m();
    }
    
    public static String m() {
        try {
            return "hi";
        } finally {
            String note = "finally";
        }
    }
     
}
