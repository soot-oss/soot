/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
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
 The reference version is: $JimpleVersion: 0.5 $

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

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/
 
package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;

public class LookupSwitchStmt extends Stmt
{
    StmtBox defaultTargetBox;
    ImmediateBox keyBox;
    List lookupValues;
    StmtBox[] targetBoxes;

    List stmtBoxes;
        
    LookupSwitchStmt(Immediate key, List lookupValues, List targets, Stmt defaultTarget)
    {
        this.keyBox = new ImmediateBox(key);
        this.defaultTargetBox = new StmtBox(defaultTarget);
        
        this.lookupValues = new ArrayList();
        this.lookupValues.addAll(lookupValues);
        
        this.targetBoxes = new StmtBox[targets.size()];
        
        for(int i = 0; i < targetBoxes.length; i++)
            targetBoxes[i] = new StmtBox((Stmt) targets.get(i));
        
        // Build up stmtBoxes
        {    
            stmtBoxes = new ArrayList();
            
            for(int i = 0; i < targetBoxes.length; i++)
                stmtBoxes.add(targetBoxes[i]);
            
            stmtBoxes.add(defaultTargetBox);
            stmtBoxes = Collections.unmodifiableList(stmtBoxes);
        }    
    }
    
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("lookupswitch(" + keyBox.getValue().toString() + ")\n");
        buffer.append("{\n");
        
        for(int i = 0; i < lookupValues.size(); i++)
        {
            buffer.append("case " + lookupValues.get(i) + ": goto ?;\n");
        }
        
        buffer.append("default: goto ?;\n");
        buffer.append("}");
        
        return buffer.toString();
    }
    
    public Stmt getDefaultTarget()
    {
        return (Stmt) defaultTargetBox.getUnit();
    }
    
    public void setDefaultTarget(Stmt defaultTarget)
    {
        defaultTargetBox.setUnit(defaultTarget);
    }
    
    public StmtBox getDefaultTargetBox()
    {
        return defaultTargetBox;
    }
    
    public Immediate getKey()
    {
        return (Immediate) keyBox.getValue();
    }    
    
    public void setKey(Immediate key)
    {
        keyBox.setValue(key);
    }
    
    public ImmediateBox getKeyBox()
    {
        return keyBox;
    }
    
    public void setLookupValues(List lookupValues)
    {
        this.lookupValues = new ArrayList();
        this.lookupValues.addAll(lookupValues);
    }
    
    public void setLookupValue(int index, int value)
    {
        this.lookupValues.set(index, new Integer(value));
    }
    
    public int getLookupValue(int index)
    {
        return ((Integer) lookupValues.get(index)).intValue();
    }
    
    public  List getLookupValues()
    {
        return Collections.unmodifiableList(lookupValues);
    }
    
    public int getTargetCount()
    {
        return targetBoxes.length;
    }
    
    public Stmt getTarget(int index)
    {
        return (Stmt) targetBoxes[index].getUnit();
    }
    
    public StmtBox getTargetBox(int index)
    {
        return targetBoxes[index];
    }
    
    public void setTarget(int index, Stmt target)
    {
        targetBoxes[index].setUnit(target);
    }
    
    public List getTargets()
    {
        List targets = new ArrayList();
        
        for(int i = 0; i < targetBoxes.length; i++)
            targets.add(targetBoxes[i].getUnit());
            
        return targets;
    }
    
    public void setTargets(Stmt[] targets)
    {
        for(int i = 0; i < targets.length; i++)
            targetBoxes[i].setUnit(targets[i]);
    }
    

    public List getDefBoxes()
    {
        return emptyList;
    }
    
    public List getUseBoxes()
    {
        List list = new ArrayList();
        
        list.add(keyBox);
        list.addAll(keyBox.getValue().getUseBoxes());
        
        return list;
    }    
    
    public List getStmtBoxes()
    {
        return stmtBoxes;
    }
    
    public void apply(Switch sw)
    {
        ((StmtSwitch) sw).caseLookupSwitchStmt(this);
    }
}









