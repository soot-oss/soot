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
 	        	
/* 04.04.2006 mbatch	if there is a $ in the name,
 *						we need to check if it's a real file, 
 * 						not just inner class								
 */

package soot;
import java.io.*;

/** A class provider looks for a file of a specific format for a specified
 * class, and returns a ClassSource for it if it finds it.
 */
public class JavaClassProvider implements ClassProvider
{
    /** Look for the specified class. Return a ClassSource for it if found,
     * or null if it was not found. */
    public ClassSource find( String className ) {

        if (soot.javaToJimple.InitialResolver.v().hasASTForSootName(className)){
            soot.javaToJimple.InitialResolver.v().setASTForSootName(className);
            return new JavaClassSource(className);
        }
        else {
        	/* 04.04.2006 mbatch	if there is a $ in the name,
			 *						we need to check if it's a real file, 
			 * 						not just inner class								
			 */
          	boolean checkAgain = className.indexOf('$') >= 0;
          	
            String javaClassName = SourceLocator.v().getSourceForClass(className);
            String fileName = javaClassName.replace('.', '/') + ".java";
            SourceLocator.FoundFile file = 
                SourceLocator.v().lookupInClassPath(fileName);

            /* 04.04.2006 mbatch	if inner class not found,
		     *						check if it's a real file							
			 */
            if( file == null) {
            
              if (checkAgain) {
                fileName = className.replace('.', '/') + ".java";
                file = SourceLocator.v().lookupInClassPath(fileName);
              }
            }
            /* 04.04.2006 mbatch	end */

            if (file == null)
            	return null;         
            
            if( file.file == null ) {
                throw new RuntimeException( "Class "+className+" was found in a .jar, but Polyglot doesn't support reading source files out of a .jar" );
            }
            return new JavaClassSource(className, file.file);
        }
    }
}