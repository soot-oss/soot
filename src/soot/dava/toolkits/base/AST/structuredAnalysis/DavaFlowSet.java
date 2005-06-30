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


package soot.dava.toolkits.base.AST.structuredAnalysis;

import soot.toolkits.scalar.*;
import java.util.*;


public class DavaFlowSet extends AbstractFlowSet{


    static final int DEFAULT_SIZE = 8; 
    
    int numElements;
    int maxElements;
    public Object[] elements;

    public HashMap breakList;
    HashMap continueList;


    public DavaFlowSet()
    {
        maxElements = DEFAULT_SIZE;
        elements = new Object[DEFAULT_SIZE];
        numElements = 0;
	breakList = new HashMap();
	continueList = new HashMap();
    }
    
    private DavaFlowSet(DavaFlowSet other)
    {
        numElements = other.numElements;
        maxElements = other.maxElements;
        elements = (Object[]) other.elements.clone();
	breakList = (HashMap)other.breakList.clone();
	continueList = (HashMap)other.continueList.clone();
    }
    
    /** Returns true if flowSet is the same type of flow set as this. */
    private boolean sameType(Object flowSet)
    {
        return (flowSet instanceof DavaFlowSet);
    }

    public Object clone()
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
    public void add(Object e)
    {
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

  /* copy last element to the position of deleted element, and
   * decrease the array size.
   * pointed out by Florian Loitsch, March 2002
   */
    private void removeElementAt(int index)
    {
      elements[index] = elements[--numElements];
    }

    public void union(FlowSet otherFlow, FlowSet destFlow)
    {
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

    public void intersection(FlowSet otherFlow, FlowSet destFlow)
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

    public void copy(FlowSet destFlow)
    {
      if (sameType(destFlow)) {
        DavaFlowSet dest = (DavaFlowSet) destFlow;

        while(dest.maxElements < this.maxElements)
            dest.doubleCapacity();
    
        dest.numElements = this.numElements;
        
        System.arraycopy(this.elements, 0,
            dest.elements, 0, this.numElements);
      } else
        super.copy(destFlow);
    }




    public void addToBreakList(String labelBroken, DavaFlowSet set){
	Object obj = breakList.get(labelBroken);
	if(obj == null){
	    List labelsBreakList = new ArrayList();
	    labelsBreakList.add(set);
	    breakList.put(labelBroken,labelsBreakList);
	    //System.out.println("ADDED"+labelBroken+" with"+set.toString());
	}
	else{
	    List labelsBreakList = (List)obj;

	    //if set is not already present in the labelsBreakList then add it
	    Iterator it = labelsBreakList.iterator();
	    boolean found=false;
	    while(it.hasNext()){
		DavaFlowSet temp = (DavaFlowSet)it.next();
		if(temp.equals(set) && temp.internalDataMatchesTo(set)){
		    found=true;
		    break;
		}
	    }
	    if(!found){
		labelsBreakList.add(set);
		breakList.put(labelBroken,labelsBreakList);
		//System.out.println("ADDED"+labelBroken+" with"+set.toString());
	    }
	}
    }





    public void addToContinueList(String labelContinued, DavaFlowSet set){
	Object obj = continueList.get(labelContinued);
	if(obj == null){
	    List labelsContinueList = new ArrayList();
	    labelsContinueList.add(set);
	    continueList.put(labelContinued,labelsContinueList);

	}
	else{
	    List labelsContinueList = (List)obj;

	    //if set is not already present in the labelsContinueList then add it
	    Iterator it = labelsContinueList.iterator();
	    boolean found=false;
	    while(it.hasNext()){
		DavaFlowSet temp = (DavaFlowSet)it.next();
		if(temp.equals(set) && temp.internalDataMatchesTo(set)){
		    found=true;
		    break;
		}
	    }
	    if(!found){
		labelsContinueList.add(set);
		continueList.put(labelContinued,labelsContinueList);
	    }
	}
    }




    private HashMap getBreakList(){
	return breakList;
    }


    private HashMap getContinueList(){
	return continueList;
    }

    
    public void copyInternalDataFrom(Object fromThis){
	if(!sameType(fromThis))
	    return;

	//copy elements of breaklist
	{
	    HashMap fromThisBreakList = ((DavaFlowSet)fromThis).getBreakList();

	    Iterator keys = fromThisBreakList.keySet().iterator();
	    while(keys.hasNext()){
		String labelBroken = (String)keys.next();
		List temp = (List)fromThisBreakList.get(labelBroken);
		
		List currentList = (List)breakList.get(labelBroken);
		if(currentList == null){
		    breakList.put(labelBroken,temp);
		}
		else{
		    //add those elements from temp to currentList which dont already exist there
		    Iterator tempIt = temp.iterator();
		    while(tempIt.hasNext()){
			DavaFlowSet check = (DavaFlowSet)tempIt.next();
			Iterator currentListIt = currentList.iterator();
			boolean found=false;
			while(currentListIt.hasNext()){
			    //see if currentList has check
			    DavaFlowSet currentSet = (DavaFlowSet)currentListIt.next();
			    if(check.equals(currentSet) && check.internalDataMatchesTo(currentSet)){
				found=true;
				break;
			    }
			}
			if(!found){
			    currentList.add(check);
			}
		    }
		    breakList.put(labelBroken,currentList);
		}
	    }
	}



	    
       	//copy elements of continuelist
	{
	    HashMap fromThisContinueList = ((DavaFlowSet)fromThis).getContinueList();
	    
	    Iterator keys = fromThisContinueList.keySet().iterator();
	    while(keys.hasNext()){
		String labelContinued = (String)keys.next();
		List temp = (List)fromThisContinueList.get(labelContinued);
		
		List currentList = (List)continueList.get(labelContinued);
		if(currentList == null){
		    continueList.put(labelContinued,temp);
		}
		else{
		    //add those elements from temp to currentList which dont already exist there
		    Iterator tempIt = temp.iterator();
		    while(tempIt.hasNext()){
			DavaFlowSet check = (DavaFlowSet)tempIt.next();
			Iterator currentListIt = currentList.iterator();
			boolean found=false;
			while(currentListIt.hasNext()){
			    //see if currentList has check
			    DavaFlowSet currentSet = (DavaFlowSet)currentListIt.next();
			    if(check.equals(currentSet) && check.internalDataMatchesTo(currentSet)){
				found=true;
				break;
			    }
			}
			if(!found){
			    currentList.add(check);
			}
		    }
		    continueList.put(labelContinued,currentList);
		}
	    }
	}
    }



    private boolean compareLists(List listOne , List listTwo){
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

    public boolean internalDataMatchesTo(Object thisObj){
	if(!(thisObj instanceof DavaFlowSet))
	    return false;

	//check if same break list
	{
	    HashMap otherBreakList = ((DavaFlowSet)thisObj).getBreakList();
	    List otherKeyList = new ArrayList();

	    Iterator keys = otherBreakList.keySet().iterator();
 	    while(keys.hasNext()){
	 	String labelBroken = (String)keys.next();
		otherKeyList.add(labelBroken);
		List temp = (List)otherBreakList.get(labelBroken);
 		List currentList = (List)breakList.get(labelBroken);
	 	if(currentList == null){
		    return false;
 		}
	 	else{
		    //compare the two lists
		    if(!compareLists(temp,currentList)){
			//if lists done match internalData doesnt match
			return false;
		    }
		}
	    }
	    //have gone through the first hashMap

	    //going through the second one
	    keys = breakList.keySet().iterator();
	    while(keys.hasNext()){
	 	String labelBroken = (String)keys.next();

		Iterator keyListIt = otherKeyList.iterator();
		boolean alreadyDone=false;

		while(keyListIt.hasNext()){
		    String doneLabel = (String)keyListIt.next();
		    if(labelBroken.equals(doneLabel)){
			alreadyDone=true;
			break;
		    }
		}
		if(!alreadyDone){
		    //we have come across a label
		    //which was not done by the first hashmap
		    //meaning it was NOT in the first hashMap
		    return false;
		}
	    }
	}


	//check if same continue list
	{
	    HashMap otherContinueList = ((DavaFlowSet)thisObj).getContinueList();
	    List otherKeyList = new ArrayList();

	    Iterator keys = otherContinueList.keySet().iterator();
 	    while(keys.hasNext()){
	 	String labelContinued = (String)keys.next();
		otherKeyList.add(labelContinued);

		List temp = (List)otherContinueList.get(labelContinued);
 		List currentList = (List)continueList.get(labelContinued);
	 	if(currentList == null){
		    return false;
 		}
	 	else{
		    //compare the two lists
		    if(!compareLists(temp,currentList)){
			//if lists done match internalData doesnt match
			return false;
		    }
		}
	    }
	    //have gone through the first hashMap

	    //going through the second one
	    keys = continueList.keySet().iterator();
	    while(keys.hasNext()){
	 	String labelContinued = (String)keys.next();

		Iterator keyListIt = otherKeyList.iterator();
		boolean alreadyDone=false;

		while(keyListIt.hasNext()){
		    String doneLabel = (String)keyListIt.next();
		    if(labelContinued.equals(doneLabel)){
			alreadyDone=true;
			break;
		    }
		}
		if(!alreadyDone){
		    //we have come across a label
		    //which was not done by the first hashmap
		    //meaning it was NOT in the first hashMap
		    return false;
		}
	    }
	}
	return true;
    }


    public List getContinueSet(String label){
	return (List)continueList.remove(label);
    }


    public List getBreakSet(String label){
	return (List)breakList.remove(label);
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
