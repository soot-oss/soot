package soot.JastAddJ;

import java.util.HashSet;
import java.io.File;
import java.util.*;
import beaver.*;
import java.util.ArrayList;
import java.util.zip.*;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Collection;
import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.coffi.ClassFile;
import soot.coffi.method_info;
import soot.coffi.CONSTANT_Utf8_info;
import soot.tagkit.SourceFileTag;
import soot.coffi.CoffiMethodSource;

/**
  * @ast interface
 * 
 */
public interface BranchPropagation {
public void collectBranches(Collection c);

public Stmt branchTarget(Stmt branchStmt);

public void collectFinally(Stmt branchStmt, ArrayList list);

  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:32
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection targetContinues();
  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:33
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection targetBreaks();
  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:34
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection targetBranches();
  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:35
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection escapedBranches();
  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:36
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection branches();
  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:39
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean targetOf(ContinueStmt stmt);
  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:40
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean targetOf(BreakStmt stmt);
}
