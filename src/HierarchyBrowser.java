import ca.mcgill.sable.soot.*;

import java.util.*;
import java.io.*;

public class HierarchyBrowser
{
    public static void usage()
    {
        System.out.println("Usage: java HierarchyBrowser <command> <class> [context-class1] [context-class2] ...");
        System.out.println("    where command is one of: [d]subc[_i], [d]subi[_i], [d]imp\n");
        System.out.println("OR:    java HierarchyBrowser interactive <context-class1> [context-class2] ...");
        System.out.println("\nExample: java HierarchyBrowser subi ca.mcgill.sable.soot.Unit ca.mcgill.sable.soot.Main");
    }

    public static void doCommand(Hierarchy h, String command, String className)
    {
        List target = null;
        SootClass c = Scene.v().getSootClass(className);

        if (command.equals("subc_i"))
            target = h.getSubclassesOfIncluding(c);
        else if (command.equals("subc"))
            target = h.getSubclassesOf(c);
        else if (command.equals("dsubc_i"))
            target = h.getDirectSubclassesOfIncluding(c);
        else if (command.equals("dsubc"))
            target = h.getDirectSubclassesOf(c);
        else if (command.equals("supc"))
            target = h.getSuperclassesOf(c);
        else if (command.equals("subi_i"))
            target = h.getSubinterfacesOfIncluding(c);
        else if (command.equals("subi"))
            target = h.getSubinterfacesOf(c);
        else if (command.equals("dsubi_i"))
            target = h.getDirectSubinterfacesOfIncluding(c);
        else if (command.equals("dsubi"))
            target = h.getDirectSubinterfacesOf(c);
        else if (command.equals("dimp"))
            target = h.getDirectImplementersOf(c);
        else if (command.equals("imp"))
            target = h.getImplementersOf(c);

        ArrayList al = new ArrayList(); 
        if (target != null) 
            al.addAll(target);
        System.out.println(command + " on "+className+" gives: "+al);
    }

    public static void main(String[] argv)
    {
        if (argv.length < 2)
            { usage(); System.exit(1); }

        String command = argv[0];
        String className = argv[1];

        for (int i = 1; i < argv.length; i++)
        {
            System.out.println("Resolving "+argv[i]+"...");
            Scene.v().loadClassAndSupport(argv[i]);
        }

        System.out.print("Building hierarchy... ");
        Hierarchy h = new Hierarchy(Scene.v());
        System.out.println("done!");

        if (command.equals("interactive"))
        {
            while(true)
            {
                try
                {
                    InputStreamReader isr=new InputStreamReader(System.in);
                    BufferedReader stdin=new BufferedReader(isr);
                
                    System.out.print("[Command]: ");
                    command = stdin.readLine();
                    if (command.equals("quit"))
                        break;
                    
                    className = command.substring(command.indexOf(' ')+1, command.length());
                    command = command.substring(0, command.indexOf(' '));

                    doCommand(h, command, className);
                }
                catch (IOException e) { }
            }
        }
        else
            doCommand(h, command, className);
    }
}
