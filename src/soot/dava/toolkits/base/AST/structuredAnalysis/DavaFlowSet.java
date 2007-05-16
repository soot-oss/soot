/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Nomair A. Naeem
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
 * Maintained by: Nomair A. Naeem
 * Initial code taken from an existing implementation of the AbstractFlowSet in Soot
 */


/*
 * CHANGE LOG:
 * 16 nov, 2005:  Adding <implicitTargets> feature to be able to store mappings of breaks 
 *                and continues implicitly targetting nodes
 * 21 Nov, 2005   * Reasoning that this implmentation is correct. Adding comments
 *                * Refactored addIfNotDuplicate method since the same chunk of code was being used in
 *                  multiple places
 */



package soot.dava.toolkits.base.AST.structuredAnalysis;

import soot.toolkits.scalar.*;

import java.io.Serializable;
import java.util.*;

import soot.dava.internal.AST.*;
import soot.dava.internal.SET.*;
import soot.dava.internal.javaRep.*;
import soot.dava.toolkits.base.AST.traversals.ClosestAbruptTargetFinder;

public class DavaFlowSet extends AbstractFlowSet{


    static final int DEFAULT_SIZE = 8; 
    
    int numElements;
    int maxElements;
    public Object[] elements;

    /**
     * Whenever in a structured flow analysis a break or continue stmt is encountered the current DavaFlowSet
     * is stored in the break/continue list with the appropriate label for the target code.
     * This is how explicit breaks and continues are handled by the analysis framework
     */
    HashMap<Serializable, List<DavaFlowSet>> breakList; 
    HashMap<Serializable, List<DavaFlowSet>> continueList;

    /**
     * To handle implicit breaks and continues the following HashMaps store the DavaFlowSets as value
     * with the key being the targeted piece of code (an ASTNode)
     */
    HashMap<Serializable, List<DavaFlowSet>> implicitBreaks; //map a node and all the dataflowsets due to implicit breaks targetting it
    HashMap<Serializable, List<DavaFlowSet>> implicitContinues; //map a node and all the dataflowsets due to implicit continues targetting it

    public DavaFlowSet(){
        maxElements = DEFAULT_SIZE;
        elements = new Object[DEFAULT_SIZE];
        numElements = 0;
        breakList = new HashMap<Serializable, List<DavaFlowSet>>();
        continueList = new HashMap<Serializable, List<DavaFlowSet>>();
        implicitBreaks = new HashMap<Serializable, List<DavaFlowSet>>();
        implicitContinues = new HashMap<Serializable, List<DavaFlowSet>>();
    }
    
    public DavaFlowSet(DavaFlowSet other){
        numElements = other.numElements;
        maxElements = other.maxElements;
        elements = other.elements.clone();
        breakList = (HashMap<Serializable, List<DavaFlowSet>>)other.breakList.clone();
        continueList = (HashMap<Serializable, List<DavaFlowSet>>)other.continueList.clone();
        implicitBreaks = (HashMap<Serializable, List<DavaFlowSet>>)other.implicitBreaks.clone();
        implicitContinues = (HashMap<Serializable, List<DavaFlowSet>>)other.implicitContinues.clone();
    }
    
    /** Returns true if flowSet is the same type of flow set as this. */
    private boolean sameType(Object flowSet)
    {
        return (flowSet instanceof DavaFlowSet);
    }

    public DavaFlowSet clone()
    {
        return new DavaFlowSet(this);
    }

    public Object emptySet()
    {
        return new DavaFlowSet();
    }

    public void clear()
    {
        numElements = 0;
    }
    
    public int size()
    {
        return numElements;
    }

    public boolean isEmpty()
    {
        return numElements == 0;
    }

    /** Returns a unbacked list of elements in this set. */
    public List toList()
    {
        Object[] copiedElements = new Object[numElements];
        System.arraycopy(elements, 0, copiedElements, 0, numElements);
        return Arrays.asList(copiedElements);
    }

