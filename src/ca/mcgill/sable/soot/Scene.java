/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Soot, a Java(TM) classfile optimization framework.                *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:

 - Modified on March 27, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Changed the way classes are retrieved and loaded in.  
 
 - Modified on November 21, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Changed the default resolution state of new classes.
   
 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot;

import ca.mcgill.sable.soot.jimple.toolkit.invoke.*;
import ca.mcgill.sable.util.*;
import java.util.*;

public class Scene extends AbstractHost
{
    private static Scene constant = new Scene();
    
    Chain classes = new HashChain();
    Chain applicationClasses = new HashChain();
    Chain libraryClasses = new HashChain();
    Chain contextClasses = new HashChain();
    Chain phantomClasses = new HashChain();
    
    Map nameToClass = new HashMap();
    Map methodSignatureToMethod = new HashMap();
    Map fieldSignatureToField = new HashMap();

    Hierarchy activeHierarchy;
    InvokeGraph activeInvokeGraph;
    boolean allowsPhantomRefs = false;

    public static Scene v()
    {
        return constant;
    }
    
    private Scene()
    {
    }

    private int stateCount;
    int getState() { return this.stateCount; }

    public void addClass(SootClass c) throws AlreadyManagedException, DuplicateNameException
    {
        if(c.isInScene())
            throw new AlreadyManagedException(c.getName());

        if(containsClass(c.getName()))
            throw new DuplicateNameException(c.getName());

        classes.add(c);
        nameToClass.put(c.getName(), c);
        c.isInScene = true;
        c.scene = this;
        this.stateCount++;
    }

    public void removeClass(SootClass c)
    {
        if(!c.isInScene())
            throw new RuntimeException();

        classes.remove(c);
        nameToClass.remove(c.getName());
        c.isInScene = false;
        this.stateCount++;
    }

    public boolean containsClass(String className)
    {
        return nameToClass.containsKey(className);
    }

    public boolean containsField(String fieldSignature)
    {
        return fieldSignatureToField.containsKey(fieldSignature);
    }
    
    public boolean containsMethod(String methodSignature)
    {
        return methodSignatureToMethod.containsKey(methodSignature);
    }

    public SootField getField(String fieldSignature)
    {
        SootField f = (SootField) fieldSignatureToField.get(fieldSignature);
        if (f != null)
            return f;
        throw new RuntimeException("tried to get nonexistent field!");
    }

    public SootMethod getMethod(String methodSignature)
    {
        SootMethod m = (SootMethod) methodSignatureToMethod.get(methodSignature);
        if (m != null)
            return m;
        throw new RuntimeException("tried to get nonexistent method!");
    }

    /** 
     * Loads the given class and all of the required support classes.  Returns the first class.
     */
     
    public SootClass loadClassAndSupport(String className) throws ClassFileNotFoundException,
                                             CorruptClassFileException,
                                             DuplicateNameException
    {   
        /*
        if(Main.isProfilingOptimization)
            Main.resolveTimer.start();
        */
        
        Scene.v().setPhantomRefs(true);
        SootClass toReturn = ca.mcgill.sable.soot.coffi.Util.resolveClassAndSupportClasses(className, this);
        Scene.v().setPhantomRefs(false);

        return toReturn;
        
        /*
        if(Main.isProfilingOptimization)
            Main.resolveTimer.end(); */
    }
    
    /**
     * Returns the SootClass with the given className.  
     */

    public SootClass getSootClass(String className) throws ClassFileNotFoundException
    {   
        SootClass toReturn = (SootClass) nameToClass.get(className);
        
        if(toReturn == null)
        {
            if(Scene.v().allowsPhantomRefs())
            {
                SootClass c = new SootClass(className);
                c.setPhantom(true);
                addClass(c);
                return c;
            }
            else
                throw new ClassFileNotFoundException();
        }
        else
            return toReturn;
    }

    /**
     * Returns an backed chain of the classes in this manager.
     */
     
    public Chain getClasses()
    {
        return classes;
    }

    /* The four following chains are mutually disjoint. */

    /**
     * Returns a chain of the application classes in this scene.
     * These classes are the ones which can be freely analysed & modified.
     */
    public Chain getApplicationClasses()
    {
        return applicationClasses;
    }

    /**
     * Returns a chain of the library classes in this scene.
     * These classes can be analysed but not modified.
     */
    public Chain getLibraryClasses()
    {
        return libraryClasses;
    }

    /**
     * Returns a chain of the context classes in this scene.
     * These classes may not be analysed, typically for speed reasons.
     */
    public Chain getContextClasses()
    {
        return contextClasses;
    }

    /**
     * Returns a chain of the signature classes in this scene.
     * These classes are referred to by other classes, but cannot be loaded.
     */
    public Chain getPhantomClasses()
    {
        return phantomClasses;
    }

    Chain getContainingChain(SootClass c)
    {
        if (c.isApplicationClass())
            return getApplicationClasses();
        else if (c.isLibraryClass())
            return getLibraryClasses();
        else if (c.isContextClass())
            return getContextClasses();
        else if (c.isPhantomClass())
            return getPhantomClasses();

        return null;
    }

    /**
        Retrieves the active hierarchy for this method.
     */

    public Hierarchy getActiveHierarchy() 
    {
        if(!hasActiveHierarchy())
            throw new RuntimeException("no active Hierarchy present for scene");
            
        return activeHierarchy;
    }
    
    /**
        Sets the active hierarchy for this method. 
     */
     
    public void setActiveHierarchy(Hierarchy hierarchy)
    {
        activeHierarchy = hierarchy;
    }

    public boolean hasActiveHierarchy()
    {
        return activeHierarchy != null;
    }
    
    public void releaseActiveHierarchy()
    {
        activeHierarchy = null;
    }

    /**
        Retrieves the active invokeGraph for this method.
     */

    public InvokeGraph getActiveInvokeGraph() 
    {
        if(!hasActiveInvokeGraph())
            throw new RuntimeException("no active InvokeGraph present for scene");
            
        return activeInvokeGraph;
    }
    
    /**
        Sets the active invokeGraph for this method. 
     */
     
    public void setActiveInvokeGraph(InvokeGraph invokeGraph)
    {
        activeInvokeGraph = invokeGraph;
    }

    public boolean hasActiveInvokeGraph()
    {
        return activeInvokeGraph != null;
    }
    
    public void releaseActiveInvokeGraph()
    {
        activeInvokeGraph = null;
    }
    
    public void setPhantomRefs(boolean value)
    {
        allowsPhantomRefs = value;
    }
    
    public boolean allowsPhantomRefs()
    {
        return allowsPhantomRefs;
    }
}










