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
import java.io.File;

import polyglot.ast.Node;

import soot.javaToJimple.IInitialResolver;
import soot.javaToJimple.InitialResolver;
import soot.javaToJimple.IInitialResolver.Dependencies;
import soot.options.Options;
import soot.toolkits.astmetrics.ComputeASTMetrics;

/** A class source for resolving from .java files using javaToJimple.
 */
public class JavaClassSource extends ClassSource
{
    public JavaClassSource( String className, File fullPath ) {
        super( className );
        this.fullPath = fullPath;
    }
    public JavaClassSource( String className ) {
        super( className );
    }
    
    public Dependencies resolve( SootClass sc ) {
        if (Options.v().verbose())
            G.v().out.println("resolving [from .java]: " + className);
                    
        IInitialResolver resolver;
        if(Options.v().polyglot())
        	resolver = InitialResolver.v();
        else
        	resolver = JastAddInitialResolver.v();

        if (fullPath != null){
            resolver.formAst(fullPath.getPath(), SourceLocator.v().sourcePath(), className);
        }
        //System.out.println("about to call initial resolver in j2j: "+sc.getName());
        Dependencies references = resolver.resolveFromJavaFile(sc);
        
        /*
         * 1st March 2006
         * Nomair
         * This seems to be a good place to calculate all the
         * AST Metrics needed from Java's AST
         */
		if(Options.v().ast_metrics()){
			//System.out.println("CALLING COMPUTEASTMETRICS!!!!!!!");
			Node ast = InitialResolver.v().getAst();
			if(ast==null) {
				G.v().out.println("No compatible AST available for AST metrics. Skipping. Try -polyglot option.");
			} else {
				ComputeASTMetrics metrics = new ComputeASTMetrics(ast);
				metrics.apply();
			}
		}
        
        return references;
    }

    private File fullPath;
}

