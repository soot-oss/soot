/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

package soot.jimple.toolkits.base;

import soot.*;
import soot.jimple.*;
import soot.tagkit.*;

public class ExceptionCheckerError extends Exception {

    public ExceptionCheckerError(SootMethod m, SootClass sc, Stmt s, SourceLnPosTag pos){
        method(m);
        excType(sc);
        throwing(s);
        position(pos);
    }
        
    private SootMethod method;
    private SootClass excType;
    private Stmt throwing;
    private SourceLnPosTag position;
    
    public SootMethod method(){
        return method;
    }

    public void method(SootMethod sm){
        method = sm;
    }

    public SootClass excType(){
        return excType;
    }

    public void excType(SootClass sc){
        excType = sc;
    }

    public Stmt throwing(){
        return throwing;
    }

    public void throwing(Stmt s){
        throwing = s;
    }

    public SourceLnPosTag position(){
        return position;
    }

    public void position(SourceLnPosTag pos){
        position = pos;
    }
    
}