  /* Expand array only when necessary, pointed out by Florian Loitsch
   * March 08, 2002
   */
    public void add(Object e){
    	/* Expand only if necessary! and removes one if too:) */
    	
    	// Add element
            if(!contains(e)) {
              // Expand array if necessary
              if(numElements == maxElements)
                doubleCapacity();
              elements[numElements++] = e;
            }
    }

    private void doubleCapacity()
    {        
        int newSize = maxElements * 2;
                    
        Object[] newElements = new Object[newSize];
                
        System.arraycopy(elements, 0, newElements, 0, numElements);
        elements = newElements;
        maxElements = newSize;
    }    

    public void remove(Object obj)
    {
        int i = 0;
        while (i < this.numElements) {
            if (elements[i].equals(obj))
            {
                elements[i] = elements[--numElements];
                return;
            } else
                i++;
        }
    }

  /**
     * Notice that the union method only merges the elements of the flow set
     * DavaFlowSet also contains information regarding abrupt control flow
     * This should also be merged using the copyInternalDataFrom method
     */
    public void union(FlowSet otherFlow, FlowSet destFlow){
      if (sameType(otherFlow) && sameType(destFlow)) {
        DavaFlowSet other = (DavaFlowSet) otherFlow;
        DavaFlowSet dest = (DavaFlowSet) destFlow;

        // For the special case that dest == other
            if(dest == other)
            {
                for(int i = 0; i < this.numElements; i++)
                    dest.add(this.elements[i]);
            }
        
        // Else, force that dest starts with contents of this
        else {
            if(this != dest)
                copy(dest);

            for(int i = 0; i < other.numElements; i++)
                dest.add(other.elements[i]);
        }
      } else
        super.union(otherFlow, destFlow);
    }


    /**
     * Notice that the intersection method only merges the elements of the flow set
     * DavaFlowSet also contains information regarding abrupt control flow
     * This should also be merged using the copyInternalDataFrom method
     */
    public void intersection(FlowSet otherFlow, FlowSet destFlow)
    {
    	//System.out.println("DAVA FLOWSET INTERSECTION INVOKED!!!");
        if (sameType(otherFlow) && sameType(destFlow)) {
            DavaFlowSet other = (DavaFlowSet) otherFlow;
            DavaFlowSet dest = (DavaFlowSet) destFlow;
            DavaFlowSet workingSet;
            
            if(dest == other || dest == this)
                workingSet = new DavaFlowSet();
            else { 
                workingSet = dest;
                workingSet.clear();
            }
            
            for(int i = 0; i < this.numElements; i++)
            {
                if(other.contains(this.elements[i]))
                    workingSet.add(this.elements[i]);
            }
            
            if(workingSet != dest)
                workingSet.copy(dest);
          } else
            super.intersection(otherFlow, destFlow);
        }



    public void difference(FlowSet otherFlow, FlowSet destFlow)
    {
      if (sameType(otherFlow) &&
          sameType(destFlow)) {
        DavaFlowSet other = (DavaFlowSet) otherFlow;
        DavaFlowSet dest = (DavaFlowSet) destFlow;
        DavaFlowSet workingSet;
        
        if(dest == other || dest == this)
            workingSet = new DavaFlowSet();
        else { 
            workingSet = dest;
            workingSet.clear();
        }
        
        for(int i = 0; i < this.numElements; i++)
        {
            if(!other.contains(this.elements[i]))
                workingSet.add(this.elements[i]);
        }
        
        if(workingSet != dest)
            workingSet.copy(dest);
      } else
        super.difference(otherFlow, destFlow);
    }


    
    public boolean contains(Object obj)
    {
        for(int i = 0; i < numElements; i++)
            if(elements[i].equals(obj))
                return true;
                
        return false;
    }




    /**
     * Notice that the equals method only checks the equality of  the elements of the flow set
     * DavaFlowSet also contains information regarding abrupt control flow
     * This should also be checked by invoking the internalDataMatchesTo method
     */

