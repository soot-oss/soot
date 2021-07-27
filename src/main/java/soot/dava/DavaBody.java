package soot.dava;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
 * Copyright (C) 2004 - 2006 Nomair A. Naeem
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.G;
import soot.IntType;
import soot.Local;
import soot.PatchingChain;
import soot.PhaseOptions;
import soot.RefType;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.SET.SETNode;
import soot.dava.internal.SET.SETTopNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.asg.AugmentedStmtGraph;
import soot.dava.internal.javaRep.DCmpExpr;
import soot.dava.internal.javaRep.DCmpgExpr;
import soot.dava.internal.javaRep.DCmplExpr;
import soot.dava.internal.javaRep.DInstanceFieldRef;
import soot.dava.internal.javaRep.DIntConstant;
import soot.dava.internal.javaRep.DInterfaceInvokeExpr;
import soot.dava.internal.javaRep.DLengthExpr;
import soot.dava.internal.javaRep.DNegExpr;
import soot.dava.internal.javaRep.DNewArrayExpr;
import soot.dava.internal.javaRep.DNewInvokeExpr;
import soot.dava.internal.javaRep.DNewMultiArrayExpr;
import soot.dava.internal.javaRep.DSpecialInvokeExpr;
import soot.dava.internal.javaRep.DStaticFieldRef;
import soot.dava.internal.javaRep.DStaticInvokeExpr;
import soot.dava.internal.javaRep.DThisRef;
import soot.dava.internal.javaRep.DVirtualInvokeExpr;
import soot.dava.toolkits.base.AST.UselessTryRemover;
import soot.dava.toolkits.base.AST.transformations.ASTCleaner;
import soot.dava.toolkits.base.AST.transformations.ASTCleanerTwo;
import soot.dava.toolkits.base.AST.transformations.AndAggregator;
import soot.dava.toolkits.base.AST.transformations.BooleanConditionSimplification;
import soot.dava.toolkits.base.AST.transformations.DeInliningFinalFields;
import soot.dava.toolkits.base.AST.transformations.DecrementIncrementStmtCreation;
import soot.dava.toolkits.base.AST.transformations.FinalFieldDefinition;
import soot.dava.toolkits.base.AST.transformations.ForLoopCreator;
import soot.dava.toolkits.base.AST.transformations.IfElseSplitter;
import soot.dava.toolkits.base.AST.transformations.LocalVariableCleaner;
import soot.dava.toolkits.base.AST.transformations.LoopStrengthener;
import soot.dava.toolkits.base.AST.transformations.NewStringBufferSimplification;
import soot.dava.toolkits.base.AST.transformations.OrAggregatorFour;
import soot.dava.toolkits.base.AST.transformations.OrAggregatorOne;
import soot.dava.toolkits.base.AST.transformations.OrAggregatorTwo;
import soot.dava.toolkits.base.AST.transformations.PushLabeledBlockIn;
import soot.dava.toolkits.base.AST.transformations.ShortcutArrayInit;
import soot.dava.toolkits.base.AST.transformations.ShortcutIfGenerator;
import soot.dava.toolkits.base.AST.transformations.SuperFirstStmtHandler;
import soot.dava.toolkits.base.AST.transformations.TypeCastingError;
import soot.dava.toolkits.base.AST.transformations.UselessAbruptStmtRemover;
import soot.dava.toolkits.base.AST.transformations.UselessLabeledBlockRemover;
import soot.dava.toolkits.base.AST.traversals.ClosestAbruptTargetFinder;
import soot.dava.toolkits.base.AST.traversals.CopyPropagation;
import soot.dava.toolkits.base.finders.AbruptEdgeFinder;
import soot.dava.toolkits.base.finders.CycleFinder;
import soot.dava.toolkits.base.finders.ExceptionFinder;
import soot.dava.toolkits.base.finders.ExceptionNode;
import soot.dava.toolkits.base.finders.IfFinder;
import soot.dava.toolkits.base.finders.LabeledBlockFinder;
import soot.dava.toolkits.base.finders.SequenceFinder;
import soot.dava.toolkits.base.finders.SwitchFinder;
import soot.dava.toolkits.base.finders.SynchronizedBlockFinder;
import soot.dava.toolkits.base.misc.MonitorConverter;
import soot.dava.toolkits.base.misc.ThrowNullConverter;
import soot.grimp.GrimpBody;
import soot.grimp.NewInvokeExpr;
import soot.jimple.ArrayRef;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.CmpExpr;
import soot.jimple.CmpgExpr;
import soot.jimple.CmplExpr;
import soot.jimple.ConditionExpr;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.Expr;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.LengthExpr;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.MonitorStmt;
import soot.jimple.NegExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.ParameterRef;
import soot.jimple.Ref;
import soot.jimple.ReturnStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThisRef;
import soot.jimple.ThrowStmt;
import soot.jimple.UnopExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.TrapUnitGraph;
import soot.util.IterableSet;
import soot.util.Switchable;

/*
 * CHANGE LOG: Nomair - January 2006: Moved the AST Analyses to a separate method
 *             These are now invoked as a very last staged (just before generating decompiled
 *             output. Invoked by PackManager
 *
 *             Nomair - 7th Feb, 2006: Starting work on a naming mechanism
 *             Nomair - 13th Feb 2006: Added db phase options
 *
 *				renamer: on /off  DEFAULT:TRUE
 *				deobfuscate: DEFAULT: FALSE, dead code eliminateion,
 *				class/field renaming, constant field elimination
 *              force-recompilability: DEFAULT TRUE, super, final
 *
 *            Nomair: March 28th, 2006: Removed the applyRenamerAnalyses method from DavaBody to InterProceduralAnalyses
 *                    Although currently renaming is done intra-proceduraly  there is strong indication that
 *                    inter procedural analyses will be required to get good names
 *
 *			Nomair: March 29th, 2006: dealing with trying to remove fully qualified names
 *
 */
