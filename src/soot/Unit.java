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

import soot.util.*;
import java.util.*;

/** The Unit interface describes a code fragment (Stmt or Inst), used within Body
 * classes.  Intermediate representations must use an implementation
 * of Unit for their code.
 */
public interface Unit extends Switchable, Directed
{
    public List getUseBoxes();
    public List getDefBoxes();
    public List getUnitBoxes();
    public List getBoxesPointingToThis();
    public List getUseAndDefBoxes();
    public Object clone();

    public boolean fallsThrough();        
    public boolean branches();        
    
    public String toBriefString();
    public String toBriefString(Map stmtToName, String indentation);
    public String toString(Map stmtToName, String indentation);

    public void redirectJumpsToThisTo(Unit newLocation);

}
