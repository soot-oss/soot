public class Hello extends java.lang.Object
{

    public void <init>()
    {
        word r0;

        r0 := @this;
        load.r r0;
        specialinvoke <init>;
        return;
    }

    public static void main(java.lang.String[])
    {
        word r0;

        r0 := @parameter0;
        staticget java.lang.System.out;
        push "Hello, World!";
        virtualinvoke println;
        return;
    }
}
