/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai and Patrick Lam
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot;

import java.util.*;
import soot.util.*;

/** Maintains the triple (phaseName, singleton, options) needed for a
 * transformation. */
public class Transform
{
    String phaseName;
    Transformer t;
    String options;
    
    public Transform(String phaseName, Transformer t, String options)
    {
        this.phaseName = phaseName;
        this.t = t;
        this.options = options;
    }

    public Transform(String phaseName, Transformer t)
    {
        this(phaseName, t, "");
    }

    public String getPhaseName() { return phaseName; }
    public Transformer getTransformer() { return t; }
    public String getOptions() { return options; }
}
