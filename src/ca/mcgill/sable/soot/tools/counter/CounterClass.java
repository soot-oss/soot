package ca.mcgill.sable.soot.tools.counter;

import java.io.*;
import java.text.*;

public class CounterClass
{
    public static long virtualInvokeCount;
    public static long interfaceInvokeCount;
    public static long staticInvokeCount;
    public static long specialInvokeCount;
    
    public static void startProfiling()
    {
    }
    
    public static void stopProfiling()
    {
        // Open file
            FileOutputStream streamOut = null;
            PrintWriter out = null;
            
            try {
                streamOut = new FileOutputStream("counters.raw");
                out = new PrintWriter(streamOut);
            }
            catch (IOException e)
            {
                System.out.println("Cannot output file counters.raw");
            }
        
        // Write out profiling information
        {
            out.println("   staticInvokeCount = " + staticInvokeCount);
            out.println("  specialInvokeCount = " + specialInvokeCount);
            out.println("  virtualInvokeCount = " + virtualInvokeCount);
            out.println("interfaceInvokeCount = " + interfaceInvokeCount);
        }
        
        // Close file
            try     
            {   
                out.flush();
                streamOut.close();
            }
            catch (IOException e)
            {
                System.out.println("Cannot output file counters.raw");
            }

    }
}
