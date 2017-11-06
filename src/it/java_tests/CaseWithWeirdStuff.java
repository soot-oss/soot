public class CaseWithWeirdStuff {
    public static void main (String [] args) {
        int i = 4;
        long j = 10L;
        
        switch(i) {
            case -3: System.out.println("");
            case ~1024: System.out.println("");
            case 5%2: System.out.println("");
            case 7: System.out.println("");
            case 100<<8: System.out.println("");
            case 1000>>4: System.out.println("");
          
        }
    }
}
