package soot.dexpler;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 *
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import static soot.dexpler.instructions.InstructionFactory.fromInstruction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jf.dexlib2.analysis.ClassPath;
import org.jf.dexlib2.analysis.ClassPathResolver;
import org.jf.dexlib2.analysis.ClassProvider;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.ExceptionHandler;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.TryBlock;
import org.jf.dexlib2.iface.debug.DebugItem;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.immutable.debug.ImmutableLineNumber;
import org.jf.dexlib2.util.MethodUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.DoubleType;
import soot.Local;
import soot.LongType;
import soot.Modifier;
import soot.NullType;
import soot.PackManager;
import soot.PhaseOptions;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.UnknownType;
import soot.Value;
import soot.ValueBox;
import soot.dexpler.instructions.DanglingInstruction;
import soot.dexpler.instructions.DeferableInstruction;
import soot.dexpler.instructions.DexlibAbstractInstruction;
import soot.dexpler.instructions.MoveExceptionInstruction;
import soot.dexpler.instructions.OdexInstruction;
import soot.dexpler.instructions.PseudoInstruction;
import soot.dexpler.instructions.RetypeableInstruction;
import soot.dexpler.typing.DalvikTyper;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ConditionExpr;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.EqExpr;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NeExpr;
import soot.jimple.NullConstant;
import soot.jimple.NumericConstant;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.toolkits.base.Aggregator;
import soot.jimple.toolkits.scalar.ConditionalBranchFolder;
import soot.jimple.toolkits.scalar.ConstantCastEliminator;
import soot.jimple.toolkits.scalar.CopyPropagator;
import soot.jimple.toolkits.scalar.DeadAssignmentEliminator;
import soot.jimple.toolkits.scalar.FieldStaticnessCorrector;
import soot.jimple.toolkits.scalar.IdentityCastEliminator;
import soot.jimple.toolkits.scalar.IdentityOperationEliminator;
import soot.jimple.toolkits.scalar.MethodStaticnessCorrector;
import soot.jimple.toolkits.scalar.NopEliminator;
import soot.jimple.toolkits.scalar.UnreachableCodeEliminator;
import soot.jimple.toolkits.typing.TypeAssigner;
import soot.options.JBOptions;
import soot.options.Options;
import soot.tagkit.LineNumberTag;
import soot.tagkit.SourceLineNumberTag;
import soot.toolkits.exceptions.TrapTightener;
import soot.toolkits.scalar.LocalPacker;
import soot.toolkits.scalar.LocalSplitter;
import soot.toolkits.scalar.UnusedLocalEliminator;

/**
 * A DexBody contains the code of a DexMethod and is used as a wrapper around JimpleBody in the jimplification process.
 *
 * @author Michael Markert
 * @author Frank Hartmann
 */
public class DexBody {
  private static final Logger logger = LoggerFactory.getLogger(DexBody.class);
  protected List<DexlibAbstractInstruction> instructions;
  // keeps track about the jimple locals that are associated with the dex
  // registers
  protected Local[] registerLocals;
  protected Local storeResultLocal;
  protected Map<Integer, DexlibAbstractInstruction> instructionAtAddress;

  protected List<DeferableInstruction> deferredInstructions;
  protected Set<RetypeableInstruction> instructionsToRetype;
  protected DanglingInstruction dangling;

  protected int numRegisters;
  protected int numParameterRegisters;
  protected final List<Type> parameterTypes;
  protected boolean isStatic;

  protected JimpleBody jBody;
  protected List<? extends TryBlock<? extends ExceptionHandler>> tries;

  protected RefType declaringClassType;

  protected final DexFile dexFile;
  protected final Method method;

  // detect array/instructions overlapping obfuscation
  protected List<PseudoInstruction> pseudoInstructionData = new ArrayList<PseudoInstruction>();

  PseudoInstruction isAddressInData(int a) {
    for (PseudoInstruction pi : pseudoInstructionData) {
      int fb = pi.getDataFirstByte();
      int lb = pi.getDataLastByte();
      if (fb <= a && a <= lb) {
        return pi;
      }
    }
    return null;
  }

