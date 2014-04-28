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
import soot.javaToJimple.IInitialResolver;
import soot.javaToJimple.IInitialResolver.Dependencies;
import soot.options.*;
import java.io.*;
import java.util.*;

/** A class source for resolving from .class files through coffi.
 */
public class CoffiClassSource extends ClassSource
{
    protected final InputStream classFile;
	private final String fileName;
	private final String zipFileName;
	
    public CoffiClassSource( String className, InputStream classFile, String fileName, String zipFileName ) {
        super( className );
        this.classFile = classFile;
        this.fileName = fileName;
        this.zipFileName = zipFileName;
    }
    public Dependencies resolve( SootClass sc ) {
        if(Options.v().verbose())
            G.v().out.println("resolving [from .class]: " + className );
        List<Type> references = new ArrayList<Type>();
        soot.coffi.Util.v().resolveFromClassFile(sc, classFile, fileName, references);

        try {
            classFile.close();
        } catch (IOException e) { throw new RuntimeException("!?"); }
        
        addSourceFileTag(sc);
        
        IInitialResolver.Dependencies deps = new IInitialResolver.Dependencies();
        deps.typesToSignature.addAll(references);
        return deps;
    }
    
    protected void addSourceFileTag(soot.SootClass sc){
    	if (fileName == null && zipFileName == null)
    		return;
    	
        soot.tagkit.SourceFileTag tag = null;
        if (sc.hasTag("SourceFileTag")) {
            tag = (soot.tagkit.SourceFileTag)sc.getTag("SourceFileTag");
        }
        else {
            tag = new soot.tagkit.SourceFileTag();
            sc.addTag(tag);
        }
        
        // Sets sourceFile only when it hasn't been set before
        if (tag.getSourceFile() == null) {
            String name = zipFileName == null ? new File(fileName).getName() : new File(zipFileName).getName();
            tag.setSourceFile(name); 
        }
    }
}

