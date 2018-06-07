package soot.shimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.SootMethod;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.StmtBody;
import soot.options.Options;
import soot.options.ShimpleOptions;
import soot.shimple.internal.SPatchingChain;
import soot.shimple.internal.ShimpleBodyBuilder;
import soot.util.HashChain;

// * <p> We decided to hide all the intelligence in
// * internal.ShimpleBodyBuilder for clarity of API.  Eventually we will
// * likely switch to an explicit Strategy pattern that will allow us to
// * select different SSA behaviours and algorithms.
/**
 * Implementation of the Body class for the SSA Shimple IR. This class provides methods for maintaining SSA form as well as
 * eliminating SSA form.
 *
 * @author Navindra Umanee
 * @see soot.shimple.internal.ShimpleBodyBuilder
 * @see <a href="http://citeseer.nj.nec.com/cytron91efficiently.html">Efficiently Computing Static Single Assignment Form and
 *      the Control Dependence Graph</a>
 **/
public class ShimpleBody extends StmtBody {
  private static final Logger logger = LoggerFactory.getLogger(ShimpleBody.class);
  /**
   * Holds our options map...
   **/
  protected ShimpleOptions options;

  protected ShimpleBodyBuilder sbb;

  protected boolean isExtendedSSA = false;

  /**
   * Construct an empty ShimpleBody associated with m.
   **/
  ShimpleBody(SootMethod m, Map options) {
    super(m);

    // must happen before SPatchingChain gets created
    this.options = new ShimpleOptions(options);
    setSSA(true);
    isExtendedSSA = this.options.extended();

    unitChain = new SPatchingChain(this, new HashChain());
    sbb = new ShimpleBodyBuilder(this);
  }

  /**
   * Constructs a ShimpleBody from the given Body and options.
   *
   * <p>
   * Currently available option is "naive-phi-elimination", typically in the "shimple" phase (eg, -p shimple
   * naive-phi-elimination) which can be useful for understanding the effect of analyses.
   **/
  ShimpleBody(Body body, Map options) {
    super(body.getMethod());

    if (!(body instanceof JimpleBody || body instanceof ShimpleBody)) {
      throw new RuntimeException("Cannot construct ShimpleBody from given Body type.");
    }

    if (Options.v().verbose()) {
      logger.debug("[" + getMethod().getName() + "] Constructing ShimpleBody...");
    }

    // must happen before SPatchingChain gets created
    this.options = new ShimpleOptions(options);

    unitChain = new SPatchingChain(this, new HashChain());
    importBodyContentsFrom(body);

    /* Shimplise body */
    sbb = new ShimpleBodyBuilder(this);

    if (body instanceof ShimpleBody) {
      rebuild(true);
    } else {
      rebuild(false);
    }
  }

  /**
   * Recompute SSA form.
   *
   * <p>
   * Note: assumes presence of Phi nodes in body that require elimination. If you *know* there are no Phi nodes present, you
   * may prefer to use rebuild(false) in order to skip some transformations during the Phi elimination process.
   **/
  public void rebuild() {
    rebuild(true);
  }

  /**
   * Rebuild SSA form.
   *
   * <p>
   * If there are Phi nodes already present in the body, it is imperative that we specify this so that the algorithm can
   * eliminate them before rebuilding SSA.
   *
   * <p>
   * The eliminate Phi nodes stage is harmless, but if you *know* that no Phi nodes are present and you wish to avoid the
   * transformations involved in eliminating Phi nodes, use rebuild(false).
   **/
  public void rebuild(boolean hasPhiNodes) {
    isExtendedSSA = options.extended();
    sbb.transform();
    setSSA(true);
  }

  /**
   * Returns an equivalent unbacked JimpleBody of the current Body by eliminating the Phi nodes.
   *
   * <p>
   * Currently available option is "naive-phi-elimination", typically specified in the "shimple" phase (eg, -p shimple
   * naive-phi-elimination) which skips the dead code elimination and register allocation phase before eliminating Phi nodes.
   * This can be useful for understanding the effect of analyses.
   *
   * <p>
   * Remember to setActiveBody() if necessary in your SootMethod.
   *
   * @see #eliminatePhiNodes()
   **/
  public JimpleBody toJimpleBody() {
    ShimpleBody sBody = (ShimpleBody) this.clone();

    sBody.eliminateNodes();
    JimpleBody jBody = Jimple.v().newBody(sBody.getMethod());
    jBody.importBodyContentsFrom(sBody);
    return jBody;
  }

  /**
   * Remove Phi nodes from body. SSA form is no longer a given once done.
   *
   * <p>
   * Currently available option is "naive-phi-elimination", typically specified in the "shimple" phase (eg, -p shimple
   * naive-phi-elimination) which skips the dead code elimination and register allocation phase before eliminating Phi nodes.
   * This can be useful for understanding the effect of analyses.
   *
   * @see #toJimpleBody()
   **/
  public void eliminatePhiNodes() {
    sbb.preElimOpt();
    sbb.eliminatePhiNodes();
    sbb.postElimOpt();
    setSSA(false);
  }

  public void eliminatePiNodes() {
    sbb.eliminatePiNodes();
  }

  public void eliminateNodes() {
    sbb.preElimOpt();
    sbb.eliminatePhiNodes();
    if (isExtendedSSA) {
      sbb.eliminatePiNodes();
    }
    sbb.postElimOpt();
    setSSA(false);
  }

  /**
   * Returns a copy of the current ShimpleBody.
   **/
  public Object clone() {
    Body b = Shimple.v().newBody(getMethod());
    b.importBodyContentsFrom(this);
    return b;
  }

  /**
   * Set isSSA boolean to indicate whether a ShimpleBody is still in SSA form or not. Could be useful for book-keeping
   * purposes.
   **/
  protected boolean isSSA = false;

  /**
   * Sets a flag that indicates whether ShimpleBody is still in SSA form after a transformation or not. It is often up to the
   * user to indicate if a body is no longer in SSA form.
   **/
  public void setSSA(boolean isSSA) {
    this.isSSA = isSSA;
  }

  /**
   * Returns value of, optional, user-maintained SSA boolean.
   *
   * @see #setSSA(boolean)
   **/
  public boolean isSSA() {
    return isSSA;
  }

  public boolean isExtendedSSA() {
    return isExtendedSSA;
  }

  /**
   * Returns the Shimple options applicable to this body.
   **/
  public ShimpleOptions getOptions() {
    return options;
  }

  /**
   * Make sure the locals in this body all have unique String names. If the standard-local-names option is specified to
   * Shimple, this results in the LocalNameStandardizer being applied. Otherwise, renaming is kept to a minimum and an
   * underscore notation is used to differentiate locals previously of the same name.
   *
   * @see soot.jimple.toolkits.scalar.LocalNameStandardizer
   **/
  public void makeUniqueLocalNames() {
    sbb.makeUniqueLocalNames();
  }
}
