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

import ca.mcgill.sable.util.*;
import java.util.*;

public class Scene
{
    private static Scene constant = new Scene();
    
    List classes = new ArrayList();
    
    Map nameToClass = new HashMap();
    Map methodSignatureToMethod = new HashMap();
    Map fieldSignatureToField = new HashMap();
    
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
        return (SootField) fieldSignatureToField.get(fieldSignature);
    }

    public SootMethod getMethod(String methodSignature)
    {
        return (SootMethod) methodSignatureToMethod.get(methodSignature);
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
        
        return ca.mcgill.sable.soot.coffi.Util.resolveClassAndSupportClasses(className, this);

        /*
        if(Main.isProfilingOptimization)
            Main.resolveTimer.end(); */
    }
    
    /**
     * Returns the SootClass with the given className.  
     */

    public SootClass getClass(String className) throws ClassFileNotFoundException
    {   
        SootClass toReturn = (SootClass) nameToClass.get(className);
        
        if(toReturn == null)
            throw new ClassFileNotFoundException();
        else
            return toReturn;
    }

    /**
     * Returns an unbacked list of the classes in this manager.
     */
     
    public List getClasses()
    {
        return Collections.unmodifiableList(classes);
    }
}
