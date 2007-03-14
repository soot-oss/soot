/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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

/* Reference Version: $SootVersion: 1.2.5.dev.1 $ */





package soot.jimple.toolkits.thread.mhp.stmt;


import soot.tagkit.*;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.util.*;
import soot.toolkits.graph.*;

import java.util.*;

// *** USE AT YOUR OWN RISK ***
// May Happen in Parallel (MHP) analysis by Lin Li.
// This code should be treated as beta-quality code.
// It was written in 2003, but not incorporated into Soot until 2006.
// As such, it may contain incorrect assumptions about the usage
// of certain Soot classes.
// Some portions of this MHP analysis have been quality-checked, and are
// now used by the Transactions toolkit.
//
// -Richard L. Halpert, 2006-11-30


public class StartStmt extends JPegStmt

//public class JPegStmt implements CommunicationStmt
//public class JPegStmt extends AbstractStmt implements CommunicationStm 
//public class JPegStmt extends AbstractStm 

{
	
	public StartStmt(String obj, String ca, Unit un, UnitGraph ug, SootMethod sm)
	{
		this.object = obj;
		this.name = "start";
		this.caller = ca;
		this.unit = un;
		this.unitGraph = ug;
		this.sootMethod = sm;
	}
	
	
	
	
	
}
