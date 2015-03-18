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
package soot.dava.toolkits.base.AST.transformations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Value;
import soot.ValueBox;
import soot.dava.internal.javaRep.DNewInvokeExpr;
import soot.dava.internal.javaRep.DVirtualInvokeExpr;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.grimp.internal.GAddExpr;

/*
 * Matches the output pattern
 *   (new StringBuffer()).append ............ .toString();
 *   Convert it to 
 *   append1 + append2 .....;
 */

public class NewStringBufferSimplification extends DepthFirstAdapter {
	final static Logger logger = LoggerFactory.getLogger(NewStringBufferSimplification.class);
	
    public NewStringBufferSimplification(){
    	
    }
    
    public NewStringBufferSimplification(boolean verbose){
    	super(verbose);
    }


    public void inExprOrRefValueBox(ValueBox argBox){
    	logger.debug("ValBox is: {}",argBox.toString());
    	
    	Value tempArgValue = argBox.getValue();
    	logger.debug("arg value is: {}",tempArgValue);
    	
    	
    	if(! (tempArgValue instanceof DVirtualInvokeExpr)){
    		logger.debug("Not a DVirtualInvokeExpr{}",tempArgValue.getClass());
    		return;
    	}
    		
    	//check this is a toString for StringBuffer
    	logger.debug("arg value is a virtual invokeExpr");
    	DVirtualInvokeExpr vInvokeExpr = ((DVirtualInvokeExpr)tempArgValue);
    	
    	
    	//need this try catch since DavaStmtHandler expr will not have a "getMethod"
    	try{
    		if( ! (vInvokeExpr.getMethod().toString().equals("<java.lang.StringBuffer: java.lang.String toString()>")))
    			return;
    	}catch(Exception e){
    		return;
    	}
    	
    	logger.debug("Ends in toString()");
    	
    	Value base = vInvokeExpr.getBase();
    	List args = new ArrayList();
    	while( base instanceof DVirtualInvokeExpr){
    		DVirtualInvokeExpr tempV = (DVirtualInvokeExpr)base;
    		logger.debug("base method is {}",tempV.getMethod());
    		if(!tempV.getMethod().toString().startsWith("<java.lang.StringBuffer: java.lang.StringBuffer append")){
    			logger.debug("Found a virtual invoke which is not a append{}",tempV.getMethod());
    			return;
    		}
    		args.add(0,tempV.getArg(0));
    		//logger.info("Append: "+((DVirtualInvokeExpr)base).getArg(0) );
    		//move to next base
    		base = ((DVirtualInvokeExpr)base).getBase();
    	}
    	
    	if(! (base instanceof DNewInvokeExpr ))
    		return;
    	
    	logger.debug("New expr is {}", ((DNewInvokeExpr)base).getMethod() );
    	
    	if(!  ((DNewInvokeExpr)base).getMethod().toString().equals("<java.lang.StringBuffer: void <init>()>") )
    			return;
    	
    	/*
    	 * The arg is a new invoke expr of StringBuffer and all the appends are present in the args list
    	 */
    	logger.debug("Found a new StringBuffer.append list in it");
    	
    	//argBox contains the new StringBuffer
    	Iterator it = args.iterator();
    	Value newVal = null;
    	while(it.hasNext()){
    		Value temp = (Value)it.next();
    		if(newVal == null)
    			newVal = temp;
    		else{
    			//create newVal + temp
    			newVal = new GAddExpr(newVal,temp);
    		}
    		
    	}
    	logger.debug("New expression for logger.info is{}",newVal);
    	
    	argBox.setValue(newVal);
    }
}
