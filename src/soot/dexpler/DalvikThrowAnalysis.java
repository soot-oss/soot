// 
// (c) 2012 University of Luxembourg - Interdisciplinary Centre for 
// Security Reliability and Trust (SnT) - All rights reserved
//
// Author: Alexandre Bartel
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 2.1 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>. 
//

package soot.dexpler;

import soot.G;
import soot.Singletons;
import soot.baf.EnterMonitorInst;
import soot.baf.ReturnInst;
import soot.baf.ReturnVoidInst;
import soot.jimple.AssignStmt;
import soot.jimple.ClassConstant;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.StringConstant;
import soot.toolkits.exceptions.ThrowableSet;
import soot.toolkits.exceptions.UnitThrowAnalysis;

/**
 * 
 * @author alex
 *
 *
 * According to https://android.googlesource.com/platform/dalvik/+/2988c4f272f62af2e96f1e6161d4e99bc1dc1b16/opcode-gen/bytecode.txt
 * the following Dalvik bytecode instructions might throw an exception:
 * 
op   1a const-string                21c  y string-ref    continue|throw
op   1b const-string/jumbo          31c  y string-ref    continue|throw
op   1c const-class                 21c  y type-ref      continue|throw
op   1d monitor-enter               11x  n none          continue|throw
op   1e monitor-exit                11x  n none          continue|throw
op   1f check-cast                  21c  y type-ref      continue|throw
op   20 instance-of                 22c  y type-ref      continue|throw
op   21 array-length                12x  y none          continue|throw
op   22 new-instance                21c  y type-ref      continue|throw
op   23 new-array                   22c  y type-ref      continue|throw
op   24 filled-new-array            35c  n type-ref      continue|throw
op   25 filled-new-array/range      3rc  n type-ref      continue|throw

op   27 throw                       11x  n none          throw

op   44 aget                        23x  y none          continue|throw
op   45 aget-wide                   23x  y none          continue|throw
op   46 aget-object                 23x  y none          continue|throw
op   47 aget-boolean                23x  y none          continue|throw
op   48 aget-byte                   23x  y none          continue|throw
op   49 aget-char                   23x  y none          continue|throw
op   4a aget-short                  23x  y none          continue|throw
op   4b aput                        23x  n none          continue|throw
op   4c aput-wide                   23x  n none          continue|throw
op   4d aput-object                 23x  n none          continue|throw
op   4e aput-boolean                23x  n none          continue|throw
op   4f aput-byte                   23x  n none          continue|throw
op   50 aput-char                   23x  n none          continue|throw
op   51 aput-short                  23x  n none          continue|throw
op   52 iget                        22c  y field-ref     continue|throw
op   53 iget-wide                   22c  y field-ref     continue|throw
op   54 iget-object                 22c  y field-ref     continue|throw
op   55 iget-boolean                22c  y field-ref     continue|throw
op   56 iget-byte                   22c  y field-ref     continue|throw
op   57 iget-char                   22c  y field-ref     continue|throw
op   58 iget-short                  22c  y field-ref     continue|throw
op   59 iput                        22c  n field-ref     continue|throw
op   5a iput-wide                   22c  n field-ref     continue|throw
op   5b iput-object                 22c  n field-ref     continue|throw
op   5c iput-boolean                22c  n field-ref     continue|throw
op   5d iput-byte                   22c  n field-ref     continue|throw
op   5e iput-char                   22c  n field-ref     continue|throw
op   5f iput-short                  22c  n field-ref     continue|throw
op   60 sget                        21c  y field-ref     continue|throw
op   61 sget-wide                   21c  y field-ref     continue|throw
op   62 sget-object                 21c  y field-ref     continue|throw
op   63 sget-boolean                21c  y field-ref     continue|throw
op   64 sget-byte                   21c  y field-ref     continue|throw
op   65 sget-char                   21c  y field-ref     continue|throw
op   66 sget-short                  21c  y field-ref     continue|throw
op   67 sput                        21c  n field-ref     continue|throw
op   68 sput-wide                   21c  n field-ref     continue|throw
op   69 sput-object                 21c  n field-ref     continue|throw
op   6a sput-boolean                21c  n field-ref     continue|throw
op   6b sput-byte                   21c  n field-ref     continue|throw
op   6c sput-char                   21c  n field-ref     continue|throw
op   6d sput-short                  21c  n field-ref     continue|throw
op   6e invoke-virtual              35c  n method-ref    continue|throw|invoke
op   6f invoke-super                35c  n method-ref    continue|throw|invoke
op   70 invoke-direct               35c  n method-ref    continue|throw|invoke
op   71 invoke-static               35c  n method-ref    continue|throw|invoke
op   72 invoke-interface            35c  n method-ref    continue|throw|invoke
# unused: op 73
op   74 invoke-virtual/range        3rc  n method-ref    continue|throw|invoke
op   75 invoke-super/range          3rc  n method-ref    continue|throw|invoke
op   76 invoke-direct/range         3rc  n method-ref    continue|throw|invoke
op   77 invoke-static/range         3rc  n method-ref    continue|throw|invoke
op   78 invoke-interface/range      3rc  n method-ref    continue|throw|invoke

op   93 div-int                     23x  y none          continue|throw
op   94 rem-int                     23x  y none          continue|throw

op   9e div-long                    23x  y none          continue|throw
op   9f rem-long                    23x  y none          continue|throw

op   b3 div-int/2addr               12x  y none          continue|throw
op   b4 rem-int/2addr               12x  y none          continue|throw

op   be div-long/2addr              12x  y none          continue|throw
op   bf rem-long/2addr              12x  y none          continue|throw

op   d3 div-int/lit16               22s  y none          continue|throw
op   d4 rem-int/lit16               22s  y none          continue|throw

op   db div-int/lit8                22b  y none          continue|throw
op   dc rem-int/lit8                22b  y none          continue|throw

op   e3 +iget-volatile              22c  y field-ref     optimized|continue|throw
op   e4 +iput-volatile              22c  n field-ref     optimized|continue|throw
op   e5 +sget-volatile              21c  y field-ref     optimized|continue|throw
op   e6 +sput-volatile              21c  n field-ref     optimized|continue|throw
op   e7 +iget-object-volatile       22c  y field-ref     optimized|continue|throw
op   e8 +iget-wide-volatile         22c  y field-ref     optimized|continue|throw
op   e9 +iput-wide-volatile         22c  n field-ref     optimized|continue|throw
op   ea +sget-wide-volatile         21c  y field-ref     optimized|continue|throw
op   eb +sput-wide-volatile         21c  n field-ref     optimized|continue|throw

op   ed ^throw-verification-error   20bc n varies        optimized|throw
op   ee +execute-inline             35mi n inline-method optimized|continue|throw
op   ef +execute-inline/range       3rmi n inline-method optimized|continue|throw

op   f0 +invoke-object-init/range   35c  n method-ref    optimized|continue|throw|invoke

op   f2 +iget-quick                 22cs y field-offset  optimized|continue|throw
op   f3 +iget-wide-quick            22cs y field-offset  optimized|continue|throw
op   f4 +iget-object-quick          22cs y field-offset  optimized|continue|throw
op   f5 +iput-quick                 22cs n field-offset  optimized|continue|throw
op   f6 +iput-wide-quick            22cs n field-offset  optimized|continue|throw
op   f7 +iput-object-quick          22cs n field-offset  optimized|continue|throw
op   f8 +invoke-virtual-quick       35ms n vtable-offset optimized|continue|throw|invoke
op   f9 +invoke-virtual-quick/range 3rms n vtable-offset optimized|continue|throw|invoke
op   fa +invoke-super-quick         35ms n vtable-offset optimized|continue|throw|invoke
op   fb +invoke-super-quick/range   3rms n vtable-offset optimized|continue|throw|invoke
op   fc +iput-object-volatile       22c  n field-ref     optimized|continue|throw
op   fd +sget-object-volatile       21c  y field-ref     optimized|continue|throw
op   fe +sput-object-volatile       21c  n field-ref     optimized|continue|throw

 * In brief:
 * - const [string|class]  
 * - monitor [enter|exit]  already handled in UnitThrowAnalysis
 * - check cast            already handled in UnitThrowAnalysis
 * - instanceof            already handled in UnitThrowAnalysis
 * - array length          already handled in UnitThrowAnalysis
 * - new [instance|array]  already handled in UnitThrowAnalysis
 * - filled new array
 * - throw                 already handled in UnitThrowAnalysis
 * - invoke*               already handled in UnitThrowAnalysis
 * - [ais][get|put]        already handled in UnitThrowAnalysis
 * - div/rem               already handled in UnitThrowAnalysis
 * 
 * For a reference manual, look at https://code.google.com/p/android-source-browsing
 * 
 * 
 */

