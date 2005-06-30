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

import soot.*;
import java.util.*;

public class heuristicTuple{
    BitSet heuristics;
    int bitSetSize;
    Vector methodName;
    String objectClassName;
    String fieldName;
   

    public heuristicTuple(int bits){
	heuristics =new BitSet(bits);
	this.methodName= new Vector();
	this.objectClassName=null;
	bitSetSize=bits;
    }

    public void setFieldName(String fieldName){
	if(fieldName != null){
	    this.fieldName=fieldName;
	    setHeuristic(infoGatheringAnalysis.FIELDASSIGN);
	}
    }
    
    public String getFieldName(){
	return fieldName;
    }

    public void setObjectClassName(String objectClassName){
	if(objectClassName != null){
	    this.objectClassName=objectClassName;
	    setHeuristic(infoGatheringAnalysis.CLASSNAME);
	}
    }
    
    public String getObjectClassName(){
	return objectClassName;
    }

    public void setMethodName(String methodName){
	this.methodName.add(methodName);
	setHeuristic(infoGatheringAnalysis.METHODNAME);
	if(methodName.substring(0,3).compareTo("get")==0 || methodName.substring(0,3).compareTo("set")==0)
	    setHeuristic(infoGatheringAnalysis.GETSET);
    }
    
    public Iterator getMethodName(){
	return methodName.iterator();
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

	temp=temp.concat("  Field: "+fieldName);

	temp=temp.concat("  Method: ");
	Iterator it = getMethodName();
	while(it.hasNext()){
	    temp = temp.concat((String)it.next()+" , ");
	}

	temp=temp.concat("  Class: "+objectClassName);

	//System.out.println("TUPLE:"+temp);
	return temp;
    }

}