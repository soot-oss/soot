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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.toolkits.scalar;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.options.Options;
import soot.toolkits.exceptions.PedanticThrowAnalysis;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.util.ArraySet;

/**
 * Provides methods for register coloring. Jimple uses these methods to assign
 * the local slots appropriately.
 */
public class FastColorer {
	/**
	 * Provides a coloring for the locals of <code>unitBody</code>, attempting
	 * to not split locals assigned the same name in the original Jimple.
	 */
	public static void unsplitAssignColorsToLocals(Body unitBody,
			Map<Local, Object> localToGroup, Map<Local, Integer> localToColor,
			Map<Object, Integer> groupToColorCount) {
		// To understand why a pedantic throw analysis is required, see comment
		// in assignColorsToLocals method
		ExceptionalUnitGraph unitGraph = new ExceptionalUnitGraph(unitBody,
				PedanticThrowAnalysis.v(), Options.v()
						.omit_excepting_unit_edges());

		LiveLocals liveLocals;
		liveLocals = new SimpleLiveLocals(unitGraph);

		UnitInterferenceGraph intGraph = new UnitInterferenceGraph(unitBody,
				localToGroup, liveLocals, unitGraph);

		Map<Local, String> localToOriginalName = new HashMap<Local, String>();

		// Map each local variable to its original name
		{
			for (Local local : intGraph.getLocals()) {
				int signIndex;

				signIndex = local.getName().indexOf("#");

				if (signIndex != -1) {
					localToOriginalName.put(local,
							local.getName().substring(0, signIndex));
				} else
					localToOriginalName.put(local, local.getName());

			}
		}

		Map<StringGroupPair, List<Integer>> originalNameAndGroupToColors = new HashMap<StringGroupPair, List<Integer>>();
		// maps an original name to the colors being used for it

		// Assign a color for each local.
		{
			int[] freeColors = new int[10];
			for (Local local : intGraph.getLocals()) {
				if (localToColor.containsKey(local)) {
					// Already assigned, probably a parameter
					continue;
				}

				Object group = localToGroup.get(local);
				int colorCount = groupToColorCount.get(group).intValue();

				if (freeColors.length < colorCount)
					freeColors = new int[Math.max(freeColors.length * 2,
							colorCount)];

				// Set all colors to free.
				{
					for (int i = 0; i < colorCount; i++)
						freeColors[i] = 1;
				}

				// Remove unavailable colors for this local
				{
					Local[] interferences = intGraph.getInterferencesOf(local);

					if (interferences != null)
						for (Local element : interferences) {
							if (localToColor.containsKey(element)) {
								int usedColor = localToColor.get(element)
										.intValue();

								freeColors[usedColor] = 0;
							}
						}
				}

				// Assign a color to this local.
				{
					String originalName = localToOriginalName.get(local);
					List<Integer> originalNameColors = originalNameAndGroupToColors
							.get(new StringGroupPair(originalName, group));

					if (originalNameColors == null) {
						originalNameColors = new ArrayList<Integer>();
						originalNameAndGroupToColors.put(new StringGroupPair(
								originalName, group), originalNameColors);
					}

					boolean found = false;
					int assignedColor = 0;

					// Check if the colors assigned to this
					// original name is already free
					{
						Iterator<Integer> colorIt = originalNameColors
								.iterator();

						while (colorIt.hasNext()) {
							Integer color = colorIt.next();

							if (freeColors[color.intValue()] == 1) {
								found = true;
								assignedColor = color.intValue();
							}
						}
					}

					if (!found) {
						assignedColor = colorCount++;
						groupToColorCount.put(group, new Integer(colorCount));
						originalNameColors.add(new Integer(assignedColor));
					}

					localToColor.put(local, new Integer(assignedColor));
				}
			}
		}
	}

