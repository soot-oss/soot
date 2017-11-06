public class Faint {
    public static void main(String [] args){
        Faint f = new Faint();
        f.doit();
    }
    
    public void doit(){
        int i = 0;
        int fc = 0;
        for (i = 0; i < 10; i++ ){
            fc++;
            System.out.println(fc);
        }
        i = 5;
        System.out.println(fc--);
    }
}
