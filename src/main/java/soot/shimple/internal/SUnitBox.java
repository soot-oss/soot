/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.shimple.internal;

import soot.UnitBox;

/**
 * Extension of UnitBox to provide some extra information needed by
 * SPatchingChain.
 *
 * @author Navindra Umanee
 **/
public interface SUnitBox extends UnitBox
{
    /**
     * Indicates whether the contents of the UnitBox may have been
     * changed.  Returns true if setUnit(Unit) has been called
     * recently and was not followed by setUnitChanged(false).
     *
     * <p> Needed for Shimple internal Unit chain patching.
     **/
    public boolean isUnitChanged();
    
    /**
     * Updates the value of the flag used to indicate whether the
     * contents of the UnitBox may have changed.
     *
     * <p> Needed for Shimple internal Unit chain patching.
     *
     * @see #isUnitChanged()
     **/
    public void setUnitChanged(boolean unitChanged);
}