public class DalvikThrowAnalysis extends UnitThrowAnalysis {
 
    /**
     * Constructs a <code>DalvikThrowAnalysis</code> for inclusion in 
     * Soot's global variable manager, {@link G}.
     *
     * @param g guarantees that the constructor may only be called 
     * from {@link Singletons}.
     */
    public DalvikThrowAnalysis(Singletons.Global g) {}

    /**
     * Returns the single instance of <code>DalvikThrowAnalysis</code>.
     *
     * @return Soot's <code>UnitThrowAnalysis</code>.
     */
    public static DalvikThrowAnalysis v() { return G.v().soot_dexpler_DalvikThrowAnalysis(); }

    protected DalvikThrowAnalysis(boolean isInterproc) {
    	super(isInterproc);
    }
    
    public DalvikThrowAnalysis(Singletons.Global g, boolean isInterproc) {
    	super(isInterproc);
    }
    
    public static DalvikThrowAnalysis interproceduralAnalysis = null;
    
    public static DalvikThrowAnalysis interproc() {
    	return G.v().interproceduralDalvikThrowAnalysis();
    }
    
    @Override
	protected ThrowableSet defaultResult() {
		return mgr.EMPTY;
	}
	
	@Override
	protected UnitSwitch unitSwitch() {
		return new UnitThrowAnalysis.UnitSwitch() {	
		  
			// Dalvik does not throw an exception for this instruction
			@Override
			public void caseReturnInst(ReturnInst i) { 
			}
			
			// Dalvik does not throw an exception for this instruction
			@Override
			public void caseReturnVoidInst(ReturnVoidInst i) {
			}
			
			@Override
			public void caseEnterMonitorInst(EnterMonitorInst i) {
			    result = result.add(mgr.NULL_POINTER_EXCEPTION);
			    result = result.add(mgr.ILLEGAL_MONITOR_STATE_EXCEPTION);
			}

			@Override
			public void caseEnterMonitorStmt(EnterMonitorStmt s) {
			    result = result.add(mgr.NULL_POINTER_EXCEPTION);
			    result = result.add(mgr.ILLEGAL_MONITOR_STATE_EXCEPTION);
			    result = result.add(mightThrow(s.getOp()));
			}
		
			@Override
			public void caseAssignStmt(AssignStmt s) {
				// Dalvik only throws ArrayIndexOutOfBounds and
				// NullPointerException which are both handled through the
				// ArrayRef expressions. There is no ArrayStoreException in
				// Dalvik.
			    result = result.add(mightThrow(s.getLeftOp()));
			    result = result.add(mightThrow(s.getRightOp()));
			}

		};
	}
	
