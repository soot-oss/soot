package soot.jimple.parser;

import soot.jimple.parser.parser.*;
import soot.jimple.parser.lexer.*;
import soot.jimple.parser.node.*;
import soot.jimple.parser.analysis.*;
import java.io.*;

public class Parse 
{
    private static final String EXT = ".jimple";
    private static boolean debug = false;
    private static boolean verbose = false;

    private static final String USAGE = "usage: java Parse [options] " +
        "jimple_file [jimple_file ...]";

    public static void main(String args[]) 
    {
        InputStream inFile;
        
        // check arguments
        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(0);
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-")) {
                arg = arg.substring(1);
                if (arg.equals("d"))
                    debug = true;
                else if (arg.equals("v"))
                    verbose = true;
            }
            else {
                try {
                    if (verbose)
                        System.out.println(" ... looking for " + arg);
                    inFile = new FileInputStream(arg);
                } catch (FileNotFoundException e) {
                    if (arg.endsWith(EXT)) {
                        System.out.println(" *** can't find " + arg);
                        continue;
                    }
                    arg = arg + EXT;
                    try {
                        if (verbose)
                            System.out.println(" ... looking for " + arg);
                        inFile = new BufferedInputStream(new FileInputStream(arg));
                    } catch (FileNotFoundException ee) {
                        System.out.println(" *** can't find " + arg);
                        continue;
                    }
                }
                try {
                    Parser p =
                        new Parser(
                            new Lexer(
                                new PushbackReader(
                                    new InputStreamReader(inFile), 1024)));

                    Start tree = p.parse();
                    
                    tree.apply(new Walker());
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    } // main
} // Parse





