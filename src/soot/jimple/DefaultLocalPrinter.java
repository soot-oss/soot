package soot.jimple;

import soot.tagkit.*;
import soot.*;
import java.util.*;
import soot.util.*;
import soot.jimple.*;
import soot.toolkits.graph.*;

public class DefaultLocalPrinter implements LocalPrinter
{
    private static DefaultLocalPrinter instance = new DefaultLocalPrinter();
    private DefaultLocalPrinter() {}
    
    public static DefaultLocalPrinter v() { return instance; }
    
    /** Prints the given <code>JimpleBody</code> to the specified <code>PrintWriter</code>. */
    public void printLocalsInBody(Body body, java.io.PrintWriter out, boolean isPrecise)
    {
        // Print out local variables
        {
            Map typeToLocals = new DeterministicHashMap(body.getLocalCount() * 2 + 1, 0.7f);

            // Collect locals
            {
                Iterator localIt = body.getLocals().iterator();

                while(localIt.hasNext())
                {
                    Local local = (Local) localIt.next();

                    List localList;
 
                    String typeName;
                    Type t = local.getType();

                    typeName = (isPrecise) ?  t.toString() :  t.toBriefString();

                    if(typeToLocals.containsKey(typeName))
                        localList = (List) typeToLocals.get(typeName);
                    else
                    {
                        localList = new ArrayList();
                        typeToLocals.put(typeName, localList);
                    }

                    localList.add(local);
                }
            }

            // Print locals
            {
                Iterator typeIt = typeToLocals.keySet().iterator();

                while(typeIt.hasNext())
                {
                    String type = (String) typeIt.next();

                    List localList = (List) typeToLocals.get(type);
                    Object[] locals = localList.toArray();
                    out.print("        "  + type + " ");
                    
                    for(int k = 0; k < locals.length; k++)
                    {
                        if(k != 0)
                            out.print(", ");

                        out.print(((Local) locals[k]).getName());
                    }

                    out.println(";");
                }
            }


            if(!typeToLocals.isEmpty())
                out.println();
        }
    }
}
