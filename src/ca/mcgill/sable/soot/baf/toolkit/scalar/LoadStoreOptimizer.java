package ca.mcgill.sable.soot.baf.toolkit.scalar;

import java.util.*;
import java.io.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.soot.toolkit.scalar.*;



public class LoadStoreOptimizer 
{

    final static protected int FAILURE = 0;
    final static protected int SUCCESS = 1;
    final static protected  int MAKE_DUP = 2;
    final static protected int MAKE_DUP1_X1 = 3;
    final static protected int SPECIAL_SUCCESS = 4;
    final static protected int HAS_CHANGED = 5;
    final static protected int  SPECIAL_SUCCESS_2 = 6;

    final static  boolean debug = false;
    final static boolean printStats = false;

    private static LoadStoreOptimizer mSingleton = new LoadStoreOptimizer();

    public static LoadStoreOptimizer v()
    {
        return mSingleton;
    }

    
    protected LoadStoreOptimizer()
    {
    }
    

    
    protected Chain mUnits;
    protected Body mBody;
    protected CompleteUnitGraph mCompleteUnitGraph;
    protected LocalDefs mLocalDefs;
    protected LocalUses mLocalUses;
    protected Map mUnitToBlockMap;
    protected Map mUnitToStackSlotMap;
    
    private statistics stats = new statistics();

    /*
     *  Compute a map binding each unit in the method to the unique basic block    
     *  that contains it. At the same time build a store list of all the stores in
     *  the method. 
     *  @return  A list of the the store instruction contained in the method body. 
     */
    protected  List  buildStoreListAndUnitToBlockMap()
    {
	BlockGraph blockGraph = new BriefBlockGraph(mBody);
        if(debug) { System.out.println(blockGraph);}
        List blocks = blockGraph.getBlocks();
        List storeList = new ArrayList();
        mUnitToBlockMap = new HashMap();
        
        Iterator blockIt = blocks.iterator();
        while(blockIt.hasNext() ) {
            Block block = (Block) blockIt.next();
            
            Iterator unitIt = block.iterator();
            while(unitIt.hasNext()) {
                Unit unit = (Unit) unitIt.next();
                //delme[
                if(mUnitToBlockMap.containsKey(unit))
                    throw new RuntimeException("error: this means a unit is in 2 distinct blocks");
                //delme]
                mUnitToBlockMap.put(unit, block);
                
                if(unit instanceof StoreInst)
                    storeList.add(unit);
            }
        }
        return storeList;
    }

    protected void computeLocalDefsAndLocalUsesInfo() 
    {        
        mCompleteUnitGraph =  new CompleteUnitGraph(mBody);
        mLocalDefs = new SimpleLocalDefs(mCompleteUnitGraph);
        mLocalUses = new SimpleLocalUses(mCompleteUnitGraph, mLocalDefs);            
    }


    public void optimize(Body body) 
    {

	
        mBody = body;        
        mUnits =  mBody.getUnits();
	
	// if the method contains no code or has 4 or less instructions, then return 
        if(mUnits.isEmpty()) {
            return;
        } else if (mUnits.size() <= 4)
	    return;

        
        //delme[
        if(debug) { System.out.println("Optimizing: " + body.getMethod().toString());}
        //delme] 

	stats.setMethodName(body.getMethod().toString());
        buildStoreListAndUnitToBlockMap();

        optimizeLoadStores();         if(printStats) {System.out.println("Pass 1" + stats); } 
	doInterBlockOptimizations(); if(printStats) {System.out.println("Pass 2" + stats); }
	optimizeLoadStores();         if(printStats) { System.out.println("Pass 3" + stats);}    
	pushLoadsUp();                 if(printStats) { System.out.println("Pass 4" + stats); }
	optimizeLoadStores();          if(printStats) { System.out.println("Pass 5" + stats); }
	propagateLoadsForward();         if(printStats) { System.out.println("Pass 6" + stats); }
	propagateBackwardsIndependentHunk(); if(printStats) { System.out.println("Pass 7" + stats); }
	optimizeLoadStores();       if(printStats) { System.out.println("Pass 8" + stats); } 
	//	pushLoadsUp();
	if(printStats) {	System.out.println(stats);}
	stats.reset();
	//	superDuper();
    }




    


    protected void pushLoadsUp()
    {        
        // Create a nop instruction as the begin unit of each trap to avoid 
        // having trap boundries pushed up along with the instruction they are bound to. 
        //

        //createTrapBoundryPlaceholders(); 
        boolean hasChanged = true;
        while(hasChanged) {
            hasChanged = false;
                
            List tempList = new ArrayList();
            tempList.addAll(mUnits);

            Iterator it = tempList.iterator();
            while(it.hasNext()) {
                Unit currentInst = (Unit) it.next();
                    
                if(currentInst instanceof LoadInst) {
                    Block block = (Block)  mUnitToBlockMap.get(currentInst);                    
                    Unit insertPoint = pushLoadUp(currentInst, block);
                        
                    if(insertPoint != null) {
                        if(block.getTail() != currentInst) { // xxx deal with this better
                            block.remove(currentInst);
                            block.insertBefore(currentInst, insertPoint);
                            hasChanged = true;
                        } else {
                            throw new RuntimeException();
                        }
                    }
                }
            }        
        }        
    }

