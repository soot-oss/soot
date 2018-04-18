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

package soot.javaToJimple;

import soot.*;

public class AnonClassInitMethodSource extends soot.javaToJimple.PolyglotMethodSource {

    private boolean hasOuterRef;
    public void hasOuterRef(boolean b){
        hasOuterRef = b;
    }
    public boolean hasOuterRef(){
        return hasOuterRef;
    }
    
    private boolean hasQualifier;
    public void hasQualifier(boolean b){
        hasQualifier = b;
    }
    public boolean hasQualifier(){
        return hasQualifier;
    }
    
    
    private boolean inStaticMethod;

    public void inStaticMethod(boolean b){
        inStaticMethod = b;
    }
    public boolean inStaticMethod(){
        return inStaticMethod;
    }
    

    private boolean isSubType = false;
    public void isSubType(boolean b){
        isSubType = b;
    }
    public boolean isSubType(){
        return isSubType;
    }
    
    private soot.Type superOuterType = null;
    private soot.Type thisOuterType = null;

    public void superOuterType(soot.Type t){
        superOuterType = t;
    }
    public soot.Type superOuterType(){
        return superOuterType;
    }

    public void thisOuterType(soot.Type t){
        thisOuterType = t;
    }
    public soot.Type thisOuterType(){
        return thisOuterType;
    }

    private polyglot.types.ClassType polyglotType;
    public void polyglotType(polyglot.types.ClassType type){
        polyglotType = type;
    }
    public polyglot.types.ClassType polyglotType() {
        return polyglotType;
    }
    
    private polyglot.types.ClassType anonType;
    public void anonType(polyglot.types.ClassType type){
        anonType = type;
    }
    public polyglot.types.ClassType anonType() {
        return anonType;
    }
    
    public soot.Body getBody(soot.SootMethod sootMethod, String phaseName){
        AnonInitBodyBuilder aibb = new AnonInitBodyBuilder();
        soot.jimple.JimpleBody body = aibb.createBody(sootMethod);
        
        PackManager.v().getPack("jj").apply(body);
    
        return body;
    }
    
    private soot.Type outerClassType;

    public soot.Type outerClassType(){
        return outerClassType;
    }
    
    public void outerClassType(soot.Type type){
        outerClassType = type;
    }
}