	@Override
	protected ValueSwitch valueSwitch() {
	  return new UnitThrowAnalysis.ValueSwitch() {


// from ./vm/mterp/c/OP_CONST_STRING.c
//
//	    HANDLE_OPCODE(OP_CONST_STRING /*vAA, string@BBBB*/)                                                                                                                                            
//	    {   
//	        StringObject* strObj;
//
//	        vdst = INST_AA(inst);
//	        ref = FETCH(1);
//	        ILOGV("|const-string v%d string@0x%04x", vdst, ref);
//	        strObj = dvmDexGetResolvedString(methodClassDex, ref);
//	        if (strObj == NULL) {
//	            EXPORT_PC();
//	            strObj = dvmResolveString(curMethod->clazz, ref);
//	            if (strObj == NULL)
//	                GOTO_exceptionThrown(); <--- HERE
//	        }   
//	        SET_REGISTER(vdst, (u4) strObj);
//	    }   
//	    FINISH(2);
//	    OP_END
//
      @Override
	    public void caseStringConstant(StringConstant c) {
        //
        // the string is already fetched when converting 
        // Dalvik bytecode to Jimple. A potential error 
        // would be detected there.
        //
        //result = result.add(mgr.RESOLVE_FIELD_ERRORS); // should we add another kind of exception for this?
	    }
   
//     
// from ./vm/mterp/c/OP_CONST_CLASS.c
//
//      HANDLE_OPCODE(OP_CONST_CLASS /*vAA, class@BBBB*/)                                                                                                                                              
//      {   
//          ClassObject* clazz;
//
//          vdst = INST_AA(inst);
//          ref = FETCH(1);
//          ILOGV("|const-class v%d class@0x%04x", vdst, ref);
//          clazz = dvmDexGetResolvedClass(methodClassDex, ref);
//          if (clazz == NULL) {
//              EXPORT_PC();
//              clazz = dvmResolveClass(curMethod->clazz, ref, true);
//              if (clazz == NULL)
//                  GOTO_exceptionThrown(); <--- HERE
//          }   
//          SET_REGISTER(vdst, (u4) clazz);
//      }   
//      FINISH(2);
//      OP_END
//      
	    @Override
	    public void caseClassConstant(ClassConstant c) {
	      //
        // the string is already fetched and stored in a 
	      // ClassConstant object when converting 
        // Dalvik bytecode to Jimple. A potential error 
        // would be detected there.
	      //
	      // result = result.add(mgr.RESOLVE_CLASS_ERRORS);
	    }
	  };
	}
	
	
}
