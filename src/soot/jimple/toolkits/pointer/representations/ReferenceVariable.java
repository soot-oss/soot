/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Feng Qian
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

/* ReferenceVariable provides interface for simulating assignments
 * in a native method.
 *
 * e.g.,  a = b, can be written by a.isAssigned(b);
 *
 * A reference variable is an abstract of a variable rather than a
 * representation of an abstract object, the later is represented by
 * AbstractObject.
 *
 * The simulation formulates the assignment or field assignment of such
 * high level variable. It is the resposibility of analyses to get the
 * constraints out of such abstract simulation. 
 *
 * Analyses may use very different representations of points-to, e.g.,
 * set constraints, type rules, or points-to graphs.  All the analyses
 * have to do is to let the variables in the program implement this
 * interface.  
 */

package soot.jimple.toolkits.pointer.representations;

import soot.*;

public interface ReferenceVariable {
}
