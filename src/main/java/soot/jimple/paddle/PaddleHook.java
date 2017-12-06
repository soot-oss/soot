/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004, 2005 Ondrej Lhotak
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

package soot.jimple.paddle;
import soot.*;
import java.util.*;

/** Loads the Paddle plugin.
 * @author Ondrej Lhotak
 */
public class PaddleHook extends SceneTransformer
{ 
    public PaddleHook( Singletons.Global g ) {}
    public static PaddleHook v() { return G.v().soot_jimple_paddle_PaddleHook(); }

    private IPaddleTransformer paddleTransformer;
    public IPaddleTransformer paddleTransformer() {
        if(paddleTransformer == null) {
            paddleTransformer = (IPaddleTransformer)
                instantiate("soot.jimple.paddle.PaddleTransformer");
        }
        return paddleTransformer;
    }

    protected void internalTransform( String phaseName, Map<String,String> options )
    {
        paddleTransformer().transform(phaseName, options);
    }
    public Object instantiate(String className) {
        Object ret;
        try {
            ret = Class.forName(
                className ).newInstance();
        } catch( ClassNotFoundException e ) {
            throw new RuntimeException("Could not find "+className+". Did you include Paddle on your Java classpath?");
        } catch( InstantiationException e ) {
            throw new RuntimeException( "Could not instantiate "+className+": "+e );
        } catch( IllegalAccessException e ) {
            throw new RuntimeException( "Could not instantiate "+className+": "+e );
        }
        return ret;
    }

    private Object paddleG;
    public Object paddleG() {
        if(paddleG == null) {
            paddleG = instantiate("soot.PaddleG");
        }
        return paddleG;
    }
    /** This is called when Soot finishes executing all interprocedural phases.
     * Paddle uses it to stop profiling if profiling is enabled. */
    public void finishPhases()
    {
        if(paddleTransformer != null) {
            paddleTransformer().finishPhases();
        }
    }
}