	/**
	 * Provides an economical coloring for the locals of <code>unitBody</code>.
	 */
	public static void assignColorsToLocals(Body unitBody,
			Map<Local, Object> localToGroup, Map<Local, Integer> localToColor,
			Map<Object, Integer> groupToColorCount) {
		// Build a CFG using a pedantic throw analysis to prevent JVM
		// "java.lang.VerifyError: Incompatible argument to function" errors.
		ExceptionalUnitGraph unitGraph = new ExceptionalUnitGraph(unitBody,
				PedanticThrowAnalysis.v(), Options.v()
						.omit_excepting_unit_edges());
		LiveLocals liveLocals;

		liveLocals = new SimpleLiveLocals(unitGraph);

		final UnitInterferenceGraph intGraph = new UnitInterferenceGraph(unitBody,
				localToGroup, liveLocals, unitGraph);
		
		// Assign a color for each local.
		{
			// Sort the locals first to maximize the locals per color. We first
			// assign those locals that have many conflicts and then assign the
			// easier ones to those color groups.
			List<Local> sortedLocals = new ArrayList<Local>(intGraph.getLocals());
			Collections.sort(sortedLocals, new Comparator<Local>() {

				@Override
				public int compare(Local o1, Local o2) {
					int interferences1 = intGraph.getInterferenceCount(o1);
					int interferences2 = intGraph.getInterferenceCount(o2);
					return interferences2 - interferences1;
				}
				
			});
			
			for (Local local : sortedLocals) {
				if (localToColor.containsKey(local)) {
					// Already assigned, probably a parameter
					continue;
				}

				Object group = localToGroup.get(local);
				int colorCount = groupToColorCount.get(group).intValue();

				BitSet blockedColors = new BitSet(colorCount);
				
				// Block unavailable colors for this local
				{
					Local[] interferences = intGraph.getInterferencesOf(local);

					if (interferences != null)
						for (Local element : interferences) {
							if (localToColor.containsKey(element)) {
								int usedColor = localToColor.get(element)
										.intValue();

								blockedColors.set(usedColor);
							}
						}
				}

				// Assign a color to this local.
				{
					int assignedColor = -1;

					for (int i = 0; i < colorCount; i++)
						if (!blockedColors.get(i)) {
							assignedColor = i;
							break;
						}

					if (assignedColor < 0) {
						assignedColor = colorCount++;
						groupToColorCount.put(group, new Integer(colorCount));
					}

					localToColor.put(local, new Integer(assignedColor));
				}
			}
		}

	}

	/** Implementation of a unit interference graph. */
	private static class UnitInterferenceGraph {
		Map<Local, Set<Local>> localToLocals;// Maps a local to its interfering
												// locals.
		List<Local> locals;

		public List<Local> getLocals() {
			return locals;
		}

		public UnitInterferenceGraph(Body body, Map<Local, Object> localToGroup,
				LiveLocals liveLocals, ExceptionalUnitGraph unitGraph) {
			locals = new ArrayList<Local>();
			locals.addAll(body.getLocals());

			// Initialize localToLocals
			{
				localToLocals = new HashMap<Local, Set<Local>>(
						body.getLocalCount() * 2 + 1, 0.7f);
			}

			// Go through code, noting interferences
			{
				for (Unit unit : body.getUnits()) {
					List<ValueBox> defBoxes = unit.getDefBoxes();
					
					// Note interferences if this stmt is a definition
					if (!defBoxes.isEmpty()) {
						// Only one def box is supported
						if (!(defBoxes.size() == 1))
							throw new RuntimeException(
									"invalid number of def boxes");
						
						// Remove those locals that are only live on exceptional flows.
						// If we have code like this:
						// 			a = 42
						// 			b = foo()
						//		catch -> print(a)
						// we can transform it to
						// 			a = 42
						// 			a = foo()
						//		catch -> print(a)
						// If an exception is thrown, at the second assignment, the
						// assignment will not actually happen and "a" will be unchanged.
						Set<Local> liveLocalsAtUnit = new HashSet<Local>();
						for (Unit succ : unitGraph.getUnexceptionalSuccsOf(unit)) {
							List<Local> beforeSucc = liveLocals.getLiveLocalsBefore(succ);
							liveLocalsAtUnit.addAll(beforeSucc);
						}
						

						Value defValue = ((ValueBox) defBoxes.get(0)).getValue();
						if (defValue instanceof Local) {
							Local defLocal = (Local) defValue;
							for (Local otherLocal : liveLocalsAtUnit) {
								if (localToGroup.get(otherLocal).equals(localToGroup.get(defLocal)))
									setInterference(defLocal, otherLocal);
							}
						}
					}
				}
			}
		}

		public void setInterference(Local l1, Local l2) {
			// We need the mapping in both directions
			// l1 -> l2
			Set<Local> locals = localToLocals.get(l1);
			if (locals == null) {
				locals = new ArraySet<Local>();
				localToLocals.put(l1, locals);
			}
			locals.add(l2);

			// l2 -> l1
			locals = localToLocals.get(l2);
			if (locals == null) {
				locals = new ArraySet<Local>();
				localToLocals.put(l2, locals);
			}
			locals.add(l1);
		}

		int getInterferenceCount(Local l) {
			Set<Local> localSet = localToLocals.get(l);
			return localSet == null ? 0 : localSet.size();
		}
		
		Local[] getInterferencesOf(Local l) {
			Set<Local> localSet = localToLocals.get(l);
			if (localSet == null)
				return null;
			
			return localSet.toArray(new Local[localSet.size()]);
		}
	}
}

/** Binds together a String and a Group. */
class StringGroupPair {
	String string;
	Object group;

	public StringGroupPair(String s, Object g) {
		string = s;
		group = g;
	}

	public boolean equals(Object p) {
		if (p instanceof StringGroupPair) {
			return ((StringGroupPair) p).string.equals(this.string)
					&& ((StringGroupPair) p).group.equals(this.group);
		}

		return false;
	}

	public int hashCode() {
		return string.hashCode() * 101 + group.hashCode() + 17;
	}
}
