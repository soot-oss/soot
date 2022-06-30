package soot.dava.toolkits.base.AST.structuredAnalysis;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Nomair A. Naeem
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.SET.SETNodeLabel;
import soot.dava.internal.javaRep.DAbruptStmt;
import soot.dava.toolkits.base.AST.traversals.ClosestAbruptTargetFinder;
import soot.toolkits.scalar.AbstractFlowSet;
import soot.toolkits.scalar.FlowSet;

public class DavaFlowSet<T> extends AbstractFlowSet<T> {

  static final int DEFAULT_SIZE = 8;

  int numElements;
  int maxElements;
  protected T[] elements;

  /**
   * Whenever in a structured flow analysis a break or continue stmt is encountered the current DavaFlowSet is stored in the
   * break/continue list with the appropriate label for the target code. This is how explicit breaks and continues are
   * handled by the analysis framework
   */
  HashMap<Serializable, List<DavaFlowSet<T>>> breakList;
  HashMap<Serializable, List<DavaFlowSet<T>>> continueList;

  /**
   * To handle implicit breaks and continues the following HashMaps store the DavaFlowSets as value with the key being the
   * targeted piece of code (an ASTNode)
   */
  HashMap<Serializable, List<DavaFlowSet<T>>> implicitBreaks; // map a node
  // and all the
  // dataflowsets
  // due to
  // implicit
  // breaks
  // targetting it
  HashMap<Serializable, List<DavaFlowSet<T>>> implicitContinues; // map a node
  // and all
  // the
  // dataflowsets
  // due to
  // implicit
  // continues
  // targetting
  // it

  public DavaFlowSet() {
    maxElements = DEFAULT_SIZE;
    elements = (T[]) new Object[DEFAULT_SIZE];
    numElements = 0;
    breakList = new HashMap<Serializable, List<DavaFlowSet<T>>>();
    continueList = new HashMap<Serializable, List<DavaFlowSet<T>>>();
    implicitBreaks = new HashMap<Serializable, List<DavaFlowSet<T>>>();
    implicitContinues = new HashMap<Serializable, List<DavaFlowSet<T>>>();
  }

  public DavaFlowSet(DavaFlowSet<T> other) {
    numElements = other.numElements;
    maxElements = other.maxElements;
    elements = other.elements.clone();
    breakList = (HashMap<Serializable, List<DavaFlowSet<T>>>) other.breakList.clone();
    continueList = (HashMap<Serializable, List<DavaFlowSet<T>>>) other.continueList.clone();
    implicitBreaks = (HashMap<Serializable, List<DavaFlowSet<T>>>) other.implicitBreaks.clone();
    implicitContinues = (HashMap<Serializable, List<DavaFlowSet<T>>>) other.implicitContinues.clone();
  }

  /** Returns true if flowSet is the same type of flow set as this. */
  private boolean sameType(Object flowSet) {
    return (flowSet instanceof DavaFlowSet);
  }

  public DavaFlowSet<T> clone() {
    return new DavaFlowSet<T>(this);
  }

  public FlowSet<T> emptySet() {
    return new DavaFlowSet<T>();
  }

  public void clear() {
    numElements = 0;
  }

  public int size() {
    return numElements;
  }

  public boolean isEmpty() {
    return numElements == 0;
  }

  /** Returns a unbacked list of elements in this set. */
  public List<T> toList() {
    @SuppressWarnings("unchecked")
    T[] copiedElements = (T[]) new Object[numElements];
    System.arraycopy(elements, 0, copiedElements, 0, numElements);
    return Arrays.asList(copiedElements);
  }

  /*
   * Expand array only when necessary, pointed out by Florian Loitsch March 08, 2002
   */
  @Override
  public void add(T e) {
    /* Expand only if necessary! and removes one if too:) */

    // Add element
    if (!contains(e)) {
      // Expand array if necessary
      if (numElements == maxElements) {
        doubleCapacity();
      }
      elements[numElements++] = e;
    }
  }

