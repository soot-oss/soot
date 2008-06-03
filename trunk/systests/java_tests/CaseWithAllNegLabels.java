public class CaseWithAllNegLabels { 
    public static void main(String [] args) {
        int j = -10;
        switch(j){
            case -9: j++;
            case -8: j++;
            case -7: j--;
            case -6: j--;
        }
    }
}
