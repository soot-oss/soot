package soot.toolkits.graph;

import soot.util.*;
import java.util.*;
import soot.*;
import soot.baf.*;

public class Block 
{
    private Unit mHead, mTail;
    private Body mBody;
    private List mPreds, mSuccessors;
    private int mPredCount = 0, mBlockLength = 0, mIndexInMethod = 0;
    private BlockGraph mBlockGraph;
 
    // xxx can't call remove on first or last item!!!!!! tail/ head won't get ajusted
    public Iterator iterator() 
    {
      if(mBody != null) {
          Chain units = mBody.getUnits();
          return units.iterator(mHead, mTail);
      } else {
          return null;
      }
    }
    
    //    insertBefore(aLoadInst, candidate);
    public void insertBefore(Object toInsert, Object point)
    {
        if(point == mHead) 
            mHead = (Unit) toInsert;

        Chain methodBody = mBody.getUnits();
        methodBody.insertBefore(toInsert, point);
    }

    public boolean remove(Object item) 
    {
        Chain methodBody = mBody.getUnits();
        
        if(item == mHead)
            mHead = (Unit)methodBody.getSuccOf(item);
        else if(item == mTail)
            mTail = (Unit) methodBody.getPredOf(item);
        
        return methodBody.remove(item);
    }


    public Object getSuccOf(Object aItem) 
    {        
        Chain methodBody = mBody.getUnits();
        if(aItem != mTail)
            return  methodBody.getSuccOf(aItem);
        else
            return null;
    }

    public Object getPredOf(Object aItem) 
    {
        Chain methodBody = mBody.getUnits();
        if(aItem != mHead)
            return  methodBody.getPredOf(aItem);
        else
            return null;        
    }


    public void insertAfter(Object toInsert, Object point)
    {
        if(point == mTail) 
            mTail = (Unit) toInsert;

        Chain methodBody = mBody.getUnits();
        methodBody.insertAfter(toInsert, point);
    }






    public Block(Unit aHead, Unit aTail, Body aBody, int aIndexInMethod, int aBlockLength, BlockGraph aBlockGraph)
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
        
        if(basicBlockIt.hasNext()) {
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
            if(mTail == null) 
                strBuf.append("error: null tail found; block length: " + mBlockLength +"\n");
            else if(mHead != mTail)
                strBuf.append(someUnit.toBriefString(stmtToName, indentation) + ";\n");        
        

        }  else 
            System.out.println("No basic blocks found; must be interface class.");

        return strBuf.toString();
    }

    public Unit getHead() 
    {
        return mHead;
    }
    
    public Unit getTail()
    {
        return mTail;
    }


    void setPreds(List preds)
    {
        mPreds = preds;
        return;
    }

    public List getPreds()
    {
        return mPreds;
    }


    void setSuccessors(List successors)
    {
        mSuccessors = successors;
    }


    public List getSuccessors()
    {
        return mSuccessors;
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
