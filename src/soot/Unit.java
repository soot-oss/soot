/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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


import soot.tagkit.*;
import soot.util.*;
import java.util.*;

/** A code fragment (eg Stmt or Inst), used within Body
 * classes.  Intermediate representations must use an implementation
 * of Unit for their code.  In general, a unit denotes
 * some sort of unit for execution.
 */
public interface Unit extends Switchable, Host
{
    /** Returns a list of Boxes containing Values used in this Unit. */
    public List getUseBoxes();

    /** Returns a list of Boxes containing Values defined in this Unit. */
    public List getDefBoxes();

    /** Returns a list of Boxes containing Units defined in this Unit; typically
     * branch targets. */
    public List getUnitBoxes();

    /** Returns a list of Boxes pointing to this Unit. */
    public List getBoxesPointingToThis();

    /** Returns a list of Boxes containing any Value either used or defined
     * in this Unit. */
    public List getUseAndDefBoxes();

    public Object clone();

    /** Returns true if execution after this statement may continue at the following statement.
     * GotoStmt will return false. */
    public boolean fallsThrough();
    /** Returns true if execution after this statement is not the following statement. */
    public boolean branches();        
    
    public String toBriefString();
    public String toBriefString(Map stmtToName, String indentation);
    public String toString(Map stmtToName, String indentation);

    public void redirectJumpsToThisTo(Unit newLocation);
}
