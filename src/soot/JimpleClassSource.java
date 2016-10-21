/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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

package soot;
import soot.SourceLocator.FoundFile;
import soot.jimple.parser.lexer.LexerException;
import soot.jimple.parser.parser.ParserException;
import soot.options.*;
import soot.javaToJimple.IInitialResolver.Dependencies;
import soot.jimple.*;

import java.io.*;
import java.util.*;

/** A class source for resolving from .jimple files using the Jimple parser.
 */
public class JimpleClassSource extends ClassSource {
	
	private FoundFile foundFile;
	
    public JimpleClassSource(String className, FoundFile foundFile) {
        super(className);
        if(foundFile == null)
			throw new IllegalStateException("Error: The FoundFile must not be null.");
        this.foundFile = foundFile;
    }
    public Dependencies resolve( SootClass sc ) {
        if(Options.v().verbose())
            G.v().out.println("resolving [from .jimple]: " + className );
        
        InputStream classFile = null;
        try {
        	classFile = foundFile.inputStream();
        	soot.jimple.parser.JimpleAST jimpAST = new soot.jimple.parser.JimpleAST(classFile);
        	jimpAST.getSkeleton(sc);
        	JimpleMethodSource mtdSrc = new JimpleMethodSource(jimpAST);

        	Iterator<SootMethod> mtdIt = sc.methodIterator();
        	while(mtdIt.hasNext()) {
        		SootMethod sm = mtdIt.next();
        		sm.setSource(mtdSrc);
        	}

        	Dependencies deps = new Dependencies();
        	//The method documentation states it returns RefTypes only, so this is a transformation safe
        	for (String t : jimpAST.getCstPool()){
        		deps.typesToSignature.add(RefType.v(t));
        	}

        	return deps;
        } catch (IOException e) { 
        	throw new RuntimeException("Error: Failed to create JimpleAST from source input stream for class " + className + ".",e); 
        } catch (ParserException e) {
        	throw new RuntimeException("Error: Failed when parsing class " + className + ".",e);
        } catch (LexerException e) {
        	throw new RuntimeException("Error: Failed when lexing class " + className + ".", e);
        } finally {
        	try {
        		if(classFile != null){
        			classFile.close();
        			classFile = null;
        		}
        	} catch(IOException e) {
        		throw new RuntimeException("Error: Failed to close source input stream.",e);
        	} finally {
        		close();
        	}
        }
    }
    
	@Override
	public void close() {
		if(foundFile != null){
			foundFile.close();
			foundFile = null;
		}
	}
    
}

