/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Patrick Lam
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

/** An abstract class which acts on the whole Scene. */
public abstract class SceneTransformer extends Transformer
{
    /** Calls internalTransform with the optionsString properly set up.
     *  That is, the options in optionsString override those in the Scene. */
    public final void transform(String phaseName, String optionsString)
    {
        Map options = Scene.v().computePhaseOptions(phaseName, 
                                                    getDefaultOptions() + " " + optionsString);

        Options.checkOptions(options, phaseName, getDeclaredOptions());

        if (Options.getBoolean(options, "disabled"))
            return;

        internalTransform(phaseName, options);
    }

    /** Performs the transformation on the Scene. */
    public final void transform()
    {
        transform("", "");
    }

    /** Performs the transformation on the Scene, under the given phaseName. */
    public final void transform(String phaseName)
    {
        transform(phaseName, "");
    }

    /** Performs the transformation on the Scene, under the given phaseName and with the given Options. */
    protected abstract void internalTransform(String phaseName, Map options);

    /** Returns the list of the default phase options for this transformer. */
    public String getDefaultOptions() 
    {
        return "";
    }

    /* Returns a String containing the list of phase options understood here. */
    protected String getDeclaredOptions() { return "disabled"; }
}