    protected Unit pushLoadUp(Unit aInst, Block aBlock) 
    {
        int h = 0;
        Unit candidate = null;
        Unit currentUnit =  aInst;   
        
        List loadDefList = mLocalDefs.getDefsOfAt(((LoadInst)aInst).getLocal(), aInst);
                
        currentUnit = (Unit) aBlock.getPredOf(currentUnit);        
        while( currentUnit != null) {
            
            // do not let the load jump over an iinc affecting it
            if(currentUnit instanceof IncInst) {        
                if(loadDefList.contains(currentUnit))
                    break;
                break;
            }
            else if(currentUnit instanceof StoreInst) {
                
                List uses = mLocalUses.getUsesOf(currentUnit);
                if(uses != null ) {
                    Iterator useIterator = uses.iterator();                
                    while(useIterator.hasNext()) {
                        Unit u = ((UnitValueBoxPair)useIterator.next()).getUnit();
                        if(u == aInst) {
                            return candidate;
                        }
                    }
                }
            }            
            else if(currentUnit instanceof IdentityInst)
                break;
            else if(currentUnit instanceof EnterMonitorInst  || currentUnit instanceof ExitMonitorInst)
                break;

            h -= ((Inst)currentUnit).getOutCount();                
            if(h < 0){
                break;
            }            
            h += ((Inst)currentUnit).getInCount();
            if(h == 0) {
                candidate = currentUnit;
            }
            
            currentUnit = (Unit) aBlock.getPredOf(currentUnit);
        }        
        
        return candidate;
    }
    






    public  void optimizeLoadStores() 
    {
        
        Chain units  = mUnits;
        List storeList;
        
        // build a map binding units to their basic blocks and a list of all store units in the unitbody
        storeList = buildStoreListAndUnitToBlockMap();        
       
        computeLocalDefsAndLocalUsesInfo(); 
           
        

        // Eliminate store/load  
        {

            boolean hasChanged = true;

            while(hasChanged) {
                
                hasChanged = false;
                
                // Iterate over the storeList 
                Iterator unitIt = storeList.iterator();
                
            nextUnit:
                while(unitIt.hasNext()){
                    Unit unit = (Unit) unitIt.next();                
                    List uses = mLocalUses.getUsesOf(unit);
                    
                    // if uses of a store < 3, attempt some form of store/load elimination
                    if(uses.size() < 3) {
                        
                        // check that all uses have only the current store as their definition
                        {
                            boolean invalidStore = false;
                            Iterator useIt = uses.iterator();
                            while(useIt.hasNext()) {
                                UnitValueBoxPair pair = (UnitValueBoxPair) useIt.next();
                                Unit loadUnit = pair.getUnit();
                                if(!(loadUnit instanceof LoadInst))
                                    continue nextUnit;
                            
                                List defs = mLocalDefs.getDefsOfAt((Local) pair.getValueBox().getValue(), loadUnit);
                                if(defs.size() > 1) {
                                    continue nextUnit;
                                }
                                else if(defs.get(0) != unit) {
                                    continue nextUnit; // xxx how can you get here?
                                }
                            }
                        } 
                        
                        // Check that all loads are in the same bb as the store
                        {
                            Block storeBlock = (Block) mUnitToBlockMap.get(unit);
                            Iterator useIt = uses.iterator();
                            while(useIt.hasNext()) {
                                UnitValueBoxPair pair = (UnitValueBoxPair) useIt.next();
                                Block useBlock = (Block) mUnitToBlockMap.get(pair.getUnit());
                                if(useBlock != storeBlock) 
                                    continue nextUnit;
                            }                            
                        }
                        
                        // Check for stack independance (automatic reordering may be performed by stackIndependent() fcnt)
                        {
                            Block block;
                            switch(uses.size()) {
                            case 0:        
                                // replace store by a pop and remove store from store list
                                replaceUnit(unit, Baf.v().newPopInst(((StoreInst)unit).getOpType()));
                                unitIt.remove();
  
                                hasChanged = true;
                                 break;
                                    
                            case 1:
                                Unit loadUnit = ((UnitValueBoxPair)uses.get(0)).getUnit();
                                block =  (Block) mUnitToBlockMap.get(unit);
                                int test = stackIndependent(unit, loadUnit , block, 0);
                                if(test == SUCCESS || test == SPECIAL_SUCCESS || test == SPECIAL_SUCCESS_2 ){
                                    
                                    block.remove(unit);
                                    block.remove(loadUnit);
                                    unitIt.remove();
                                    hasChanged = true;
				    stats.madeSL();
                                //delme[
                                    if(debug) { System.out.println("Store/Load elimination occurred.");}
                                //delme]
                                } 
                                break;
                                
                            case 2:
                                Unit firstLoad = ((UnitValueBoxPair)uses.get(0)).getUnit();
                                Unit secondLoad = ((UnitValueBoxPair)uses.get(1)).getUnit();
                                block = (Block) mUnitToBlockMap.get(unit);


                                
                                Unit temp;  // xxx try to optimize this
                                if(mUnits.follows(firstLoad, secondLoad)) {
                                    temp = secondLoad;
                                    secondLoad = firstLoad;
                                    firstLoad = temp;
                                }

                                 int result = stackIndependent(unit, firstLoad, block, 0);                                 
                                 if(result == SUCCESS){        
                                     
                                     block.remove(firstLoad);
                                     block.insertAfter(firstLoad, unit);                                
                                     if(debug) { System.out.println("before: \n"+ block);}
                                    
                        
                                     int res = stackIndependent(unit, secondLoad, block, -1);
				     
                                     if(res == MAKE_DUP) {
                                         // replace store by dup, drop both loads                                
                                         
                                         replaceUnit(unit,  Baf.v().newDup1Inst(((LoadInst) secondLoad).getOpType()));
                                         unitIt.remove(); // remove store from store list

                                         block.remove(firstLoad); 
                                         block.remove(secondLoad);
					 stats.madeSLL();
                                         hasChanged = true;
                                         //delme[
                                         if(debug) { System.out.println("MAKE_DUP case invoked");}
                                         
                                         //delme]       
                                     }  else if(res == MAKE_DUP1_X1) {
                                                      
                                         // replace store/load/load by a dup1_x1
                                         Unit stackUnit = getStackItemAt2(unit, block, -2); 
                                         /* if(stackUnit instanceof Dup1_x1Inst || stackUnit instanceof DupInst || stackUnit instanceof PushInst)
                                            break;*/
                                         if(stackUnit instanceof PushInst)
                                            break;
                                         Type underType = type(stackUnit);
                                        if(underType == null) {                                         
                                            throw new RuntimeException("this has to be corrected (loadstoroptimiser.java)" + stackUnit);
                                        }
                                        if(debug) { System.out.println("stack unit is: " + stackUnit + " stack type is " + underType);}
                                        replaceUnit(unit, Baf.v().newDup1_x1Inst(((LoadInst) secondLoad).getOpType(),underType));
                                        unitIt.remove();                
                                        
                                        block.remove(firstLoad); 
                                        block.remove(secondLoad);
                                        stats.madeSLL();
                                        hasChanged = true;
                                        //delme[
                                        if(debug) { System.out.println("dup_x1 occurred.");}
                                        //delme]                        
                                        break;                                        
                                        }
                                 } else if(result == SPECIAL_SUCCESS || result == HAS_CHANGED){
                                     hasChanged = true;
                                 }
                            }                    
                        }
                    }
                }
            }
        }                    
    }

    
    

    



