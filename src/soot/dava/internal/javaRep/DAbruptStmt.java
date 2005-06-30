/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
 * Copyright (C) 2005 Nomair A. Naeem
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

package soot.dava.internal.javaRep;

import java.util.*;
import soot.jimple.internal.*;
import soot.dava.internal.SET.*;
import soot.*;

public class DAbruptStmt extends AbstractStmt
{
    private String command;
    private SETNodeLabel label;


    public  boolean surpressDestinationLabel;

    public DAbruptStmt( String command, SETNodeLabel label)
    {
	this.command = command;
	this.label = label;

	label.set_Name();
	surpressDestinationLabel = false;
    }

    public boolean fallsThrough()
    {
	return false;
    }

    public boolean branches()
    {
	return false;
    }

    public Object clone()
    {
	return new DAbruptStmt( command, label);
    }

    public String toString()
    {
	StringBuffer b = new StringBuffer();

	b.append( command);

	if ((surpressDestinationLabel == false) && (label.toString() != null)) {
	    b.append( " ");
	    b.append( label.toString());
	}

	return b.toString();
    }
    
    public void toString(UnitPrinter up) {
        up.literal(command);
        if ((surpressDestinationLabel == false) && (label.toString() != null)) {
            up.literal( " ");
            up.literal( label.toString());
        }
    }

    public boolean is_Continue()
    {
	return command.equals( "continue");
    }


    public boolean is_Break(){
	return command.equals("break");
    }

    /*
      Nomair A. Naeem 20-FEB-2005
      getter and setter methods for the label are needed for the aggregators of the AST conditionals
    */
    public void setLabel(SETNodeLabel label){
	this.label=label;
    }

    public SETNodeLabel getLabel(){
	return label;
    }
}