/*
 * TODO:   Nomair- February 7th. Refactor the call
 *  	    AST.perform_Analysis( UselessTryRemover.v());
 *         use the new AnalysisAdapter routines to write this analysis. Then delete these
 *         obselete and rather clumsy way of writing analyses
 *
 * TODO: Nomair 14th Feb 2006, Use the Dava options renamer, deobfuscate, force-recompilability
 *          Specially the deobfuscate option with the boolean constant propagation analysis
 *
 */
public class DavaBody extends Body {

  public boolean DEBUG = false;

  private Map<Integer, Value> pMap;
  private Set<Object> consumedConditions;
  private HashSet<Object> thisLocals;

  private IterableSet<ExceptionNode> synchronizedBlockFacts;
  private IterableSet<ExceptionNode> exceptionFacts;
  private IterableSet<AugmentedStmt> monitorFacts;

  private IterableSet<String> importList;

  private Local controlLocal;

  private InstanceInvokeExpr constructorExpr; // holds constructorUnit.getInvokeExpr

  private Unit constructorUnit; // holds a stmt (this.init<>)

  private List<CaughtExceptionRef> caughtrefs;

  /**
   * Construct an empty DavaBody
   */
  DavaBody(SootMethod m) {
    super(m);

    this.pMap = new HashMap<Integer, Value>();
    this.consumedConditions = new HashSet<Object>();
    this.thisLocals = new HashSet<Object>();
    this.synchronizedBlockFacts = new IterableSet<ExceptionNode>();
    this.exceptionFacts = new IterableSet<ExceptionNode>();
    this.monitorFacts = new IterableSet<AugmentedStmt>();
    this.importList = new IterableSet<String>();
    this.caughtrefs = new LinkedList<CaughtExceptionRef>();
    this.controlLocal = null;
    this.constructorExpr = null;
  }

  public Unit get_ConstructorUnit() {
    return constructorUnit;
  }

  public List<CaughtExceptionRef> get_CaughtRefs() {
    return caughtrefs;
  }

  public InstanceInvokeExpr get_ConstructorExpr() {
    return constructorExpr;
  }

  public void set_ConstructorExpr(InstanceInvokeExpr expr) {
    constructorExpr = expr;
  }

  public void set_ConstructorUnit(Unit s) {
    constructorUnit = s;
  }

  public Map<Integer, Value> get_ParamMap() {
    return pMap;
  }

  public void set_ParamMap(Map<Integer, Value> map) {
    pMap = map;
  }

  public HashSet<Object> get_ThisLocals() {
    return thisLocals;
  }

  public Local get_ControlLocal() {
    if (controlLocal == null) {
      controlLocal = new JimpleLocal("controlLocal", IntType.v());
      getLocals().add(controlLocal);
    }

    return controlLocal;
  }

  public Set<Object> get_ConsumedConditions() {
    return consumedConditions;
  }

  public void consume_Condition(AugmentedStmt as) {
    consumedConditions.add(as);
  }

  public Object clone() {
    Body b = Dava.v().newBody(getMethodUnsafe());
    b.importBodyContentsFrom(this);
    return b;
  }

  public IterableSet<ExceptionNode> get_SynchronizedBlockFacts() {
    return synchronizedBlockFacts;
  }

  public IterableSet<ExceptionNode> get_ExceptionFacts() {
    return exceptionFacts;
  }

  public IterableSet<AugmentedStmt> get_MonitorFacts() {
    return monitorFacts;
  }

  public IterableSet<String> getImportList() {
    return importList;
  }

