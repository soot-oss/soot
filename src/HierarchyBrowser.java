import ca.mcgill.sable.soot.*;

import java.util.*;

public class HierarchyBrowser
{
    public static void usage()
    {
        System.out.println("java HierarchyBrowser <command> <class> [context-class1] [context-class2] ...");
        System.out.println("    where command is one of: subc, supc, subi, dirimp, imp\n");
    }

    public static void main(String[] argv)
    {
        if (argv.length < 2)
            { usage(); System.exit(1); }

        String command = argv[0];
        String className = argv[1];

        for (int i = 1; i < argv.length; i++)
            Scene.v().loadClassAndSupport(argv[i]);
        Hierarchy h = new Hierarchy(Scene.v());

        SootClass c = Scene.v().getClass(className);

        List target = null;
        if (command.equals("subc"))
            target = h.getSubclassesOfIncluding(c);
        else if (command.equals("supc"))
            target = h.getSuperclassesOf(c);
        else if (command.equals("subi"))
            target = h.getSubinterfacesOfIncluding(c);
        else if (command.equals("dirimp"))
            target = h.getDirectImplementersOf(c);
        else if (command.equals("imp"))
            target = h.getImplementersOf(c);

        ArrayList al = new ArrayList(); al.addAll(target);
        System.out.println(argv[0] + " on "+argv[1]+" gives: "+al);
    }
}