    public boolean equals(Object otherFlow)
    {
      if (sameType(otherFlow)) {
        DavaFlowSet other = (DavaFlowSet) otherFlow;
         
        if(other.numElements != this.numElements)
            return false;
     
        int size = this.numElements;
             
        // Make sure that thisFlow is contained in otherFlow  
            for(int i = 0; i < size; i++)
                if(!other.contains(this.elements[i]))
                    return false;

            /* both arrays have the same size, no element appears twice in one
             * array, all elements of ThisFlow are in otherFlow -> they are
             * equal!  we don't need to test again!
        // Make sure that otherFlow is contained in ThisFlow        
            for(int i = 0; i < size; i++)
                if(!this.contains(other.elements[i]))
                    return false;
             */
        
        return true;
      } else
        return super.equals(otherFlow);
    }

    public void copy(FlowSet destFlow){
    	if (sameType(destFlow)) {
    		DavaFlowSet dest = (DavaFlowSet) destFlow;

    		while(dest.maxElements < this.maxElements)
    			dest.doubleCapacity();
    
    		dest.numElements = this.numElements;
        
    		System.arraycopy(this.elements, 0, dest.elements, 0, this.numElements);
    	} 
    	else
    		super.copy(destFlow);
    }



    /**
     * A private method used to add an element into a List if it is NOT a duplicate
     */
    private List<DavaFlowSet> addIfNotDuplicate(List<DavaFlowSet> into, DavaFlowSet addThis){
	//if set is not already present in the labelsBreakList then add it
	Iterator<DavaFlowSet> it = into.iterator();
	boolean found=false;
	while(it.hasNext()){
	    DavaFlowSet temp = it.next();
	    if(temp.equals(addThis) && temp.internalDataMatchesTo(addThis)){
		found=true;
		break;
	    }
	}
	if(!found)
	    into.add(addThis);
	return into;
    }


    /**
     * When an explicit break statement is encountered this method should be called
     * to store the current davaflowset 
     */
    public void addToBreakList(String labelBroken, DavaFlowSet set){
	Object obj = breakList.get(labelBroken);
	if(obj == null){
	    List<DavaFlowSet> labelsBreakList = new ArrayList<DavaFlowSet>();
	    labelsBreakList.add(set);
	    breakList.put(labelBroken,labelsBreakList);
	    //System.out.println("ADDED"+labelBroken+" with"+set.toString());
	}
	else{
	    List<DavaFlowSet> labelsBreakList = (List<DavaFlowSet>)obj;
	    //add set into this list if its not a duplicate and update the hashMap
	    breakList.put(labelBroken,addIfNotDuplicate(labelsBreakList,set));
	}
    }




    /**
     * When an explicit continue statement is encountered this method should be called
     * to store the current davaflowset 
     */
    public void addToContinueList(String labelContinued, DavaFlowSet set){
	Object obj = continueList.get(labelContinued);
	if(obj == null){
	    List<DavaFlowSet> labelsContinueList = new ArrayList<DavaFlowSet>();
	    labelsContinueList.add(set);
	    continueList.put(labelContinued,labelsContinueList);

	}
	else{
	    List<DavaFlowSet> labelsContinueList = (List<DavaFlowSet>)obj;
	    continueList.put(labelContinued,addIfNotDuplicate(labelsContinueList,set));
	}
    }



    /**
     * Checks whether the input stmt is an implicit break/continue
     * A abrupt stmt is implicit if the SETLabelNode is null or the label.toString results in null
     */
    private boolean checkImplicit(DAbruptStmt ab){
	SETNodeLabel label = ab.getLabel();
	if(label==null)
	    return true;
	if(label.toString()==null)
	    return true;
	return false;
    }