  private void doubleCapacity() {
    int newSize = maxElements * 2;

    @SuppressWarnings("unchecked")
    T[] newElements = (T[]) new Object[newSize];

    System.arraycopy(elements, 0, newElements, 0, numElements);
    elements = newElements;
    maxElements = newSize;
  }

  @Override
  public void remove(Object obj) {
    for (int i = 0; i < numElements; i++) {
      if (elements[i].equals(obj)) {
        remove(i);
        break;
      }
    }
  }

  public void remove(int idx) {
    elements[idx] = elements[--numElements];
  }

  /**
   * Notice that the union method only merges the elements of the flow set DavaFlowSet also contains information regarding
   * abrupt control flow This should also be merged using the copyInternalDataFrom method
   */
  public void union(FlowSet<T> otherFlow, FlowSet<T> destFlow) {
    if (sameType(otherFlow) && sameType(destFlow)) {
      DavaFlowSet<T> other = (DavaFlowSet<T>) otherFlow;
      DavaFlowSet<T> dest = (DavaFlowSet<T>) destFlow;

      // For the special case that dest == other
      if (dest == other) {
        for (int i = 0; i < this.numElements; i++) {
          dest.add(this.elements[i]);
        }
      }

      // Else, force that dest starts with contents of this
      else {
        if (this != dest) {
          copy(dest);
        }

        for (int i = 0; i < other.numElements; i++) {
          dest.add(other.elements[i]);
        }
      }
    } else {
      super.union(otherFlow, destFlow);
    }
  }

  /**
   * Notice that the intersection method only merges the elements of the flow set DavaFlowSet also contains information
   * regarding abrupt control flow This should also be merged using the copyInternalDataFrom method
   */
  public void intersection(FlowSet<T> otherFlow, FlowSet<T> destFlow) {
    // System.out.println("DAVA FLOWSET INTERSECTION INVOKED!!!");
    if (sameType(otherFlow) && sameType(destFlow)) {
      DavaFlowSet<T> other = (DavaFlowSet<T>) otherFlow;
      DavaFlowSet<T> dest = (DavaFlowSet<T>) destFlow;
      DavaFlowSet<T> workingSet;

      if (dest == other || dest == this) {
        workingSet = new DavaFlowSet<T>();
      } else {
        workingSet = dest;
        workingSet.clear();
      }

      for (int i = 0; i < this.numElements; i++) {
        if (other.contains(this.elements[i])) {
          workingSet.add(this.elements[i]);
        }
      }

      if (workingSet != dest) {
        workingSet.copy(dest);
      }
    } else {
      super.intersection(otherFlow, destFlow);
    }
  }

  public void difference(FlowSet<T> otherFlow, FlowSet<T> destFlow) {
    if (sameType(otherFlow) && sameType(destFlow)) {
      DavaFlowSet<T> other = (DavaFlowSet<T>) otherFlow;
      DavaFlowSet<T> dest = (DavaFlowSet<T>) destFlow;
      DavaFlowSet<T> workingSet;

      if (dest == other || dest == this) {
        workingSet = new DavaFlowSet<T>();
      } else {
        workingSet = dest;
        workingSet.clear();
      }

      for (int i = 0; i < this.numElements; i++) {
        if (!other.contains(this.elements[i])) {
          workingSet.add(this.elements[i]);
        }
      }

      if (workingSet != dest) {
        workingSet.copy(dest);
      }
    } else {
      super.difference(otherFlow, destFlow);
    }
  }

