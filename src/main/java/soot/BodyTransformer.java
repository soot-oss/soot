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

/**
 *  An abstract class which acts on a Body. This class provides a harness and acts as an
 *  interface for classes that wish to transform a Body. Subclasses 
 *  provide the actual Body transformation  implementation.
 */

public abstract class BodyTransformer extends Transformer
{
	
	private Map<String, String> enabledOnlyMap = Collections.singletonMap("enabled", "true");
	
    /** 
     *  Called by clients of the transformation. Acts as a generic interface
     *  for BodyTransformers.
     *  Calls internalTransform with the optionsString properly set up.
     *  That is, the options in optionsString override those in the Scene. 
     *  @param b the body on which to apply the transformation
     *  @param phaseName phaseName for the transform. Used to retrieve options from the Scene.
     */
    public final void transform(Body b, String phaseName, Map<String, String> options)
    {
        if(!PhaseOptions.getBoolean(options, "enabled"))
            return;

        internalTransform(b, phaseName, options);
    }

    public final void transform(Body b, String phaseName)
    {
        internalTransform(b, phaseName, enabledOnlyMap);
    }

    public final void transform(Body b)
    {
    	transform(b, "");
    }

    /**
     *  This method is called to perform the transformation itself. It is declared
     *  abstract; subclasses must implement this method by making it the entry point
     *  to their actual Body transformation. 
     *  @param b the body on which to apply the transformation
     *  @param phaseName the phasename for this transform; not typically used by implementations.
     *  @param options  the actual computed options; a combination of default options and Scene specified options.
     */
    protected abstract void internalTransform(Body b, String phaseName, Map<String, String> options);

}




















