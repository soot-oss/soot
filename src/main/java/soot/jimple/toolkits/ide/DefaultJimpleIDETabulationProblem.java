package soot.jimple.toolkits.ide;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2013 Eric Bodden and others
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

import heros.InterproceduralCFG;
import heros.template.DefaultIDETabulationProblem;

import soot.SootMethod;
import soot.Unit;

/**
 * A {@link DefaultIDETabulationProblem} with {@link Unit}s as nodes and {@link SootMethod}s as methods.
 */
public abstract class DefaultJimpleIDETabulationProblem<D, V, I extends InterproceduralCFG<Unit, SootMethod>>
    extends DefaultIDETabulationProblem<Unit, D, SootMethod, V, I> {

  public DefaultJimpleIDETabulationProblem(I icfg) {
    super(icfg);
  }

}
