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

import soot.options.*;

import soot.*;
import soot.jimple.*;

import soot.jimple.parser.node.*;
import java.util.*;

/**
 * Walks a jimple AST and constructs the method bodies for all the methods of
 * the SootClass associated with this walker (see constructor). note: Contrary
 * to the plain "Walker", this walker does not create a SootClass, or interact
 * with the scene. It merely adds method bodies for each of the methods of the
 * SootClass it was initialized with.
 */

/* Modified By Marc Berndl May 17th */

public class BodyExtractorWalker extends Walker {
	Map<SootMethod, JimpleBody> methodToParsedBodyMap;

	/**
	 * Constructs a walker, and attaches it to the given SootClass, sending
	 * bodies to the given methodToParsedBodyMap.
	 */
	public BodyExtractorWalker(SootClass sc, SootResolver resolver,
			Map<SootMethod, JimpleBody> methodToParsedBodyMap) {
		super(sc, resolver);
		this.methodToParsedBodyMap = methodToParsedBodyMap;
	}

	/*
	 * file = modifier* file_type class_name extends_clause? implements_clause?
	 * file_body;
	 */
	public void caseAFile(AFile node) {
		inAFile(node);
		{
			Object temp[] = node.getModifier().toArray();
			for (Object element : temp) {
				((PModifier) element).apply(this);
			}
		}
		if (node.getFileType() != null) {
			node.getFileType().apply(this);
		}
		if (node.getClassName() != null) {
			node.getClassName().apply(this);
		}

		String className = (String) mProductions.removeLast();
		if (!className.equals(Scene.v().quotedNameOf(mSootClass.getName())))
			throw new RuntimeException("expected:  " + className
					+ ", but got: " + mSootClass.getName());

		if (node.getExtendsClause() != null) {
			node.getExtendsClause().apply(this);
		}
		if (node.getImplementsClause() != null) {
			node.getImplementsClause().apply(this);
		}
		if (node.getFileBody() != null) {
			node.getFileBody().apply(this);
		}
		outAFile(node);
	}

	public void outAFile(AFile node) {
		if (node.getImplementsClause() != null)
			mProductions.removeLast(); // implements_clause

		if (node.getExtendsClause() != null)
			mProductions.removeLast(); // extends_clause

		mProductions.removeLast(); // file_type
		mProductions.addLast(mSootClass);
	}

	/*
	 * member = {field} modifier* type name semicolon | {method} modifier* type
	 * name l_paren parameter_list? r_paren throws_clause? method_body;
	 */
	public void outAFieldMember(AFieldMember node) {
		mProductions.removeLast(); // name
		mProductions.removeLast(); // type
	}

	public void outAMethodMember(AMethodMember node) {
		Type type;
		String name;
		List<Type> parameterList = new ArrayList<Type>();
		List throwsClause = null;
		JimpleBody methodBody = null;

		if (node.getMethodBody() instanceof AFullMethodBody)
			methodBody = (JimpleBody) mProductions.removeLast();

		if (node.getThrowsClause() != null)
			throwsClause = (List) mProductions.removeLast();

		if (node.getParameterList() != null)
			parameterList = (List) mProductions.removeLast();

		name = (String) mProductions.removeLast(); // name
		type = (Type) mProductions.removeLast(); // type
		SootMethod sm = mSootClass.getMethodUnsafe(SootMethod.getSubSignature(
				name, parameterList, type));
		if (sm != null) {
			if (Options.v().verbose())
				G.v().out
						.println("[Jimple parser] "
								+ SootMethod.getSubSignature(name,
										parameterList, type));
		} else {
			G.v().out.println("[!!! Couldn't parse !!] "
					+ SootMethod.getSubSignature(name, parameterList, type));

			G.v().out.println("[!] Methods in class are:");
			for (SootMethod next : mSootClass.getMethods()) {
				G.v().out.println(next.getSubSignature());
			}

		}

		if (sm.isConcrete() && methodBody != null) {
			if (Options.v().verbose())
				G.v().out.println("[Parsed] " + sm.getDeclaration());

			methodBody.setMethod(sm);
			methodToParsedBodyMap.put(sm, methodBody);
		} else if (node.getMethodBody() instanceof AFullMethodBody) {
			if (sm.isPhantom() && Options.v().verbose())
				G.v().out.println("[jimple parser] phantom method!");
			throw new RuntimeException("Impossible: !concrete => ! instanceof "
					+ sm.getName());
		}
	}

}
