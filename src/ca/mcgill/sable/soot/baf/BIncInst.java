/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Baf, a Java(TM) bytecode analyzer framework.                      *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Patrick Lam (plam@sable.mcgill.ca) are           *
 * Copyright (C) 1999 Patrick Lam.  All rights reserved.             *
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

 - Modified on February 3, 1999 by Patrick Lam (plam@sable.mcgill.ca) (*)
   Added changes in support of the Grimp intermediate
   representation (with aggregated-expressions).

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;

public class BIncInst extends AbstractInst implements IncInst
{
    ValueBox localBox;
    ValueBox defLocalBox;
    List useBoxes;
  Constant mConstant;
  List mDefBoxes;  
       
    BIncInst(Local local, Constant constant)
    {
      mConstant = constant;
      localBox = new BafLocalBox(local);
      useBoxes = new ArrayList();
      useBoxes.add(localBox);
      useBoxes = Collections.unmodifiableList(useBoxes);

      defLocalBox = new BafLocalBox(local);
      mDefBoxes = new ArrayList();
      mDefBoxes.add(defLocalBox);
      mDefBoxes = Collections.unmodifiableList(mDefBoxes);
      
    }

    public int getInCount()
    {
        return 0;
    }

    public Object clone() 
    {
      return new  BIncInst( getLocal(), getConstant());
    }

  public int getInMachineCount()
  {
    return 0;
  }
    
  public int getOutCount()
  {
    return 0;
  }

    public int getOutMachineCount()
    {
        return 0;
    }
    
   

  
  public Constant getConstant() 
  {
    return mConstant;
  }
  
  public void setConstant(Constant aConstant) 
  {
    mConstant = aConstant;
  }



  final String getName() { return "inc.i"; }
    final String getParameters(boolean isBrief, Map unitToName) 
    { return " "+ localBox.getValue().toString(); }
    
    public void apply(Switch sw)
    {
        ((InstSwitch) sw).caseIncInst(this);
    }   
 
    public void setLocal(Local l)
    {
        localBox.setValue(l);
    }   
    
    public Local getLocal()
    {
        return (Local) localBox.getValue();
    }

    public List getUseBoxes() 
    {
        return useBoxes;
    }
    
    public List getDefBoxes() 
    {
        return mDefBoxes;
    }

  
  protected String toString(boolean isBrief, Map unitToName, String indentation)
  {
    return indentation + "inc.i" + " " +getLocal() + " " + getConstant() ;
  }

    
}
