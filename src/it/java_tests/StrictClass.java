public strictfp class StrictClass {

    private int x;
    protected int y;

    public static int j;
    volatile int k;

    public StrictClass (){
        float f = 0.9F;
    }

    public static void main(String [] args){
        StrictClass s = new StrictClass();
    }
}
