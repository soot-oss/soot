/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import soot.options.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import jedd.*;

/** This class collects the analysis results computed by Paddle.
 * @author Ondrej Lhotak
 */
public class Results implements PointsToAnalysis
{ 
    public Results( Singletons.Global g ) {}
    public static Results v() { return G.v().soot_jimple_paddle_Results(); }

    public AbsCallGraph callGraph() { return PaddleScene.v().cg; }
    public AbsP2Sets p2sets() { return PaddleScene.v().p2sets; }
    public AbsReachableMethods reachableMethods() {return PaddleScene.v().rm;}
    public AbsReachableMethods reachableContexts() {return PaddleScene.v().rc;}

    public PointsToSet reachingObjects( Local l ) {
        throw new RuntimeException( "NYI" );
    }
    public PointsToSet reachingObjects( Context c, Local l ) {
        throw new RuntimeException( "NYI" );
    }
    public PointsToSet reachingObjects( SootField f ) {
        throw new RuntimeException( "NYI" );
    }
    public PointsToSet reachingObjects( PointsToSet s, SootField f ) {
        throw new RuntimeException( "NYI" );
    }
    public PointsToSet reachingObjects( Local l, SootField f ) {
        throw new RuntimeException( "NYI" );
    }
    public PointsToSet reachingObjects( Context c, Local l, SootField f ) {
        throw new RuntimeException( "NYI" );
    }
    public PointsToSet reachingObjectsOfArrayElement( PointsToSet s ) {
        throw new RuntimeException( "NYI" );
    }
}