  /**
   * @param code
   *          the codeitem that is contained in this body
   * @param method
   *          the method that is associated with this body
   */
  protected DexBody(DexFile dexFile, Method method, RefType declaringClassType) {
    MethodImplementation code = method.getImplementation();
    if (code == null) {
      throw new RuntimeException("error: no code for method " + method.getName());
    }
    this.declaringClassType = declaringClassType;
    tries = code.getTryBlocks();

    List<? extends CharSequence> paramTypes = method.getParameterTypes();
    if (paramTypes != null) {
      parameterTypes = new ArrayList<Type>();
      for (CharSequence type : paramTypes) {
        parameterTypes.add(DexType.toSoot(type.toString()));
      }
    } else {
      parameterTypes = Collections.emptyList();
    }

    isStatic = Modifier.isStatic(method.getAccessFlags());
    numRegisters = code.getRegisterCount();
    numParameterRegisters = MethodUtil.getParameterRegisterCount(method);
    if (!isStatic) {
      numParameterRegisters--;
    }

    instructions = new ArrayList<DexlibAbstractInstruction>();
    instructionAtAddress = new HashMap<Integer, DexlibAbstractInstruction>();
    registerLocals = new Local[numRegisters];

    extractDexInstructions(code);

    // Check taken from Android's dalvik/libdex/DexSwapVerify.cpp
    if (numParameterRegisters > numRegisters) {
      throw new RuntimeException(
          "Malformed dex file: insSize (" + numParameterRegisters + ") > registersSize (" + numRegisters + ")");
    }

    for (DebugItem di : code.getDebugItems()) {
      if (di instanceof ImmutableLineNumber) {
        ImmutableLineNumber ln = (ImmutableLineNumber) di;
        DexlibAbstractInstruction ins = instructionAtAddress(ln.getCodeAddress());
        if (ins == null) {
          // Debug.printDbg("Line number tag pointing to invalid
          // offset: " + ln.getCodeAddress());
          continue;
        }
        ins.setLineNumber(ln.getLineNumber());
      }
    }

    this.dexFile = dexFile;
    this.method = method;
  }

  /**
   * Extracts the list of dalvik instructions from dexlib and converts them into our own instruction data model
   * 
   * @param code
   *          The dexlib method implementation
   */
  protected void extractDexInstructions(MethodImplementation code) {
    int address = 0;
    for (Instruction instruction : code.getInstructions()) {
      DexlibAbstractInstruction dexInstruction = fromInstruction(instruction, address);
      instructions.add(dexInstruction);
      instructionAtAddress.put(address, dexInstruction);
      address += instruction.getCodeUnits();
    }
  }

  /**
   * Return the types that are used in this body.
   */
  public Set<Type> usedTypes() {
    Set<Type> types = new HashSet<Type>();
    for (DexlibAbstractInstruction i : instructions) {
      types.addAll(i.introducedTypes());
    }

    if (tries != null) {
      for (TryBlock<? extends ExceptionHandler> tryItem : tries) {
        List<? extends ExceptionHandler> hList = tryItem.getExceptionHandlers();
        for (ExceptionHandler handler : hList) {
          String exType = handler.getExceptionType();
          if (exType == null) {
            // Exceptions
            continue;
          }
          types.add(DexType.toSoot(exType));
        }
      }
    }

    return types;
  }

  /**
   * Add unit to this body.
   *
   * @param u
   *          Unit to add.
   */
  public void add(Unit u) {
    getBody().getUnits().add(u);
  }

  /**
   * Add a deferred instruction to this body.
   *
   * @param i
   *          the deferred instruction.
   */
  public void addDeferredJimplification(DeferableInstruction i) {
    deferredInstructions.add(i);
  }

  /**
   * Add a retypeable instruction to this body.
   *
   * @param i
   *          the retypeable instruction.
   */
  public void addRetype(RetypeableInstruction i) {
    instructionsToRetype.add(i);
  }