  /**
   * Constructs a DavaBody from the given Body.
   */
  DavaBody(Body body) {
    this(body.getMethod());
    debug("DavaBody", "creating DavaBody for" + body.getMethod().toString());
    Dava.v().log("\nstart method " + body.getMethod().toString());

    if (DEBUG) {
      if (!body.getMethod().getExceptions().isEmpty()) {
        debug("DavaBody", "printing NON EMPTY exception list for " + body.getMethod().toString() + " "
            + body.getMethod().getExceptions().toString());
      }
    }
    // copy and "convert" the grimp representation
    // DEBUG=true;
    copy_Body(body);
    // DEBUG=false;

    // prime the analysis
    AugmentedStmtGraph asg = new AugmentedStmtGraph(new BriefUnitGraph(this), new TrapUnitGraph(this));
    // System.out.println(asg.toString());

    ExceptionFinder.v().preprocess(this, asg);
    SETNode SET = new SETTopNode(asg.get_ChainView());

    while (true) {
      try {
        CycleFinder.v().find(this, asg, SET);
        IfFinder.v().find(this, asg, SET);
        SwitchFinder.v().find(this, asg, SET);
        SynchronizedBlockFinder.v().find(this, asg, SET);
        ExceptionFinder.v().find(this, asg, SET);
        SequenceFinder.v().find(this, asg, SET);
        LabeledBlockFinder.v().find(this, asg, SET);
        AbruptEdgeFinder.v().find(this, asg, SET);
      } catch (RetriggerAnalysisException rae) {
        SET = new SETTopNode(asg.get_ChainView());
        consumedConditions = new HashSet<Object>();
        continue;
      }
      break;
    }

    MonitorConverter.v().convert(this);
    ThrowNullConverter.v().convert(this);

    ASTNode AST = SET.emit_AST();

    // get rid of the grimp representation, put in the new AST
    getTraps().clear();
    getUnits().clear();
    getUnits().addLast(AST);

    // perform transformations on the AST
    /*
     * Nomair This should be refactored to use the new AnalysisAdapter classes
     */
    do {
      G.v().ASTAnalysis_modified = false;

      AST.perform_Analysis(UselessTryRemover.v());

    } while (G.v().ASTAnalysis_modified);

    /*
     * Nomair A Naeem 10-MARCH-2005
     *
     * IT IS ESSENTIAL TO CALL THIS METHOD This method initializes the locals of the current method being processed Failure
     * to invoke this method here will result in no locals being printed out
     */
    if (AST instanceof ASTMethodNode) {
      ((ASTMethodNode) AST).storeLocals(this);

      /*
       * January 12th, 2006 Deal with the super() problem before continuing
       */
      Map<String, String> options = PhaseOptions.v().getPhaseOptions("db.force-recompile");
      boolean force = PhaseOptions.getBoolean(options, "enabled");
      // System.out.println("force is "+force);
      if (force) {
        AST.apply(new SuperFirstStmtHandler((ASTMethodNode) AST));
      }

      debug("DavaBody", "PreInit booleans is" + G.v().SootMethodAddedByDava);

    }
    Dava.v().log("end method " + body.getMethod().toString());
  }

  public void applyBugFixes() {
    ASTNode AST = (ASTNode) this.getUnits().getFirst();
    debug("applyBugFixes", "Applying AST analyzes for method" + this.getMethod().toString());

    AST.apply(new ShortcutIfGenerator());
    debug("applyBugFixes", "after ShortcutIfGenerator" + G.v().ASTTransformations_modified);

    AST.apply(new TypeCastingError());
    debug("applyBugFixes", "after TypeCastingError" + G.v().ASTTransformations_modified);
  }

  /*
   * Method is invoked by the packmanager just before it is actually about to generate decompiled code. Works as a separate
   * stage from the DavaBody() constructor. All AST transformations should be implemented from within this method.
   *
   * Method is also invoked from the InterProceduralAnlaysis method once those have been invoked
   */
  public void analyzeAST() {
    ASTNode AST = (ASTNode) this.getUnits().getFirst();
    debug("analyzeAST", "Applying AST analyzes for method" + this.getMethod().toString());

    /*
     * Nomair A. Naeem tranformations on the AST Any AST Transformations added should be added to the applyASTAnalyses method
     * unless we are want to delay the analysis till for example THE LAST THING DONE
     */
    applyASTAnalyses(AST);

    /*
     * Nomair A. Naeem apply structural flow analyses now
     *
     */
    debug("analyzeAST", "Applying structure analysis" + this.getMethod().toString());
    applyStructuralAnalyses(AST);
    debug("analyzeAST", "Applying structure analysis DONE" + this.getMethod().toString());
    /*
     * Renamer March 28th Nomair A. Naeem. Since there is a chance that the analyze method gets involved multiple times we
     * dont want renaming done more than once.
     *
     * hence removing the call of the renamer from here Also looking ahead i have a feeling that we will be going
     * interprocedural for the renamer hence i am placing the renamer code inside the interprocedural class
     */

    /*
     * In the end check 1, if there are labels which can be safely removed 2, int temp; temp=0 to be converted to int temp=0;
     */
    // AST.apply(new ExtraLabelNamesRemover());

    // System.out.println("\nEND analyzing method"+this.getMethod().toString());
  }

