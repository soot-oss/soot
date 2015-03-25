// 
// (c) 2012 University of Luxembourg - Interdisciplinary Centre for 
// Security Reliability and Trust (SnT) - All rights reserved
//
// Author: Alexandre Bartel
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>. 
//

package soot.dexpler.typing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.Body;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.ShortType;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.dexpler.IDalvikTyper;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.Constant;
import soot.jimple.IfStmt;
import soot.jimple.NullConstant;

public class DalvikTyper implements IDalvikTyper {

	private static final Logger logger =LoggerFactory.getLogger(DalvikTyper.class);

    private static DalvikTyper dt = null;
    
    private Set<Constraint> constraints = new HashSet<Constraint>();
    private Map<ValueBox, Type> typed = new HashMap<ValueBox, Type>();
    private Map<Local, Type> localTyped = new HashMap<Local, Type>();
    private Set<Local> localTemp = new HashSet<Local>();
    private List<LocalObj> localObjList = new ArrayList<LocalObj>();
    
    private DalvikTyper() {}
    
    public static DalvikTyper v() {
        if (dt == null)
            dt = new DalvikTyper();
        return dt;
    }
    
    public void clear() {
        constraints.clear();
        typed.clear();
        localTyped.clear();
        localTemp.clear();
        localObjList.clear();
    }
    
	public void setType(ValueBox vb, Type t, boolean isUse) {
	    if (IDalvikTyper.DEBUG)
	        logger.debug("   [setType] "+ vb +" -> "+ t);
//        if (typed.containsKey(vb)) {
//            System.err.println("warning: typed already contains vb "+ vb +" ("+ typed.get(vb) +") new type: "+ t);
//        }

        
        //for (ValueBox vb: typed.keySet())
        if (vb.getValue() instanceof Local) {
            localObjList.add(new LocalObj(vb, t, isUse));
        } else {
            logger.debug("not instance of local: vb: "+ vb +" value: "+ vb.getValue() +" class: "+ vb.getValue().getClass());
        }
	}
//
//	public void setObjectType(ValueBox v) {
//		// TODO Auto-generated method stub
//		
//	}

	public void addConstraint(ValueBox l, ValueBox r) {
	    if (IDalvikTyper.DEBUG)
	        logger.debug("   [addConstraint] "+ l +" < "+ r);
		constraints.add(new Constraint(l, r));		
	}
	
//	public void addStrongConstraint(ValueBox vb, Type t) {
//	    if (IDalvikTyper.DEBUG)
//	        logger.debug("   [addStrongConstraint] "+ vb +" -> "+ t);
//	    if (typed.containsKey(vb)) {
//	        System.err.println("warning: typed already contains vb "+ vb +" ("+ typed.get(vb) +") new type: "+ t);
//	    }
//	    typed.put(vb, t);
//	}

