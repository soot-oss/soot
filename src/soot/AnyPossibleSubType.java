/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

import soot.util.*;


@SuppressWarnings("serial")
public class AnyPossibleSubType extends RefLikeType
{
	private RefType base;
    private AnyPossibleSubType( RefType base )
    {
        this.base = base;
    }

    public static AnyPossibleSubType v( RefType base ) {
        if( base.getAnyPossibleSubType() == null ) {
            base.setAnyPossibleSubType( new AnyPossibleSubType( base ) );
        }
        return base.getAnyPossibleSubType();
    }
    
    @Override
    public String toString()
    {
        return "Any_implementing_type_of_"+base;
    }

    @Override
    public void apply(Switch sw)
    {
        ((TypeSwitch) sw).caseAnyPossibleSubType(this);
    }

    @Override
    public Type getArrayElementType() {
    	throw new RuntimeException( "Attempt to get array base type of a non-array" );  
    }
    
    public RefType getBase() { return base; }
    
    public void setBase( RefType base ) { this.base = base; }
}
