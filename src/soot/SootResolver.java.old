/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrice Pominville
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot;

import soot.coffi.*;
import java.util.*;
import java.io.*;
import soot.util.*;
import soot.jimple.*;

/** Loads symbols for SootClasses from either class files or jimple files. */
public class SootResolver 
{
    private Set markedClasses = new HashSet();
    private LinkedList classesToResolve = new LinkedList();
    private boolean mIsResolving = false;



    /** Creates a new SootResolver. */
    public SootResolver()
    {
    }

    /** Returns a SootClass object for the given className. 
     * Creates a new context class if needed. */
    public SootClass getResolvedClass(String className)
    {
        if(Scene.v().containsClass(className))
            return Scene.v().getSootClass(className);

        SootClass newClass;
        if(mIsResolving) {
            newClass = new SootClass(className);
            Scene.v().addClass(newClass);
            newClass.setContextClass();
        
            markedClasses.add(newClass);
            classesToResolve.addLast(newClass);
        } else {
            newClass = resolveClassAndSupportClasses(className);
        }
        
        return newClass;
    }



    /** Resolves the given className and all dependent classes. */
    public SootClass resolveClassAndSupportClasses(String className)
    {
        mIsResolving = true;
        SootClass resolvedClass = getResolvedClass(className);
        
        while(!classesToResolve.isEmpty()) {
            
            InputStream is = null;
            SootClass sc = (SootClass) classesToResolve.removeFirst();
            className = sc.getName();
            
            try 
            {
                is = SourceLocator.getInputStreamOf(className);
            } catch(ClassNotFoundException e) 
            {
                if(!Scene.v().allowsPhantomRefs())
                    throw new RuntimeException("couldn't find type: " + className + " (is your soot-class-path set properly?)");
                else 
                {
                    System.out.println("Warning: " + className + " is a phantom class!");
                    sc.setPhantom(true);
                    continue;
                }
            }
                
            Set s = null;
            if(is instanceof ClassInputStream) {
                if(soot.Main.isVerbose)
                    System.err.println("resolving [from .class]: " + className );
                soot.coffi.Util.resolveFromClassFile(sc, this, Scene.v());
            } else if(is instanceof JimpleInputStream) {
                if(soot.Main.isVerbose)
                    System.err.println("resolving [from .jimple]: " + className );
                if(sc == null) throw new RuntimeException("sc is null!!");
                
                soot.jimple.parser.JimpleAST jimpAST = new soot.jimple.parser.JimpleAST((JimpleInputStream) is, this);                
                jimpAST.getSkeleton(sc);
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
                }
                
            } 
            else {
                throw new RuntimeException("could not resolve class: " + is+" (is your soot-class-path correct?)");
            }
            try
            {
                is.close();
            }
            catch (IOException e) { throw new RuntimeException("!?"); }
        }        
        
        mIsResolving = false;
        return resolvedClass;
    }

    /** Asserts that type is resolved. */
    public void assertResolvedClassForType(Type type)
    {
        if(type instanceof RefType)
            assertResolvedClass(((RefType) type).className);
        else if(type instanceof ArrayType)
            assertResolvedClassForType(((ArrayType) type).baseType);
    }
    
    /** Asserts that class is resolved. */
    public void assertResolvedClass(String className)
    {
        if(!Scene.v().containsClass(className))
        {
            SootClass newClass = new SootClass(className);
            Scene.v().addClass(newClass);
            newClass.setContextClass();
            
            markedClasses.add(newClass);
            classesToResolve.addLast(newClass);
        }
    }
}