	public void assignType(Body b) {
	    
	    
	    logger.debug("list of constraints:");
	    List<ValueBox> vbList = b.getUseAndDefBoxes();
	    
	    // clear constraints after local splitting and dead code eliminator
	    List<Constraint> toRemove = new ArrayList<Constraint>();
		for (Constraint c: constraints) {    
		    if (!vbList.contains(c.l)) {
		        logger.debug("warning: "+ c.l +" not in locals! removing...");
		        toRemove.add(c);
		        continue;
		    }
		    if (!vbList.contains(c.r)) {
                logger.debug("warning: "+ c.r +" not in locals! removing...");
                toRemove.add(c);
                continue;
		    }
		}
		for (Constraint c: toRemove)
		    constraints.remove(c);
		
		// keep only valid locals
		for (LocalObj lo: localObjList) {
		    if (!vbList.contains(lo.vb)) {
		        logger.debug("removing vb: "+ lo.vb +" with type "+ lo.t);
		        continue;
		    }
		    Local l = lo.getLocal();
		    Type t = lo.t;
            if (localTemp.contains(l) && lo.isUse) {
                logger.debug("def already added for local "+ l +"! for vb: "+ lo.vb);
            } else {
                logger.debug("add type "+ t +" to local "+ l + " for vb: "+ lo.vb);
                localTemp.add(l);
                typed.put(lo.vb, t);
            }
		}
	    for (ValueBox vb: typed.keySet()) {
	        if (vb.getValue() instanceof Local) {
	            Local l = (Local)vb.getValue();
	            localTyped.put(l, typed.get(vb));
	        }
	    }
		
		for (Constraint c: constraints)
		    logger.debug("constraint: "+ c);
		for (ValueBox vb: typed.keySet()) {
		    logger.debug("typed: "+ vb +" -> "+ typed.get(vb));
		}
		for (Local l: localTyped.keySet()){
		    logger.debug("localTyped: "+ l + " -> "+ localTyped.get(l));
		}
		
		while (!constraints.isEmpty()) {
		    boolean update = false;
		    for (Constraint c: constraints) {
		        logger.debug("current constraint: "+ c);
		        Value l = c.l.getValue();
		        Value r = c.r.getValue();
		        if (l instanceof Local && r instanceof Constant) {
		            Constant cst = (Constant)r;
		            if (!localTyped.containsKey(l))
		                continue;
		            Type lt = localTyped.get((Local)l);
		            logger.debug("would like to set type "+ lt +" to constant: "+ c);
		            Value newValue = null;
		            if (lt instanceof IntType
		                    || lt instanceof BooleanType
		                    || lt instanceof ShortType
		                    || lt instanceof CharType
		                    || lt instanceof ByteType) {
		                UntypedIntOrFloatConstant uf = (UntypedIntOrFloatConstant)cst;
		                newValue = uf.toIntConstant();
		            } else if (lt instanceof FloatType) {
		                UntypedIntOrFloatConstant uf = (UntypedIntOrFloatConstant)cst;
		                newValue = uf.toFloatConstant();
		            } else if (lt instanceof DoubleType) {
		                UntypedLongOrDoubleConstant ud = (UntypedLongOrDoubleConstant)cst;
		                newValue = ud.toDoubleConstant();
		            } else if (lt instanceof LongType) {
		                UntypedLongOrDoubleConstant ud = (UntypedLongOrDoubleConstant)cst;
		                newValue = ud.toLongConstant();
		            } else {
		                if (cst instanceof UntypedIntOrFloatConstant && ((UntypedIntOrFloatConstant)cst).value == 0) {
		                    newValue = NullConstant.v();
		                    logger.info("new null constant for constraint "+ c +" with l type: "+ localTyped.get(l));
		                } else {
		                    throw new RuntimeException("unknow type for constance: "+ lt);
		                }
		            }
		            c.r.setValue(newValue);
		            //c.r.setValue(new Int)
		            logger.debug("remove constraint: "+ c);
		            constraints.remove(c);
		            update = true;
		            break;
		        } else if (l instanceof Local && r instanceof Local) {
		            Local leftLocal = (Local)l;
		            Local rightLocal = (Local)r;
		            if (localTyped.containsKey(leftLocal)) {
		                Type leftLocalType = localTyped.get(leftLocal);
		                if (!localTyped.containsKey(rightLocal)) {
		                    logger.debug("set type "+ leftLocalType +" to local "+ rightLocal);
		                    rightLocal.setType(leftLocalType);
		                    setLocalTyped(rightLocal, leftLocalType);
		                }
		                logger.debug("remove constraint: "+ c);
		                constraints.remove(c);
		                update = true;
		                break;
		            } else if (localTyped.containsKey(rightLocal)) {
		                Type rightLocalType = localTyped.get(rightLocal);
		                if (!localTyped.containsKey(leftLocal)) {
		                    logger.debug("set type "+ rightLocalType +" to local "+ leftLocal);
		                    leftLocal.setType(rightLocalType);
		                    setLocalTyped(leftLocal, rightLocalType);
		                }
		                logger.debug("remove constraint: "+ c);
		                constraints.remove(c);
	                    update = true;
	                    break;
		            }
		        } else if (l instanceof ArrayRef && r instanceof Local) {
		            Local rightLocal = (Local)r;
		            ArrayRef ar = (ArrayRef)l;
		            Local base = (Local)ar.getBase();
		            logger.debug("base: "+ base);
		            logger.debug("index: "+ ar.getIndex());
		            if (localTyped.containsKey(base)) {
		                Type t = localTyped.get(base);
		                //ArrayType at = (ArrayType) t;
		                //Type elementType = at.getElementType();
		                logger.debug("type of local1: "+ t +" "+ t.getClass());
		                Type elementType = null;
		                if (t instanceof ArrayType) {
		                    ArrayType at = (ArrayType)t;
		                    elementType = at.getArrayElementType();
		                } else {
		                    continue;
		                    //throw new RuntimeException("do not handle this :" + t.getClass());
		                }
		                 
		                if (!localTyped.containsKey(rightLocal)) {
		                    logger.debug("set type "+ elementType +" to local "+ r);
		                    rightLocal.setType(elementType);
		                    setLocalTyped(rightLocal, elementType);
		                }
		                logger.debug("remove constraint: "+ c);
	                    constraints.remove(c);
	                    update = true;
	                    break;
		            }
		        } else if (l instanceof Local && r instanceof ArrayRef) {
		            Local leftLocal = (Local)l;
                    ArrayRef ar = (ArrayRef)r;
                    Local base = (Local)ar.getBase();
                    if (localTyped.containsKey(base)) {
                        Type t = localTyped.get(base);
                        //ArrayType at = (ArrayType)t;
                        //Type elementType = at.getElementType();
                        logger.debug("type of local2: "+ t +" "+ t.getClass());
                        Type elementType = null;
                        if (t instanceof ArrayType) {
                            ArrayType at = (ArrayType)t;
                            elementType = at.getArrayElementType();
                        } else {
                            continue;
                            //throw new RuntimeException("do not handle this :" + t.getClass());
                        }
                        
                        if (!localTyped.containsKey(leftLocal)) {
                            logger.debug("set type "+ elementType +" to local "+ l);
                            leftLocal.setType(elementType);
                            setLocalTyped(leftLocal, elementType);
                        }
                        logger.debug("remove constraint: "+ c);
                        constraints.remove(c);
                        update = true;
                        break;
                    }
                }  else {
		            throw new RuntimeException("error: do not handling this kind of constraint: "+ c);
		        }
		    }
		    if (!update)
		        break;
		}
		
		// At this point some constants may be untyped.
		// (for instance if it is only use in a if condition).
		// We assume type in integer.
		//
		for (Constraint c: constraints) {
            logger.debug("current constraint: "+ c);
            Value l = c.l.getValue();
            Value r = c.r.getValue();
            if (l instanceof Local && r instanceof Constant) {
                if (r instanceof UntypedIntOrFloatConstant) {
                    UntypedIntOrFloatConstant cst = (UntypedIntOrFloatConstant)r;
                    Value newValue = null;
                    if (cst.value != 0) {
                        logger.debug("[untyped constaints] set type int to non zero constant: "+ c +" = "+ cst.value);
                        newValue = cst.toIntConstant();
                    } else { // check if used in cast, just in case...
                        for (Unit u: b.getUnits()) {
                            for (ValueBox vb1: u.getUseBoxes()) {
                                Value v1 = vb1.getValue();
                                if (v1 == l) {
                                    logger.info("local used in "+ u);
                                    if (u instanceof AssignStmt) {
                                        AssignStmt a = (AssignStmt)u;
                                        Value right = a.getRightOp();
                                        if (right instanceof CastExpr) {
                                            newValue = NullConstant.v();
                                        } else {
                                            newValue = cst.toIntConstant();
                                        }
                                    } else if (u instanceof IfStmt) {
                                        newValue = cst.toIntConstant();//TODO check this better
                                    }
                                }
                            }
                        }
                    }
                    c.r.setValue(newValue);
                } else if (r instanceof UntypedLongOrDoubleConstant) {
                    logger.debug("[untyped constaints] set type long to constant: {}",c);
                    Value newValue = ((UntypedLongOrDoubleConstant)r).toLongConstant();
                    c.r.setValue(newValue);
                }
            }
		}
		
	}

	
	
	
	
	
	private void setLocalTyped(Local l, Type t) {
        localTyped.put(l, t);
    }


	class LocalObj {
	    ValueBox vb;
	    Type t;
	    //private Local l;
	    boolean isUse;
	    public LocalObj(ValueBox vb, Type t, boolean isUse) {
	        this.vb = vb;
	        //this.l = (Local)vb.getValue();
	        this.t = t;
	        this.isUse = isUse;
	    }
	    
	    public Local getLocal(){
	        return (Local)vb.getValue();
	    }
	    
	}



    class Constraint {
	    ValueBox l;
	    ValueBox r;
	    public Constraint(ValueBox l, ValueBox r) {
	        this.l = l;
	        this.r = r;
	    }
	    
	    public String toString() {
	        return l +" < "+ r;
	    }
	}

}
