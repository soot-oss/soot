public class PrivateStringField {

    private String s = "h";

    public static void main(String [] args){
        PrivateStringField psf = new PrivateStringField();
        psf.run();
    }

    public void run(){
        new Object(){
            public void run(){
                System.out.println(s+="ello");
            }
        }.run();
        
    }
}
