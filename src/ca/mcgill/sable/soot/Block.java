package ca.mcgill.sable.soot;


import java.util.*;
import ca.mcgill.sable.soot.*;
import java.io.*;


class Block 
{
    private Unit mHead, mTail;
    private UnitBody mBody;
    private List mPreds, mSuccessors;
    private int mPredCount = 0, mBlockLength = 0, mIndexInMethod = 0;
    private BlockGraph mBlockGraph;
    
    public Block(Unit aHead, Unit aTail, UnitBody aBody, int aIndexInMethod, int aBlockLength, BlockGraph aBlockGraph)
    {
	mHead = aHead;
	mTail = aTail;
	mBody = aBody;
	mIndexInMethod = aIndexInMethod;
	mBlockLength = aBlockLength;
	mBlockGraph = aBlockGraph;
    }

    public int getIndexInMethod()
    {
	return mIndexInMethod;
    }

    public void optimize()
    {
	// TODO
    }
    /*
    public void loadStoreElimination() 
    {
	changed = false;
	while(!changed && it.hasNext()) {
	    Inst u = (Inst) it.next();
	    topo.setElementAt(u, stackHeight);
	    stackHeight += (u.getOutCount() - u.getInCount());
			
	    if(u instanceof StoreInst ) {
		storeInstList.add(u);
	    } else if(u instanceof LoadInst) {
			   
		Iterator storeIt = storeInstList.iterator();
		while(storeIt.hasNext()) {
		    StoreInst inst = (StoreInst) storeIt.next();
		    
		    if(((LoadInst)u).getLocal() == inst.getLocal()) {
				    
			/	int isUnique = 0;
			Iterator loadIt = c.iterator();
			while(loadIt.hasNext()) {
			    Inst i = (Inst) loadIt.next();
			    if(i instanceof LoadInst) {
				if(((LoadInst) i).getLocal() == ((LoadInst)u).getLocal())
				    isUnique++;
			    }
			    }/
				    
					
			//if((isUnique<2) && stackIndependent(inst,u, c)) {
			  
			if( !isLive(((LoadInst)u).getLocal()) && stackIndependent(inst, u, c)) {
			    changed = true;
			    c.remove(u);
			    c.remove(inst);
			    
			    // temporary while both HashChain and Lists are used 
			    unitList.remove(u);
			    unitList.remove(inst);
			    
			    // debug
			    System.out.println("Store/Load elimination occurred.");
			}
			
		    }
							 
		}
			    
			    
	    }
			
	}
	}*/
    /*
    public static boolean stackIndependent(Inst from, Inst to) 
    {
	HashChain c = mUnitBody;
	int stackHeight = 0;
	Iterator it = c.iterator(from);
	Inst currentInst;
	Vector loads = new Vector(8);

	if(from == to) 
	    return true;
	
	currentInst = (Inst) it.next();
	currentInst = (Inst) it.next();
	if(currentInst == to)
	    return true;
	
	while(currentInst != to) {
	    stackHeight -= currentInst.getInCount();
	    if(stackHeight < 0)
		return false;
	    else
		stackHeight += currentInst.getOutCount();

	    currentInst = (Inst) it.next();
	}
	if(stackHeight == 0)
	    return true;     
	else {
	    boolean result = false;
	    if(stackHeight == 1)
		stackHeight -= reorderInstructions(loads);
	    if(stackHeight != 0)
		result = true;
	    return result;
	}
    }

    */
    Unit getHead() 
    {
	return mHead;
    }
    
    Unit getTail()
    {
	return mTail;
    }


    void setPreds(List preds)
    {
	mPreds = preds;
	return;
    }

    List getPreds()
    {
	return mPreds;
    }


    void setSuccessors(List successors)
    {
	mSuccessors = successors;
    }

    List getSuccessors()
    {
	return mSuccessors;
    }