  private void applyASTAnalyses(ASTNode AST) {
    debug("applyASTAnalyses", "initial one time analyses started");
    /*
     * Nomair A. Naeem Transformations on the AST
     */
    // The BooleanConditionSimplification changes flag==false to just flag

    // AST.apply(new DepthFirstAdapter(true));

    AST.apply(new BooleanConditionSimplification());

    AST.apply(new DecrementIncrementStmtCreation());

    debug("applyASTAnalyses", "initial one time analyses completed");

    boolean flag = true;

    G.v().ASTTransformations_modified = false;
    G.v().ASTIfElseFlipped = false;

    int countFlipping = 0;

    if (flag) {
      // perform transformations on the AST
      do {
        debug("applyASTAnalyses", "ITERATION");
        G.v().ASTTransformations_modified = false;

        AST.apply(new AndAggregator());
        debug("applyASTAnalyses", "after AndAggregator" + G.v().ASTTransformations_modified);
        /*
         * The OrAggregatorOne internally calls UselessLabelFinder which sets the label to null Always apply a
         * UselessLabeledBlockRemover in the end to remove such labeled blocks
         */

        AST.apply(new OrAggregatorOne());
        debug("applyASTAnalyses", "after OraggregatorOne" + G.v().ASTTransformations_modified);

        /*
         * Note OrAggregatorTwo should always be followed by an emptyElseRemover since orAggregatorTwo can create empty else
         * bodies and the ASTIfElseNode can be replaced by ASTIfNodes OrAggregator has two patterns see the class for them
         */

        AST.apply(new OrAggregatorTwo());
        debug("applyASTAnalyses", "after OraggregatorTwo" + G.v().ASTTransformations_modified);
        debug("applyASTAnalyses", "after OraggregatorTwo ifElseFlipped is" + G.v().ASTIfElseFlipped);

        AST.apply(new OrAggregatorFour());
        debug("applyASTAnalyses", "after OraggregatorFour" + G.v().ASTTransformations_modified);

        /*
         * ASTCleaner currently does the following tasks: 1, Remove empty Labeled Blocks UselessLabeledBlockRemover 2,
         * convert ASTIfElseNodes with empty else bodies to ASTIfNodes 3, Apply OrAggregatorThree
         */
        AST.apply(new ASTCleaner());
        debug("applyASTAnalyses", "after ASTCleaner" + G.v().ASTTransformations_modified);

        /*
         * PushLabeledBlockIn should not be called unless we are sure that all labeledblocks have non null labels. A good way
         * of ensuring this is to run the ASTCleaner directly before calling this
         */
        AST.apply(new PushLabeledBlockIn());
        debug("applyASTAnalyses", "after PushLabeledBlockIn" + G.v().ASTTransformations_modified);

        AST.apply(new LoopStrengthener());
        debug("applyASTAnalyses", "after LoopStrengthener" + G.v().ASTTransformations_modified);

        /*
         * Pattern two carried out in OrAggregatorTwo restricts some patterns in for loop creation. Pattern two was
         * implemented to give loopStrengthening a better chance SEE IfElseBreaker
         */
        AST.apply(new ASTCleanerTwo());
        debug("applyASTAnalyses", "after ASTCleanerTwo" + G.v().ASTTransformations_modified);

        AST.apply(new ForLoopCreator());
        debug("applyASTAnalyses", "after ForLoopCreator" + G.v().ASTTransformations_modified);

        AST.apply(new NewStringBufferSimplification());
        debug("applyASTAnalyses", "after NewStringBufferSimplification" + G.v().ASTTransformations_modified);

        AST.apply(new ShortcutArrayInit());
        debug("applyASTAnalyses", "after ShortcutArrayInit" + G.v().ASTTransformations_modified);

        AST.apply(new UselessLabeledBlockRemover());
        debug("applyASTAnalyses", "after UselessLabeledBlockRemover" + G.v().ASTTransformations_modified);

        if (!G.v().ASTTransformations_modified) {
          AST.apply(new IfElseSplitter());
          debug("applyASTAnalyses", "after IfElseSplitter" + G.v().ASTTransformations_modified);
        }

        if (!G.v().ASTTransformations_modified) {
          AST.apply(new UselessAbruptStmtRemover());
          debug("applyASTAnalyses", "after UselessAbruptStmtRemover" + G.v().ASTTransformations_modified);
        }

        AST.apply(new ShortcutIfGenerator());
        debug("applyASTAnalyses", "after ShortcutIfGenerator" + G.v().ASTTransformations_modified);

        AST.apply(new TypeCastingError());
        debug("applyASTAnalyses", "after TypeCastingError" + G.v().ASTTransformations_modified);

        /*
         * if we matched some useful pattern we reserve the right to flip conditions again
         */
        if (G.v().ASTTransformations_modified) {
          G.v().ASTIfElseFlipped = false;
          countFlipping = 0;
          debug("applyASTanalyses", "Transformation modified was true hence will reiterate. set flipped to false");
        } else {
          // check if only the ifelse was flipped
          if (G.v().ASTIfElseFlipped) {
            debug("", "ifelseflipped and transformations NOT modified");
            // we couldnt transform but we did flip
            if (countFlipping == 0) {
              debug("", "ifelseflipped and transformations NOT modified count is 0");
              // let this go on just once more in the hope of some other pattern being matched
              G.v().ASTIfElseFlipped = false;
              countFlipping++;
              G.v().ASTTransformations_modified = true;
            } else {
              debug("", "ifelseflipped and transformations NOT modified count is not 0 TERMINATE");
            }
          }
        } // if ASTTransformations was not modified

      } while (G.v().ASTTransformations_modified);
      // System.out.println("The AST trasnformations has run"+times);
    }

    /*
     * ClosestAbruptTargetFinder should be reinitialized everytime there is a change to the AST This is utilized internally
     * by the DavaFlowSet implementation to handle Abrupt Implicit Stmts
     */
    AST.apply(ClosestAbruptTargetFinder.v());
    debug("applyASTAnalyses", "after ClosestAbruptTargetFinder" + G.v().ASTTransformations_modified);

    // 29th Jan 2006
    // make sure when recompiling there is no variable might not be initialized error

    Map<String, String> options = PhaseOptions.v().getPhaseOptions("db.force-recompile");
    boolean force = PhaseOptions.getBoolean(options, "enabled");
    // System.out.println("Force is"+force);

    if (force) {
      debug("applyASTAnalyses", "before FinalFieldDefinition" + G.v().ASTTransformations_modified);
      new FinalFieldDefinition((ASTMethodNode) AST);
      debug("applyASTAnalyses", "after FinalFieldDefinition" + G.v().ASTTransformations_modified);
    }

    // this analysis has to be after ShortcutArrayInit to give that analysis more chances
    AST.apply(new DeInliningFinalFields());

    debug("applyASTAnalyses", "end applyASTAnlayses" + G.v().ASTTransformations_modified);
  }

