/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

package soot.jbco.jimpleTransformations;

import java.util.*;
import soot.*;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.*;
import soot.jimple.*;

/**
 * @author Michael Batchelder
 * 
 *         Created on 24-Jan-2006
 */
public class MethodRenamer extends SceneTransformer implements IJbcoTransform {

	public static String dependancies[] = new String[] { "wjtp.jbco_mr" };

	public String[] getDependancies() {
		return dependancies;
	}

	public static String name = "wjtp.jbco_mr";

	public String getName() {
		return name;
	}

	public void outputSummary() {
	}

	private static final char stringChars[][] = { { 'S', '5', '$' },
			{ 'l', '1', 'I' }, { '_' } };
	
	public static Vector<?> namesToNotRename = new Vector<Object>();
	public static HashMap<String, String> oldToNewMethodNames = new HashMap<String, String>();
	private static Hierarchy hierarchy;

	protected void internalTransform(String phaseName,
			Map<String, String> options) {
		if (output)
			out.println("Transforming Method Names...");

		soot.jbco.util.BodyBuilder.retrieveAllBodies();
		soot.jbco.util.BodyBuilder.retrieveAllNames();

		Scene scene = G.v().soot_Scene();
		scene.releaseActiveHierarchy();
		hierarchy = scene.getActiveHierarchy();

		// iterate through application classes, rename methods with junk
		for (SootClass c : scene.getApplicationClasses()) {
			Vector<String> fields = new Vector<String>();
			Iterator<SootField> fIt = c.getFields().iterator();
			while (fIt.hasNext()) {
				fields.add(fIt.next().getName());
			}

			for (SootMethod m : c.getMethods()) {
				String subSig = m.getSubSignature();

				if (!allowsRename(c, m))
					continue;

				boolean rename = true;
				for (SootClass _c : hierarchy.getSuperclassesOfIncluding(c
						.getSuperclass())) {
					if (_c.declaresMethod(subSig)
							&& hierarchy.isVisible(c, _c.getMethod(subSig))
							&& _c.isLibraryClass()) {
						if (output)
							out.println("\t" + _c.getName() + "'s method "
									+ subSig + " is overridden in "
									+ c.getName());
						rename = false;
						break;
					}
				}

				if (rename) {
					// TODO: This is flawed since it all methods of a similar
					// name will get same name
					String newName = oldToNewMethodNames.get(m.getName());
					if (newName == null) {
						if (fields.size() > 0) {
							int rand = Rand.getInt(fields.size());
							newName = fields.remove(rand);
							if (oldToNewMethodNames.containsValue(newName))
								newName = getNewName();
						} else {
							newName = getNewName();
						}
					}
					oldToNewMethodNames.put(m.getName(), newName);
					if (output)
						out.println("\tChanged " + m.getSignature() + " to "
								+ newName);
					m.setName(newName);
				}
			}
		}

		for (SootClass c : scene.getApplicationClasses()) {
			for (SootMethod m : c.getMethods()) {
				if (!m.isConcrete() || m.getDeclaringClass().isLibraryClass())
					continue;
				Body aBody = null;
				try {
					aBody = m.getActiveBody();
				} catch (Exception exc) {
					// no active body present
					continue;
				}
				Iterator<Unit> uIt = aBody.getUnits().iterator();
				while (uIt.hasNext()) {
					Iterator<ValueBox> ubIt = uIt.next().getUseBoxes()
							.iterator();
					while (ubIt.hasNext()) {
						Value v = ubIt.next().getValue();
						if (!(v instanceof InvokeExpr))
							continue;

						InvokeExpr ie = (InvokeExpr) v;

						try {
							// if the method won't resolve, then we know it's
							// not a lib method
							ie.getMethod();
							continue;
						} catch (Exception e) {
						}

						SootMethodRef r = ie.getMethodRef();
						String newName = oldToNewMethodNames.get(r.name());
						if (newName == null)
							continue;

						r = scene.makeMethodRef(r.declaringClass(), newName,
								r.parameterTypes(), r.returnType(),
								r.isStatic());
						ie.setMethodRef(r);
					}
				}
			}
		}

		scene.releaseActiveHierarchy();
		scene.getActiveHierarchy();
		scene.setFastHierarchy(new FastHierarchy());
	}

	/*
	 * @return String newly generated junk name that DOES NOT exist yet
	 */
	public static String getNewName() {
		int size = 5;
		int tries = 0;
		int index = Rand.getInt(stringChars.length);
		int length = stringChars[index].length;

		String result = null;
		char cNewName[] = new char[size];
		do {
			if (tries == size) {
				cNewName = new char[++size];
				tries = 0;
			}

			do {
				cNewName[0] = stringChars[index][Rand.getInt(length)];
			} while (!Character.isJavaIdentifierStart(cNewName[0]));

			// generate random string
			for (int i = 1; i < cNewName.length; i++) {
				int rand = Rand.getInt(length);
				cNewName[i] = stringChars[index][rand];
			}

			result = String.copyValueOf(cNewName);
			tries++;
		} while (oldToNewMethodNames.containsValue(result)
				|| BodyBuilder.nameList.contains(result));

		BodyBuilder.nameList.add(result);

		return result;
	}

	private static boolean allowsRename(SootClass c, SootMethod m) {

		if (soot.jbco.Main.getWeight(MethodRenamer.name, m.getName()) == 0)
			return false;

		String subSig = m.getSubSignature();
		if (subSig.equals("void main(java.lang.String[])") && m.isPublic()
				&& m.isStatic()) {
			return false; // skip the main method - it needs to be named 'main'
		} else if (subSig.indexOf("void <init>(") >= 0
				|| subSig.equals("void <clinit>()")) {
			return false; // skip constructors for now
		} else {
			for (SootClass _c : hierarchy.getSuperclassesOfIncluding(c
					.getSuperclass())) {
				if (_c.isLibraryClass() && _c.declaresMethod(subSig)
						&& hierarchy.isVisible(c, _c.getMethod(subSig))) {
					return false;
				}
			}

			do {
				if (checkInterfacesForMethod(c, m))
					return false;
			} while (c.hasSuperclass() && (c = c.getSuperclass()) != null);
		}

		return true;
	}

	private static boolean checkInterfacesForMethod(SootClass c, SootMethod m) {
		for (SootClass sc : c.getInterfaces()) {
			if (sc.isLibraryClass()
					&& sc.declaresMethod(m.getName(), m.getParameterTypes(),
							m.getReturnType()))
				return true;
		}
		return false;
	}
}