/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */



package soot.tagkit;


import soot.*;
import java.util.*;

/** The aggregator for LineNumberTable attribute. */
public class InnerClassTagAggregator extends SceneTransformer {
    
    public InnerClassTagAggregator( Singletons.Global g ) {}
    public static InnerClassTagAggregator v() { return G.v().soot_tagkit_InnerClassTagAggregator(); }

    
    public String aggregatedName()
    {
        return "InnerClasses";
    }

    public void internalTransform(String phaseName, Map options){
        Iterator it = Scene.v().getApplicationClasses().iterator();
        while (it.hasNext()){
            ArrayList list = new ArrayList();
            short length = 0;
            short numInnerClasses = 0;
            SootClass nextSc = (SootClass)it.next();
            Iterator tagsIt = nextSc.getTags().iterator();
            while (tagsIt.hasNext()){
                Tag t = (Tag)tagsIt.next();
                if (t instanceof InnerClassTag){
                    list.add(t);
                }
            }
            if (!list.isEmpty()){
                nextSc.addTag(new InnerClassAttribute(list)); 
            }
        }
    }
}