  private void applyStructuralAnalyses(ASTNode AST) {
    // TESTING REACHING DEFS
    // ReachingDefs defs = new ReachingDefs(AST);
    // AST.apply(new tester(true,defs));

    // TESTING REACHING COPIES
    // ReachingCopies copies = new ReachingCopies(AST);
    // AST.apply(new tester(true,copies));

    // TESTING ASTUSESANDDEFS
    // AST.apply(new ASTUsesAndDefs(AST));

    /*
     * Structural flow analyses.....
     */

    // CopyPropagation.DEBUG=true;
    debug("applyStructureAnalyses", "invoking copy propagation");
    CopyPropagation prop = new CopyPropagation(AST);
    AST.apply(prop);
    debug("applyStructureAnalyses", "invoking copy propagation DONE");
    // copy propagation should be followed by LocalVariableCleaner to get max effect
    // ASTUsesAndDefs.DEBUG=true;
    debug("applyStructureAnalyses", "Local Variable Cleaner started");
    AST.apply(new LocalVariableCleaner(AST));
    debug("applyStructureAnalyses", "Local Variable Cleaner DONE");

  }

  /*
   * Copy and patch a GrimpBody so that it can be used to output Java.
   */

  private void copy_Body(Body body) {
    if (!(body instanceof GrimpBody)) {
      throw new RuntimeException("You can only create a DavaBody from a GrimpBody!");
    }

    GrimpBody grimpBody = (GrimpBody) body;

    /*
     * Import body contents from Grimp.
     */

    {
      HashMap<Switchable, Switchable> bindings = new HashMap<Switchable, Switchable>();
      HashMap<Unit, Unit> reverse_binding = new HashMap<Unit, Unit>();

      // Clone units in body's statement list
      for (Unit original : grimpBody.getUnits()) {
        Unit copy = (Unit) original.clone();

        // Add cloned unit to our unitChain.
        getUnits().addLast(copy);

        // Build old <-> new map to be able to patch up references to other units
        // within the cloned units. (these are still refering to the original
        // unit objects).
        bindings.put(original, copy);
        reverse_binding.put(copy, original);
      }

      // patch up the switch statments
      for (Unit u : getUnits()) {
        Stmt s = (Stmt) u;

        if (s instanceof TableSwitchStmt) {
          TableSwitchStmt ts = (TableSwitchStmt) s;

          TableSwitchStmt original_switch = (TableSwitchStmt) reverse_binding.get(u);
          ts.setDefaultTarget((Unit) bindings.get(original_switch.getDefaultTarget()));

          LinkedList<Unit> new_target_list = new LinkedList<Unit>();
          int target_count = ts.getHighIndex() - ts.getLowIndex() + 1;
          for (int i = 0; i < target_count; i++) {
            new_target_list.add((Unit) bindings.get(original_switch.getTarget(i)));
          }
          ts.setTargets(new_target_list);

        } else if (s instanceof LookupSwitchStmt) {
          LookupSwitchStmt ls = (LookupSwitchStmt) s;

          LookupSwitchStmt original_switch = (LookupSwitchStmt) reverse_binding.get(u);
          ls.setDefaultTarget((Unit) bindings.get(original_switch.getDefaultTarget()));

          Unit[] new_target_list = new Unit[original_switch.getTargetCount()];
          for (int i = 0; i < original_switch.getTargetCount(); i++) {
            new_target_list[i] = (Unit) bindings.get(original_switch.getTarget(i));
          }
          ls.setTargets(new_target_list);

          ls.setLookupValues(original_switch.getLookupValues());
        }
      }

      // Clone locals.
      for (Local original : grimpBody.getLocals()) {
        Local copy = Dava.v().newLocal(original.getName(), original.getType());

        getLocals().add(copy);

        // Build old <-> new mapping.
        bindings.put(original, copy);
      }

      // Patch up references within units using our (old <-> new) map.
      for (UnitBox box : getAllUnitBoxes()) {
        Unit newObject = (Unit) bindings.get(box.getUnit());
        // if we have a reference to an old object, replace it with its clone.
        if (newObject != null) {
          box.setUnit(newObject);
        }
      }

      // backpatch all local variables.
      for (ValueBox vb : getUseAndDefBoxes()) {
        Value val = vb.getValue();
        if (val instanceof Local) {
          vb.setValue((Value) bindings.get(val));
        }
      }

      // clone the traps
      for (Trap originalTrap : grimpBody.getTraps()) {
        Trap cloneTrap = (Trap) originalTrap.clone();

        cloneTrap.setHandlerUnit((Unit) bindings.get(originalTrap.getHandlerUnit()));
        cloneTrap.setBeginUnit((Unit) bindings.get(originalTrap.getBeginUnit()));
        cloneTrap.setEndUnit((Unit) bindings.get(originalTrap.getEndUnit()));

        getTraps().add(cloneTrap);
      }
    }

    /*
     * Add one level of indirection to "if", "switch", and exceptional control flow. This allows for easy handling of breaks,
     * continues and exceptional loops.
     */
    {
      PatchingChain<Unit> units = getUnits();
      for (Iterator<Unit> it = units.snapshotIterator(); it.hasNext();) {
        Unit u = it.next();
        if (u instanceof IfStmt) {
          IfStmt ifs = (IfStmt) u;

          JGotoStmt jgs = new JGotoStmt(units.getSuccOf(u));
          units.insertAfter(jgs, u);

          JGotoStmt jumper = new JGotoStmt(ifs.getTarget());
          units.insertAfter(jumper, jgs);
          ifs.setTarget(jumper);
        } else if (u instanceof TableSwitchStmt) {
          TableSwitchStmt tss = (TableSwitchStmt) u;

          int targetCount = tss.getHighIndex() - tss.getLowIndex() + 1;
          for (int i = 0; i < targetCount; i++) {
            JGotoStmt jgs = new JGotoStmt(tss.getTarget(i));
            units.insertAfter(jgs, tss);
            tss.setTarget(i, jgs);
          }

          JGotoStmt jgs = new JGotoStmt(tss.getDefaultTarget());
          units.insertAfter(jgs, tss);
          tss.setDefaultTarget(jgs);
        } else if (u instanceof LookupSwitchStmt) {
          LookupSwitchStmt lss = (LookupSwitchStmt) u;

          for (int i = 0; i < lss.getTargetCount(); i++) {
            JGotoStmt jgs = new JGotoStmt(lss.getTarget(i));
            units.insertAfter(jgs, lss);
            lss.setTarget(i, jgs);
          }

          JGotoStmt jgs = new JGotoStmt(lss.getDefaultTarget());
          units.insertAfter(jgs, lss);
          lss.setDefaultTarget(jgs);
        }
      }

      for (Trap t : getTraps()) {
        JGotoStmt jgs = new JGotoStmt(t.getHandlerUnit());
        units.addLast(jgs);
        t.setHandlerUnit(jgs);
      }
    }

    /*
     * Fix up the grimp representations of statements so they can be compiled as java.
     */
    {
      for (Local l : getLocals()) {
        Type t = l.getType();
        if (t instanceof RefType) {
          RefType rt = (RefType) t;

          String className = rt.getSootClass().toString();
          String packageName = rt.getSootClass().getJavaPackageName();

          String classPackageName = packageName;
          if (className.lastIndexOf('.') > 0) {
            // 0 doesnt make sense
            classPackageName = className.substring(0, className.lastIndexOf('.'));
          }
          if (!packageName.equals(classPackageName)) {
            throw new DecompilationException("Unable to retrieve package name for identifier. Please report to developer.");
          }

          addToImportList(className);
          // addPackage(rt.getSootClass().getJavaPackageName());
        }
      }

      for (Unit u : getUnits()) {
        if (u instanceof IfStmt) {
          javafy(((IfStmt) u).getConditionBox());
        } else if (u instanceof ThrowStmt) {
          javafy(((ThrowStmt) u).getOpBox());
        } else if (u instanceof TableSwitchStmt) {
          javafy(((TableSwitchStmt) u).getKeyBox());
        } else if (u instanceof LookupSwitchStmt) {
          javafy(((LookupSwitchStmt) u).getKeyBox());
        } else if (u instanceof MonitorStmt) {
          javafy(((MonitorStmt) u).getOpBox());
        } else if (u instanceof DefinitionStmt) {
          DefinitionStmt ds = (DefinitionStmt) u;

          javafy(ds.getRightOpBox());
          javafy(ds.getLeftOpBox());

          Value rightOp = ds.getRightOp();
          if (rightOp instanceof IntConstant) {
            ds.getRightOpBox().setValue(DIntConstant.v(((IntConstant) rightOp).value, ds.getLeftOp().getType()));
          }
        } else if (u instanceof ReturnStmt) {
          ReturnStmt rs = (ReturnStmt) u;

          Value op = rs.getOp();
          if (op instanceof IntConstant) {
            rs.getOpBox().setValue(DIntConstant.v(((IntConstant) op).value, body.getMethod().getReturnType()));
          } else {
            javafy(rs.getOpBox());
          }
        } else if (u instanceof InvokeStmt) {
          javafy(((InvokeStmt) u).getInvokeExprBox());
        }
      }
    }

    /*
     * Convert references to "this" and parameters.
     */
    for (Unit u : getUnits()) {
      if (u instanceof IdentityStmt) {
        IdentityStmt ids = (IdentityStmt) u;
        Value ids_rightOp = ids.getRightOp();
        Value ids_leftOp = ids.getLeftOp();

        if ((ids_leftOp instanceof Local) && (ids_rightOp instanceof ThisRef)) {
          Local thisLocal = (Local) ids_leftOp;
          thisLocals.add(thisLocal);
          thisLocal.setName("this");
        }
      }
      if (u instanceof DefinitionStmt) {
        DefinitionStmt ds = (DefinitionStmt) u;
        Value rightOp = ds.getRightOp();

        if (rightOp instanceof ParameterRef) {
          pMap.put(((ParameterRef) rightOp).getIndex(), ds.getLeftOp());
        }

        if (rightOp instanceof CaughtExceptionRef) {
          caughtrefs.add((CaughtExceptionRef) rightOp);
        }
      }
    }

    /*
     * Fix up the calls to other constructors. Note, this is seriously underbuilt.
     */
    for (Unit u : getUnits()) {
      if (u instanceof InvokeStmt) {
        InvokeStmt ivs = (InvokeStmt) u;
        Value ie = ivs.getInvokeExpr();

        if (ie instanceof InstanceInvokeExpr) {
          InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
          Value base = iie.getBase();

          if ((base instanceof Local) && "this".equals(((Local) base).getName())) {
            String name = iie.getMethodRef().name();

            if (SootMethod.constructorName.equals(name) || SootMethod.staticInitializerName.equals(name)) {
              if (constructorUnit != null) {
                throw new RuntimeException("More than one candidate for constructor found.");
              }

              constructorExpr = iie;
              constructorUnit = u;
            }
          }
        }
      }
    }
  }

