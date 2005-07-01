/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrice Pominville
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
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.baf.toolkits.base;
import soot.options.*;

import soot.util.*;
import java.util.*;
import soot.*;
import soot.baf.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.baf.internal.*;

public class LoadStoreOptimizer extends BodyTransformer
{
    public LoadStoreOptimizer( Singletons.Global g ) {}
    public static LoadStoreOptimizer v() { return G.v().soot_baf_toolkits_base_LoadStoreOptimizer(); }

    // Constants
    boolean debug = false;
    
    // constants returned by the stackIndependent function.
    final static private int FAILURE = 0;
    final static private int SUCCESS = 1;
    final static private int MAKE_DUP = 2;
    final static private int MAKE_DUP1_X1 = 3;
    final static private int SPECIAL_SUCCESS = 4;
    final static private int HAS_CHANGED = 5;
    final static private int SPECIAL_SUCCESS2 = 6;

    final static private int STORE_LOAD_ELIMINATION = 0;
    final static private int STORE_LOAD_LOAD_ELIMINATION = -1;

        
    private Map gOptions;

    /** The method that drives the optimizations. */
    /* This is the public interface to LoadStoreOptimizer */
  
    protected void internalTransform(Body body, String phaseName, Map options) 
    {   

        gOptions = options;

        Instance instance = new Instance();
        instance.mBody = body;        
        instance.mUnits =  body.getUnits();
        
        debug = PhaseOptions.getBoolean(gOptions, "debug");
        
        if(Options.v().verbose())
            G.v().out.println("[" + body.getMethod().getName() + "] Performing LoadStore optimizations...");

        if(debug) { G.v().out.println("\n\nOptimizing Method: " + body.getMethod().getName());}
        
        instance.go();
    }
class Instance {
    // Instance vars.
    private Chain mUnits;
    private Body mBody;
    private ExceptionalUnitGraph mExceptionalUnitGraph;
    private LocalDefs mLocalDefs;
    private LocalUses mLocalUses;
    private Map mUnitToBlockMap;     // maps a unit it's containing block
    private boolean mPass2 = false;


    void go() {
        if(!mUnits.isEmpty()) {                    
            buildUnitToBlockMap();
            computeLocalDefsAndLocalUsesInfo();  
           
            
           
            if(debug){G.v().out.println("Calling optimizeLoadStore(1)\n");}
            optimizeLoadStores(); 
        
            if(PhaseOptions.getBoolean(gOptions, "inter") ) {
                if(debug){G.v().out.println("Calling doInterBlockOptimizations");}
                doInterBlockOptimizations(); 
                             
                //computeLocalDefsAndLocalUsesInfo();          
                //propagateLoadsBackwards();         if(debug)     G.v().out.println("pass 3");         
                //optimizeLoadStores();      if(debug)   G.v().out.println("pass 4"); 
                //propagateLoadsForward();   if(debug)   G.v().out.println("pass 5"); 
                //propagateBackwardsIndependentHunk(); if(debug)  G.v().out.println("pass 6");                        
            }

            if(PhaseOptions.getBoolean(gOptions, "sl2") || PhaseOptions.getBoolean(gOptions, "sll2")  ) {        
                mPass2 = true;
                if(debug){G.v().out.println("Calling optimizeLoadStore(2)");}
                optimizeLoadStores();   
            }
        }        
    }
    /*
     *  Computes a map binding each unit in a method to the unique basic block    
     *  that contains it.
     */    
    private  void buildUnitToBlockMap()
    {
        BlockGraph blockGraph = new ZonedBlockGraph(mBody); 
        if(debug) {
            G.v().out.println("Method " +  mBody.getMethod().getName()+ " Block Graph: ");
            G.v().out.println(blockGraph);
        }
       
        List blocks = blockGraph.getBlocks();
        mUnitToBlockMap = new HashMap();
        
        Iterator blockIt = blocks.iterator();
        while(blockIt.hasNext() ) {
            Block block = (Block) blockIt.next();
            
            Iterator unitIt = block.iterator();
            while(unitIt.hasNext()) {
                Unit unit = (Unit) unitIt.next();                
                mUnitToBlockMap.put(unit, block);                
            }
        }
    }

    
    // computes a list of all the stores in mUnits in order of their occurence therein.
  