  /**
   * Return the associated JimpleBody.
   *
   * @throws RuntimeException
   *           if no jimplification happened yet.
   */
  public Body getBody() {
    if (jBody == null) {
      throw new RuntimeException("No jimplification happened yet, no body available.");
    }
    return jBody;
  }

  /**
   * Return the Locals that are associated with the current register state.
   *
   */
  public Local[] getRegisterLocals() {
    return registerLocals;
  }

  /**
   * Return the Local that are associated with the number in the current register state.
   *
   * Handles if the register number actually points to a method parameter.
   *
   * @param num
   *          the register number
   * @throws InvalidDalvikBytecodeException
   */
  public Local getRegisterLocal(int num) throws InvalidDalvikBytecodeException {
    int totalRegisters = registerLocals.length;
    if (num > totalRegisters) {
      throw new InvalidDalvikBytecodeException(
          "Trying to access register " + num + " but only " + totalRegisters + " is/are available.");
    }
    return registerLocals[num];
  }

  public Local getStoreResultLocal() {
    return storeResultLocal;
  }

  /**
   * Return the instruction that is present at the byte code address.
   *
   * @param address
   *          the byte code address.
   * @throws RuntimeException
   *           if address is not part of this body.
   */
  public DexlibAbstractInstruction instructionAtAddress(int address) {
    DexlibAbstractInstruction i = null;
    while (i == null && address >= 0) {
      // catch addresses can be in the middlde of last instruction. Ex. in
      // com.letang.ldzja.en.apk:
      //
      // 042c46: 7020 2a15 0100 |008f: invoke-direct {v1, v0},
      // Ljavax/mi...
      // 042c4c: 2701 |0092: throw v1
      // catches : 4
      // <any> -> 0x0065
      // 0x0069 - 0x0093
      //
      // SA, 14.05.2014: We originally scanned only two code units back.
      // This is not sufficient
      // if we e.g., have a wide constant and the line number in the debug
      // sections points to
      // some address the middle.
      i = instructionAtAddress.get(address);
      address--;
    }
    return i;
  }

