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

package soot.dava.toolkits.base.renamer;

import java.util.*;

public class heuristicTuple{
    BitSet heuristics;
    int bitSetSize;
    Vector<String> methodName;  //local is assigned the result of this method call
    Vector<String> objectClassName; //local is initialized with a new invocation of this class
    Vector<String> fieldName; //local is initialized with a field
    Vector<String> castStrings; //local is casted to a type

    public heuristicTuple(int bits){
    	heuristics =new BitSet(bits);
    	this.methodName= new Vector<String>();
    	this.objectClassName = new Vector<String>();
    	this.fieldName = new Vector<String>();
    	this.castStrings = new Vector<String>();
    	bitSetSize=bits;
    }

    public void addCastString(String castString){
    	this.castStrings.add(castString);
    	setHeuristic(infoGatheringAnalysis.CAST);
    }
    
    public List<String> getCastStrings(){
    	return castStrings;
    }
    
    public void setFieldName(String fieldName){
	this.fieldName.add(fieldName);
	setHeuristic(infoGatheringAnalysis.FIELDASSIGN);
    }
    
    public List<String> getFieldName(){
	return fieldName;
    }

    public void setObjectClassName(String objectClassName){
	this.objectClassName.add(objectClassName);
	setHeuristic(infoGatheringAnalysis.CLASSNAME);
    }
    
    public List<String> getObjectClassName(){
	return objectClassName;
    }

    public void setMethodName(String methodName){
    	this.methodName.add(methodName);
    	setHeuristic(infoGatheringAnalysis.METHODNAME);
    	if(methodName.startsWith("get") || methodName.startsWith("set"))
    		setHeuristic(infoGatheringAnalysis.GETSET);
    }
    
    public List<String> getMethodName(){
	return methodName;
    }

    public void setHeuristic(int bitIndex){
	heuristics.set(bitIndex);
    }

    public boolean getHeuristic(int bitIndex){
	return heuristics.get(bitIndex);
    }

    public boolean isAnyHeuristicSet(){
	return !heuristics.isEmpty();
    }


    public String getPrint(){
	String temp ="BitSet: ";
	for(int i=0;i<bitSetSize;i++){
	    if(getHeuristic(i))//i bit is set
		temp=temp.concat("1");
	    else
		temp=temp.concat("0");
	}

	temp=temp.concat("  Field: "+fieldName.toString());

	temp=temp.concat("  Method: ");
	Iterator<String> it = getMethodName().iterator();
	while(it.hasNext()){
	    temp = temp.concat(it.next()+" , ");
	}

	temp=temp.concat("  Class: "+objectClassName.toString());

	//System.out.println("TUPLE:"+temp);
	return temp;
    }

}