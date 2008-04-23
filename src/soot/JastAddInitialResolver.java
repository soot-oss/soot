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
package soot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.javaToJimple.IInitialResolver;
import AST.BodyDecl;
import AST.CompilationUnit;
import AST.MethodDecl;
import AST.Program;
import AST.TypeDecl;

public class JastAddInitialResolver implements IInitialResolver {

    public JastAddInitialResolver(soot.Singletons.Global g){}

    public static JastAddInitialResolver v() {
        return soot.G.v().soot_JastAddInitialResolver();
    }
	
	protected Map<String,CompilationUnit> classNameToCU = new HashMap<String, CompilationUnit>();
	
	public void formAst(String fullPath, List<String> locations, String className) {
        Program program = SootResolver.v().getProgram();		
		program.addSourceFile(fullPath);
		CompilationUnit u = program.getCompilationUnit(className);
	  	program.addCompilationUnit(u);
	  	u.jimplify1phase1();
	  	u.jimplify1phase2();
	  	if(classNameToCU.containsKey(className)) {
	  		throw new IllegalStateException();
	  	}
	  	classNameToCU.put(className, u);	  	
	}

	public Dependencies resolveFromJavaFile(SootClass sc) {
		Dependencies deps = new Dependencies(); 
		for (SootMethod m : sc.getMethods()) {
			m.setSource(new MethodSource() {
				public Body getBody(SootMethod m, String phaseName) {
					CompilationUnit u = classNameToCU.get(m.getDeclaringClass().getName());
					AST.List<TypeDecl> typeDeclList = u.getTypeDeclList();
					for (TypeDecl typeDecl : typeDeclList) {
						AST.List<BodyDecl> bodyDeclList = typeDecl.getBodyDeclList();
						for (BodyDecl bodyDecl : bodyDeclList) {
							if(bodyDecl instanceof MethodDecl) {
								MethodDecl methodDecl = (MethodDecl) bodyDecl;
								if(m.equals(methodDecl.sootMethod))
									methodDecl.jimplify2();
							}
						}
					}					
					return m.getActiveBody();
				}
			});
			CompilationUnit u = classNameToCU.get(m.getDeclaringClass().getName());
		  	u.collectTypesToHierarchy(deps.typesToHierarchy);
		  	u.collectTypesToSignatures(deps.typesToSignature);
		}
		
        return deps;
	}

}
