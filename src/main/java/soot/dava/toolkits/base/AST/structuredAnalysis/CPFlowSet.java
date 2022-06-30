package soot.dava.toolkits.base.AST.structuredAnalysis;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import soot.dava.DecompilationException;
import soot.toolkits.scalar.FlowSet;

/*
 * Really the only reason for needing a specialized flow set is that
 * intersection is done differently for constant propagation
 */
public class CPFlowSet extends DavaFlowSet<CPTuple> {

  public CPFlowSet() {
    super();
  }

  /*
   * invoked by the clone method
   *
   * This is not as simple as one would think cloning is
   *
   * We have to make sure that certain important things.....like the bloody constant value inside the the variablevaluetuple
   * is being cloned!!!
   */
  public CPFlowSet(CPFlowSet other) {

    numElements = other.numElements;
    maxElements = other.maxElements;

    elements = new CPTuple[other.getElementCount()];
    for (int i = 0; i < other.getElementCount(); i++) {
      if (other.getElementAt(i) != null) {
        elements[i] = other.getElementAt(i).clone();
      } else {
        elements[i] = null;
      }
    }
    // elements = (Object[]) other.elements.clone();

    /*
     * Reason about the fact whether we need deep cloning of these shits or not anything which is in these lists is not being
     * cloned so care should be taken to not modify any value... c
     */
    breakList = (HashMap<Serializable, List<DavaFlowSet<CPTuple>>>) other.breakList.clone();
    continueList = (HashMap<Serializable, List<DavaFlowSet<CPTuple>>>) other.continueList.clone();
    implicitBreaks = (HashMap<Serializable, List<DavaFlowSet<CPTuple>>>) other.implicitBreaks.clone();
    implicitContinues = (HashMap<Serializable, List<DavaFlowSet<CPTuple>>>) other.implicitContinues.clone();

  }

  /*
   * helper method to be invoked by CPApplication when trying to do transformation
   *
   * returns a non null if the local or field is contained and has a constant value returns a null if the local or field is
   * eithe rnot present or is trop
   */
  public Object contains(String className, String localOrField) {
    for (int i = 0; i < this.numElements; i++) {
      CPTuple current = getElementAt(i);
      if (!(current.getSootClassName().equals(className))) {
        continue;
      }

      if (current.containsField()) {
        if (!current.getVariable().getSootField().getName().equals(localOrField)) {
          continue;
        } else {
          return current.getValue();
        }
      } else if (current.containsLocal()) {
        if (!current.getVariable().getLocal().getName().equals(localOrField)) {
          continue;
        } else {
          return current.getValue();
        }
      }
    }
    return null;
  }

  /*
   * This is more of an update method than an add method.
   *
   * Go through all the elements in the flowSet See if we can find an element (CPTuple with the same className and same
   * CPVariable) if we dont find one: add this to the flowset if we find one: update the Value with the value of the newTuple
   */
  public void addIfNotPresent(CPTuple newTuple) {
    // System.out.println("addIfnotPresent invoked");
    // going through all the elements in the set
    for (int i = 0; i < this.numElements; i++) {
      CPTuple current = (CPTuple) elements[i];
      if (!(current.getSootClassName().equals(newTuple.getSootClassName()))) {
        // different classNAmes
        continue;
      }

      // same class names

      CPVariable curVar = current.getVariable();
      CPVariable newTupleVar = newTuple.getVariable();

      if (!(curVar.equals(newTupleVar))) {
        // different variable
        continue;
      }

      // same class and same variable
      // UPDATE this elements VALUE
      // System.out.println("got here"+newTuple.getValue());

      current.setValue(newTuple.getValue());
      // since the tuple was present no need to ADD since we updated
      return;
    }

    // if we get to this part we know that we need to add
    // System.out.println("no got here");
    this.add(newTuple);
  }

  /*
   * Specialized method for handling conditionals this is used to add a belief derived from the condition of an if or ifelse
   *
   * The belief is only added if the current belief about the variable is top since later on when leaving the conditional our
   * new belief intersected with top will give top and we would not have added anything incorrect or presumptuous into our
   * flow set (LAURIE)
   */

