package ca.mcgill.sable.soot.tools.counter;

import java.io.*;
import java.text.*;

public class FormatCounters
{
    static long staticInvokeCount;
    static long specialInvokeCount;
    static long virtualInvokeCount;
    static long interfaceInvokeCount;

    public static void main(String[] args)
    {   
        if(args.length == 0)
        {
            System.out.println("Syntax: java FormatCountersClass <executionTime>");
        }
     
        // Read in execution time   
            double duration = new Double(args[0]).doubleValue();
        
            System.out.println("Adjusting counters for " + duration + "s of execution time");
            duration = duration * 1000.0;
                
            
        // Read in the counter info
        {
            FileReader in = null;
            
            try {
                in = new FileReader("counters.raw");
                
                StreamTokenizer inTokens = new StreamTokenizer(in);
        
                inTokens.resetSyntax();
                inTokens.whitespaceChars(' ', ' ');
                inTokens.whitespaceChars('\t', '\t');
                inTokens.whitespaceChars('\r', '\r');
                inTokens.whitespaceChars('\n', '\n');
                inTokens.wordChars(33, 127);
            
                        
                for(;;)
                {    
                    String name, value;
                 
                    // Get name of variable
                    {
                        inTokens.nextToken();
                        
                        if(inTokens.ttype == StreamTokenizer.TT_EOF)
                            break;
                        
                         name = inTokens.sval;
                    }
                    
                    // Get '='
                    {    
                        inTokens.nextToken();                        
                    }
                    
                    // Get value   
                    {
                        inTokens.nextToken();
                        value = inTokens.sval;
                    }
                    
                    if(name.equals("staticInvokeCount"))
                        staticInvokeCount = new Long(value).longValue();
                    else if(name.equals("specialInvokeCount"))
                        specialInvokeCount = new Long(value).longValue();
                    else if(name.equals("virtualInvokeCount"))
                        virtualInvokeCount = new Long(value).longValue();
                    else if(name.equals("interfaceInvokeCount"))
                        interfaceInvokeCount = new Long(value).longValue();
                }


                in.close();
                
            }
            catch (IOException e)
            {
                System.out.println("Cannot readinput file counters.raw");
                System.exit(1);
            }
            
        }
        
        // Open output files
            FileOutputStream streamOut = null;
            PrintWriter out = null;
            
            try {
                streamOut = new FileOutputStream("counters.txt");
                out = new PrintWriter(streamOut);
            }
            catch (IOException e)
            {
                System.out.println("Cannot open output file counters.txt");
            }
            
        
        // Write out profiling information
        {
            long totalInvokeCount = staticInvokeCount + specialInvokeCount + virtualInvokeCount + interfaceInvokeCount;
            DecimalFormat percFormat = new DecimalFormat(" (#0.0%)");
            DecimalFormat intFormat = new DecimalFormat("####0.0/ms");
                    
            out.println("   staticInvokeCount: " + intFormat.format(staticInvokeCount / duration) + 
                percFormat.format(staticInvokeCount * 1.0 / totalInvokeCount));

            out.println("  specialInvokeCount: " + intFormat.format(specialInvokeCount / duration) + 
                percFormat.format(specialInvokeCount * 1.0 / totalInvokeCount));

            out.println("  virtualInvokeCount: " + intFormat.format(virtualInvokeCount / duration) + 
                percFormat.format(virtualInvokeCount * 1.0 / totalInvokeCount));

            out.println("interfaceInvokeCount: " + intFormat.format(interfaceInvokeCount / duration) + 
                percFormat.format(interfaceInvokeCount * 1.0 / totalInvokeCount));
        }
        
        // Close output file
            try     
            {   
                out.flush();
                streamOut.close();
            }
            catch (IOException e)
            {
                System.out.println("Cannot output file counters.txt");
            }

    }
}
