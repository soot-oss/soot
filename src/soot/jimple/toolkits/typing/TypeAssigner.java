/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2000 Etienne Gagnon.  All rights reserved.
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


package soot.jimple.toolkits.typing;

import soot.*;
import soot.options.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;
import java.util.*;

/**
 * This transformer assigns types to local variables.
 **/
public class TypeAssigner extends BodyTransformer
{
    public TypeAssigner( Singletons.Global g ) {}
    public static TypeAssigner v() { return G.v().soot_jimple_toolkits_typing_TypeAssigner(); }

  /** Assign types to local variables. **/
  protected void internalTransform(Body b, String phaseName, Map options)
  {
    if(b == null)
      {
	throw new NullPointerException();
      }

    Date start = new Date();
    
    if (Options.v().verbose())
      G.v().out.println("[TypeAssigner] typing system started on "+start);

    TypeResolver.resolve((JimpleBody)b, Scene.v());

    Date finish = new Date();
    if (Options.v().verbose())
      {
	long runtime = finish.getTime()-start.getTime();
	long mins = runtime/60000;
	long secs = (runtime%60000)/1000;
	G.v().out.println("[TypeAssigner] typing system ended. It took "+mins+" mins and "+secs+" secs.");
      }

    if(typingFailed((JimpleBody) b))
      throw new RuntimeException("type inference failed!");
  }
    private boolean typingFailed(JimpleBody b)
    {
        // Check to see if any locals are untyped
        {
            Iterator localIt = b.getLocals().iterator();

            while(localIt.hasNext())
            {
                Local l = (Local) localIt.next();

                  if(l.getType().equals(UnknownType.v()) ||
                    l.getType().equals(ErroneousType.v()))
                {
		  return true;
                }
            }
        }
        
        return false;
    }


}

