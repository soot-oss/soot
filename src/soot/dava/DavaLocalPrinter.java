package soot.dava;

import soot.tagkit.*;
import soot.*;
import java.util.*;
import soot.util.*;
import soot.jimple.*;
import soot.toolkits.graph.*;

public class DavaLocalPrinter implements LocalPrinter
{
    private static DavaLocalPrinter instance = new DavaLocalPrinter();
    private DavaLocalPrinter() {}
    
    public static DavaLocalPrinter v() { return instance; }
    
    /** Prints the given <code>JimpleBody</code> to the specified <code>PrintWriter</code>. */
    public void printLocalsInBody(Body body, java.io.PrintWriter out, boolean isPrecise)
    {
	if ((body instanceof DavaBody) == false)
	    throw new RuntimeException( "Only DavaBodies should use the DavaLocalPrinter");

	DavaBody davaBody = (DavaBody) body;
	
        // Print out local variables
        {
            Map typeToLocals = new DeterministicHashMap(body.getLocalCount() * 2 + 1, 0.7f);

	    Collection params = davaBody.get_ParamMap().values();
	    HashSet thisLocals = davaBody.get_ThisLocals();
	    

            // Collect locals
            {
                Iterator localIt = body.getLocals().iterator();

                while(localIt.hasNext())
                {
                    Local local = (Local) localIt.next();

		    if (params.contains( local) || thisLocals.contains( local))
			continue;

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

	    /*
	    if (davaBody.constructorUnit != null) {

		if (davaBody.constructorUnit instanceof InvokeStmt) {
		    InvokeExpr ie = (InvokeExpr) ((InvokeStmt) davaBody.constructorUnit).getInvokeExpr();
		    
		    if (ie instanceof SpecialInvokeExpr) {
			SpecialInvokeExpr sie = (SpecialInvokeExpr) ie;
			
			{
			    InstanceInvokeExpr iie = (InstanceInvokeExpr) ((InvokeStmt) davaBody.constructorUnit).getInvokeExpr();
			    
			    
			    if (((DavaMethod) davaBody.getMethod()).getClassName().equals( iie.getMethod().getDeclaringClass().toString()))
				out.print("        this(");
			    else
				out.print("        super(");

			    Iterator ait = iie.getArgs().iterator();
			    while (ait.hasNext()) {
				out.print(ait.next().toString());
				
				if (ait.hasNext())
				    out.print( ", ");
			    }
			    
			    out.print( ");\n\n");
			}
		    }
		}
	    }
	    */

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
