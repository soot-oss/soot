package soot.jimple.toolkit.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2018 John Toman
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

import java.lang.reflect.Method;

import soot.jimple.toolkit.callgraph.A;

public class EntryPoint {
	public void ptaResolution() {
		final Interface f = PhantomField.fld;
		try {
  		final Method m = f.getClass().getMethod(System.currentTimeMillis() + "", String.class);
  		m.invoke(f, f.args());
  		
  		final A x = PhantomField.fld2;
  		final Method m2 = x.getClass().getMethod(System.currentTimeMillis() + "", String.class);
  		m2.invoke(x, f.args());
		} catch(Exception e) { }
	}
	
	public void typestateResolution() {
    final Interface f = PhantomField.fld;
    try {
      final Method m = f.getClass().getMethod(System.currentTimeMillis() + "", String.class);
      m.invoke(f, "foo");
      
      final A x = PhantomField.fld2;
      final Method m2 = x.getClass().getMethod(System.currentTimeMillis() + "", String.class);
      m2.invoke(x, "foo");
    } catch(Exception e) { }
  }
}
