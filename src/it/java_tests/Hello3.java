public class Hello3
{  
    public static void main(String args[])
    {
        new Hello3().foo(1);
    }
    int foo(final int i)
    {
        class LocalClass2
        {
            int run () 
            {
                return i;
          
            }
        };
        class LocalClass 
        {
            int run () 
            {
                return new LocalClass2().run();
            }                       
        };
        return new LocalClass().run();
    }
}

