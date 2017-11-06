public class FinallysAndReturns {

  static String note = "original";

    public static void main(String[] args) {
        if (m() != "hi") 
          System.out.println("call to m() bad");
        if (note != "finally") 
          System.out.println("note left from m() bad, note is " + note);
        if (m1() != "hi1") 
          System.out.println("call to m1() bad");
        if (note != "trying") 
          System.out.println("note left from m1() bad, note is " +note);
    }

    public static String m() {
        try {
            return "hi";
        } finally {
             note = "finally";
        }
    }

    public static String m1() {
        try { note = "trying";
        } finally {
            return "hi1";
        }
    }

}
