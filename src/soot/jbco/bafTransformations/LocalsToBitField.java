/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

package soot.jbco.bafTransformations;

import java.util.*;

import soot.*;
import soot.baf.*;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.Rand;
import soot.jimple.*;

public class LocalsToBitField extends BodyTransformer  implements IJbcoTransform {

  int replaced = 0;
  int locals = 0;
  
  public static String dependancies[] = new String[]{"jtp.jbco_jl","bb.jbco_plvb", "bb.jbco_ful", "bb.lp"}; 
  
  public String[] getDependancies() {
    return dependancies;
  }
  
  public static String name = "bb.jbco_plvb";
  
  public String getName() {
    return name;
  }

  
  public void outputSummary() {
    out.println("Local fields inserted into bitfield: "+replaced);
    out.println("Original number of locals: "+locals);
  }
  

  @SuppressWarnings("fallthrough")
  protected void internalTransform(Body b, String phaseName, Map<String,String> options) {
    
    int weight = soot.jbco.Main.getWeight(phaseName, b.getMethod().getSignature());
    if (weight == 0) return;
    
    //  build mapping of baf locals to jimple locals
    Collection<Local> bLocals = b.getLocals();
    PatchingChain<Unit> u = b.getUnits();

    Unit first = null;
    List<Value> params = new ArrayList<Value>();
    Iterator<Unit> uit = u.iterator();
    while (uit.hasNext()) {
      Unit unit = uit.next();
      if (unit instanceof IdentityInst) {
        IdentityInst ii = (IdentityInst)unit;
        if (ii.getRightOpBox().getValue() instanceof ParameterRef) {
          Value v = ii.getLeftOp();
          if (v instanceof Local) {
            params.add(v);
            first = unit;
          }
        }
      }
    }
    
    //  build mapping of baf locals to jimple locals
    Map<Local, Local> bafToJLocals = new HashMap<Local, Local>();
    Iterator<Local> jlocIt = soot.jbco.Main.methods2JLocals.get(b.getMethod()).iterator();
    while (jlocIt.hasNext()) {
      Local jl = jlocIt.next();
      Iterator<Local> blocIt = bLocals.iterator();
      while (blocIt.hasNext()) {
        Local bl = blocIt.next();
        if (bl.getName().equals(jl.getName())) {
          bafToJLocals.put(bl, jl);
          break;
        }
      }
    }
    
    List<Local> booleans = new ArrayList<Local>();
    List<Local> bytes = new ArrayList<Local>();
    List<Local> chars = new ArrayList<Local>();
    List<Local> ints = new ArrayList<Local>();
    Map<Local, Integer> sizes = new HashMap<Local, Integer>();
    Iterator<Local> blocs = bLocals.iterator();
    while (blocs.hasNext()) {
      Local bl = blocs.next();
      if (params.contains(bl) )
          continue;
      
      locals++;
      Local jlocal = bafToJLocals.get(bl);
      if (jlocal !=null) {
        Type t = jlocal.getType();
        if (t instanceof PrimType 
            && !(t instanceof DoubleType || t instanceof LongType)
            && Rand.getInt(10) <= weight)
        {
          if (t instanceof BooleanType) {
            booleans.add(bl);
            sizes.put(bl,new Integer(1));
          } else if (t instanceof ByteType) {
            bytes.add(bl);
            sizes.put(bl,new Integer(8));
          } else if (t instanceof CharType) {// || t instanceof ShortType) {
            chars.add(bl);
            sizes.put(bl,new Integer(16));
          } else if (t instanceof IntType) {
            ints.add(bl);
            sizes.put(bl,new Integer(32));
          }
        }
      }
    }
    
    int count = 0;
    Map<Local,Local> bafToNewLocs = new HashMap<Local,Local>();
    int total = booleans.size() + bytes.size()*8 + chars.size()*16 + ints.size()*32;
    Map<Local,Map<Local,Integer>> newLocs = new HashMap<Local,Map<Local,Integer>>();
    while (total >= 32 && booleans.size() + bytes.size() + chars.size() + ints.size() > 2) {
      Local nloc = Baf.v().newLocal("newDumby"+count++, LongType.v()); //soot.jbco.util.Rand.getInt(2) > 0 ? DoubleType.v() : LongType.v());
      Map<Local, Integer> nlocMap = new HashMap<Local, Integer>();
      
      boolean done = false;
      int index = 0;
      while (index < 64 && !done) {
        int max = 64 - index;
        max = max > 31 ? 4 : max > 15 ? 3 : max > 7 ? 2 : 1;
        int rand = Rand.getInt(max);
        max = index;
        switch (rand) {
          case 3:
            if (ints.size()>0) {
              Local l = ints.remove(Rand.getInt(ints.size()));
              nlocMap.put(l,new Integer(index));
              index = index+32;
              bafToNewLocs.put(l,nloc);
              index = getNewIndex(index,ints,chars,bytes,booleans);
              break;
            }
          case 2:
            if (chars.size()>0) {
              Local l = chars.remove(Rand.getInt(chars.size()));
              nlocMap.put(l,new Integer(index));
              index = index+16;
              bafToNewLocs.put(l,nloc);
              index = getNewIndex(index,ints,chars,bytes,booleans);
              break;
            }
          case 1:
            if (bytes.size()>0) {
              Local l = bytes.remove(Rand.getInt(bytes.size()));
              nlocMap.put(l,new Integer(index));
              index = index+8;
              bafToNewLocs.put(l,nloc);
              index = getNewIndex(index,ints,chars,bytes,booleans);
              break;
            }
          case 0: 
            if (booleans.size()>0) {
              Local l = booleans.remove(Rand.getInt(booleans.size()));
              nlocMap.put(l,new Integer(index++));
              bafToNewLocs.put(l,nloc);
              index = getNewIndex(index,ints,chars,bytes,booleans);
              break;
            }
        } // end switch
        if (max == index) done = true;
      }
      newLocs.put(nloc,nlocMap);
      bLocals.add(nloc);
      if (first!=null) {
        u.insertAfter(Baf.v().newStoreInst(LongType.v(),nloc),first);
        u.insertAfter(Baf.v().newPushInst(LongConstant.v(0)),first);
      } else {
        u.addFirst(Baf.v().newStoreInst(LongType.v(),nloc));
        u.addFirst(Baf.v().newPushInst(LongConstant.v(0)));
      }
      total = booleans.size() + bytes.size()*8 + chars.size()*16 + ints.size()*32;
    }
    
    if (bafToNewLocs.size()==0) return;
    
    Iterator<Unit> it = u.snapshotIterator();
    while (it.hasNext()) {
      Unit unit = it.next();
      if (unit instanceof StoreInst) {
        StoreInst si = (StoreInst)unit;
        Local bafLoc = si.getLocal();
        Local nloc = bafToNewLocs.get(bafLoc);
        if (nloc != null) {
          Local jloc = bafToJLocals.get(bafLoc);
          
          int index = (newLocs.get(nloc).get(bafLoc)).intValue();
          int size = sizes.get(bafLoc).intValue();
          long longmask = 0xFFFFFFFFFFFFFFFFL ^ ( 
              (size == 1 ? 0x1L : size == 8 ? 0xFFL : 
                size==16 ? 0xFFFFL : 0xFFFFFFFFL) << index);
          
          u.insertBefore(Baf.v().newPrimitiveCastInst(jloc.getType(),LongType.v()),unit);
          if (index>0) {
            u.insertBefore(Baf.v().newPushInst(IntConstant.v(index)),unit);
            u.insertBefore(Baf.v().newShlInst(LongType.v()),unit);
          }
          u.insertBefore(Baf.v().newPushInst(LongConstant.v(longmask ^ 0xFFFFFFFFFFFFFFFFL)),unit);
          u.insertBefore(Baf.v().newAndInst(LongType.v()),unit);
          u.insertBefore(Baf.v().newLoadInst(LongType.v(),nloc),unit);
          u.insertBefore(Baf.v().newPushInst(LongConstant.v(longmask)),unit);
          u.insertBefore(Baf.v().newAndInst(LongType.v()),unit);
          u.insertBefore(Baf.v().newXorInst(LongType.v()),unit);
          u.insertBefore(Baf.v().newStoreInst(LongType.v(),nloc),unit);
          u.remove(unit);
        }
      } else if (unit instanceof LoadInst) {
        LoadInst li = (LoadInst)unit;
        Local bafLoc = li.getLocal();
        Local nloc = bafToNewLocs.get(bafLoc);
        if (nloc != null) {
          int index = (newLocs.get(nloc).get(bafLoc)).intValue();
          int size = sizes.get(bafLoc).intValue();
          long longmask = (size == 1 ? 0x1L : size == 8 ? 0xFFL : 
                size==16 ? 0xFFFFL : 0xFFFFFFFFL) << index;
          
          u.insertBefore(Baf.v().newLoadInst(LongType.v(),nloc),unit);
          u.insertBefore(Baf.v().newPushInst(LongConstant.v(longmask)),unit);
          u.insertBefore(Baf.v().newAndInst(LongType.v()),unit);
          if (index>0) {
            u.insertBefore(Baf.v().newPushInst(IntConstant.v(index)),unit);
            u.insertBefore(Baf.v().newShrInst(LongType.v()),unit);
          }
          
          Type origType = bafToJLocals.get(bafLoc).getType();
          Type t = getType(origType);
          u.insertBefore(Baf.v().newPrimitiveCastInst(LongType.v(),t),unit);
          if (!(origType instanceof IntType) && !(origType instanceof BooleanType))
            u.insertBefore(Baf.v().newPrimitiveCastInst(t,origType),unit);  
          u.remove(unit);
        }
      } else if (unit instanceof IncInst) {
        IncInst ii = (IncInst)unit;
        Local bafLoc = ii.getLocal();
        Local nloc = bafToNewLocs.get(bafLoc);
        if (nloc != null) {
          Type jlocType = getType(bafToJLocals.get(bafLoc).getType());
          
          int index = (newLocs.get(nloc).get(bafLoc)).intValue();
          int size = sizes.get(bafLoc).intValue();
          long longmask = (size == 1 ? 0x1L : size == 8 ? 0xFFL : 
                size==16 ? 0xFFFFL : 0xFFFFFFFFL) << index;
          
          u.insertBefore(Baf.v().newPushInst(ii.getConstant()),unit);
          u.insertBefore(Baf.v().newLoadInst(LongType.v(),nloc),unit);
          u.insertBefore(Baf.v().newPushInst(LongConstant.v(longmask)),unit);
          u.insertBefore(Baf.v().newAndInst(LongType.v()),unit);
          if (index>0) {
            u.insertBefore(Baf.v().newPushInst(IntConstant.v(index)),unit);
            u.insertBefore(Baf.v().newShrInst(LongType.v()),unit);
          }
          u.insertBefore(Baf.v().newPrimitiveCastInst(LongType.v(),ii.getConstant().getType()),unit);
          u.insertBefore(Baf.v().newAddInst(ii.getConstant().getType()),unit);
          u.insertBefore(Baf.v().newPrimitiveCastInst(jlocType,LongType.v()),unit);
          if (index>0) {
            u.insertBefore(Baf.v().newPushInst(IntConstant.v(index)),unit);
            u.insertBefore(Baf.v().newShlInst(LongType.v()),unit);
          }
         
          longmask = 0xFFFFFFFFFFFFFFFFL ^ longmask;
          u.insertBefore(Baf.v().newLoadInst(LongType.v(),nloc),unit);
          u.insertBefore(Baf.v().newPushInst(LongConstant.v(longmask)),unit);
          u.insertBefore(Baf.v().newAndInst(LongType.v()),unit);
          u.insertBefore(Baf.v().newXorInst(LongType.v()),unit);
          u.insertBefore(Baf.v().newStoreInst(LongType.v(),nloc),unit);
          u.remove(unit);
        }
      }
    }
    
    for(Iterator<Local> lit = bLocals.iterator(); lit.hasNext(); ) {
    	Local l = lit.next();
        if (bafToNewLocs.containsKey(l)) {
            it.remove();
            replaced++;
          }
    }
  }
  
  private Type getType(Type t) {
    if (t instanceof BooleanType || t instanceof CharType || t instanceof ShortType || t instanceof ByteType)
      return IntType.v();
    else
      return t;
  }
  
  private int getNewIndex(int index, List<Local> ints, List<Local> chars, List<Local> bytes, List<Local> booleans) {
    int max = 0;
    if (booleans.size() > 0 && index < 63)
      max = 64;
    else if (bytes.size() > 0 && index < 56)
      max = 57;
    else if (chars.size() > 0 && index < 48)
      max = 49;
    else if (ints.size() > 0 && index < 32)
      max = 33;
    
    if (max != 0) {
      int rand = Rand.getInt(4);
      max = max - index;
      if (max > rand) 
        max = rand;
      else if (max != 1) 
        max = Rand.getInt(max);
      index+=max;
    }
    return index;
  }
}