    /**
     * The next two methods take an abruptStmt as input along with a flowSet.
     * It should be only invoked for abrupt stmts which do not have explicit labels

     * The node being targeted by this implicit stmt should be found 
     * Then the flow set should be added to the list within the appropriate hashmap
     */
    public void addToImplicitBreaks(DAbruptStmt ab, DavaFlowSet set){
	if(!checkImplicit(ab))
	    throw new RuntimeException("Tried to add explicit break statement in the implicit list in");

	if(!ab.is_Break())
	    throw new RuntimeException("Tried to add continue statement in the break list in DavaFlowSet.addToImplicitBreaks");

	//okkay so its an implicit break
	//get the targetted node, use the ClosestAbruptTargetFinder
	ASTNode node = ClosestAbruptTargetFinder.v().getTarget(ab);
	
	//get the list of flow sets already stored for this node
	Object list = implicitBreaks.get(node);
	ArrayList<DavaFlowSet> listSets = null;
	if(list == null){
	    listSets = new ArrayList<DavaFlowSet>();
	}
	else{
	    //if not null
	    listSets = (ArrayList<DavaFlowSet>)list;
	}

	//if set is not already present in listSets add it and update hashMap
	implicitBreaks.put(node,addIfNotDuplicate(listSets,set));	
    }
    
    public void addToImplicitContinues(DAbruptStmt ab, DavaFlowSet set){
	if(!checkImplicit(ab))
	    throw new RuntimeException("Tried to add explicit continue statement in the implicit list ");

	if(!ab.is_Continue())
	    throw new RuntimeException("Tried to add break statement in the continue list");

	//okkay so its an implicit continue
	//get the targetted node, use the ClosestAbruptTargetFinder
	ASTNode node = ClosestAbruptTargetFinder.v().getTarget(ab);
	
	//get the list of flow sets already stored for this node
	Object list = implicitContinues.get(node);
	ArrayList<DavaFlowSet> listSets = null;
	if(list == null){
	    listSets = new ArrayList<DavaFlowSet>();
	}
	else{
	    //if not null
	    listSets = (ArrayList<DavaFlowSet>)list;
	}

	//if set is not already present in listSets add it and update hashMap
	implicitContinues.put(node,addIfNotDuplicate(listSets,set));
    }



    private HashMap<Serializable, List<DavaFlowSet>> getBreakList(){
	return breakList;
    }

    private HashMap<Serializable, List<DavaFlowSet>> getContinueList(){
	return continueList;
    }

    public HashMap<Serializable, List<DavaFlowSet>> getImplicitBreaks(){
	return implicitBreaks;
    }

    public HashMap<Serializable, List<DavaFlowSet>> getImplicitContinues(){
	return implicitContinues;
    }


    public List getImplicitlyBrokenSets(ASTNode node){	
	Object toReturn = implicitBreaks.get(node);
	if(toReturn != null)
	    return (List)toReturn;
	return null;
    }

    public List getImplicitlyContinuedSets(ASTNode node){
	Object toReturn = implicitContinues.get(node);
	if(toReturn != null)
	    return (List)toReturn;
	return null;
    }





    /** 
     * An internal method used to copy non-duplicate entries from the temp list 
     * into the currentList
     */
    private List<DavaFlowSet> copyDavaFlowSetList(List<DavaFlowSet> currentList, List<DavaFlowSet> temp){
	Iterator<DavaFlowSet> tempIt = temp.iterator();
	while(tempIt.hasNext()){
	    DavaFlowSet check = tempIt.next();
	    Iterator<DavaFlowSet> currentListIt = currentList.iterator();
	    boolean found=false;
	    while(currentListIt.hasNext()){
		//see if currentList has check
		DavaFlowSet currentSet = currentListIt.next();
		if(check.equals(currentSet) && check.internalDataMatchesTo(currentSet)){
		    found=true;
		    break;
		}
	    }
	    if(!found){
		currentList.add(check);
	    }
	}
	return currentList;
    }
    