    private boolean isRequiredByFollowingUnits(Unit from, Unit to)
    {
        Iterator it = mUnits.iterator(from, to);
        int stackHeight = 0;
        boolean res = false;
        
        if(from != to)  {
            // advance past the 'from' unit
            it.next();
            while(it.hasNext()) {
                Unit  currentInst = (Unit) it.next();
                if(currentInst == to) 
                    if(!(to instanceof DupInst))
                        break;

                stackHeight -= ((Inst)currentInst).getInCount();
                if(stackHeight < 0 ) {
                    res = true;
                    break;
                }
                stackHeight += ((Inst)currentInst).getOutCount();
            }
        }
        return res;
    }
            

    
    
   
    protected int pushStoreToLoad(Unit from , Unit to, Block block)
    {
        Unit storePred =  (Unit) block.getPredOf(from);
        if(storePred != null) {
            if(((Inst)storePred).getOutCount() == 1){
                Set unitsToMove = new HashSet();
                unitsToMove.add(storePred);
                unitsToMove.add(from);
                int h = ((Inst)storePred).getInCount();
                Unit currentUnit = storePred;
                if(h != 0) {           
                    currentUnit = (Unit)block.getPredOf(storePred);
                    while(currentUnit != null) {
                        
                        h-= ((Inst)currentUnit).getOutCount();
                        if(h<0){ // xxx could be more flexible here?
                            if(debug) { System.out.println("xxx: negative");}
                            return FAILURE;
                        }
                        h+= ((Inst)currentUnit).getInCount();
                        unitsToMove.add(currentUnit);
                        if(h == 0)
                            break;
                        currentUnit  = (Unit) block.getPredOf(currentUnit);
                    }
                }
                if(currentUnit == null) {
                    if(debug) { System.out.println("xxx: null");}
                    return FAILURE;        
                }
                
                Unit uu = from;
                for(;;) {
                    uu = (Unit) block.getSuccOf(uu);
                    if(uu == to)
                        break;
                    Iterator it2 = unitsToMove.iterator();
                    while(it2.hasNext()) {
                        Unit nu = (Unit) it2.next();
                        if(debug) { System.out.println("xxxspecial;success pushing forward stuff.");}
                        
                        
                        if(!canMoveUnitOver(nu, uu)){
                            if(debug) { System.out.println("xxx: cant move over faillure" + nu);}
                            return FAILURE;
                        }
                        if(debug) { System.out.println("can move" + nu + " over " + uu);}
                    }
                }        
                
                // if we get here it means we can move all the units in the set pass the units in between [to, from]
                Unit unitToMove = currentUnit; 
                while(unitToMove != from) {        
                    Unit succ = (Unit) block.getSuccOf(unitToMove);
                    if(debug) { System.out.println("moving " + unitToMove);}
                    block.remove(unitToMove);
                    block.insertBefore(unitToMove, to);                            
                    unitToMove = succ;
                }
                block.remove(from);
                block.insertBefore(from, to);
                               
                if(debug) { System.out.println("xxx1success pushing forward stuff.");}
                return SPECIAL_SUCCESS;
            }
        }

        return FAILURE;
    }
    
                
                
   






        
    protected  int stackIndependent(Unit from, Unit to, Block block, int aThreshold) 
    {                
        if(debug) { System.out.println("trying: " + from);}

        int minStackHeightAttained = 0;
        int stackHeight = 0;
        Iterator it = mUnits.iterator(mUnits.getSuccOf(from));      
        int res = FAILURE;

        Unit currentInst = (Unit) it.next();
        
        if(aThreshold == -1) {
            currentInst =  (Unit) it.next();
        } 
        
        while(currentInst != to) {
            stackHeight -= ((Inst)currentInst).getInCount();
            if(stackHeight < minStackHeightAttained)
                minStackHeightAttained = stackHeight;
            
            
            stackHeight += ((Inst)currentInst).getOutCount();                
            
            if(debug) { System.out.println(currentInst + " " + ((Inst)currentInst).getNetCount());}
            currentInst = (Unit) it.next();
        }
        
        
        boolean hasChanged = true;

        if(debug) { System.out.println("Stack height= " + stackHeight + "minattained: " + minStackHeightAttained + "from succ: " + block.getSuccOf(from));
	System.out .println(mUnitToBlockMap.get(from));}
        while(hasChanged) {
            hasChanged = false;

            if(aThreshold == -1) {
                
                
                if(stackHeight == 0 && minStackHeightAttained == 0){if(debug) { System.out.println("xxx: succ: -1, makedup ");}
                return MAKE_DUP;}
                else if(stackHeight == -1 && minStackHeightAttained == -1){
                    if(debug) { System.out.println("xxx: succ: -1, makedup , -1");}        
                    return MAKE_DUP;
                }
                else if(stackHeight == -2 && minStackHeightAttained == -2){if(debug) { System.out.println("xxx: succ -1 , make dupx1 ");}
                return MAKE_DUP1_X1; }
                else  if (minStackHeightAttained < -2) {
                    if(debug) { System.out.println("xxx: failled due: minStackHeightAttained < -2 ");}
                    return FAILURE;
                }                
            }
            
            
            
            if(aThreshold == 0) {                
                if(stackHeight == 0 && minStackHeightAttained == 0){
                    if(debug) { System.out.println("xxx: success due: 0, SUCCESS ");}
                    return SUCCESS;
                }
                else if (minStackHeightAttained == -1 && stackHeight == -1) { // try to make it more generic
                    Unit u = (Unit) block.getPredOf(from);
                    if(u instanceof FieldGetInst)
                        if(block.getPredOf(u) instanceof Dup1Inst) {
                            block.remove(u);
                            block.insertBefore(u, to);
                            if(debug) { System.out.println("xxx: success due to 1, SUCCESS");}
                            return SPECIAL_SUCCESS;
                        }                    
                }
                else if (minStackHeightAttained < 0){
                    return   pushStoreToLoad(from , to, block);
                }                  
            }        
            
            it = mUnits.iterator(mUnits.getSuccOf(from), to);
            

            Unit unitToMove = null; 
            Unit u = (Unit) it.next();

            if(aThreshold == -1) {
                u =  (Unit) it.next();
            } 
            int currentH = 0;
            
            // find a candidate to move before the store/load/(load)  group 
            while( u != to) {        
                
                if(((Inst)u).getNetCount() == 1) {
                    // xxx remove this check
                    if(u instanceof LoadInst || u instanceof PushInst || u instanceof NewInst || u instanceof StaticGetInst) {
                        
                        // verify that unitToMove is not required by following units (until the 'to' unit)
                        if(!isRequiredByFollowingUnits(u, to)) {
                            unitToMove = u;
                        }
                        
                    }
                    else{
                        if(debug) { System.out.println("1003:(LoadStoreOptimizer@stackIndependent): found unknown unit w/ getNetCount == 1" + u);}
                    }
                }
                


           
                
                if(unitToMove != null) {
                    int flag = 0;
                    //if(aThreshold == 0 || !(stackHeight > -2 && minStackHeightAttained == -2 ) ) {
                        
                    if(tryToMoveUnit(unitToMove, block, from, to, flag)) {
                        if(stackHeight > -2 && minStackHeightAttained == -2){
                            return HAS_CHANGED;
                        }
                        
                        stackHeight--;
                        if(stackHeight < minStackHeightAttained)
                            minStackHeightAttained = stackHeight;
                        hasChanged = true;
                        break;
                    }
                }
               
                currentH += ((Inst)u).getNetCount();                
                unitToMove = null;
                u =  (Unit) it.next();
            }
        }

        if(isCommutativeBinOp((Unit) block.getSuccOf(to))) {
	    if(aThreshold == 0 && stackHeight == 1 && minStackHeightAttained == 0) {
		if(debug) { System.out.println("xxx: commutative ");}
		return SPECIAL_SUCCESS_2;
	    }
	    else if( ((Inst) to).getOutCount()  == 1 &&
		     ((Inst) to).getInCount() == 0  &&
		     ((Inst) mUnits.getPredOf(to)).getOutCount() == 1 &&
		     ((Inst) mUnits.getPredOf(to)).getInCount() == 0) {
                
		Object toPred =  mUnits.getPredOf(to);
		block.remove(toPred);
		block.insertAfter(toPred, to);
		return HAS_CHANGED; // return has changed
		} 
	    else return FAILURE;
        }
        if (aThreshold == 0)
	    return   pushStoreToLoad(from , to, block);


        
  if(debug) { System.out.println("xxx: end of method faillure ");}
        return res;
    }

