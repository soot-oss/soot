/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Phong Co
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



package soot.baf.toolkits.base;

import java.util.*;

import soot.tagkit.*;
import soot.*;
import soot.baf.*;
import soot.tagkit.*;

public class  CodeAttributeGenerator extends BodyTransformer
{
    private static CodeAttributeGenerator instance = new CodeAttributeGenerator();
    private CodeAttributeGenerator() {}

    public static CodeAttributeGenerator v() { return instance; }

    static boolean debug = soot.Main.isInDebugMode;


    public  void internalTransform(Body b, String phaseName, Map options) 
    {
        BafBody body = (BafBody) b;
        
        if (soot.Main.isVerbose) 
            System.out.println("[" + body.getMethod().getName() + "] Aggregating Unit Tags...");
	
	List tagAggregators = new LinkedList();
	tagAggregators.add(new ArrayCheckTagAggregator());
	
        Iterator unitIt = body.getUnits().iterator();
        while (unitIt.hasNext()) 
        {
            Unit unit = (Unit) unitIt.next();
	    List l = unit.getTags();
	    
	    Iterator tagIt = l.iterator();
	    while(tagIt.hasNext()) {
		Tag t = (Tag) tagIt.next();		

		Iterator it = tagAggregators.iterator();
		while(it.hasNext()) {
		    TagAggregator ta = (TagAggregator) it.next();
		    ta.aggregateTag(t, unit);
		}
	    }         
        }        
	
	Iterator it = tagAggregators.iterator();
	while(it.hasNext()) {
	    TagAggregator ta = (TagAggregator) it.next();
	    Attribute attr = (Attribute) ta.produceAggregateTag();
	    if(attr != null)
		b.addTag(attr);
	}	
    }
}
    