    /*
    public String toBriefString() 
    {
	return "block #" + mIndexInMethod ;
    }
    
    public String toString() 
    {
	StringBuffer strBuf = new StringBuffer();
	
	strBuf.append(toBriefString() + " of method " + mBody.getMethod().getName() + ".\n");
	strBuf.append("Head: " + mHead.toBriefString() + '\n');
	strBuf.append("Tail: " + mTail.toBriefString() + '\n');
	strBuf.append("Predecessors: \n");
	
	// print out predecessors.
	int count = 0;
	if(mPreds != null) {
	    Iterator it = mPreds.iterator();
	    while(it.hasNext()) {
		
		strBuf.append(((Block) it.next()).toBriefString() + '\n');
	    }
	}
	return strBuf.toString();
    }
    */
    
    public String toBriefString()
    {
        return toString(true, buildMapForBlock(), "        ");
    }
    
    public String toBriefString(Map stmtToName)
    {
        return toString(true, stmtToName, "");
    }
    
    public String toBriefString(String indentation)
    {
        return toString(true, buildMapForBlock(), indentation);
    }




    
    public String toBriefString(Map stmtToName, String indentation)
    {
        return toString(true, stmtToName, indentation);
    }
    
    public String toString()
    {
        return toString(false, allMapToUnnamed, "");
    }
    
    public String toString(Map stmtToName)
    {
        return toString(false, stmtToName, "");
    }
    
    public String toString(String indentation)
    {
        return toString(false, allMapToUnnamed, indentation);
    }
    
    public String toString(Map stmtToName, String indentation)
    {
        return toString(false, stmtToName, indentation);
    }
    
    protected String toString(boolean isBrief, Map stmtToName, String indentation)
    {
	StringBuffer strBuf = new StringBuffer();

	
   
	/*
	strBuf.append(toShortString() + " of method " + mBody.getMethod().getName() + ".\n");
	strBuf.append("Head: " + mHead.toBriefString(stmtToName, indentation ) + '\n');
	strBuf.append("Tail: " + mTail.toBriefString(stmtToName, indentation) + '\n');
	strBuf.append("Predecessors: \n");*/
	
	strBuf.append("     block" + mIndexInMethod + ":\t\t\t\t\t[preds: ");
	// print out predecessors.
	int count = 0;
	if(mPreds != null) {
	    Iterator it = mPreds.iterator();
	    while(it.hasNext()) {
		
		strBuf.append(((Block) it.next()).getIndexInMethod()+ " ");
	    }
	}
	strBuf.append("] [succs: ");
	if(mSuccessors != null) {
	    Iterator it = mSuccessors.iterator();
	    while(it.hasNext()) {
		
		strBuf.append(((Block) it.next()).getIndexInMethod() + " ");
	    }
	    
	}
	    
	strBuf.append("]\n");
	

	
	//strBuf.append("     block" + mIndexInMethod + ":\n");

	Chain methodUnits = mBody.getUnits();
	Iterator basicBlockIt = methodUnits.iterator(mHead);
	Unit someUnit = (Unit) basicBlockIt.next();
	strBuf.append(someUnit.toBriefString(stmtToName, indentation) + ";\n");
	if(!isBrief) {
	    while(basicBlockIt.hasNext()){
		someUnit = (Unit) basicBlockIt.next();
		if(someUnit == mTail)
		    break;
		strBuf.append(someUnit.toBriefString(stmtToName, indentation) + ";\n");	
	    }
	} else {
	    if(mBlockLength > 1)
		strBuf.append("          ...\n");
	}
	someUnit = mTail;
	if(mHead != mTail)
	    strBuf.append(someUnit.toBriefString(stmtToName, indentation) + ";\n");	
	

	
	return strBuf.toString();
    }

    public String toShortString() {return "Block #" + mIndexInMethod; }
    



    private Map buildMapForBlock() 
    {
	Map m = new HashMap();
	List basicBlocks = mBlockGraph.getBlocks();
	Iterator it = basicBlocks.iterator();
	while(it.hasNext()) {
	    Block currentBlock = (Block) it.next();
	    m.put(currentBlock.getHead(),  "block" + (new Integer(currentBlock.getIndexInMethod()).toString()));
	}	
	return m;
    }


    static Map allMapToUnnamed = new AllMapTo("???");

    static class AllMapTo extends AbstractMap
    {
        Object dest;
        
        public AllMapTo(Object dest)
        {
            this.dest = dest;
        }
        
        public Object get(Object key)
        {
            return dest;
        }
        
        public Set entrySet()
        {
            throw new UnsupportedOperationException();
        }
    }
}
