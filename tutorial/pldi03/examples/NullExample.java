public class NullExample
{
    int foo(Object o, String p)
    {
        if (o == null)
        {
            return 2;
        }
        int i = 2;
        i += p.length();
        System.out.println(p);
        return 4;
    }
}
