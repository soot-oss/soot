import java.io.*;

public class Debug
{
    private static String getStackTrace()
    {
	Throwable t = new Throwable();
	ByteArrayOutputStream os = new ByteArrayOutputStream();
	
	PrintStream ps = new PrintStream(os);
	t.printStackTrace(ps);
	return os.toString();
    }

    public static void assert(boolean condition, String message)    
    {
	if (!condition)
	    {
		System.out.println("Assert [" + message + "] fired at:");
		System.out.println(getStackTrace());
		System.exit(1);
	
	    }
    }
}