    protected boolean isNonLocalReadOrWrite(Unit aUnit)
    {
        if((aUnit instanceof FieldArgInst ) ||
           (aUnit instanceof ArrayReadInst) ||
           (aUnit instanceof ArrayWriteInst) )
            return true;
        else
            return false;
    }


    // xxx: go over this for correctness
    protected boolean canMoveUnitOver(Unit aUnitToMove, Unit aUnitToGoOver) // xxx missing cases
    {
        
        // can't change method call order or change fieldargInst and method call order
        if((aUnitToGoOver instanceof MethodArgInst && aUnitToMove instanceof MethodArgInst) ||
           (aUnitToGoOver instanceof MethodArgInst && isNonLocalReadOrWrite(aUnitToMove))  ||
           (isNonLocalReadOrWrite(aUnitToGoOver)  && aUnitToMove instanceof MethodArgInst) ||
           
           (aUnitToGoOver instanceof ArrayReadInst  && aUnitToMove instanceof ArrayWriteInst) ||
           (aUnitToGoOver instanceof ArrayWriteInst && aUnitToMove instanceof ArrayReadInst)  ||
           (aUnitToGoOver instanceof ArrayWriteInst  && aUnitToMove instanceof ArrayWriteInst)||

           
           (aUnitToGoOver instanceof FieldPutInst && aUnitToMove instanceof FieldGetInst &&
            ((FieldArgInst)aUnitToGoOver).getField() == ((FieldArgInst)aUnitToMove).getField()) ||
           (aUnitToGoOver instanceof FieldGetInst && aUnitToMove instanceof FieldPutInst && 
            ((FieldArgInst)aUnitToGoOver).getField() == ((FieldArgInst)aUnitToMove).getField()) ||
           (aUnitToGoOver instanceof FieldPutInst && aUnitToMove instanceof FieldPutInst && 
            ((FieldArgInst)aUnitToGoOver).getField() == ((FieldArgInst)aUnitToMove).getField()) ||
           
           
           (aUnitToGoOver instanceof StaticPutInst && aUnitToMove instanceof StaticGetInst &&
            ((FieldArgInst)aUnitToGoOver).getField() == ((FieldArgInst)aUnitToMove).getField()) ||
           (aUnitToGoOver instanceof StaticGetInst && aUnitToMove instanceof StaticPutInst && 
            ((FieldArgInst)aUnitToGoOver).getField() == ((FieldArgInst)aUnitToMove).getField())||
           (aUnitToGoOver instanceof StaticPutInst && aUnitToMove instanceof StaticPutInst && 
            ((FieldArgInst)aUnitToGoOver).getField() == ((FieldArgInst)aUnitToMove).getField()))
            return false;
           

        // xxx to be safe don't mess w/ monitors. These rules could be relaxed.
        if(aUnitToGoOver instanceof EnterMonitorInst || aUnitToGoOver instanceof ExitMonitorInst)
            return false;

	if(aUnitToMove instanceof EnterMonitorInst || aUnitToGoOver instanceof ExitMonitorInst)
            return false;

        if(aUnitToGoOver instanceof IdentityInst || aUnitToMove instanceof IdentityInst)
            return false;

        
        if(aUnitToMove instanceof LoadInst ) {
            if(aUnitToGoOver instanceof StoreInst) {
                if(((StoreInst)aUnitToGoOver).getLocal() == ((LoadInst)aUnitToMove).getLocal()) {
                    return false;
                }
            }
            else if(aUnitToGoOver instanceof IncInst) {
                if(((IncInst)aUnitToGoOver).getLocal() == ((LoadInst)aUnitToMove).getLocal()){
                    return false;
                }
            }
        }

        // don't move def of load  pass it.
        if(aUnitToMove instanceof StoreInst) {
            if(aUnitToGoOver instanceof LoadInst) {
                if(((LoadInst)aUnitToGoOver).getLocal() == ((StoreInst)aUnitToMove).getLocal()) {
                    return false;
                }
            }
            else if(aUnitToGoOver instanceof IncInst) {
                if(((IncInst)aUnitToGoOver).getLocal() == ((StoreInst)aUnitToMove).getLocal()){
                    return false;
                }
            }
        }
        
        if(aUnitToMove instanceof IncInst) {
            if(aUnitToGoOver instanceof StoreInst) {
                if(((StoreInst)aUnitToGoOver).getLocal() == ((IncInst)aUnitToMove).getLocal()) {
                    return false;
                }
            }
            else if(aUnitToGoOver instanceof LoadInst) {
                if(((LoadInst)aUnitToGoOver).getLocal() == ((IncInst)aUnitToMove).getLocal()){
                    return false;
                }
            }
        }
        return true;
    }






