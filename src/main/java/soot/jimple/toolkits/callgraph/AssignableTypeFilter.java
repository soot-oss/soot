package soot.jimple.toolkits.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import soot.FastHierarchy;
import soot.RefType;
import soot.Type;

/**
 * A {@link VirtualCallSiteFilter} that only accepts reaching types assignable
 * to a given base type. Reused for every concurrency helper (Thread, Executor,
 * AsyncTask, Handler) since they all share the same "must be assignable to X"
 * rule -- only the base type X differs.
 */
public class AssignableTypeFilter implements VirtualCallSiteFilter {

  private final RefType requiredType;

  public AssignableTypeFilter(RefType requiredType) {
    if (requiredType == null) {
      throw new IllegalArgumentException("requiredType must not be null");
    }
    this.requiredType = requiredType;
  }

  @Override
  public boolean skipSite(VirtualCallSite site, FastHierarchy fh, Type type) {
    return !fh.canStoreType(type, requiredType);
  }
}

//A reusable class .Here we can reuse this class for THREAD,EXECUTOR,ASYNCTASK,HANDLER
//Following the DRY(Dont Repeat yourself) principle