  public void addIfNotPresentButDontUpdate(CPTuple newTuple) {
    // System.out.println("addIfnotPresent invoked");
    // going through all the elements in the set
    for (int i = 0; i < this.numElements; i++) {
      CPTuple current = (CPTuple) elements[i];
      if (!(current.getSootClassName().equals(newTuple.getSootClassName()))) {
        // different classNAmes
        continue;
      }

      // same class names

      CPVariable curVar = current.getVariable();
      CPVariable newTupleVar = newTuple.getVariable();

      if (!(curVar.equals(newTupleVar))) {
        // different variable
        continue;
      }

      /*
       * We only assign value if there is one not already present
       */
      if (current.isTop()) {
        current.setValue(newTuple.getValue());
      }

      return;
    }

    // if we get to this part we know that we need to add
    /*
     * DO NOT ADD IF NOT FOUND SINCE THAT MEANS IT IS BOTTOM and we dont want the following to occur
     *
     * if( bla ){
     *
     * if a var is bottom before the loop and we add something because of bla } then the merge rules will cause the after set
     * of if to have the something Body A since bottom merged with something is that something THIS WOULD BE INCORRECT
     */
    // this.add(newTuple);
  }

  /*
   * The intersection method is called by the object whose set is to be intersected with otherFlow and the result to be
   * stored in destFlow
   *
   * So we will be intersecting elements of "this" and otherFlow
   *
   * Definition of Intersection: If an element e belongs to Set A then Set A ^ B contains e if B contains an element such
   * that the constantpropagationFlowSet equals method returns true (this will happen if they have the same variable and the
   * same value which better not be TOP)
   *
   * If the element e is not added to set A ^ B then element e should be changed to hold TOP as the value is unknown and that
   * value should be added to the set A ^ B
   *
   *
   */
  public void intersection(FlowSet otherFlow, FlowSet destFlow) {
    // System.out.println("In specialized intersection for CopyPropagation");
    if (!(otherFlow instanceof CPFlowSet && destFlow instanceof CPFlowSet)) {
      super.intersection(otherFlow, destFlow);
      return;
    }

    CPFlowSet other = (CPFlowSet) otherFlow;
    CPFlowSet dest = (CPFlowSet) destFlow;
    CPFlowSet workingSet;

    if (dest == other || dest == this) {
      workingSet = new CPFlowSet();
    } else {
      workingSet = dest;
      workingSet.clear();
    }

    /*
     * HERE IS THE MERGE TABLE
     *
     *
     * THIS OTHER RESULT
     *
     * 1 BOTTOM BOTTOM WHO CARES 2 BOTTOM C C (Laurie Convinced me) 3 BOTTOM TOP TOP 4 C BOTTOM C (Laurie Convinced me) 5 C1
     * C2 C if C1 == C2 else TOP 6 C TOP TOP 7 TOP BOTTOM TOP 8 TOP C TOP 9 TOP TOP TOP
     */

    for (int i = 0; i < this.numElements; i++) {
      CPTuple thisTuple = this.getElementAt(i);
      String className = thisTuple.getSootClassName();
      CPVariable thisVar = thisTuple.getVariable();

      CPTuple matchFound = null;
      /*
       * Find a matching tuple if one exists
       */
      CPTuple otherTuple = null;
      for (int j = 0; j < other.numElements; j++) {
        otherTuple = other.getElementAt(j);

        /*
         * wont use the CPTuple equal method since that ignores tops we want to implement the intersection rules given in the
         * merge table
         */

        // check that the two tuples have the same class
        String tempClass = otherTuple.getSootClassName();
        if (!tempClass.equals(className)) {
          continue;
        }

        // Check that the variable contained is the same name and type (local or sootfield)
        if (!otherTuple.getVariable().equals(thisVar)) {
          continue;
        }

        // match of sootClass and Variable have to use merge table on matchFound and elements[i]
        matchFound = otherTuple;
        break;
      } // end looking for a match in other set

      if (matchFound != null) {
        // cases 5 6 8 and 9 are the ones in which both sets have a value for this variable

        if (thisTuple.isTop()) {
          // cases 8 and 9
          workingSet.add(thisTuple.clone());
        } else if (matchFound.isTop()) {
          // case 6
          workingSet.add(matchFound.clone());
        } else if (!matchFound.isTop() && !thisTuple.isTop()) {
          // this has to be case 5 since there is no other case possible

          Object matchedValue = matchFound.getValue();
          Object thisValue = thisTuple.getValue();

          // using the equals method of Boolean/Float/Integer etc
          if (matchedValue.equals(thisValue)) {
            workingSet.add(thisTuple.clone());
          } else {
            // if we get here either the types dont match or the values didnt match just add top
            workingSet.add(new CPTuple(className, thisVar, true));
          }
        } else {
          throw new DecompilationException("Ran out of cases in CPVariable values...report bug to developer");
        }
      } // if match was found
      else {
        // could not find a match for element[i] in other hence its bottom in other
        // CASE 4 and 7 (cant be case 1 since element[i]s presence means its not bottom

        // add CLONE OF element[i] to working set unchanged
        /*
         * TODO: why should a field be turned to TOP... a field is only a constant value field and hence should never be
         * changed!!!!!
         *
         * BUG FOUND DUE TO CHROMOSOME benchmark if(Debug.flag) was not being detected as it was being set to top
         *
         * April 3rd 2006
         */
        /*
         * if(thisTuple.containsField()){ //add top workingSet.add(new
         * CPTuple(thisTuple.getSootClassName(),thisTuple.getVariable(),true)); } else if(thisTuple.containsLocal()){
         */

        workingSet.add(thisTuple.clone());

        /*
         * } else throw new DecompilationException("CPVariable is not local and not field");
         */

      }

    } // end going through all elements of this flowset

    /*
     * havent covered cases 2 and 3 in which case this has bottom (a.k.a variable is not present) and the other has the
     * elements
     */

    for (int i = 0; i < other.numElements; i++) {
      CPTuple otherTuple = other.getElementAt(i);
      String otherClassName = otherTuple.getSootClassName();
      CPVariable otherVar = otherTuple.getVariable();
      // System.out.print("\t Other:"+otherVar.toString());
      boolean inBoth = false;
      for (int j = 0; j < this.numElements; j++) {
        CPTuple thisTuple = this.getElementAt(j);
        String thisClassName = thisTuple.getSootClassName();
        CPVariable thisVar = thisTuple.getVariable();

        if (!otherClassName.equals(thisClassName)) {
          continue;
        }

        if (!thisVar.equals(otherVar)) {
          continue;
        }

        // if we get here we know both sets have this variable so this is not case 2 or 3
        // System.out.println(">>>> FOUND"+thisVar.toString());
        inBoth = true;
        break;
      }

      if (!inBoth) {
        // System.out.println("....NOT FOUND ....SET IS:"+this.toString());
        // not in both so this is case 2 or 3
        /*
         * clone and add if its local
         *
         * if field then add top
         */

        /*
         * TODO why should a field be converted to TOP when all the fiels are only constant value fields
         *//*
            * if(otherTuple.containsField()){ //add top workingSet.add(new
            * CPTuple(otherTuple.getSootClassName(),otherTuple.getVariable(),true)); } else if(otherTuple.containsLocal()){
            */

        workingSet.add(otherTuple.clone());
        /*
         * } else throw new DecompilationException("CPVariable is not local and not field");
         */
      }
    } // end going through other elements
  }

  public CPFlowSet clone() {

    return new CPFlowSet(this);
  }

  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append("Printing CPFlowSet: ");
    for (int i = 0; i < this.numElements; i++) {
      b.append("\n" + ((CPTuple) elements[i]).toString());
    }
    b.append("\n");
    return b.toString();
  }
}
