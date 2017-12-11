/* Soot - a J*va Optimization Framework
 * Copyright (C) 2008 Eric Bodden
 * Copyright (C) 2008 Torbjorn Ekman
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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import soot.javaToJimple.IInitialResolver;
import soot.JastAddJ.BodyDecl;
import soot.JastAddJ.CompilationUnit;
import soot.JastAddJ.ConstructorDecl;
import soot.JastAddJ.MethodDecl;
import soot.JastAddJ.Program;
import soot.JastAddJ.TypeDecl;

/**
 * An {@link IInitialResolver} for the JastAdd frontend.
 * 
 * @author Torbjorn Ekman
 * @author Eric Bodden
 */
public class JastAddInitialResolver implements IInitialResolver {

    public JastAddInitialResolver(soot.Singletons.Global g){}

    public static JastAddInitialResolver v() {
        return soot.G.v().soot_JastAddInitialResolver();
    }
	
	protected Map<String,CompilationUnit> classNameToCU = new HashMap<String, CompilationUnit>();
	
	public void formAst(String fullPath, List<String> locations, String className) {
	      Program program = SootResolver.v().getProgram();
    	  CompilationUnit u = program.getCachedOrLoadCompilationUnit(fullPath);
    	  if(u != null && !u.isResolved) {
    		  u.isResolved = true;
	          java.util.ArrayList<soot.JastAddJ.Problem> errors = new java.util.ArrayList<soot.JastAddJ.Problem>();
	          u.errorCheck(errors);
	          if(!errors.isEmpty()) {
	        	  for(soot.JastAddJ.Problem p : errors)
	        		  G.v().out.println(p);
	        	  //die
	        	  throw new CompilationDeathException(CompilationDeathException.COMPILATION_ABORTED,
	        			  "there were errors during parsing and/or type checking (JastAdd frontend)");
	          }
	          u.transformation();
	          u.jimplify1phase1();
	          u.jimplify1phase2();
	  		  HashSet<SootClass> types = new HashSet<SootClass>();
			  for(TypeDecl typeDecl : u.getTypeDecls())
				  collectTypeDecl(typeDecl, types);
			  if(types.isEmpty())
				  classNameToCU.put(className, u);
		      else
		    	  for(SootClass sc : types)
		    		  classNameToCU.put(sc.getName(), u);	     
    	  }
	}
	
	@SuppressWarnings("unchecked")
	private void collectTypeDecl(TypeDecl typeDecl, HashSet<SootClass> types) {
		types.add(typeDecl.getSootClassDecl());
		for(TypeDecl nestedType : (Collection<TypeDecl>)typeDecl.nestedTypes()) {
			collectTypeDecl(nestedType, types);
		}
	}
	
	@SuppressWarnings("unchecked")
	private TypeDecl findNestedTypeDecl(TypeDecl typeDecl, SootClass sc) {
		if(typeDecl.sootClass() == sc)
			return typeDecl;
		for(TypeDecl nestedType : (Collection<TypeDecl>)typeDecl.nestedTypes()) {
			TypeDecl t = findNestedTypeDecl(nestedType, sc);
			if(t != null)
				return t;
		}
		return null;			
	}

	public Dependencies resolveFromJavaFile(SootClass sootclass) {
		CompilationUnit u = classNameToCU.get(sootclass.getName());

		if(u == null)
			throw new RuntimeException("Error: couldn't find class: "+sootclass.getName()+" are the packages set properly?");


		HashSet<SootClass> types = new HashSet<SootClass>();
		for(TypeDecl typeDecl : u.getTypeDecls()) {
			collectTypeDecl(typeDecl, types);
		}
		Dependencies deps = new Dependencies(); 
		u.collectTypesToHierarchy(deps.typesToHierarchy);
	  	u.collectTypesToSignatures(deps.typesToSignature);
		
		for(SootClass sc : types) {
		for (SootMethod m : sc.getMethods()) {
			m.setSource(new MethodSource() {
				public Body getBody(SootMethod m, String phaseName) {
					SootClass sc = m.getDeclaringClass();
					CompilationUnit u = classNameToCU.get(sc.getName());
					for(TypeDecl typeDecl : u.getTypeDecls()) {
						typeDecl = findNestedTypeDecl(typeDecl, sc);
						if(typeDecl != null) {
							if(typeDecl.clinit == m) {
								typeDecl.jimplify2clinit();
								return m.getActiveBody();
							}
							for(BodyDecl bodyDecl : typeDecl.getBodyDecls()) {
								if(bodyDecl instanceof MethodDecl) {
									MethodDecl methodDecl = (MethodDecl) bodyDecl;
									if(m.equals(methodDecl.sootMethod)) {
										methodDecl.jimplify2();
										return m.getActiveBody();
									}
								} else if(bodyDecl instanceof ConstructorDecl) {
									ConstructorDecl constrDecl = (ConstructorDecl) bodyDecl;
									if(m.equals(constrDecl.sootMethod)) {
										constrDecl.jimplify2();
										return m.getActiveBody();
									}
								}
							}
							
						}
					}
					throw new RuntimeException("Could not find body for " + m.getSignature() + " in " + m.getDeclaringClass().getName());
				}
			});
		}
		}
        return deps;
	}

}
