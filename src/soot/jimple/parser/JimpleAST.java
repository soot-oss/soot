/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrice Pominville
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


package soot.jimple.parser;

import soot.jimple.parser.parser.*;
import soot.jimple.parser.lexer.*;
import soot.jimple.parser.node.*;
import soot.jimple.parser.analysis.*;
import java.io.*;
import java.util.*;

import soot.util.*;
import soot.*;

/** 
    This class encapsulates a JimpleAST instance and provides methods
    to act on it.      
*/
public class JimpleAST
{
    private Start mTree = null;
    private HashMap methodToParsedBodyMap = null;

    /** Constructs a JimpleAST and generates its parse tree from the given JimpleInputStream.
     *
     * @param aInputStream The JimpleInputStream to parse
     */
    public JimpleAST(JimpleInputStream aJIS)
    {
	Parser p =
	    new Parser(new Lexer(
		    new PushbackReader(new BufferedReader(
                    new InputStreamReader(aJIS)), 1024)));
	try {
	    mTree = p.parse();
	} catch(ParserException e) {
	    throw new RuntimeException("Parser exception occurred: " + e);
	} catch(LexerException e) {
	    throw new RuntimeException("Lexer exception occurred: " + e);
	} catch(IOException e) {
	    throw new RuntimeException("IOException occurred: " + e);
	}
    }

    /** Applies a SkeletonExtractorWalker to the given SootClass, using the given Resolver. */
    public SootClass getSkeleton(SootClass sc, SootResolver resolver)
    {
	Walker w = new SkeletonExtractorWalker(resolver, sc);	
	mTree.apply(w);  	
	return w.getSootClass();	
    }

    /** Runs a Walker on the JimpleInputStream associated to this object.
     * The SootClass which we want bodies for is passed as the argument. 
     */
    public void stashBodiesForClass(SootClass sc) 
    {  	
        methodToParsedBodyMap = new HashMap();

	Walker w = new BodyExtractorWalker(sc, methodToParsedBodyMap);

        boolean oldPhantomValue = Scene.v().getPhantomRefs();

        Scene.v().setPhantomRefs(true);
	mTree.apply(w);
        Scene.v().setPhantomRefs(oldPhantomValue);
    }

    /** Returns a body corresponding to the parsed jimple for m. 
     * If necessary, applies the BodyExtractorWalker to initialize the bodies map. */
    public Body getBody(SootMethod m)
    {
        if (methodToParsedBodyMap == null)
            stashBodiesForClass(m.getDeclaringClass());
        return (Body)methodToParsedBodyMap.get(m);
    } 

    /** Extracts the constant pool from the given AST. */
    public Set getCstPool() 
    {  
	CstPoolExtractorWalker w = new CstPoolExtractorWalker(); 
	
	mTree.apply(w);  	
		
	return w.getCstPool();	
    }
} // Parse