  /*
   * The following set of routines takes care of converting the syntax of single grimp statements to java.
   */

  private void javafy(ValueBox vb) {
    Value v = vb.getValue();

    if (v instanceof Expr) {
      javafy_expr(vb);
    } else if (v instanceof Ref) {
      javafy_ref(vb);
    } else if (v instanceof Local) {
      javafy_local(vb);
    } else if (v instanceof Constant) {
      javafy_constant(vb);
    }
  }

  private void javafy_expr(ValueBox vb) {
    Expr e = (Expr) vb.getValue();

    if (e instanceof BinopExpr) {
      javafy_binop_expr(vb);
    } else if (e instanceof UnopExpr) {
      javafy_unop_expr(vb);
    } else if (e instanceof CastExpr) {
      javafy_cast_expr(vb);
    } else if (e instanceof NewArrayExpr) {
      javafy_newarray_expr(vb);
    } else if (e instanceof NewMultiArrayExpr) {
      javafy_newmultiarray_expr(vb);
    } else if (e instanceof InstanceOfExpr) {
      javafy_instanceof_expr(vb);
    } else if (e instanceof InvokeExpr) {
      javafy_invoke_expr(vb);
    } else if (e instanceof NewExpr) {
      javafy_new_expr(vb);
    }
  }

  private void javafy_ref(ValueBox vb) {
    Ref r = (Ref) vb.getValue();

    if (r instanceof StaticFieldRef) {
      SootFieldRef fieldRef = ((StaticFieldRef) r).getFieldRef();
      // addPackage(fieldRef.declaringClass().getJavaPackageName());

      String className = fieldRef.declaringClass().toString();
      String packageName = fieldRef.declaringClass().getJavaPackageName();

      String classPackageName = packageName;
      if (className.lastIndexOf('.') > 0) {
        // 0 doesnt make sense
        classPackageName = className.substring(0, className.lastIndexOf('.'));
      }
      if (!packageName.equals(classPackageName)) {
        throw new DecompilationException("Unable to retrieve package name for identifier. Please report to developer.");
      }

      addToImportList(className);

      vb.setValue(new DStaticFieldRef(fieldRef, getMethod().getDeclaringClass().getName()));
    } else if (r instanceof ArrayRef) {
      ArrayRef ar = (ArrayRef) r;

      javafy(ar.getBaseBox());
      javafy(ar.getIndexBox());
    } else if (r instanceof InstanceFieldRef) {
      InstanceFieldRef ifr = (InstanceFieldRef) r;

      javafy(ifr.getBaseBox());

      vb.setValue(new DInstanceFieldRef(ifr.getBase(), ifr.getFieldRef(), thisLocals));
    } else if (r instanceof ThisRef) {
      ThisRef tr = (ThisRef) r;

      vb.setValue(new DThisRef((RefType) tr.getType()));
    }
  }

