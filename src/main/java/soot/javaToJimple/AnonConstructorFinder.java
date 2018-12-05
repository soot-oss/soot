package soot.javaToJimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import polyglot.types.Type;

public class AnonConstructorFinder extends polyglot.visit.ContextVisitor {
  private static final Logger logger = LoggerFactory.getLogger(AnonConstructorFinder.class);

  public AnonConstructorFinder(polyglot.frontend.Job job, polyglot.types.TypeSystem ts, polyglot.ast.NodeFactory nf) {
    super(job, ts, nf);
  }

  public polyglot.visit.NodeVisitor enter(polyglot.ast.Node parent, polyglot.ast.Node n) {
    if (n instanceof polyglot.ast.New && ((polyglot.ast.New) n).anonType() != null) {
      try {
        List<Type> argTypes = new ArrayList<Type>();
        for (Iterator it = ((polyglot.ast.New) n).arguments().iterator(); it.hasNext();) {
          argTypes.add(((polyglot.ast.Expr) it.next()).type());
        }
        polyglot.types.ConstructorInstance ci
            = typeSystem().findConstructor(((polyglot.ast.New) n).anonType().superType().toClass(), argTypes,
                ((polyglot.ast.New) n).anonType().superType().toClass());
        InitialResolver.v().addToAnonConstructorMap((polyglot.ast.New) n, ci);
      } catch (polyglot.types.SemanticException e) {
        System.out.println(e.getMessage());
        logger.error(e.getMessage(), e);
      }
    }
    return this;
  }

}