    protected  boolean tryToMoveUnit(Unit unitToMove, Block block, Unit from, Unit to, int flag) 
    {
       
        int h = 0;
        Unit current = unitToMove;
        boolean reachedStore = false;
        boolean reorderingOccurred =false;
              
        if(unitToMove == null) 
            return false;                
                
        while( current != block.getHead()) {   // do not go past basic block limit
            current = (Unit) mUnits.getPredOf(current);
            
            if(!canMoveUnitOver(current, unitToMove))
                return false;

            if(current == from)
                reachedStore = true;
                
        
            h -= ((Inst)current).getOutCount();                
            if(h < 0 ){
                if(debug) { System.out.println("1006:(LoadStoreOptimizer@stackIndependent): Stack went negative while trying to reorder code.");}
                return false;
            }
            h += ((Inst)current).getInCount();
            
            
            if(h == 0 && reachedStore == true) {
                if(!isRequiredByFollowingUnits(unitToMove, to)) {
                    if(debug) { System.out.println("10077:(LoadStoreOptimizer@stackIndependent): reordering bytecode move: " + unitToMove + "before: " + current);}
                    block.remove(unitToMove);
                    block.insertBefore(unitToMove, current);
                    
                    reorderingOccurred = true;
                    break;
                }                    
            }                                      
        }
            
        if(reorderingOccurred) {
            if(debug) { System.out.println("reordering occured");}
            return true;
        } else {
            if(debug) { System.out.println("1008:(LoadStoreOptimizer@stackIndependent):failled to find a new slot for unit to move");}
            return false;
        }
    }

    