  public boolean contains(Object obj) {
    for (int i = 0; i < numElements; i++) {
      if (elements[i].equals(obj)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Notice that the equals method only checks the equality of the elements of the flow set DavaFlowSet also contains
   * information regarding abrupt control flow This should also be checked by invoking the internalDataMatchesTo method
   */
  public boolean equals(Object otherFlow) {
    if (sameType(otherFlow)) {
      @SuppressWarnings("unchecked")
      DavaFlowSet<T> other = (DavaFlowSet<T>) otherFlow;

      if (other.numElements != this.numElements) {
        return false;
      }

      int size = this.numElements;

      // Make sure that thisFlow is contained in otherFlow
      for (int i = 0; i < size; i++) {
        if (!other.contains(this.elements[i])) {
          return false;
        }
      }

      /*
       * both arrays have the same size, no element appears twice in one array, all elements of ThisFlow are in otherFlow ->
       * they are equal! we don't need to test again! // Make sure that otherFlow is contained in ThisFlow for(int i = 0; i <
       * size; i++) if(!this.contains(other.elements[i])) return false;
       */

      return true;
    } else {
      return super.equals(otherFlow);
    }
  }

  public void copy(FlowSet<T> destFlow) {
    if (this == destFlow) {
      return;
    }
    if (sameType(destFlow)) {
      DavaFlowSet<T> dest = (DavaFlowSet<T>) destFlow;

      while (dest.maxElements < this.maxElements) {
        dest.doubleCapacity();
      }

      dest.numElements = this.numElements;

      System.arraycopy(this.elements, 0, dest.elements, 0, this.numElements);
    } else {
      super.copy(destFlow);
    }
  }

  /**
   * A private method used to add an element into a List if it is NOT a duplicate
   */
  private List<DavaFlowSet<T>> addIfNotDuplicate(List<DavaFlowSet<T>> into, DavaFlowSet<T> addThis) {
    // if set is not already present in the labelsBreakList then add it
    Iterator<DavaFlowSet<T>> it = into.iterator();
    boolean found = false;
    while (it.hasNext()) {
      DavaFlowSet<T> temp = it.next();
      if (temp.equals(addThis) && temp.internalDataMatchesTo(addThis)) {
        found = true;
        break;
      }
    }
    if (!found) {
      into.add(addThis);
    }
    return into;
  }

  /**
   * When an explicit break statement is encountered this method should be called to store the current davaflowset
   */
  public void addToBreakList(String labelBroken, DavaFlowSet<T> set) {
    List<DavaFlowSet<T>> labelsBreakList = breakList.get(labelBroken);
    if (labelsBreakList == null) {
      labelsBreakList = new ArrayList<DavaFlowSet<T>>();
      labelsBreakList.add(set);
      breakList.put(labelBroken, labelsBreakList);
      // System.out.println("ADDED"+labelBroken+" with"+set.toString());
    } else {
      // add set into this list if its not a duplicate and update the
      // hashMap
      breakList.put(labelBroken, addIfNotDuplicate(labelsBreakList, set));
    }
  }

  /**
   * When an explicit continue statement is encountered this method should be called to store the current davaflowset
   */
  public void addToContinueList(String labelContinued, DavaFlowSet<T> set) {
    List<DavaFlowSet<T>> labelsContinueList = continueList.get(labelContinued);
    if (labelsContinueList == null) {
      labelsContinueList = new ArrayList<DavaFlowSet<T>>();
      labelsContinueList.add(set);
      continueList.put(labelContinued, labelsContinueList);
    } else {
      continueList.put(labelContinued, addIfNotDuplicate(labelsContinueList, set));
    }
  }

  /**
   * Checks whether the input stmt is an implicit break/continue A abrupt stmt is implicit if the SETLabelNode is null or the
   * label.toString results in null
   */
  private boolean checkImplicit(DAbruptStmt ab) {
    SETNodeLabel label = ab.getLabel();
    if (label == null) {
      return true;
    }
    if (label.toString() == null) {
      return true;
    }
    return false;
  }

  /**
   * The next two methods take an abruptStmt as input along with a flowSet. It should be only invoked for abrupt stmts which
   * do not have explicit labels
   *
   * The node being targeted by this implicit stmt should be found Then the flow set should be added to the list within the
   * appropriate hashmap
   */
  public void addToImplicitBreaks(DAbruptStmt ab, DavaFlowSet<T> set) {
    if (!checkImplicit(ab)) {
      throw new RuntimeException("Tried to add explicit break statement in the implicit list in");
    }

    if (!ab.is_Break()) {
      throw new RuntimeException("Tried to add continue statement in the break list in DavaFlowSet.addToImplicitBreaks");
    }

    // okkay so its an implicit break
    // get the targetted node, use the ClosestAbruptTargetFinder
    ASTNode node = ClosestAbruptTargetFinder.v().getTarget(ab);

    // get the list of flow sets already stored for this node
    List<DavaFlowSet<T>> listSets = implicitBreaks.get(node);
    if (listSets == null) {
      listSets = new ArrayList<DavaFlowSet<T>>();
    }

    // if set is not already present in listSets add it and update hashMap
    implicitBreaks.put(node, addIfNotDuplicate(listSets, set));
  }

  public void addToImplicitContinues(DAbruptStmt ab, DavaFlowSet<T> set) {
    if (!checkImplicit(ab)) {
      throw new RuntimeException("Tried to add explicit continue statement in the implicit list ");
    }

    if (!ab.is_Continue()) {
      throw new RuntimeException("Tried to add break statement in the continue list");
    }

    // okkay so its an implicit continue
    // get the targetted node, use the ClosestAbruptTargetFinder
    ASTNode node = ClosestAbruptTargetFinder.v().getTarget(ab);

    // get the list of flow sets already stored for this node
    List<DavaFlowSet<T>> listSets = implicitContinues.get(node);
    if (listSets == null) {
      listSets = new ArrayList<DavaFlowSet<T>>();
    }

    // if set is not already present in listSets add it and update hashMap
    implicitContinues.put(node, addIfNotDuplicate(listSets, set));
  }

  private HashMap<Serializable, List<DavaFlowSet<T>>> getBreakList() {
    return breakList;
  }

  private HashMap<Serializable, List<DavaFlowSet<T>>> getContinueList() {
    return continueList;
  }

  public HashMap<Serializable, List<DavaFlowSet<T>>> getImplicitBreaks() {
    return implicitBreaks;
  }

  public HashMap<Serializable, List<DavaFlowSet<T>>> getImplicitContinues() {
    return implicitContinues;
  }

  public List<DavaFlowSet<T>> getImplicitlyBrokenSets(ASTNode node) {
    List<DavaFlowSet<T>> toReturn = implicitBreaks.get(node);
    if (toReturn != null) {
      return toReturn;
    }
    return null;
  }

  public List<DavaFlowSet<T>> getImplicitlyContinuedSets(ASTNode node) {
    List<DavaFlowSet<T>> toReturn = implicitContinues.get(node);
    if (toReturn != null) {
      return toReturn;
    }
    return null;
  }

  /**
   * An internal method used to copy non-duplicate entries from the temp list into the currentList
   */
  private List<DavaFlowSet<T>> copyDavaFlowSetList(List<DavaFlowSet<T>> currentList, List<DavaFlowSet<T>> temp) {
    Iterator<DavaFlowSet<T>> tempIt = temp.iterator();
    while (tempIt.hasNext()) {
      DavaFlowSet<T> check = tempIt.next();
      Iterator<DavaFlowSet<T>> currentListIt = currentList.iterator();
      boolean found = false;
      while (currentListIt.hasNext()) {
        // see if currentList has check
        DavaFlowSet<T> currentSet = currentListIt.next();
        if (check.equals(currentSet) && check.internalDataMatchesTo(currentSet)) {
          found = true;
          break;
        }
      }
      if (!found) {
        currentList.add(check);
      }
    }
    return currentList;
  }

  public void copyInternalDataFrom(DavaFlowSet<T> fromThis) {
    if (!sameType(fromThis)) {
      return;
    }

    // copy elements of breaklist
    {
      Map<Serializable, List<DavaFlowSet<T>>> fromThisBreakList = fromThis.getBreakList();

      Iterator<Serializable> keys = fromThisBreakList.keySet().iterator();
      while (keys.hasNext()) {
        String labelBroken = (String) keys.next();
        List<DavaFlowSet<T>> temp = fromThisBreakList.get(labelBroken);
        List<DavaFlowSet<T>> currentList = breakList.get(labelBroken);

        if (currentList == null) {
          breakList.put(labelBroken, temp);
        } else {
          List<DavaFlowSet<T>> complete = copyDavaFlowSetList(currentList, temp);
          breakList.put(labelBroken, complete);
        }
      }
    }

    // copy elements of continuelist
    {
      HashMap<Serializable, List<DavaFlowSet<T>>> fromThisContinueList = fromThis.getContinueList();

      Iterator<Serializable> keys = fromThisContinueList.keySet().iterator();
      while (keys.hasNext()) {
        String labelContinued = (String) keys.next();
        List<DavaFlowSet<T>> temp = fromThisContinueList.get(labelContinued);
        List<DavaFlowSet<T>> currentList = continueList.get(labelContinued);
        if (currentList == null) {
          continueList.put(labelContinued, temp);
        } else {
          List<DavaFlowSet<T>> complete = copyDavaFlowSetList(currentList, temp);
          continueList.put(labelContinued, complete);
        }
      }
    }

    // copy elements of implicitBreaks
    // this hashMap contains a mapping of ASTNodes to DavaFlowSets due to
    // impicit breaks
    {
      HashMap<Serializable, List<DavaFlowSet<T>>> copyThis = fromThis.getImplicitBreaks();
      Iterator<Serializable> it = copyThis.keySet().iterator();
      while (it.hasNext()) { // going through all nodes in the other
        // objects implicitBreaks hashMap
        // each is a node
        ASTNode node = (ASTNode) it.next();
        // get list of dava flow sets targetting this node implicitly
        List<DavaFlowSet<T>> fromDavaFlowSets = copyThis.get(node);
        // Have copy non duplicates in this to the implicitbreak hashMap
        // the current dava flow set has

        List<DavaFlowSet<T>> toDavaFlowSets = implicitBreaks.get(node);
        if (toDavaFlowSets == null) {
          // there was no dava flow set currently targetting this node
          // implicitly
          // put the fromDavaFlowSets into the hashMap
          implicitBreaks.put(node, fromDavaFlowSets);
        } else {
          List<DavaFlowSet<T>> complete = copyDavaFlowSetList(toDavaFlowSets, fromDavaFlowSets);
          implicitBreaks.put(node, complete);
        }
      }
    }

    // copy elements of implicitContinues
    // this hashMap contains a mapping of ASTNodes to DavaFlowSets due to
    // impicit continues
    {
      HashMap<Serializable, List<DavaFlowSet<T>>> copyThis = fromThis.getImplicitContinues();
      Iterator<Serializable> it = copyThis.keySet().iterator();
      while (it.hasNext()) { // going through all nodes in the other
        // objects implicitcontinues hashMap
        // each is a node
        ASTNode node = (ASTNode) it.next();
        // get list of dava flow sets targetting this node implicitly
        List<DavaFlowSet<T>> fromDavaFlowSets = copyThis.get(node);
        // Have copy non duplicates in this to the implicitContinue
        // hashMap the current dava flow set has

        List<DavaFlowSet<T>> toDavaFlowSets = implicitContinues.get(node);
        if (toDavaFlowSets == null) {
          // there was no dava flow set currently targetting this node
          // implicitly
          // put the fromDavaFlowSets into the hashMap
          implicitContinues.put(node, fromDavaFlowSets);
        } else {
          List<DavaFlowSet<T>> complete = copyDavaFlowSetList(toDavaFlowSets, fromDavaFlowSets);
          implicitContinues.put(node, complete);
        }
      }

    }

  }

  private <X> boolean compareLists(List<X> listOne, List<X> listTwo) {
    if (listOne == null && listTwo == null) {
      return true;
    }
    if (listOne == null || listTwo == null) {
      return false;
    }

    // compare elements of the list
    if (listOne.size() != listTwo.size()) {
      // size has to be same for lists to match
      return false;
    }
    Iterator<X> listOneIt = listOne.iterator();
    boolean found = false;
    while (listOneIt.hasNext()) {
      // going through the first list
      Object listOneObj = listOneIt.next();

      Iterator<X> listTwoIt = listTwo.iterator();
      while (listTwoIt.hasNext()) {
        // find the object in the second list
        Object listTwoObj = listTwoIt.next();
        if (listOneObj.equals(listTwoObj)) {
          // if object is found stop search
          found = true;
          break;
        }
      }
      if (!found) {
        // if didnt find object return false
        return false;
      }
      found = false;
    }
    return true;
  }

  public boolean internalDataMatchesTo(Object otherObj) {
    if (!(otherObj instanceof DavaFlowSet)) {
      return false;
    }

    @SuppressWarnings("unchecked")
    DavaFlowSet<T> other = (DavaFlowSet<T>) otherObj;

    // check if same break list
    HashMap<Serializable, List<DavaFlowSet<T>>> otherMap = other.getBreakList();
    if (!compareHashMaps(breakList, otherMap)) {
      return false;
    }

    // check if same continue list
    otherMap = other.getContinueList();
    if (!compareHashMaps(continueList, otherMap)) {
      return false;
    }

    // check implicitBreaks match
    otherMap = other.getImplicitBreaks();
    if (!compareHashMaps(implicitBreaks, otherMap)) {
      return false;
    }

    // check implicitContinues match
    otherMap = other.getImplicitContinues();
    if (!compareHashMaps(implicitContinues, otherMap)) {
      return false;
    }

    return true;
  }

  private boolean compareHashMaps(HashMap<Serializable, List<DavaFlowSet<T>>> thisMap,
      HashMap<Serializable, List<DavaFlowSet<T>>> otherMap) {
    List<String> otherKeyList = new ArrayList<String>();

    Iterator<Serializable> keys = otherMap.keySet().iterator();
    while (keys.hasNext()) {
      String otherKey = (String) keys.next();
      otherKeyList.add(otherKey);

      List<DavaFlowSet<T>> listOther = otherMap.get(otherKey);
      List<DavaFlowSet<T>> listThis = thisMap.get(otherKey);

      // compare the two lists
      if (!compareLists(listOther, listThis)) {
        // if lists dont match internalData doesnt match
        return false;
      }
    }
    // have gone through otherMap

    // going through thisMap
    keys = thisMap.keySet().iterator();
    while (keys.hasNext()) {
      String key = (String) keys.next();

      Iterator<String> keyListIt = otherKeyList.iterator();
      boolean alreadyDone = false;

      while (keyListIt.hasNext()) {
        String doneKey = keyListIt.next();
        if (key.equals(doneKey)) {
          alreadyDone = true;
          break;
        }
      }
      if (!alreadyDone) {
        /*
         * we have come across a label which was not done by the first hashmap meaning it was NOT in the first hashMap
         */
        return false;
      }
    }
    return true;
  }

  public List<DavaFlowSet<T>> getContinueSet(String label) {
    return continueList.remove(label);
  }

  public List<DavaFlowSet<T>> getBreakSet(String label) {
    return breakList.remove(label);
  }

  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append(" SET={");
    for (int i = 0; i < this.numElements; i++) {
      if (i != 0) {
        b.append(" , ");
      }

      b.append(this.elements[i].toString());
    }
    b.append(" }");
    return b.toString();
  }

  @Override
  public Iterator<T> iterator() {
    return new Iterator<T>() {

      int lastIdx = 0;

      @Override
      public boolean hasNext() {
        return lastIdx < numElements;
      }

      @Override
      public T next() {
        return elements[lastIdx++];
      }

      @Override
      public void remove() {
        DavaFlowSet.this.remove(--lastIdx);
      }

    };
  }

  /*
   * public String toString(){ StringBuffer b = new StringBuffer(); b.append("\nSETTTTT\n"); for(int i = 0; i <
   * this.numElements; i++){ b.append("\t"+this.elements[i]+"\n"); } b.append("BREAK LIST\n");
   * b.append("\t"+breakList.toString()+"\n");
   *
   *
   * b.append("CONTINUE LIST\n"); b.append("\t"+continueList.toString()+"\n");
   *
   * b.append("EXCEPTION LIST\n"); b.append("\t"+exceptionList.toString()+"\n"); return b.toString(); }
   */

  public int getElementCount() {
    return elements.length;
  }

  public T getElementAt(int idx) {
    return elements[idx];
  }
}