  private void javafy_local(ValueBox vb) {
  }

  private void javafy_constant(ValueBox vb) {
  }

  private void javafy_binop_expr(ValueBox vb) {
    BinopExpr boe = (BinopExpr) vb.getValue();

    ValueBox leftOpBox = boe.getOp1Box(), rightOpBox = boe.getOp2Box();
    Value leftOp = leftOpBox.getValue(), rightOp = rightOpBox.getValue();

    if (rightOp instanceof IntConstant) {
      if (!(leftOp instanceof IntConstant)) {
        javafy(leftOpBox);
        leftOp = leftOpBox.getValue();

        if (boe instanceof ConditionExpr) {
          rightOpBox.setValue(DIntConstant.v(((IntConstant) rightOp).value, leftOp.getType()));
        } else {
          rightOpBox.setValue(DIntConstant.v(((IntConstant) rightOp).value, null));
        }
      }
    } else if (leftOp instanceof IntConstant) {
      javafy(rightOpBox);
      rightOp = rightOpBox.getValue();

      if (boe instanceof ConditionExpr) {
        leftOpBox.setValue(DIntConstant.v(((IntConstant) leftOp).value, rightOp.getType()));
      } else {
        leftOpBox.setValue(DIntConstant.v(((IntConstant) leftOp).value, null));
      }
    } else {
      javafy(rightOpBox);
      rightOp = rightOpBox.getValue();

      javafy(leftOpBox);
      leftOp = leftOpBox.getValue();
    }

    if (boe instanceof CmpExpr) {
      vb.setValue(new DCmpExpr(leftOp, rightOp));
    } else if (boe instanceof CmplExpr) {
      vb.setValue(new DCmplExpr(leftOp, rightOp));
    } else if (boe instanceof CmpgExpr) {
      vb.setValue(new DCmpgExpr(leftOp, rightOp));
    }
  }

  private void javafy_unop_expr(ValueBox vb) {
    UnopExpr uoe = (UnopExpr) vb.getValue();

    javafy(uoe.getOpBox());

    if (uoe instanceof LengthExpr) {
      vb.setValue(new DLengthExpr(((LengthExpr) uoe).getOp()));
    } else if (uoe instanceof NegExpr) {
      vb.setValue(new DNegExpr(((NegExpr) uoe).getOp()));
    }
  }

  private void javafy_cast_expr(ValueBox vb) {
    CastExpr ce = (CastExpr) vb.getValue();
    javafy(ce.getOpBox());
  }