    public void copyInternalDataFrom(Object fromThis){
	if(!sameType(fromThis))
	    return;

	//copy elements of breaklist
	{
	    HashMap<Serializable, List<DavaFlowSet>> fromThisBreakList = ((DavaFlowSet)fromThis).getBreakList();

	    Iterator<Serializable> keys = fromThisBreakList.keySet().iterator();
	    while(keys.hasNext()){
		String labelBroken = (String)keys.next();
		List<DavaFlowSet> temp = fromThisBreakList.get(labelBroken);
		
		Object list = breakList.get(labelBroken);

		if(list == null){
		    breakList.put(labelBroken,temp);
		}
		else{
		List<DavaFlowSet> currentList = (List<DavaFlowSet>)list;
		    List<DavaFlowSet> complete = copyDavaFlowSetList(currentList,temp);
		    breakList.put(labelBroken,complete);
		}
	    }
	}



	    
       	//copy elements of continuelist
	{
	    HashMap<Serializable, List<DavaFlowSet>> fromThisContinueList = ((DavaFlowSet)fromThis).getContinueList();
	    
	    Iterator<Serializable> keys = fromThisContinueList.keySet().iterator();
	    while(keys.hasNext()){
		String labelContinued = (String)keys.next();
		List<DavaFlowSet> temp = fromThisContinueList.get(labelContinued);
		
		Object list = continueList.get(labelContinued);
		if(list == null){
		    continueList.put(labelContinued,temp);
		}
		else{
		    List<DavaFlowSet> currentList = (List<DavaFlowSet>)list;
		    List<DavaFlowSet> complete = copyDavaFlowSetList(currentList,temp);
		    continueList.put(labelContinued,currentList);
		}
	    }
	}
	
	//copy elements of implicitBreaks
	//this hashMap contains a mapping of ASTNodes to DavaFlowSets due to impicit breaks
	{
	    HashMap<Serializable, List<DavaFlowSet>> copyThis = ((DavaFlowSet)fromThis).getImplicitBreaks();
	    Iterator<Serializable> it = copyThis.keySet().iterator();
	    while(it.hasNext()){ //going through all nodes in the other objects implicitBreaks hashMap
		//each is a node
		ASTNode node = (ASTNode)it.next();
		//get list of dava flow sets targetting this node implicitly
		ArrayList<DavaFlowSet> fromDavaFlowSets = (ArrayList<DavaFlowSet>)copyThis.get(node);
		//Have copy non duplicates in this to the implicitbreak hashMap the current dava flow set has
		

		Object list = implicitBreaks.get(node);
		if(list==null){
		    //there was no dava flow set currently targetting this node implicitly
		    //put the fromDavaFlowSets into the hashMap
		    implicitBreaks.put(node,fromDavaFlowSets);
		}
		else{
		    ArrayList<DavaFlowSet> toDavaFlowSets = (ArrayList<DavaFlowSet>)list;
		    List<DavaFlowSet> complete = copyDavaFlowSetList(toDavaFlowSets,fromDavaFlowSets);
		    implicitBreaks.put(node,complete);		    
		}
	    }
	}

	//copy elements of implicitContinues
	//this hashMap contains a mapping of ASTNodes to DavaFlowSets due to impicit continues
	{
	    HashMap<Serializable, List<DavaFlowSet>> copyThis = ((DavaFlowSet)fromThis).getImplicitContinues();
	    Iterator<Serializable> it = copyThis.keySet().iterator();
	    while(it.hasNext()){ //going through all nodes in the other objects implicitcontinues hashMap
		//each is a node
		ASTNode node = (ASTNode)it.next();
		//get list of dava flow sets targetting this node implicitly
		ArrayList<DavaFlowSet> fromDavaFlowSets = (ArrayList<DavaFlowSet>)copyThis.get(node);
		//Have copy non duplicates in this to the implicitContinue hashMap the current dava flow set has
		


		Object list = implicitContinues.get(node);
		if(list==null){
		    //there was no dava flow set currently targetting this node implicitly
		    //put the fromDavaFlowSets into the hashMap
		    implicitContinues.put(node,fromDavaFlowSets);
		}
		else{
		    ArrayList<DavaFlowSet> toDavaFlowSets = (ArrayList<DavaFlowSet>)list;
		    List<DavaFlowSet> complete = copyDavaFlowSetList(toDavaFlowSets,fromDavaFlowSets);
		    implicitContinues.put(node,complete);		    
		}
	    }
	    
	}

    }

