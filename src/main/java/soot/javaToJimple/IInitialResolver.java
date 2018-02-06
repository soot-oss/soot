/* Soot - a J*va Optimization Framework
 * Copyright (C) 2008 Eric Bodden
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
package soot.javaToJimple;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.SootClass;
import soot.Type;

public interface IInitialResolver {
	
	public void formAst(String fullPath, List<String> locations, String className);
	
	public Dependencies resolveFromJavaFile(SootClass sc);
	
	public class Dependencies {
		public final Set<Type> typesToHierarchy, typesToSignature;
		public Dependencies() {
			typesToHierarchy = new HashSet<Type>();
			typesToSignature = new HashSet<Type>();
		}
	}

}