  private void javafy_newarray_expr(ValueBox vb) {
    NewArrayExpr nae = (NewArrayExpr) vb.getValue();
    javafy(nae.getSizeBox());
    vb.setValue(new DNewArrayExpr(nae.getBaseType(), nae.getSize()));
  }

  private void javafy_newmultiarray_expr(ValueBox vb) {
    NewMultiArrayExpr nmae = (NewMultiArrayExpr) vb.getValue();
    for (int i = 0; i < nmae.getSizeCount(); i++) {
      javafy(nmae.getSizeBox(i));
    }
    vb.setValue(new DNewMultiArrayExpr(nmae.getBaseType(), nmae.getSizes()));
  }

  private void javafy_instanceof_expr(ValueBox vb) {
    InstanceOfExpr ioe = (InstanceOfExpr) vb.getValue();
    javafy(ioe.getOpBox());
  }

  private void javafy_invoke_expr(ValueBox vb) {
    InvokeExpr ie = (InvokeExpr) vb.getValue();
    String className = ie.getMethodRef().declaringClass().toString();
    String packageName = ie.getMethodRef().declaringClass().getJavaPackageName();
    String classPackageName = packageName;

    if (className.lastIndexOf('.') > 0) {
      // 0 doesnt make sense
      classPackageName = className.substring(0, className.lastIndexOf('.'));
    }
    if (!packageName.equals(classPackageName)) {
      throw new DecompilationException("Unable to retrieve package name for identifier. Please report to developer.");
    }

    addToImportList(className);

    for (int i = 0; i < ie.getArgCount(); i++) {
      Value arg = ie.getArg(i);

      if (arg instanceof IntConstant) {
        ie.getArgBox(i).setValue(DIntConstant.v(((IntConstant) arg).value, ie.getMethodRef().parameterType(i)));
      } else {
        javafy(ie.getArgBox(i));
      }
    }

    if (ie instanceof InstanceInvokeExpr) {
      javafy(((InstanceInvokeExpr) ie).getBaseBox());

      if (ie instanceof VirtualInvokeExpr) {
        VirtualInvokeExpr vie = (VirtualInvokeExpr) ie;
        vb.setValue(new DVirtualInvokeExpr(vie.getBase(), vie.getMethodRef(), vie.getArgs(), thisLocals));
      } else if (ie instanceof SpecialInvokeExpr) {
        SpecialInvokeExpr sie = (SpecialInvokeExpr) ie;
        vb.setValue(new DSpecialInvokeExpr(sie.getBase(), sie.getMethodRef(), sie.getArgs()));
      } else if (ie instanceof InterfaceInvokeExpr) {
        InterfaceInvokeExpr iie = (InterfaceInvokeExpr) ie;
        vb.setValue(new DInterfaceInvokeExpr(iie.getBase(), iie.getMethodRef(), iie.getArgs()));
      } else {
        throw new RuntimeException("InstanceInvokeExpr " + ie + " not javafied correctly");
      }
    } else if (ie instanceof StaticInvokeExpr) {
      StaticInvokeExpr sie = (StaticInvokeExpr) ie;

      if (sie instanceof NewInvokeExpr) {
        NewInvokeExpr nie = (NewInvokeExpr) sie;

        RefType rt = nie.getBaseType();

        className = rt.getSootClass().toString();
        packageName = rt.getSootClass().getJavaPackageName();

        classPackageName = packageName;
        if (className.lastIndexOf('.') > 0) {
          // 0 doesnt make sense
          classPackageName = className.substring(0, className.lastIndexOf('.'));
        }
        if (!packageName.equals(classPackageName)) {
          throw new DecompilationException("Unable to retrieve package name for identifier. Please report to developer.");
        }

        addToImportList(className);
        vb.setValue(new DNewInvokeExpr((RefType) nie.getType(), nie.getMethodRef(), nie.getArgs()));
      } else {
        SootMethodRef methodRef = sie.getMethodRef();
        className = methodRef.declaringClass().toString();
        packageName = methodRef.declaringClass().getJavaPackageName();

        classPackageName = packageName;

        if (className.lastIndexOf('.') > 0) {
          // 0 doesnt make sense
          classPackageName = className.substring(0, className.lastIndexOf('.'));
        }
        if (!packageName.equals(classPackageName)) {
          throw new DecompilationException("Unable to retrieve package name for identifier. Please report to developer.");
        }

        addToImportList(className);

        // addPackage(methodRef.declaringClass().getJavaPackageName());
        vb.setValue(new DStaticInvokeExpr(methodRef, sie.getArgs()));
      }
    } else {
      throw new RuntimeException("InvokeExpr " + ie + " not javafied correctly");
    }
  }

  private void javafy_new_expr(ValueBox vb) {
    NewExpr ne = (NewExpr) vb.getValue();

    String className = ne.getBaseType().getSootClass().toString();
    String packageName = ne.getBaseType().getSootClass().getJavaPackageName();

    String classPackageName = packageName;
    if (className.lastIndexOf('.') > 0) {
      // 0 doesnt make sense
      classPackageName = className.substring(0, className.lastIndexOf('.'));
    }
    if (!packageName.equals(classPackageName)) {
      throw new DecompilationException("Unable to retrieve package name for identifier. Please report to developer.");
    }

    addToImportList(className);
  }

  public void addToImportList(String className) {
    if (!className.isEmpty()) {
      importList.add(className);
    }
  }

  public void debug(String methodName, String debug) {
    if (DEBUG) {
      System.out.println(methodName + "    DEBUG: " + debug);
    }
  }
}
