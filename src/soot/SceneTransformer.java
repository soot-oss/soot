/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Patrick Lam
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot;

import java.util.*;

/** An abstract class which acts on the whole Scene. */
public abstract class SceneTransformer extends Transformer
{
    /** Performs the transformation on the Scene, under the given phaseName. */
    public final void transform(String phaseName, Map options)
    {
        if (!PhaseOptions.getBoolean(options, "enabled"))
            return;

        internalTransform(phaseName, options);
    }
    public final void transform(String phaseName)
    {
        HashMap dummyOptions = new HashMap();
        dummyOptions.put( "enabled", "true" );
        transform(phaseName, dummyOptions);
    }

    public final void transform()
    {
        transform("");
    }
    
    /** Performs the transformation on the Scene, under the given phaseName and with the given Options. */
    protected abstract void internalTransform(String phaseName, Map options);

}
