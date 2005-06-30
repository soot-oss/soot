/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Nomair A. Naeem
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


package soot.dava.toolkits.base.AST.structuredAnalysis;

import java.util.*;
import soot.*;

public class LocalPair{
    private Local leftLocal;
    private Local rightLocal;

    public LocalPair(Local left, Local right){
	leftLocal=left;
	rightLocal=right;
    }


    public Local getLeftLocal(){
	return leftLocal;
    }

    public Local getRightLocal(){
	return rightLocal;
    }

    public boolean equals(Object other){
	if(other instanceof LocalPair){
	    if(this.leftLocal.toString().equals(((LocalPair)other).getLeftLocal().toString())){
		if(this.rightLocal.toString().equals(((LocalPair)other).getRightLocal().toString())){
		    return true;
		}
	    }
	}
	return false;
    }

    public boolean contains(Local local){
	if(leftLocal.toString().equals(local.toString()) || rightLocal.toString().equals(local.toString())){
	    return true;
	}
	return false;
    }

    public String toString(){
	StringBuffer b = new StringBuffer();
	b.append("<"+leftLocal.toString()+","+rightLocal.toString()+">");
	return b.toString();
    }

}