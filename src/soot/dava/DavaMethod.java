package soot.dava;

import soot.*;
import java.util.*;

public class DavaMethod extends SootMethod
{
    public static final String constructorName = "<init>";
    public static final String staticInitializerName = "<clinit>";

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
	setModifiers( other.getModifiers());
	setPhantom( other.isPhantom());
	setExceptions( other.getExceptions());
	if (other.hasActiveBody())
	    setActiveBody( other.getActiveBody());
	setSource( other.getSource());
    }

    public void setClassName( String className)
    {
	this.className = className;

	if (Main.getWithPackagedOutput()) {
	    int index = className.lastIndexOf( '.');
	    
	    if (index == (className.length() - 1))
		throw new RuntimeException( "Malformed class name for packaging: " + className);
	    
	    if (index != -1)
		this.className = className.substring( index + 1);
	}	
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

	if (name.equals( staticInitializerName)) 
	    return "static";

	else {

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
	    if (name.equals( constructorName))
		buffer.append( className);  
	    else {
		Type rt;
		if ((rt = this.getReturnType()) != null)
		    buffer.append( rt + " ");

		buffer.append(Scene.v().quotedNameOf( this.getName()));
	    }

	    buffer.append( "(");

	    // parameters

	    Iterator typeIt = this.getParameterTypes().iterator();
	    int count = 0;
	    while (typeIt.hasNext()) {
		Type t = (Type) typeIt.next();

		buffer.append( t);
		buffer.append( " ");

		if (hasActiveBody()) 
		    buffer.append( ((DavaBody) getActiveBody()).get_ParamMap().get( new Integer( count++)));

		else {
		    if (t ==BooleanType.v())
			buffer.append( "z" + count++);
		    else if (t == ByteType.v())
			buffer.append( "b" + count++);
		    else if (t == ShortType.v())
			buffer.append( "s" + count++);
		    else if (t == CharType.v())
			buffer.append( "c" + count++);
		    else if (t == IntType.v())
			buffer.append( "i" + count++);
		    else if (t == LongType.v())
			buffer.append( "l" + count++);
		    else if (t == DoubleType.v())
			buffer.append( "d" + count++);
		    else if (t == FloatType.v())
			buffer.append( "f" + count++);
		    else if (t == StmtAddressType.v())
			buffer.append( "a" + count++);
		    else if (t == ErroneousType.v())
			buffer.append( "e" + count++);
		    else if (t == NullType.v())
			buffer.append( "n" + count++);
		    else 
			buffer.append( "r" + count++);
		}

		if (typeIt.hasNext())
		    buffer.append( ", ");
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
    }
}
