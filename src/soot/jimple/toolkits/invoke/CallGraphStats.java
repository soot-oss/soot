/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Raja Vallee-Rai, Felix Kwok
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

package soot.jimple.toolkits.invoke;

import java.util.*;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.*;

class CallGraphStats 
{  
    int nodes;
    int benchNodes;
    int monoCS;
    int polyCS;
    int monoEdges;
    int polyEdges;
    int benchMonoCS;
    int benchPolyCS;
    int benchMonoEdges;
    int benchPolyEdges;

    public CallGraphStats() 
    {
        nodes = 0;
        benchNodes = 0;
        monoCS = 0;
        polyCS = 0;
        monoEdges = 0;
        polyEdges = 0;
        benchMonoCS = 0;
        benchPolyCS = 0;
        benchMonoEdges = 0;
        benchPolyEdges = 0;
    }

    public String toString() 
    {
        String s = "\n";
        s = s + "          Call Graph Statistics\n";
        s = s + "============================================\n";
        s = s + "Number of nodes = "+nodes+"\n";
        s = s + "Number of sites = "+(monoCS+polyCS)+"\n";
        s = s + "Number of resolved sites = "+monoCS+"\n";
        s = s + "Number of unresolved sites = "+polyCS+"\n";
        s = s + "Number of resolved edges = "+monoEdges+"\n";
        s = s + "Number of unresolved edges = "+polyEdges+"\n";
        s = s + "Number of benchmark nodes = "+benchNodes+"\n";
        s = s + "Number of benchmark sites = "+(benchMonoCS+benchPolyCS)+"\n";
        s = s + "Number of resolved benchmark sites = "+benchMonoCS+"\n";
        s = s + "Number of unresolved benchmark sites = "+benchPolyCS+"\n";
        s = s + "Number of resolved benchmark edges = "+benchMonoEdges+"\n";
        s = s + "Number of unresolved benchmark edges = "+benchPolyEdges+"\n";
        return s;
    }
}