    private boolean compareLists(Object One , Object Two){
	if(One==null && Two == null)
	    return true;

	if(One == null || Two == null)
	    return false;

	List listOne = (List)One;
	List listTwo = (List)Two;

	//compare elements of the list
	if(listOne.size()!= listTwo.size()){
	    //size has to be same for lists to match
	    return false;
	}
	Iterator listOneIt = listOne.iterator();
	boolean found=false;
	while(listOneIt.hasNext()){
	    //going through the first list
	    Object listOneObj = listOneIt.next();


	    Iterator listTwoIt = listTwo.iterator();
	    while(listTwoIt.hasNext()){
		//find the object in the second list
		Object listTwoObj = listTwoIt.next();
		if(listOneObj.equals(listTwoObj)){
		    //if object is found stop search
		    found=true;
		    break;
		}
	    }
	    if(!found){
		//if didnt find object return false
		return false;
	    }
	    found=false;
	}
	return true;
    }

    public boolean internalDataMatchesTo(Object otherObj){
	if(!(otherObj instanceof DavaFlowSet))
	    return false;

	DavaFlowSet other = (DavaFlowSet)otherObj;

	//check if same break list
	HashMap<Serializable, List<DavaFlowSet>> otherMap = other.getBreakList();
	if( ! compareHashMaps(breakList,otherMap) )
	    return false;


	//check if same continue list
	otherMap = other.getContinueList();
	if( ! compareHashMaps(continueList,otherMap) )
	    return false;


	//check implicitBreaks match
	otherMap = other.getImplicitBreaks();
	if( ! compareHashMaps(implicitBreaks,otherMap) )
	    return false;

	//check implicitContinues match
	otherMap = other.getImplicitContinues();
	if( ! compareHashMaps(implicitContinues,otherMap) )
	    return false;
	
	return true;
    }


    private boolean compareHashMaps(HashMap<Serializable, List<DavaFlowSet>> thisMap,HashMap<Serializable, List<DavaFlowSet>> otherMap){
	List<String> otherKeyList = new ArrayList<String>();
	
	Iterator<Serializable> keys = otherMap.keySet().iterator();
	while(keys.hasNext()){
	    String otherKey = (String)keys.next();
	    otherKeyList.add(otherKey);
	    
	    Object listOther = otherMap.get(otherKey);
	    Object listThis = thisMap.get(otherKey);

	    //compare the two lists
	    if(!compareLists(listOther,listThis)){
		//if lists dont match internalData doesnt match
		return false;
	    }
	}
	//have gone through otherMap
	
	//going through thisMap
	keys = thisMap.keySet().iterator();
	while(keys.hasNext()){
	    String key = (String)keys.next();
	    
	    Iterator<String> keyListIt = otherKeyList.iterator();
	    boolean alreadyDone=false;
	    
	    while(keyListIt.hasNext()){
		String doneKey = keyListIt.next();
		if(key.equals(doneKey)){
		    alreadyDone=true;
		    break;
		}
	    }
	    if(!alreadyDone){
		/*
		  we have come across a label
		  which was not done by the first hashmap
		  meaning it was NOT in the first hashMap
		*/
		return false;
	    }
	}
	return true;
    }


    public List getContinueSet(String label){
	return continueList.remove(label);
    }


    public List getBreakSet(String label){
	return breakList.remove(label);
    }



    public String toString(){
	StringBuffer b = new StringBuffer();
	b.append(" SET={");
        for(int i = 0; i < this.numElements; i++){
	    if(i!=0)
		b.append(" , ");

	    b.append(this.elements[i].toString());
	}
	b.append(" }");
	return b.toString();
    }




    /*    public String toString(){
	StringBuffer b = new StringBuffer();
	b.append("\nSETTTTT\n");
        for(int i = 0; i < this.numElements; i++){
	    b.append("\t"+this.elements[i]+"\n");
	}
	b.append("BREAK LIST\n");
	b.append("\t"+breakList.toString()+"\n");
	

	b.append("CONTINUE LIST\n");
	b.append("\t"+continueList.toString()+"\n");

	b.append("EXCEPTION LIST\n");
	b.append("\t"+exceptionList.toString()+"\n");
	return b.toString();
    }
    */
}
