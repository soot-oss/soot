/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrice Pominville
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


package soot.jimple.parser;

import soot.baf.*;
import soot.*;
import soot.jimple.*;
import soot.util.*;

import soot.jimple.parser.parser.*;
import soot.jimple.parser.lexer.*;
import soot.jimple.parser.node.*;
import soot.jimple.parser.analysis.*;

import java.io.*;
import java.util.*;


/** 
 *  Walks a jimple AST, extracting all the contained reference 
 *  type names.
 */

class CstPoolExtractor
{

    private  Set mRefTypes = null;
    private Start mParseTree;

    public CstPoolExtractor(Start parseTree) 
    {
        mParseTree = parseTree;
    }

    public Set getCstPool()
    {
        if(mRefTypes == null) {            
            mRefTypes = new HashSet();
            CstPoolExtractorWalker  walker = new CstPoolExtractorWalker();         
            mParseTree.apply(walker);          
            mParseTree = null; // allow garbage collection
        }           
        return mRefTypes;
    }        
                

    private class CstPoolExtractorWalker extends DepthFirstAdapter
    {               
        CstPoolExtractorWalker() 
        {        
        }
   
        public void inStart(Start node)
        {
            defaultIn(node);
        }


        public void outAQuotedClassName(AQuotedClassName node)
        {
	    String tokenString = node.getQuotedName().getText();
	    tokenString = tokenString.substring(1, tokenString.length() -1 );                                       
	    tokenString = StringTools.getUnEscapedStringOf(tokenString);

            mRefTypes.add(tokenString);
       
        }

        public void outAIdentClassName(AIdentClassName node)
        {
	    String tokenString = node.getIdentifier().getText();
	    tokenString = StringTools.getUnEscapedStringOf(tokenString);
	    
	    mRefTypes.add(tokenString);
        }

        public void outAFullIdentClassName(AFullIdentClassName node)
        {
	    String tokenString = node.getFullIdentifier().getText();
	    tokenString = StringTools.getUnEscapedStringOf(tokenString);
	    
            mRefTypes.add(tokenString);
        }

        public void outAQuotedNonvoidType(AQuotedNonvoidType node)
        {
	    String tokenString = node.getQuotedName().getText();
	    tokenString = tokenString.substring(1, tokenString.length() -1 );                                       
	    tokenString = StringTools.getUnEscapedStringOf(tokenString);

            mRefTypes.add(tokenString);
        }
   
        public void outAFullIdentNonvoidType(AFullIdentNonvoidType node)
        {
	    String tokenString = node.getFullIdentifier().getText();
	    tokenString = StringTools.getUnEscapedStringOf(tokenString);

            mRefTypes.add(tokenString);
        }    
    
        public void outAIdentNonvoidType(AIdentNonvoidType node)
        {
	    String tokenString = node.getIdentifier().getText();
	    tokenString = StringTools.getUnEscapedStringOf(tokenString);
	    
            mRefTypes.add(tokenString);
        }
    }
} 

















