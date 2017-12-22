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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import soot.Body;
import soot.FastHierarchy;
import soot.G;
import soot.Hierarchy;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.BodyBuilder;
import soot.jbco.util.Rand;
import soot.jimple.InvokeExpr;

import static java.util.stream.Collectors.toList;

/**
 * @author Michael Batchelder
 * 
 *         Created on 24-Jan-2006
 */
public class MethodRenamer extends SceneTransformer implements IJbcoTransform {

	public static String name = "wjtp.jbco_mr";
	public static String dependancies[] = new String[]{"wjtp.jbco_mr"};

	public static HashMap<String, String> oldToNewMethodNames = new HashMap<>();

	private static final char stringChars[][] = {{'S', '5', '$'}, {'l', '1', 'I'}, {'_'}};
	private static Hierarchy hierarchy;

	public String getName() {
        return name;
    }

	public String[] getDependancies() {
        return dependancies;
    }

	public void outputSummary() {
	}

	protected void internalTransform(String phaseName, Map<String, String> options) {
		if (output) {
		    out.println("Transforming Method Names...");
		}

		BodyBuilder.retrieveAllBodies();
		BodyBuilder.retrieveAllNames();

		Scene scene = G.v().soot_Scene();
		scene.releaseActiveHierarchy();
		hierarchy = scene.getActiveHierarchy();

		// iterate through application classes, rename methods with junk
		for (SootClass sc : scene.getApplicationClasses()) {
			List<String> fieldNames = sc.getFields().stream().map(SootField::getName).collect(toList());
			List<SootMethod> methods = new ArrayList<>(sc.getMethods());

			for (SootMethod method : methods) {
				String subSig = method.getSubSignature();

				if (!allowsRename(sc, method)) {
					continue;
				}

				boolean rename = true;
				//use getSuperclassesOfIncluding instead of getSuperclassesOf to avoid problems when sc is an interface
				for (SootClass c : hierarchy.getSuperclassesOfIncluding(sc.getSuperclass())) {
					if (c.declaresMethod(subSig)
							&& hierarchy.isVisible(sc, c.getMethod(subSig))
							&& c.isLibraryClass()) {
						if (output) {
							out.println("\t" + c.getName() + "'s method "
									+ subSig + " is overridden in "
									+ sc.getName());
						}
						rename = false;
						break;
					}
				}

				if (rename) {
					String newName = oldToNewMethodNames.get(method.getName());
					if (newName == null) {
						if (!fieldNames.isEmpty()) {
							int rand = Rand.getInt(fieldNames.size());
							newName = fieldNames.remove(rand);
							//check both key and value, if class already contains method and field with same name
                            //then we likely will fall in trouble when renaming this method before previous
							if (oldToNewMethodNames.containsKey(newName) || oldToNewMethodNames.containsValue(newName)) {
								newName = getNewName();
							}
						} else {
							newName = getNewName();
						}
					}
					oldToNewMethodNames.put(method.getName(), newName);
					if (output) {
						out.println("\tChanged " + method.getSignature() + " to " + newName);
					}
					method.setName(newName);
				}
			}
		}

		// iterate through application classes, update references of renamed methods
		for (SootClass c : scene.getApplicationClasses()) {
            final List<SootMethod> methods = new ArrayList<>(c.getMethods());
            for (SootMethod m : methods) {
				if (!m.isConcrete() || m.getDeclaringClass().isLibraryClass()) {
					continue;
				}
				Body body;
				try {
					body = m.getActiveBody();
				} catch (Exception exc) {
					// no active body present
					continue;
				}
				for (Unit unit : body.getUnits()) {
					for (ValueBox valueBox : unit.getUseBoxes()) {
						Value v = valueBox.getValue();
						if (!(v instanceof InvokeExpr)) {
							continue;
						}

						InvokeExpr invokeExpr = (InvokeExpr) v;
						SootMethodRef methodRef = invokeExpr.getMethodRef();

                        // if the method won't be resolved in declaring class by subsignature of method ref,
                        // then we know it was renamed and update that method ref with new name
                        if (methodRef.declaringClass().getMethodUnsafe(methodRef.getSubSignature()) != null) {
                            continue;
                        }

						String newName = oldToNewMethodNames.get(methodRef.name());
						if (newName == null) {
							continue;
						}

						methodRef = scene.makeMethodRef(methodRef.declaringClass(), newName,
								methodRef.parameterTypes(), methodRef.returnType(),
								methodRef.isStatic());
                        invokeExpr.setMethodRef(methodRef);
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

		String result;
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
		} while (oldToNewMethodNames.containsValue(result) || BodyBuilder.nameList.contains(result));

		BodyBuilder.nameList.add(result);

		return result;
	}

	private static boolean allowsRename(SootClass sc, SootMethod method) {
		if (soot.jbco.Main.getWeight(MethodRenamer.name, method.getName()) == 0) {
			return false;
		}

		String subSig = method.getSubSignature();
		if ("void main(java.lang.String[])".equals(subSig) && method.isPublic() && method.isStatic()) {
			return false; // skip the main method - it needs to be named 'main'
		} else if (subSig.contains(SootMethod.constructorName) || subSig.equals(SootMethod.staticInitializerName)) {
			return false; // skip constructors for now
		} else {
			return !(isOverriddenLibraryInterfaceMethod(sc, method) || isOverriddenLibrarySuperclassMethod(sc, method));
		}
	}

    private static boolean isOverriddenLibrarySuperclassMethod(SootClass sc, SootMethod method) {
        String subSignature = method.getSubSignature();
        //use getSuperclassesOfIncluding instead of getSuperclassesOf to avoid problems when 'sc' parameter is interface
        return hierarchy.getSuperclassesOfIncluding(sc.getSuperclass()).stream()
                .filter(SootClass::isLibraryClass)
                .filter(c -> c.declaresMethod(subSignature))
                .anyMatch(c -> hierarchy.isVisible(sc, c.getMethod(subSignature)));
    }

    private static boolean isOverriddenLibraryInterfaceMethod(SootClass sc, SootMethod method) {
        return getAllInterfacesOf(sc).stream()
                .filter(SootClass::isLibraryClass)
                .anyMatch(c -> c.declaresMethod(method.getName(), method.getParameterTypes(), method.getReturnType()));
    }

    private static List<SootClass> getAllInterfacesOf(SootClass sc) {
        Stream<SootClass> superClassInterfaces = sc.isInterface() ? Stream.empty() : hierarchy.getSuperclassesOf(sc)
                .stream()
                .map(MethodRenamer::getAllInterfacesOf)
                .flatMap(Collection::stream);
        Stream<SootClass> directInterfaces = Stream.concat(sc.getInterfaces().stream(), sc.getInterfaces().stream()
                .map(MethodRenamer::getAllInterfacesOf)
                .flatMap(Collection::stream));
        return Stream.concat(superClassInterfaces, directInterfaces).collect(toList());
    }

}