    protected void replaceUnit(Unit aToReplace1, Unit aToReplace2,  Unit aReplacement) 
    {
        Block block = (Block) mUnitToBlockMap.get(aToReplace1);
                 
	if(aToReplace2 != null) {
	    block.insertAfter(aReplacement, aToReplace2);
	    block.remove(aToReplace2);
	} else {
	    block.insertAfter(aReplacement, aToReplace1);	    
	}

	block.remove(aToReplace1);
                                                        
        // add the new unit the block map
        mUnitToBlockMap.put(aReplacement, block);        
    }
    



    protected void replaceUnit(Unit aToReplace, Unit aReplacement) 
    {

	replaceUnit(aToReplace, null, aReplacement);
	/* Block block = (Block) mUnitToBlockMap.get(aToReplace);
                    
        block.insertAfter(aReplacement, aToReplace);
        block.remove(aToReplace);
        
                                
        // add the new unit the block map
        mUnitToBlockMap.put(aReplacement, block);        */
    }



    protected Type type(Unit aUnit)
    {
        if(aUnit instanceof InstanceCastInst || aUnit instanceof NewInst) {
            return  RefType.v();        
        } else if(aUnit instanceof LoadInst) {
            return ((LoadInst)aUnit).getOpType();
        } else  if (aUnit instanceof FieldGetInst)
            return ((FieldGetInst)aUnit).getField().getType();
        else if (aUnit instanceof Dup1Inst)
            return ((Dup1Inst)aUnit).getOp1Type();
        else if(aUnit instanceof StaticGetInst)
            return ((StaticGetInst) aUnit).getField().getType();
        else if(aUnit instanceof OpTypeArgInst)
            return ((OpTypeArgInst)aUnit).getOpType();
        else if(aUnit instanceof PushInst)
            return ((PushInst)aUnit).getConstant().getType();
        else if (aUnit instanceof MethodArgInst)
            return ((MethodArgInst) aUnit).getMethod().getReturnType();
        else if(aUnit instanceof Dup1_x1Inst)
            return ((Dup1_x1Inst) aUnit).getOp1Type();
        else if(aUnit instanceof Dup1Inst)
            return ((Dup1Inst) aUnit).getOp1Type();
        else
            return null;
    }

    

    protected Unit getStackItemAt2(Unit aUnit, Block aBlock, int aDeltaHeight) 
    {
        int h = 0;
        Unit currentUnit  = aUnit;
        Unit candidate = null;
        
        while(currentUnit != null) {
            currentUnit  = (Unit) aBlock.getPredOf(currentUnit);
            if(currentUnit == null) {
                if(debug) { System.out.println(aBlock);}
                throw new RuntimeException("impossible");
            }
            
            
            h -= ((Inst)currentUnit).getOutCount();
            if(h <= aDeltaHeight) {
                candidate = currentUnit;
                break;
            }            
            h += ((Inst)currentUnit).getInCount();            
        }        
        return candidate;
    }
    





    



    // not a save function :: goes over block boundries
    protected int getDeltaStackHeightFromTo(Unit aFrom, Unit aTo) 
    {
        Iterator it = mUnits.iterator(aFrom, aTo);
        int h = 0;
        
        while(it.hasNext()) {
            h += ((Inst)it.next()).getNetCount();
        }
        
        return h;
    }

    

