package soot;

import soot.coffi.*;
import java.util.*;
import java.io.*;
import soot.util.*;


public class SootResolver 
{
    private Scene mScene;
    private Set markedClasses;
    private LinkedList classesToResolve;
    private boolean debug = false;

    //        SootClass toReturn = soot.coffi.Util.resolveClassAndSupportClasses(className, this);
    public SootResolver(Scene aScene)
    {
	mScene = aScene;
	markedClasses = new HashSet();
	classesToResolve = new LinkedList();
    }
   

    public  SootClass getResolvedClass(String className)
    {
        if(mScene.containsClass(className))
            return mScene.getSootClass(className);
            
        SootClass newClass = new SootClass(className);
        mScene.addClass(newClass);
        newClass.setContextClass();
        
	
	markedClasses.add(newClass);
        classesToResolve.addLast(newClass);
	
	return newClass;
    }



    public SootClass resolveClassAndSupportClasses(String className)
    {
	SootClass resolvedClass = getResolvedClass(className);
	
	while(!classesToResolve.isEmpty()) {
	    
	    InputStream is= null;	    
	    SootClass sc = (SootClass) classesToResolve.removeFirst();
	    className = sc.getName();
	    
	    try {
		is = SourceLocator.getInputStreamOf(className);
	    } catch(ClassNotFoundException e) {
		throw new RuntimeException("couldn't find type: " + className);
	    }
		
	    Set s = null;
	    if(is instanceof ClassInputStream) {
		if(debug)
		    System.err.println("resolving [from .class]: " + className );
		soot.coffi.Util.resolveFromClassFile(sc, this, mScene);
	    } else if(is instanceof JimpleInputStream) {
		if(debug)
		    System.err.println("resolving [from .jimple]: " + className );
		if(sc == null) throw new RuntimeException("sc is null!!");

		
		soot.jimple.parser.JimpleAST jimpAST = new soot.jimple.parser.JimpleAST((JimpleInputStream) is);		
		jimpAST.getSkeleton(sc, this);
		JimpleMethodSource mtdSrc = new JimpleMethodSource(jimpAST);

		Iterator mtdIt = sc.getMethods().iterator();
		while(mtdIt.hasNext()) {
		    SootMethod sm = (SootMethod) mtdIt.next();
		    sm.setSource(mtdSrc);
		}
		
		Iterator it = jimpAST.getCstPool().iterator();
		
		while(it.hasNext()) {
		    String nclass = (String) it.next();
		    assertResolvedClass(nclass);
//  		    if(debug)
//  			System.out.println(nclass);
		}
	    } 
	    else {
		throw new RuntimeException("This is Utterly Impossible: " + is);
	    }
            try
            {
                is.close();
            }
            catch (IOException e) { throw new RuntimeException("!?"); }
	}	
	
	return resolvedClass;
    }   
    public void assertResolvedClassForType(Type type)
    {
        if(type instanceof RefType)
            assertResolvedClass(((RefType) type).className);
        else if(type instanceof ArrayType)
            assertResolvedClassForType(((ArrayType) type).baseType);
    }
    
    public void assertResolvedClass(String className)
    {
        if(!mScene.containsClass(className))
        {
            SootClass newClass = new SootClass(className);
            mScene.addClass(newClass);
            newClass.setContextClass();
            
            markedClasses.add(newClass);
            classesToResolve.addLast(newClass);
        }
    }



}
