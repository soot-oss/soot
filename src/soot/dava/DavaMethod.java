package soot.dava;

import soot.*;
import soot.dava.*;
import java.util.*;

public class DavaMethod extends SootMethod
{
    private static String constructorName = "<init>";
    private static String staticInitializerName = "<clinit>";

    private String className = "";

    public DavaMethod(String name, List parameterTypes, Type returnType)
    {
	super( name, parameterTypes, returnType);
    }

    public void copy( SootMethod other)
    {
	setName( other.getName());
	setParameterTypes( other.getParameterTypes());
	setReturnType( other.getReturnType());
	setDeclared( other.isDeclared());
	setDeclaringClass( other.getDeclaringClass());
	setModifiers( other.getModifiers());
	setPhantom( other.isPhantom());
	setExceptions( other.getExceptions());
	setActiveBody( other.getActiveBody());
	setSource( other.getSource());
    }

    public void setClassName( String className)
    {
	this.className = className;
    }

    public String getClassName()
    {
	return className;
    }
    
    /**
     * Returns the declaration of this method, as used at the top of textual body representations 
     *  (before the {}'s containing the code for representation.)
     */
    public String getDeclaration()
    {
	String name = getName();

	if (name.equals( constructorName)) {

	    StringBuffer buffer = new StringBuffer();
	    
	    // modifiers
	    StringTokenizer st = new StringTokenizer(Modifier.toString(this.getModifiers()));
	    if (st.hasMoreTokens())
		buffer.append(st.nextToken());
	    
	    while(st.hasMoreTokens())
		buffer.append(" " + st.nextToken());
	    
	    if(buffer.length() != 0)
		buffer.append(" ");


	    // name
	    buffer.append( className + "(");            
	    
	    // parameters
	    Iterator typeIt = this.getParameterTypes().iterator();
	    
	    if(typeIt.hasNext())
		{
		    Type t = (Type) typeIt.next();
		    
		    buffer.append(t);
		    
		    Body body = getActiveBody();
		    int count = 0;
		    
		    if (body instanceof DavaBody)
			buffer.append( " " + ((DavaBody) body).get_ParamMap().get( new Integer( count++)));
		    
		    
		    while(typeIt.hasNext())
			{
			    buffer.append(", ");
			    t = (Type) typeIt.next();
			    
			    buffer.append(t);
			    
			    if (body instanceof DavaBody)
				buffer.append( " " + ((DavaBody) body).get_ParamMap().get( new Integer( count++)));
			    
			}
		}
	    
	    buffer.append(")");
	    
	    // Print exceptions
	    {
		Iterator exceptionIt = this.getExceptions().iterator();
		
		if(exceptionIt.hasNext())
		    {
			buffer.append(" throws "+((SootClass) exceptionIt.next()).getName() + " ");
			
			while(exceptionIt.hasNext())
			    {
				buffer.append(", " + ((SootClass) exceptionIt.next()).getName());
			    }
		    }
	    }
	    
	    return buffer.toString();
	    
	    
	}
	
	if (name.equals( staticInitializerName))
	    return "static";

	return super.getDeclaration();
    }
}