    protected void doInterBlockOptimizations() 
    {
        boolean hasChanged = true;
        while(hasChanged) {
            hasChanged = false;
            
            List tempList = new ArrayList();
            tempList.addAll(mUnits);
            Iterator it = tempList.iterator();        
            while(it.hasNext()) {
                Unit u = (Unit) it.next();
                
                if(u instanceof LoadInst) {
                    if(debug) { System.out.println("inter trying: " + u);}
                    Block loadBlock = (Block) mUnitToBlockMap.get(u);
                    List defs = mLocalDefs.getDefsOfAt(((LoadInst)u).getLocal(), u);
                    if(defs.size() ==  1) {
                        Block defBlock =(Block)  mUnitToBlockMap.get(defs.get(0));
                        if(defBlock != loadBlock) {
                            Unit storeUnit = (Unit) defs.get(0);
                            if(storeUnit instanceof StoreInst) {
                                List uses = mLocalUses.getUsesOf(storeUnit);
                                if(uses.size() == 1){
                                    if(allSuccesorsOfAreThePredecessorsOf(defBlock, loadBlock)) {
                                        if(getDeltaStackHeightFromTo((Unit) defBlock.getSuccOf(storeUnit), (Unit)defBlock.getTail()) == 0) {
                                            Iterator it2 = defBlock.getSuccessors().iterator();
                                            boolean res = true;
                                            while(it2.hasNext()) {
                                                Block b = (Block) it2.next();
                                                if(getDeltaStackHeightFromTo((Unit) b.getHead(), (Unit) b.getTail()) != 0) {
                                                    res = false;
                                                    break;
                                                }
                                                if(b.getPreds().size() != 1 || b.getSuccessors().size() != 1){
                                                    res = false;
                                                    break;
                                                }
                                            }                        
                                            if(debug) { System.out.println(defBlock.toString() + loadBlock.toString());}
                                            
                                            if(res) {
                                                defBlock.remove(storeUnit);                            
                                                mUnitToBlockMap.put(storeUnit, loadBlock);
                                                loadBlock.insertBefore(storeUnit, loadBlock.getHead());
                                                hasChanged = true;
                                                if(debug) { System.out.println("inter-block opti occurred " + storeUnit + " " + u);}
                                                if(debug) { System.out.println(defBlock.toString() + loadBlock.toString());}
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if(defs.size() == 2) {
                        
                            Unit def0 = (Unit) defs.get(0);
                            Unit def1 = (Unit) defs.get(1);
                            Block defBlock0 = (Block)  mUnitToBlockMap.get(def0);
                            Block defBlock1 = (Block)  mUnitToBlockMap.get(def1);
                            if(defBlock0 != loadBlock && defBlock1 != loadBlock) {                                
                                if(mLocalUses.getUsesOf(def0).size() == 1  && mLocalUses.getUsesOf(def1).size() == 1) {
                                    List def0Succs = defBlock0.getSuccessors();
                                    List def1Succs  = defBlock1.getSuccessors();
                                    if(def0Succs.size()==1 && def1Succs.size()==1) {
                                        if(def0Succs.get(0) == loadBlock && def1Succs.get(0)== loadBlock) {                                         
                                            if(loadBlock.getPreds().size() == 2) {
                                                if( (def0 == defBlock0.getTail()|| 
                                                     getDeltaStackHeightFromTo((Unit)defBlock0.getSuccOf(def0),(Unit) defBlock0.getTail())  == 0) && 
                                                    (def1 == defBlock1.getTail() ||
                                                     getDeltaStackHeightFromTo((Unit)defBlock1.getSuccOf(def1), (Unit) defBlock1.getTail()) == 0)) {
                                                    defBlock0.remove(def0);                            
                                                    defBlock1.remove(def1);
                                                    loadBlock.insertBefore(def0, loadBlock.getHead());
                                                    mUnitToBlockMap.put(def0, loadBlock);
                                                    hasChanged = true;
                                                    if(debug) { System.out.println("inter-block opti2 occurred " + def0);}
                                                } else { if(debug) { System.out.println("failed: inter1");}}
                                            } else { if(debug) { System.out.println("failed: inter2");}}
                                        } else { if(debug) { System.out.println("failed: inter3");}}                                        
                                    } else { if(debug) { System.out.println("failed: inter4");}}
                                }        else { if(debug) { System.out.println("failed: inter5");}}                        
                            } else { if(debug) { System.out.println("failed: inter6");}}
                    }                         
                }        
            }
        }
    }
    
    

    
    protected boolean allSuccesorsOfAreThePredecessorsOf(Block defBlock, Block loadBlock)
    {        
        int size = defBlock.getSuccessors().size();        
        Iterator it = defBlock.getSuccessors().iterator();
        
        List preds = loadBlock.getPreds();
        while(it.hasNext()) {
            if(!preds.contains(it.next())) 
                return false;
        }
        
        if(size == preds.size())
            return true;
        else
            return false;        
    }



    protected boolean propagateStoreForward(Unit aInst, Unit aUnitToReach, Unit aCurrentUnit, int aStackHeight) 
    {
        boolean res = false;
        Unit currentUnit =  aCurrentUnit;           
        Local storeLocal = ((StoreInst)aInst).getLocal();
        Block block  = (Block) mUnitToBlockMap.get(aCurrentUnit);
        
        while( currentUnit != block.getTail()) {
            if(currentUnit == aUnitToReach) {
                if(aStackHeight == 0)
                    return true;
                else
                    return false;
            }
            
            if(!canMoveUnitOver(aInst, currentUnit)) {
                res = false;
                break;
            }
            
            aStackHeight -= ((Inst)currentUnit).getInCount();                
            if(aStackHeight < 0){
                res = false;
                break;
            }            
            aStackHeight += ((Inst)currentUnit).getOutCount();
            
            currentUnit = (Unit) block.getSuccOf(currentUnit);
        }        

        // if we hit the block boundry 
        if( currentUnit == block.getTail()) {
            Iterator it = block.getSuccessors().iterator();        
            while(it.hasNext()) {
                Block b = (Block) it.next();
                if(!propagateStoreForward(aInst, aUnitToReach,  b.getHead(), aStackHeight))
                    return false;                
            }
            res = true;
        }        
        return res;
    }
    
     
    
    protected boolean isCommutativeBinOp(Unit aUnit)
    {
        if(aUnit == null )
            return false;
        
        if(aUnit instanceof AddInst ||
           aUnit instanceof MulInst ||
           aUnit instanceof AndInst ||
           aUnit instanceof OrInst ||
           aUnit instanceof XorInst) 
            return true;
        else
            return false;
    }

    protected boolean propagateLoadForward(Unit aInst) 
    {
        Unit currentUnit;
        Local loadLocal = ((LoadInst)aInst).getLocal();
        Block block  = (Block) mUnitToBlockMap.get(aInst);
        boolean res = false;
        int h = 0;
        Unit candidate = null;
	
	if(aInst == block.getTail())
	  return false;
	
        currentUnit = (Unit) block.getSuccOf(aInst);
       
        while( currentUnit != block.getTail()) {
	    
            if(!canMoveUnitOver(aInst,  currentUnit)){ if(debug) { System.out.println("can't go over: " + currentUnit);}
                break;}
            

            h -= ((Inst)currentUnit).getInCount();                
            if(h < 0){ if(debug) { System.out.println("breaking at: " + currentUnit);}
                break;
            }            
            h += ((Inst)currentUnit).getOutCount();
            
            if(h == 0){
                candidate = currentUnit; // don't stop now; try to go still forward.
            }
            
            currentUnit = (Unit) block.getSuccOf(currentUnit);            
        }        
        if(candidate != null) {
            if(debug) { System.out.println("successfull propagation "  + candidate + block.getTail());}
            block.remove(aInst);
            if(block.getTail() == mUnitToBlockMap.get(candidate)){
                
                block.insertAfter(aInst, candidate);
                if(block.getTail() != aInst)
                    throw new RuntimeException();
            }
            block.insertAfter(aInst, candidate);            
	    return true;
        }
	
	return false;
    }


    
    protected void propagateLoadsForward()
    { 
        buildStoreListAndUnitToBlockMap();
        boolean hasChanged = true;

        while(hasChanged) {
	    hasChanged = false;
	    List tempList = new ArrayList();
	    tempList.addAll(mUnits);
	    Iterator it = tempList.iterator();
	    
        while(it.hasNext()) {
            Unit u = (Unit) it.next();
            if( u instanceof LoadInst) {
		if(debug) { System.out.println("trying to push:"  + u);}
                boolean res =propagateLoadForward(u);
		if(!hasChanged)
		    hasChanged = res;
            } else if(u instanceof NopInst) {
		Block block  = (Block) mUnitToBlockMap.get(u);
		block.remove(u);
	    }
        }
	}
    }


    void propagateBackwardsIndependentHunk() 
    {
	buildStoreListAndUnitToBlockMap();
	List tempList = new ArrayList();
	tempList.addAll(mUnits);
	Iterator it = tempList.iterator();
	
	while(it.hasNext()) {
	    Unit u = (Unit) it.next();
	    
	    if( u instanceof NewInst) {
		Block block  = (Block) mUnitToBlockMap.get(u);
		Unit succ = (Unit) block.getSuccOf(u);
		if( succ instanceof StoreInst) {
		    Unit currentUnit = u;
		    Unit candidate = null;
		    
		    while(currentUnit != block.getHead()) {
			currentUnit = (Unit) block.getPredOf(currentUnit);
			if(canMoveUnitOver(currentUnit, succ)){
			    candidate = currentUnit;
			} else
			    break;
		    }
		    if(candidate != null) {
			block.remove(u);
			block.remove(succ);
			block.insertBefore(u, candidate);
			block.insertBefore(succ, candidate);			
		    }		    
		}
	    } 
	}
    }
    
    void superDuper() {
    	// compress a serie of loads using dup2's and dup's
	{
	    List tempList = new ArrayList();
	    tempList.addAll(mUnits);
	    Iterator it = tempList.iterator();
	    while(it.hasNext()) {
		Unit currentInst = (Unit) it.next();
		   
		if(currentInst instanceof LoadInst) {
		    Block block = (Block) mUnitToBlockMap.get(currentInst);
		    
		    int loadCount = 1;
		    Local local = ((LoadInst)currentInst).getLocal();
		    
		    // count the number of identical loads
		    
		    Unit u;
		    for(;;){
			u = (Unit) it.next();
			if(mUnitToBlockMap.get(u) != block)
			    break;
			
			if(u instanceof LoadInst) {
			    if(((LoadInst)u).getLocal() == local)
				loadCount++;
			    else
				break;
			} else {
			    break;
			}
		    }
		    

		    if(loadCount > 1) {
			Type loadType = ((LoadInst)currentInst).getOpType();
	
			// must leave at least one load on stack before dupping
			Unit currentLoad = (Unit) mUnits.getSuccOf(currentInst);
			loadCount--; 
		  
			if(loadType instanceof LongType || loadType instanceof DoubleType) {
				
			 
				
			    while(loadCount > 0) {
				Unit nextLoad = (Unit) mUnits.getSuccOf(currentLoad); 
				replaceUnit(currentLoad,  Baf.v().newDup1Inst(loadType));						
				    
				currentLoad = nextLoad;				 
				loadCount--;
			    }				
			} else {
			    boolean canMakeDup2 = false;
			    if(loadCount >= 3) {
				canMakeDup2 = true;
				currentLoad = (Unit) mUnits.getSuccOf(currentLoad);
				loadCount--;
			    }
			    
			    while(loadCount > 0) {
				
				if(loadCount == 1 || !canMakeDup2) {
				    Unit nextLoad = (Unit) mUnits.getSuccOf(currentLoad); 
				    replaceUnit(currentLoad,  Baf.v().newDup1Inst(loadType));						
				    
				    currentLoad = nextLoad;
				    loadCount--;	
				} else {
				    if(canMakeDup2) {
					Unit nextLoad = (Unit) mUnits.getSuccOf(mUnits.getSuccOf(currentLoad));
					replaceUnit(currentLoad, (Unit) mUnits.getSuccOf(currentLoad),  Baf.v().newDup2Inst(loadType, loadType));  		  
					currentLoad = nextLoad;
					loadCount -= 2;	
				    }
				}			    				
			    }
			}
		    }		    
		    currentInst = u;
		}		
	    }
	}
    }
    

    class statistics {
	
	private int mSLL = 0;
	private int mSL = 0;
	private String methodName;
	
	void setMethodName(String aMethodName){methodName = aMethodName;}
	void madeSL(){mSL++;};
	void madeSLL(){mSLL++;};
	void reset(){mSLL = 0; mSL = 0; }
	public String toString(){return "Statistics: for "+ methodName + "\n"  + mSL + " " + mSLL ; }
    }
    
}   



