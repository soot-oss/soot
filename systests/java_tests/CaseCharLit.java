public class CaseCharLit {
    public static void main(String[] args) {
        char x = 'p';
        switch (x) {
            case 'e':
                {
                    CaseCharLit.print("vowel");
                    break;
                }
            case 'f':
                {
                    CaseCharLit.print("conson");
                    break;
                }
            case 'g':
                {
                    CaseCharLit.print("conson");
                    break;
                }
            case 'h':
                {
                    CaseCharLit.print("conson");
                    break;
                }
            case 'i':
                {
                    CaseCharLit.print("vowel");
                    break;
                }
        }
    }
    
    public static void print(String s) { System.out.println(s); }
    
}