  /**
   * Return the jimple equivalent of this body.
   *
   * @param m
   *          the SootMethod that contains this body
   */
  public Body jimplify(Body b, SootMethod m) {

    final Jimple jimple = Jimple.v();
    final UnknownType unknownType = UnknownType.v();
    final NullConstant nullConstant = NullConstant.v();
    final Options options = Options.v();

    /*
     * Timer t_whole_jimplification = new Timer(); Timer t_num = new Timer(); Timer t_null = new Timer();
     *
     * t_whole_jimplification.start();
     */

    JBOptions jbOptions = new JBOptions(PhaseOptions.v().getPhaseOptions("jb"));
    jBody = (JimpleBody) b;
    deferredInstructions = new ArrayList<DeferableInstruction>();
    instructionsToRetype = new HashSet<RetypeableInstruction>();
    
    if (jbOptions.use_original_names()) {
      PhaseOptions.v().setPhaseOptionIfUnset("jb.lns", "only-stack-locals");
    }
    if (jbOptions.stabilize_local_names()) {
      PhaseOptions.v().setPhaseOption("jb.lns", "sort-locals:true");
    }

    if (IDalvikTyper.ENABLE_DVKTYPER) {
      DalvikTyper.v().clear();
    }

    // process method parameters and generate Jimple locals from Dalvik
    // registers
    List<Local> paramLocals = new LinkedList<Local>();
    if (!isStatic) {
      int thisRegister = numRegisters - numParameterRegisters - 1;

      Local thisLocal = jimple.newLocal("$u" + thisRegister, unknownType); // generateLocal(UnknownType.v());
      jBody.getLocals().add(thisLocal);

      registerLocals[thisRegister] = thisLocal;
      JIdentityStmt idStmt = (JIdentityStmt) jimple.newIdentityStmt(thisLocal, jimple.newThisRef(declaringClassType));
      add(idStmt);
      paramLocals.add(thisLocal);
      if (IDalvikTyper.ENABLE_DVKTYPER) {
        DalvikTyper.v().setType(idStmt.leftBox, jBody.getMethod().getDeclaringClass().getType(), false);
      }

    }
    {
      int i = 0; // index of parameter type
      int parameterRegister = numRegisters - numParameterRegisters; // index of parameter register
      for (Type t : parameterTypes) {
        // may only use UnknownType here because the local may be reused with a different 
        // type later (before splitting)
        Local gen = jimple.newLocal("$u" + parameterRegister, unknownType);
        jBody.getLocals().add(gen);

        registerLocals[parameterRegister] = gen;
        JIdentityStmt idStmt = (JIdentityStmt) jimple.newIdentityStmt(gen, jimple.newParameterRef(t, i++));
        add(idStmt);
        paramLocals.add(gen);
        if (IDalvikTyper.ENABLE_DVKTYPER) {
          DalvikTyper.v().setType(idStmt.leftBox, t, false);
        }

        // some parameters may be encoded on two registers.
        // in Jimple only the first Dalvik register name is used
        // as the corresponding Jimple Local name. However, we also add
        // the second register to the registerLocals array since it
        // could be used later in the Dalvik bytecode
        if (t instanceof LongType || t instanceof DoubleType) {
          parameterRegister++;
          // may only use UnknownType here because the local may be reused with a different 
          // type later (before splitting)
          Local g = jimple.newLocal("$u" + parameterRegister, unknownType);
          jBody.getLocals().add(g);
          registerLocals[parameterRegister] = g;
        }

        parameterRegister++;
      }
    }

    for (int i = 0; i < (numRegisters - numParameterRegisters - (isStatic ? 0 : 1)); i++) {
      registerLocals[i] = jimple.newLocal("$u" + i, unknownType);
      jBody.getLocals().add(registerLocals[i]);
    }

    // add local to store intermediate results
    storeResultLocal = jimple.newLocal("$u-1", unknownType);
    jBody.getLocals().add(storeResultLocal);

    // process bytecode instructions
    final boolean isOdex = dexFile instanceof DexBackedDexFile ? ((DexBackedDexFile) dexFile).isOdexFile() : false;

    ClassPath cp = null;
    if (isOdex) {
      String[] sootClasspath = options.soot_classpath().split(File.pathSeparator);
      List<String> classpathList = new ArrayList<String>();
      for (String str : sootClasspath) {
        classpathList.add(str);
      }
      try {
        ClassPathResolver resolver = new ClassPathResolver(classpathList, classpathList, classpathList, dexFile);
        cp = new ClassPath(resolver.getResolvedClassProviders().toArray(new ClassProvider[0]));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    int prevLineNumber = -1;
    for (DexlibAbstractInstruction instruction : instructions) {
      if (isOdex && instruction instanceof OdexInstruction) {
        ((OdexInstruction) instruction).deOdex(dexFile, method, cp);
      }
      if (dangling != null) {
        dangling.finalize(this, instruction);
        dangling = null;
      }
      instruction.jimplify(this);
      if (instruction.getLineNumber() > 0) {
        prevLineNumber = instruction.getLineNumber();
      } else {
        instruction.setLineNumber(prevLineNumber);
      }
    }
    if (dangling != null) {
      dangling.finalize(this, null);
    }
    for (DeferableInstruction instruction : deferredInstructions) {
      instruction.deferredJimplify(this);
    }

    if (tries != null) {
      addTraps();
    }

    if (options.keep_line_number()) {
      fixLineNumbers();
    }

    // At this point Jimple code is generated
    // Cleaning...

    instructions = null;
    // registerLocals = null;
    // storeResultLocal = null;
    instructionAtAddress.clear();
    // localGenerator = null;
    deferredInstructions = null;
    // instructionsToRetype = null;
    dangling = null;
    tries = null;

    /*
     * We eliminate dead code. Dead code has been shown to occur under the following circumstances.
     *
     * 0006ec: 0d00 |00a2: move-exception v0 ... 0006f2: 0d00 |00a5: move-exception v0 ... 0x0041 - 0x008a
     * Ljava/lang/Throwable; -> 0x00a5 <any> -> 0x00a2
     *
     * Here there are two traps both over the same region. But the same always fires, hence rendering the code at a2
     * unreachable. Dead code yields problems during local splitting because locals within dead code will not be split. Hence
     * we remove all dead code here.
     */

    // Fix traps that do not catch exceptions
    DexTrapStackFixer.v().transform(jBody);

    // Sort out jump chains
    DexJumpChainShortener.v().transform(jBody);

    // Make sure that we don't have any overlapping uses due to returns
    DexReturnInliner.v().transform(jBody);

    // Shortcut: Reduce array initializations
    DexArrayInitReducer.v().transform(jBody);

    // split first to find undefined uses
    getLocalSplitter().transform(jBody);

    // Remove dead code and the corresponding locals before assigning types
    getUnreachableCodeEliminator().transform(jBody);
    DeadAssignmentEliminator.v().transform(jBody);
    UnusedLocalEliminator.v().transform(jBody);

    for (RetypeableInstruction i : instructionsToRetype) {
      i.retype(jBody);
    }

    // {
    // // remove instructions from instructions list
    // List<DexlibAbstractInstruction> iToRemove = new
    // ArrayList<DexlibAbstractInstruction>();
    // for (DexlibAbstractInstruction i: instructions)
    // if (!jBody.getUnits().contains(i.getUnit()))
    // iToRemove.add(i);
    // for (DexlibAbstractInstruction i: iToRemove) {
    // Debug.printDbg("removing dexinstruction containing unit '",
    // i.getUnit() ,"'");
    // instructions.remove(i);
    // }
    // }

    if (IDalvikTyper.ENABLE_DVKTYPER) {

      DexReturnValuePropagator.v().transform(jBody);
      getCopyPopagator().transform(jBody);
      DexNullThrowTransformer.v().transform(jBody);
      DalvikTyper.v().typeUntypedConstrantInDiv(jBody);
      DeadAssignmentEliminator.v().transform(jBody);
      UnusedLocalEliminator.v().transform(jBody);

      DalvikTyper.v().assignType(jBody);
      // jBody.validate();
      jBody.validateUses();
      jBody.validateValueBoxes();
      // jBody.checkInit();
      // Validate.validateArrays(jBody);
      // jBody.checkTypes();
      // jBody.checkLocals();

    } else {
      // t_num.start();
      DexNumTransformer.v().transform(jBody);
      // t_num.end();

      DexReturnValuePropagator.v().transform(jBody);
      getCopyPopagator().transform(jBody);

      DexNullThrowTransformer.v().transform(jBody);

      // t_null.start();
      DexNullTransformer.v().transform(jBody);
      // t_null.end();

      DexIfTransformer.v().transform(jBody);

      DeadAssignmentEliminator.v().transform(jBody);
      UnusedLocalEliminator.v().transform(jBody);

      // DexRefsChecker.v().transform(jBody);
      DexNullArrayRefTransformer.v().transform(jBody);
    }

    if (IDalvikTyper.ENABLE_DVKTYPER) {
      for (Local l : jBody.getLocals()) {
        l.setType(unknownType);
      }
    }

    // Remove "instanceof" checks on the null constant
    DexNullInstanceofTransformer.v().transform(jBody);

    TypeAssigner.v().transform(jBody);

    final RefType objectType = RefType.v("java.lang.Object");
    if (IDalvikTyper.ENABLE_DVKTYPER) {
      for (Unit u : jBody.getUnits()) {
        if (u instanceof IfStmt) {
          ConditionExpr expr = (ConditionExpr) ((IfStmt) u).getCondition();
          if (((expr instanceof EqExpr) || (expr instanceof NeExpr))) {
            Value op1 = expr.getOp1();
            Value op2 = expr.getOp2();
            if (op1 instanceof Constant && op2 instanceof Local) {
              Local l = (Local) op2;
              Type ltype = l.getType();
              if (ltype instanceof PrimType) {
                continue;
              }
              if (!(op1 instanceof IntConstant)) {
                // null is
                // IntConstant(0)
                // in Dalvik
                continue;
              }
              IntConstant icst = (IntConstant) op1;
              int val = icst.value;
              if (val != 0) {
                continue;
              }
              expr.setOp1(nullConstant);
            } else if (op1 instanceof Local && op2 instanceof Constant) {
              Local l = (Local) op1;
              Type ltype = l.getType();
              if (ltype instanceof PrimType) {
                continue;
              }
              if (!(op2 instanceof IntConstant)) {
                // null is
                // IntConstant(0)
                // in Dalvik
                continue;
              }
              IntConstant icst = (IntConstant) op2;
              int val = icst.value;
              if (val != 0) {
                continue;
              }
              expr.setOp2(nullConstant);
            } else if (op1 instanceof Local && op2 instanceof Local) {
              // nothing to do
            } else if (op1 instanceof Constant && op2 instanceof Constant) {

              if (op1 instanceof NullConstant && op2 instanceof NumericConstant) {
                IntConstant nc = (IntConstant) op2;
                if (nc.value != 0) {
                  throw new RuntimeException("expected value 0 for int constant. Got " + expr);
                }
                expr.setOp2(NullConstant.v());
              } else if (op2 instanceof NullConstant && op1 instanceof NumericConstant) {
                IntConstant nc = (IntConstant) op1;
                if (nc.value != 0) {
                  throw new RuntimeException("expected value 0 for int constant. Got " + expr);
                }
                expr.setOp1(nullConstant);
              }
            } else {
              throw new RuntimeException("error: do not handle if: " + u);
            }

          }
        }
      }

      // For null_type locals: replace their use by NullConstant()
      List<ValueBox> uses = jBody.getUseBoxes();
      // List<ValueBox> defs = jBody.getDefBoxes();
      List<ValueBox> toNullConstantify = new ArrayList<ValueBox>();
      List<Local> toRemove = new ArrayList<Local>();
      for (Local l : jBody.getLocals()) {

        if (l.getType() instanceof NullType) {
          toRemove.add(l);
          for (ValueBox vb : uses) {
            Value v = vb.getValue();
            if (v == l) {
              toNullConstantify.add(vb);
            }
          }
        }
      }
      for (ValueBox vb : toNullConstantify) {
        System.out.println("replace valuebox '" + vb + " with null constant");
        vb.setValue(nullConstant);
      }
      for (Local l : toRemove) {
        System.out.println("removing null_type local " + l);
        l.setType(objectType);
      }

    }

    // We pack locals that are not used in overlapping regions. This may
    // again lead to unused locals which we have to remove.
    LocalPacker.v().transform(jBody);
    UnusedLocalEliminator.v().transform(jBody);
    PackManager.v().getTransform("jb.lns").apply(jBody);

    // Some apps reference static fields as instance fields. We fix this
    // on the fly.
    if (Options.v().wrong_staticness() == Options.wrong_staticness_fix
          || Options.v().wrong_staticness() == Options.wrong_staticness_fixstrict) {
      FieldStaticnessCorrector.v().transform(jBody);
      MethodStaticnessCorrector.v().transform(jBody);
    }

    // Inline PackManager.v().getPack("jb").apply(jBody);
    // Keep only transformations that have not been done
    // at this point.
    TrapTightener.v().transform(jBody);
    TrapMinimizer.v().transform(jBody);
    // LocalSplitter.v().transform(jBody);
    Aggregator.v().transform(jBody);
    // UnusedLocalEliminator.v().transform(jBody);
    // TypeAssigner.v().transform(jBody);
    // LocalPacker.v().transform(jBody);
    // LocalNameStandardizer.v().transform(jBody);

    // Remove if (null == null) goto x else <madness>. We can only do this
    // after we have run the constant propagation as we might not be able
    // to statically decide the conditions earlier.
    ConditionalBranchFolder.v().transform(jBody);

    // Remove unnecessary typecasts
    ConstantCastEliminator.v().transform(jBody);
    IdentityCastEliminator.v().transform(jBody);

    // Remove unnecessary logic operations
    IdentityOperationEliminator.v().transform(jBody);

    // We need to run this transformer since the conditional branch folder
    // might have rendered some code unreachable (well, it was unreachable
    // before as well, but we didn't know).
    UnreachableCodeEliminator.v().transform(jBody);

    // Not sure whether we need this even though we do it earlier on as
    // the earlier pass does not have type information
    // CopyPropagator.v().transform(jBody);

    // we might have gotten new dead assignments and unused locals through
    // copy propagation and unreachable code elimination, so we have to do
    // this again
    DeadAssignmentEliminator.v().transform(jBody);
    UnusedLocalEliminator.v().transform(jBody);
    NopEliminator.v().transform(jBody);

    // Remove unnecessary chains of return statements
    DexReturnPacker.v().transform(jBody);

    for (Unit u : jBody.getUnits()) {
      if (u instanceof AssignStmt) {
        AssignStmt ass = (AssignStmt) u;
        if (ass.getRightOp() instanceof CastExpr) {
          CastExpr c = (CastExpr) ass.getRightOp();
          if (c.getType() instanceof NullType) {
            ass.setRightOp(nullConstant);
          }
        }
      }
      if (u instanceof DefinitionStmt) {
        DefinitionStmt def = (DefinitionStmt) u;
        // If the body references a phantom class in a
        // CaughtExceptionRef,
        // we must manually fix the hierarchy
        if (def.getLeftOp() instanceof Local && def.getRightOp() instanceof CaughtExceptionRef) {
          Type t = def.getLeftOp().getType();
          if (t instanceof RefType) {
            RefType rt = (RefType) t;
            if (rt.getSootClass().isPhantom() && !rt.getSootClass().hasSuperclass()
                && !rt.getSootClass().getName().equals("java.lang.Throwable")) {
              rt.getSootClass().setSuperclass(Scene.v().getSootClass("java.lang.Throwable"));
            }
          }
        }
      }
    }

    // Replace local type null_type by java.lang.Object.
    //
    // The typing engine cannot find correct type for such code:
    //
    // null_type $n0;
    // $n0 = null;
    // $r4 = virtualinvoke $n0.<java.lang.ref.WeakReference:
    // java.lang.Object get()>();
    //
    for (Local l : jBody.getLocals()) {
      Type t = l.getType();
      if (t instanceof NullType) {
        l.setType(objectType);
      }
    }
    
    //Must be last to ensure local ordering does not change
    PackManager.v().getTransform("jb.lns").apply(jBody);

    // t_whole_jimplification.end();

    return jBody;
  }

  /**
   * Fixes the line numbers. If there is a unit without a line number, it gets the line number of the last (transitive)
   * predecessor that has a line number.
   */
  protected void fixLineNumbers() {
    int prevLn = -1;
    for (DexlibAbstractInstruction instruction : instructions) {
      Unit unit = instruction.getUnit();
      int lineNumber = unit.getJavaSourceStartLineNumber();
      if (lineNumber < 0) {
        if (prevLn >= 0) {
          unit.addTag(new LineNumberTag(prevLn));
          unit.addTag(new SourceLineNumberTag(prevLn));
        }
      } else {
        prevLn = lineNumber;
      }
    }
  }

  private LocalSplitter localSplitter = null;

  protected LocalSplitter getLocalSplitter() {
    if (this.localSplitter == null) {
      this.localSplitter = new LocalSplitter(DalvikThrowAnalysis.v());
    }
    return this.localSplitter;
  }

  private UnreachableCodeEliminator unreachableCodeEliminator = null;

  protected UnreachableCodeEliminator getUnreachableCodeEliminator() {
    if (this.unreachableCodeEliminator == null) {
      this.unreachableCodeEliminator = new UnreachableCodeEliminator(DalvikThrowAnalysis.v());
    }
    return this.unreachableCodeEliminator;
  }

  private CopyPropagator copyPropagator = null;

  protected CopyPropagator getCopyPopagator() {
    if (this.copyPropagator == null) {
      this.copyPropagator = new CopyPropagator(DalvikThrowAnalysis.v(), false);
    }
    return this.copyPropagator;
  }

  /**
   * Set a dangling instruction for this body.
   *
   */
  public void setDanglingInstruction(DanglingInstruction i) {
    dangling = i;
  }

  /**
   * Return the instructions that appear (lexically) after the given instruction.
   *
   * @param instruction
   *          the instruction which successors will be returned.
   */
  public List<DexlibAbstractInstruction> instructionsAfter(DexlibAbstractInstruction instruction) {
    int i = instructions.indexOf(instruction);
    if (i == -1) {
      throw new IllegalArgumentException("Instruction" + instruction + "not part of this body.");
    }

    return instructions.subList(i + 1, instructions.size());
  }

  /**
   * Return the instructions that appear (lexically) before the given instruction.
   *
   * The instruction immediately before the given is the first instruction and so on.
   *
   * @param instruction
   *          the instruction which successors will be returned.
   */
  public List<DexlibAbstractInstruction> instructionsBefore(DexlibAbstractInstruction instruction) {
    int i = instructions.indexOf(instruction);
    if (i == -1) {
      throw new IllegalArgumentException("Instruction " + instruction + " not part of this body.");
    }

    List<DexlibAbstractInstruction> l = new ArrayList<DexlibAbstractInstruction>();
    l.addAll(instructions.subList(0, i));
    Collections.reverse(l);
    return l;
  }

  /**
   * Add the traps of this body.
   *
   * Should only be called at the end jimplify.
   */
  private void addTraps() {
    final Jimple jimple = Jimple.v();
    for (TryBlock<? extends ExceptionHandler> tryItem : tries) {
      int startAddress = tryItem.getStartCodeAddress();
      int length = tryItem.getCodeUnitCount();// .getTryLength();
      int endAddress = startAddress + length;// - 1;
      Unit beginStmt = instructionAtAddress(startAddress).getUnit();
      // (startAddress + length) typically points to the first byte of the
      // first instruction after the try block
      // except if there is no instruction after the try block in which
      // case it points to the last byte of the last
      // instruction of the try block. Removing 1 from (startAddress +
      // length) always points to "somewhere" in
      // the last instruction of the try block since the smallest
      // instruction is on two bytes (nop = 0x0000).
      Unit endStmt = instructionAtAddress(endAddress).getUnit();
      // if the try block ends on the last instruction of the body, add a
      // nop instruction so Soot can include
      // the last instruction in the try block.
      if (jBody.getUnits().getLast() == endStmt && instructionAtAddress(endAddress - 1).getUnit() == endStmt) {
        Unit nop = jimple.newNopStmt();
        jBody.getUnits().insertAfter(nop, endStmt);
        endStmt = nop;
      }

      List<? extends ExceptionHandler> hList = tryItem.getExceptionHandlers();
      for (ExceptionHandler handler : hList) {
        String exceptionType = handler.getExceptionType();
        if (exceptionType == null) {
          exceptionType = "Ljava/lang/Throwable;";
        }
        Type t = DexType.toSoot(exceptionType);
        // exceptions can only be of RefType
        if (t instanceof RefType) {
          SootClass exception = ((RefType) t).getSootClass();
          DexlibAbstractInstruction instruction = instructionAtAddress(handler.getHandlerCodeAddress());
          if (!(instruction instanceof MoveExceptionInstruction)) {
            logger.debug("" + String.format("First instruction of trap handler unit not MoveException but %s",
                instruction.getClass().getName()));
          } else {
            ((MoveExceptionInstruction) instruction).setRealType(this, exception.getType());
          }

          Trap trap = jimple.newTrap(exception, beginStmt, endStmt, instruction.getUnit());
          jBody.getTraps().add(trap);
        }
      }
    }
  }

}