    private  List  buildStoreList()
    {
        Iterator it = mUnits.iterator();
        List storeList = new ArrayList();
        
        while(it.hasNext()) {
            Unit unit = (Unit) it.next();
            if(unit  instanceof StoreInst)
                storeList.add(unit);
        }     
        return storeList;
    }
    

    
    private void computeLocalDefsAndLocalUsesInfo() 
    {        
        mExceptionalUnitGraph =  new ExceptionalUnitGraph(mBody);
        mLocalDefs = new SmartLocalDefs(mExceptionalUnitGraph, new SimpleLiveLocals(mExceptionalUnitGraph));
        mLocalUses = new SimpleLocalUses(mExceptionalUnitGraph, mLocalDefs);
    }
   

    


    // main optimizing method
    private void optimizeLoadStores() 
    {
        //if(mBody.getMethod().getName().equals("aM")) {
        Chain units  = mUnits;
        List storeList;
        
        
        // build a list of all store units in mUnits
        storeList = buildStoreList();        
        

        // Eliminate store/load  
        {
            
            boolean hasChanged = true;
        
            boolean hasChangedFlag = false;
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
                            case 0:        /*
                                if(Options.getBoolean(gOptions, "s-elimination")) {
                                // replace store by a pop and remove store from store list
                                replaceUnit(unit, Baf.v().newPopInst(((StoreInst)unit).getOpType()));
                                unitIt.remove();
                                    
                                hasChanged = true;        hasChangedFlag = false;
                                }*/
                                break;
                                    
                            case 1:
                                if(PhaseOptions.getBoolean(gOptions, "sl")) {
                                    if(!mPass2 || PhaseOptions.getBoolean(gOptions, "sl2")) {
                                // try to eliminate store/load pair
                                        Unit loadUnit = ((UnitValueBoxPair)uses.get(0)).getUnit();
                                        block =  (Block) mUnitToBlockMap.get(unit);
                                        int test = stackIndependent(unit, loadUnit , block, STORE_LOAD_ELIMINATION);
                                
                                //xxx 
                                //if(block.getIndexInMethod() < 1 ) { // <13
                                        if(test == SUCCESS || test == SPECIAL_SUCCESS){
                                    
                                            block.remove(unit);
                                            block.remove(loadUnit);
                                            unitIt.remove();
                                            hasChanged = true;        hasChangedFlag = false;
                                    
                                            //delme[
                                            if(debug) { G.v().out.println("Store/Load elimination occurred case1.");}
                                            //delme]
                                        } /*else if (test == SPECIAL_SUCCESS2) {
                                            if(!hasChangedFlag) {
                                            hasChangedFlag = true;
                                            hasChanged = true;
                                            } 
                                            }*/
                                    }
                                }
                                break;
                                
                            case 2:
                                if(PhaseOptions.getBoolean(gOptions, "sll")) {
                                    if(!mPass2 || PhaseOptions.getBoolean(gOptions, "sll2")) {
                                // try to replace store/load/load trio by a flavor of the dup unit
                                        Unit firstLoad = ((UnitValueBoxPair)uses.get(0)).getUnit();
                                        Unit secondLoad = ((UnitValueBoxPair)uses.get(1)).getUnit();
                                        block = (Block) mUnitToBlockMap.get(unit);


                                
                                        Unit temp;  // xxx try to optimize this
                                        if(mUnits.follows(firstLoad, secondLoad)) {
                                            temp = secondLoad;
                                            secondLoad = firstLoad;
                                            firstLoad = temp;
                                        }

                                        int result = stackIndependent(unit, firstLoad, block, STORE_LOAD_ELIMINATION);                                 
                                        if(result == SUCCESS){        
                                  
                                            // move the first load just after its defining store.
                                            block.remove(firstLoad);
                                            block.insertAfter(firstLoad, unit);                                
                                    
                                 
                                            int res = stackIndependent(unit, secondLoad, block, STORE_LOAD_LOAD_ELIMINATION);
                                            if(res == MAKE_DUP) {                                        
                                                // replace store by dup, drop both loads
                                                Dup1Inst dup = Baf.v().newDup1Inst(((LoadInst) secondLoad).getOpType());
                                                dup.addAllTagsOf(unit);
                                                replaceUnit(unit, dup);
                                                unitIt.remove(); // remove store from store list
                                        
                                                block.remove(firstLoad); 
                                                block.remove(secondLoad);

                                                hasChanged = true;         hasChangedFlag = false;
                                        
                                            }  /* else if(res == MAKE_DUP1_X1) {
                                          
                                                  // replace store/load/load by a dup1_x1
                                                  Unit stackUnit = getStackItemAt2(unit, block, -2); 
                                        
                                                  if(stackUnit instanceof PushInst)
                                                  break;
                                        
                                                  Type underType = type(stackUnit);
                                                  if(underType == null) {                                         
                                                  throw new RuntimeException("this has to be corrected (loadstoroptimiser.java)" + stackUnit);
                                                  }
                                        
                                                  if(debug) { G.v().out.println("stack unit is: " + stackUnit + " stack type is " + underType);}
                                                  replaceUnit(unit, Baf.v().newDup1_x1Inst(((LoadInst) secondLoad).getOpType(),underType));
                                                  unitIt.remove();                
                                        
                                                  block.remove(firstLoad); 
                                                  block.remove(secondLoad);
                                        
                                                  hasChanged = true;          hasChangedFlag = false;                                      
                                                  break;                                        
                                        
                                                  } */
                                    
                                
                                        } else if(result == SPECIAL_SUCCESS || result == HAS_CHANGED || result == SPECIAL_SUCCESS2){
                                            if(!hasChangedFlag) {
                                                hasChangedFlag = true;
                                                hasChanged = true;
                                            } 
                                        }
                                    }
                                    
                                }
                            }                   
                        }
                    }
                }
            }
        }                    
    }
  
    
    
    
       
    /** 
     *  Checks if the units occuring between [from, to] consume 
     *. stack items not produced by these interval units. (ie if the
     *  stack height ever goes negative between from and to, assuming the 
     *  stack height is set to 0 upon executing the instruction following 'from'.
     *  
     */
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
            

    
    
    
    private int pushStoreToLoad(Unit from , Unit to, Block block)
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
                            if(debug) { G.v().out.println("xxx: negative");}
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
                    if(debug) { G.v().out.println("xxx: null");}
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
                        if(debug) { G.v().out.println("xxxspecial;success pushing forward stuff.");}
                        
                        
                        if(!canMoveUnitOver(nu, uu)){
                            if(debug) { G.v().out.println("xxx: cant move over faillure" + nu);}
                            return FAILURE;
                        }
                        if(debug) { G.v().out.println("can move" + nu + " over " + uu);}
                    }
                }        
                
                // if we get here it means we can move all the units in the set pass the units in between [to, from]
                Unit unitToMove = currentUnit; 
                while(unitToMove != from) {        
                    Unit succ = (Unit) block.getSuccOf(unitToMove);
                    if(debug) { G.v().out.println("moving " + unitToMove);}
                    block.remove(unitToMove);
                    block.insertBefore(unitToMove, to);                            
                    unitToMove = succ;
                }
                block.remove(from);
                block.insertBefore(from, to);
                               
                if(debug) { G.v().out.println("xxx1success pushing forward stuff.");}
                return SPECIAL_SUCCESS;
            }
        }

        return FAILURE;
    }
    
                
                
   
    /**
     *
     *
     * @return FAILURE when store load elimination is not possible in any form.
     * @return SUCCESS when a load in a store load pair can be moved IMMEDIATELY after it's defining store
     * @return MAKE_DUP when a store/load/load trio can be replaced by a dup unit.
     * @return MAKE_DUP_X1 when store/load/load trio can be replaced by a dup1_x1 unit
     * @return SPECIAL_SUCCESS when a store/load pair can AND must be immediately annihilated. 
     * @return HAS_CHANGED when store load elimination is not possible in any form, but some unit reordering has occured
     */
    
    private  int stackIndependent(Unit from, Unit to, Block block, int aContext) 
    {                
        if(debug) { 
            G.v().out.println("Trying: " + from + "/" + to +  " in block  " + block.getIndexInMethod()+":" );
            G.v().out.println("context:" + (aContext == STORE_LOAD_ELIMINATION ? 
                                             "STORE_LOAD_ELIMINATION" :
                                             "STORE_LOAD_LOAD_ELIMINATION"));
        }


        int minStackHeightAttained = 0; // records the min stack height attained between [from, to]
        int stackHeight = 0;           // records the stack height when similating the effects on the stack
        Iterator it = mUnits.iterator(mUnits.getSuccOf(from)); 
        int res = FAILURE;
        
        Unit currentInst = (Unit) it.next();   // get unit following the store
        
        if(aContext == STORE_LOAD_LOAD_ELIMINATION) {
            currentInst =  (Unit) it.next(); // jump after first load 
        } 
        
        // find minStackHeightAttained
        while(currentInst != to) {
            stackHeight -= ((Inst)currentInst).getInCount();
            if(stackHeight < minStackHeightAttained)
                minStackHeightAttained = stackHeight;
            
            
            stackHeight += ((Inst)currentInst).getOutCount();                
            
            currentInst = (Unit) it.next();
        }
                
        // note: now stackHeight contains the delta height of the stack after executing the units contained in
        // [from, to] non-inclusive.
        
        
        if(debug) { 
            G.v().out.println("nshv = " + stackHeight);
            G.v().out.println("mshv = " + minStackHeightAttained);
        }
        
        
        boolean hasChanged = true;        
        
        // Iterate until an elimination clause is taken or no reordering of the code occurs         
        while(hasChanged) {
            hasChanged = false;

            // check for possible sll elimination
            if(aContext == STORE_LOAD_LOAD_ELIMINATION) {
                                
                if(stackHeight == 0 && minStackHeightAttained == 0){
                    if(debug) { 
                        G.v().out.println("xxx: succ: -1, makedup ");
                    }
                    return MAKE_DUP;
                }
                else if(stackHeight == -1 && minStackHeightAttained == -1){
                    if(debug) { G.v().out.println("xxx: succ: -1, makedup , -1");}        
                    return MAKE_DUP;
                 }
                else if(stackHeight == -2 && minStackHeightAttained == -2){if(debug) { G.v().out.println("xxx: succ -1 , make dupx1 ");}
                return MAKE_DUP1_X1; }
                
                else  if (minStackHeightAttained < -2) {
                    if(debug) { G.v().out.println("xxx: failled due: minStackHeightAttained < -2 ");}
                    return FAILURE;
                }                
            }
            
            
            // check for possible sl elimination
            if(aContext == STORE_LOAD_ELIMINATION) {                
                if(stackHeight == 0 && minStackHeightAttained == 0){
                    if(debug) { G.v().out.println("xxx: success due: 0, SUCCESS ");}
                    return SUCCESS;
                }
                /* xxx broken data depensie problem.
		  else if (minStackHeightAttained == -1 && stackHeight == -1) { // try to make it more generic
                    Unit u = (Unit) block.getPredOf(from);
                    if(u instanceof FieldGetInst)
                        if(block.getPredOf(u) instanceof Dup1Inst) {
                            block.remove(u);
                            block.insertBefore(u, to);
                            block.remove(from);
                            block.insertBefore(from, to);
                            if(debug) { G.v().out.println("xxx: success due to 1, SPECIAL_SUCCESS2");}
                            return SPECIAL_SUCCESS2;
                        }                    
			}*/
                else if (minStackHeightAttained < 0){
                    return   pushStoreToLoad(from , to, block);
                }                  
            }        
            

            it = mUnits.iterator(mUnits.getSuccOf(from), to);
            
            Unit unitToMove = null; 
            Unit u = (Unit) it.next();

            if(aContext == STORE_LOAD_LOAD_ELIMINATION) {
                u =  (Unit) it.next();
            } 
            int currentH = 0;
            
            // find a candidate to move before the store/load/(load)  group 
            while( u != to) {        
                
                if(((Inst)u).getNetCount() == 1) {
                    // xxx remove this check
                    if(u instanceof LoadInst || u instanceof PushInst || u instanceof NewInst || u instanceof StaticGetInst || u instanceof Dup1Inst) {
                        
                        // verify that unitToMove is not required by following units (until the 'to' unit)
                        if(!isRequiredByFollowingUnits(u, (LoadInst) to)) {
                            unitToMove = u;
                        }
                        
                    }
                    else{
                        if(debug) { G.v().out.println("1003:(LoadStoreOptimizer@stackIndependent): found unknown unit w/ getNetCount == 1" + u);}
                    }
                }
                


           
                
                if(unitToMove != null) {
                    int flag = 0;
                    //if(aContext == 0 || !(stackHeight > -2 && minStackHeightAttained == -2 ) ) {
                        
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
            if(aContext == STORE_LOAD_ELIMINATION && stackHeight == 1 && minStackHeightAttained == 0) {
                if(debug) { G.v().out.println("xxx: commutative ");}
                return SPECIAL_SUCCESS;
            }
            else if( ((Inst) to).getOutCount()  == 1 &&
                     ((Inst) to).getInCount() == 0  &&
                     ((Inst) mUnits.getPredOf(to)).getOutCount() == 1 &&
                     ((Inst) mUnits.getPredOf(to)).getInCount() == 0) {
                
                Object toPred =  mUnits.getPredOf(to);
                block.remove((Unit) toPred);
                block.insertAfter((Unit) toPred, to);
                return HAS_CHANGED; // return has changed
            } 
            else return FAILURE;
        }
        if (aContext == STORE_LOAD_ELIMINATION)
            return   pushStoreToLoad(from , to, block);
        
        return res;
    }

    
    /**
     * @return true if aUnit perform a non-local read or write. false otherwise.
     */
    private boolean isNonLocalReadOrWrite(Unit aUnit)
    {
        if((aUnit instanceof FieldArgInst ) ||
           (aUnit instanceof ArrayReadInst) ||
           (aUnit instanceof ArrayWriteInst) )
            return true;
        else
            return false;
    }


    /**
     *  When reordering bytecode, check if it is safe to move aUnitToMove past aUnitToGoOver.
     *  @return true if aUnitToMove can be moved past aUnitToGoOver.
     */
    private boolean canMoveUnitOver(Unit aUnitToMove, Unit aUnitToGoOver) // xxx missing cases
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
           

        // xxx to be safe don't mess w/ monitors. These rules could be relaxed. ? Maybe.
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






    private  boolean tryToMoveUnit(Unit unitToMove, Block block, Unit from, Unit to, int flag) 
    {
       
        int h = 0;
        Unit current = unitToMove;
        boolean reachedStore = false;
        boolean reorderingOccurred =false;
              
        if(debug) {G.v().out.println("[tryToMoveUnit]: trying to move:" + unitToMove);}
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
                if(debug) { G.v().out.println("1006:(LoadStoreOptimizer@stackIndependent): Stack went negative while trying to reorder code.");}



                if(flag == 1) {
                    if(current instanceof DupInst) {
                        block.remove(unitToMove);
                        block.insertAfter(unitToMove, current);        
                        //                        block.insertAfter(  new BSwapInst(  )     ,unitToMove);
                    }
                    
                }
                return false;
            }
            h += ((Inst)current).getInCount();
            
            
            if(h == 0 && reachedStore == true) {
                if(!isRequiredByFollowingUnits(unitToMove, (LoadInst) to)) {
                    if(debug) { G.v().out.println("10077:(LoadStoreOptimizer@stackIndependent): reordering bytecode move: " + unitToMove + "before: " + current);}
                    block.remove(unitToMove);
                    block.insertBefore(unitToMove, current);
                    
                    reorderingOccurred = true;
                    break;
                }                    
            }                                      
        }
            
        if(reorderingOccurred) {
            if(debug) { G.v().out.println("reordering occured");}
            return true;
        } else {
            if(debug) { G.v().out.println("1008:(LoadStoreOptimizer@stackIndependent):failled to find a new slot for unit to move");}
            return false;
        }
    }

    


    /** 
     *  Replace 1 or 2 units by a third unit in a block. Both units to 
     *  replace should be in the same block. The map 'mUnitToBlockMap'   
     *  is updated. The replacement unit is inserted in the position,
     *  of the aToReplace2 if not null, otherwise in aToReplace1's slot.
     *
     *  @param aToReplace1 Unit to replace. (shouldn't be null)
     *  @param aToReplace2 Second Unit to replace (can be  null)
     *  @param aReplacement Unit that replaces the 2 previous units (shouldn't be null)
     */
    private void replaceUnit(Unit aToReplace1, Unit aToReplace2,  Unit aReplacement) 
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
    
 
    private void replaceUnit(Unit aToReplace, Unit aReplacement) 
    {
        replaceUnit(aToReplace, null, aReplacement);
    }


    
    /**
     *  Returns the type of the stack item produced by certain Unit objects.
     */
    private Type type(Unit aUnit)
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

    
    /* 
     * @param some block
     * @return true if the block is an exception handler.
     */
    private boolean isExceptionHandlerBlock(Block aBlock)
    {
        Unit blockHead = aBlock.getHead();
        Iterator  it = mBody.getTraps().iterator();
        while(it.hasNext()) {
            Trap trap = (Trap) it.next();
            if(trap.getHandlerUnit() == blockHead)
                return true;
        }
        return false;
    }

    private Unit getStackItemAt2(Unit aUnit, Block aBlock, int aDeltaHeight) 
    {
        int h = 0;
        Unit currentUnit  = aUnit;
        Unit candidate = null;
        
        while(currentUnit != null) {
            currentUnit  = (Unit) aBlock.getPredOf(currentUnit);
            if(currentUnit == null) {
                if(debug) { G.v().out.println(aBlock);}
                G.v().out.println("xxxxxxxxxxxx " + h);
                if(isExceptionHandlerBlock(aBlock) ) {
                    return new BLoadInst( RefType.v("dummy") , ((StoreInst) aUnit).getLocal());                     // we have a ref type. 
                }

                aBlock = (Block) aBlock.getPreds().get(0);
                currentUnit = (Unit) aBlock.getTail();

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
    private int getDeltaStackHeightFromTo(Unit aFrom, Unit aTo) 
    {
        Iterator it = mUnits.iterator(aFrom, aTo);
        int h = 0;
        
        while(it.hasNext()) {
            h += ((Inst)it.next()).getNetCount();
        }
        
        return h;
    }

    

    /** 
     *   Performs 2 simple inter-block optimizations in order to keep 
     *   some variables  on the stack between blocks. Both are intented to catch
     *   'if' like constructs where the control flow branches temporarely into two paths 
     *    that join up at a latter point. 
     *
     */
    private void doInterBlockOptimizations() 
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
                    if(debug) { G.v().out.println("inter trying: " + u);}
                    Block loadBlock = (Block) mUnitToBlockMap.get(u);
                    List defs = mLocalDefs.getDefsOfAt(((LoadInst)u).getLocal(), u);

                    // first optimization 
                    if(defs.size() ==  1) {
                        Block defBlock =(Block)  mUnitToBlockMap.get(defs.get(0));
                        if(defBlock != loadBlock && !(isExceptionHandlerBlock(loadBlock))) {
                            Unit storeUnit = (Unit) defs.get(0);
                            if(storeUnit instanceof StoreInst) {
                                List uses = mLocalUses.getUsesOf(storeUnit);
                                if(uses.size() == 1){
                                    if(allSuccesorsOfAreThePredecessorsOf(defBlock, loadBlock)) {
                                        if(getDeltaStackHeightFromTo((Unit) defBlock.getSuccOf(storeUnit), (Unit)defBlock.getTail()) == 0) {
                                            Iterator it2 = defBlock.getSuccs().iterator();
                                            boolean res = true;
                                            while(it2.hasNext()) {
                                                Block b = (Block) it2.next();
                                                if(getDeltaStackHeightFromTo((Unit) b.getHead(), (Unit) b.getTail()) != 0) {
                                                    res = false;
                                                    break;
                                                }
                                                if(b.getPreds().size() != 1 || b.getSuccs().size() != 1){
                                                    res = false;
                                                    break;
                                                }
                                            }                        
                                            if(debug) { G.v().out.println(defBlock.toString() + loadBlock.toString());}
                                            
                                            if(res) {
                                                defBlock.remove(storeUnit);                            
                                                mUnitToBlockMap.put(storeUnit, loadBlock);
                                                loadBlock.insertBefore(storeUnit, loadBlock.getHead());
                                                hasChanged = true;
                                                if(debug) { G.v().out.println("inter-block opti occurred " + storeUnit + " " + u);}
                                                if(debug) { G.v().out.println(defBlock.toString() + loadBlock.toString());}
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } 
                    // second optimization
                    else if(defs.size() == 2) {
                        
                        Unit def0 = (Unit) defs.get(0);
                        Unit def1 = (Unit) defs.get(1);
                            
                        Block defBlock0 = (Block)  mUnitToBlockMap.get(def0);
                        Block defBlock1 = (Block)  mUnitToBlockMap.get(def1);
                        if(defBlock0 != loadBlock && defBlock1 != loadBlock && defBlock0 != defBlock1
                           && !(isExceptionHandlerBlock(loadBlock))) {                                
                            if(mLocalUses.getUsesOf(def0).size() == 1  && mLocalUses.getUsesOf(def1).size() == 1) {
                                List def0Succs = defBlock0.getSuccs();
                                List def1Succs  = defBlock1.getSuccs();
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
                                                if(debug) { G.v().out.println("inter-block opti2 occurred " + def0);}
                                            } else { if(debug) { G.v().out.println("failed: inter1");}}
                                        } else { if(debug) { G.v().out.println("failed: inter2");}}
                                    } else { if(debug) { G.v().out.println("failed: inter3");}}                                        
                                } else { if(debug) { G.v().out.println("failed: inter4");}}
                            }        else { if(debug) { G.v().out.println("failed: inter5");}}                        
                        } else { if(debug) { G.v().out.println("failed: inter6");}}
                    }                         
                }        
            }
        }
    }
    
    

    /**
     *  Given 2 blocks, assertains whether all the succesors of the first block are the predecessors 
     *  of the second block.
     */    
    private boolean allSuccesorsOfAreThePredecessorsOf(Block aFirstBlock, Block aSecondBlock)
    {        
        int size = aFirstBlock.getSuccs().size();        
        Iterator it = aFirstBlock.getSuccs().iterator();
        
        List preds = aSecondBlock.getPreds();
        while(it.hasNext()) {
            if(!preds.contains(it.next())) 
                return false;
        }
        
        if(size == preds.size())
            return true;
        else
            return false;        
    }



    
     
    /**
     *  @return true if aUnit is a commutative binary operator
     */
    private boolean isCommutativeBinOp(Unit aUnit)
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

    /*
     *  @param aInst 
     */
    private boolean propagateLoadForward(Unit aInst) 
    {
        Unit currentUnit;
        Block block  = (Block) mUnitToBlockMap.get(aInst);
        boolean res = false;
        int h = 0;
        Unit candidate = null;
        
        if(aInst == block.getTail())
            return false;
        
        currentUnit = (Unit) block.getSuccOf(aInst);
       
        while( currentUnit != block.getTail()) {
            
            if(!canMoveUnitOver(aInst,  currentUnit)){ if(debug) { G.v().out.println("can't go over: " + currentUnit);}
            break;}
            

            h -= ((Inst)currentUnit).getInCount();                
            if(h < 0){
                if(!(aInst instanceof Dup1Inst && currentUnit instanceof Dup1Inst)) {
                    if(debug) { G.v().out.println("breaking at: " + currentUnit);}                
                    break;
                }
            }            
            
            h += ((Inst)currentUnit).getOutCount();
            
            if(h == 0){
                candidate = currentUnit; // don't stop now; try to go still forward.
            }
            
            currentUnit = (Unit) block.getSuccOf(currentUnit);            
        }        
        if(candidate != null) {
            if(debug) { G.v().out.println("successfull propagation "  + candidate + block.getTail());}
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



    /* 
     * 
     */
    private void propagateLoadsForward()
    { 
        boolean hasChanged = true;

        while(hasChanged) {
            hasChanged = false;
            List tempList = new ArrayList();
            tempList.addAll(mUnits);
            Iterator it = tempList.iterator();
            
            while(it.hasNext()) {
                Unit u = (Unit) it.next();
                if( u instanceof LoadInst || u instanceof Dup1Inst) {
                    if(debug) { G.v().out.println("trying to push:"  + u);}
                    boolean res =propagateLoadForward(u);
                    if(!hasChanged)
                        hasChanged = res;
                }
            }
        }
    }
    

    void propagateBackwardsIndependentHunk() 
    {
        
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
    


    // For each LoadInst in the method body, call propagateLoadBackwards to
    // try to relocate the load as close to the start of it's basic block as possible. 
    private void propagateLoadsBackwards()
    {        
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
                    Unit insertPoint = propagateLoadBackwards(currentInst, block);
                        
                    if(insertPoint != null) {                        
                        block.remove(currentInst);
                        block.insertBefore(currentInst, insertPoint);
                        hasChanged = true;                       
                    }
                }
            }        
        }        
    }
    

    // Given a LoadInst  and it's block, try to relocate the load as  close as possible to 
    // the start of it's block.
    private Unit propagateLoadBackwards(Unit aInst, Block aBlock) 
    {
        int h = 0;
        Unit candidate = null;
        Unit currentUnit =  aInst;   
        
        //List loadDefList = mLocalDefs.getDefsOfAt(((LoadInst)aInst).getLocal(), aInst);
                
        currentUnit = (Unit) aBlock.getPredOf(currentUnit);        
        while( currentUnit != null) {
            if(!canMoveUnitOver(currentUnit, aInst))
                break;
            
            h -= ((Inst)currentUnit).getOutCount();                            
            if(h < 0) break;                        
            h += ((Inst)currentUnit).getInCount();
            if(h == 0) candidate = currentUnit;
           
            
            currentUnit = (Unit) aBlock.getPredOf(currentUnit);
        }        
        
        return candidate;
    }
    


    // recycled code:
    /*
      // convertsa series of loads into dups when applicable
      void superDuper1() { 
      List tempList = new ArrayList();
      tempList.addAll(mUnits);
      Iterator it = tempList.iterator();
      boolean fetchUnit = true;
        
      while(it.hasNext()) {
      Unit currentInst = null;
            
      if(fetchUnit) {
      currentInst = (Unit) it.next();               
      }
      fetchUnit = true;
            
      if(currentInst instanceof LoadInst) {
      Block block = (Block) mUnitToBlockMap.get(currentInst);
                
      Local local = ((LoadInst)currentInst).getLocal();
                    
      // count the number of identical loads
                
      Unit u;
      for(;;){
      if(it.hasNext()) {
      u = (Unit) it.next();
      if(mUnitToBlockMap.get(u) != block)
      break;
                        
      if(u instanceof LoadInst) {
      if(((LoadInst)u).getLocal() == local) {
      replaceUnit(u, Baf.v().newDup1Inst(((LoadInst) u).getOpType()));

      } else {
      fetchUnit =false;
      break;
      }
      } else {                        
      break;
      }
      } else 
      break;
      }                
      }
      }
      }

    
      //broken use superDuper1
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
    
                                void peephole() {
                                boolean hasChanged = true;
       
                                while(hasChanged) {
                                hasChanged = false;
                                List tempList = new ArrayList();
                                tempList.addAll(mUnits);
           
                                Iterator it = tempList.iterator();
                                while(it.hasNext() ) {
                                Unit currentUnit = (Unit) it.next();
                                if(currentUnit instanceof PopInst) {
                                Block popBlock = (Block) mUnitToBlockMap.get(currentUnit);
                                Unit prev = (Unit) popBlock.getPredOf(currentUnit);
                                Unit succ = (Unit) popBlock.getSuccOf(currentUnit);
                   
                                if(prev instanceof LoadInst || prev instanceof PushInst) 
                                if(((AbstractInst)prev).getOutMachineCount() == ((AbstractInst)currentUnit).getInMachineCount()) {
                                popBlock.remove(prev);
                                popBlock.remove(currentUnit);
                                }
                                else if(succ instanceof ReturnInst) {
                                popBlock.remove(currentUnit);
                                popBlock.remove(succ);
                                }                                   
                                }
                                }       
                                }
                                }  

                
                                private boolean propagateStoreForward(Unit aInst, Unit aUnitToReach, Unit aCurrentUnit, int aStackHeight) 
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

    */

}   


}

