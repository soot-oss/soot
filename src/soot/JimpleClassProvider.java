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
import java.io.*;

/** A class provider looks for a file of a specific format for a specified
 * class, and returns a ClassSource for it if it finds it.
 */
public class JimpleClassProvider implements ClassProvider
{
    /** Look for the specified class. Return a ClassSource for it if found,
     * or null if it was not found. */
    public ClassSource find( String className ) {
        //String fileName = className.replace('.', '/') + ".jimple";
        String fileName = className + ".jimple";
        SourceLocator.FoundFile file = 
            SourceLocator.v().lookupInClassPath(fileName);
        if( file == null ) return null;
        return new JimpleClassSource(className, file.inputStream());
    }
}

