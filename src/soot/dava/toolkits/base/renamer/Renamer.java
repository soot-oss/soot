/* Soot - a J*va Optimization Framework
 * Copyright (C) 2006 Nomair A. Naeem
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.dava.toolkits.base.renamer;

//import soot.dava.toolkits.base.AST.analysis.*;
//import soot.*;
//import soot.jimple.*;
//import java.util.*;
//import soot.util.*;
//import soot.dava.*;
//import soot.grimp.*;
//import soot.grimp.internal.*;
//import soot.dava.internal.javaRep.*;
//import soot.dava.internal.asg.*;
//import soot.jimple.internal.*;
//import soot.dava.internal.AST.*;


public class Renamer{
    heuristicSet heuristics;
    
    public Renamer(heuristicSet info){
	heuristics=info;
    }

    public void rename(){
	System.out.println("Renaming started");

	//check for method Argument
	methodArgument();
    }
    

    public void methodArgument(){
	heuristics.getLocalsIterator();

    }



}