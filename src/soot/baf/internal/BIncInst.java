/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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





package soot.baf.internal;

import soot.*;
import soot.jimple.*;
import soot.baf.*;
import soot.util.*;
import java.util.*;

public class BIncInst extends AbstractInst implements IncInst
{
    ValueBox localBox;
    ValueBox defLocalBox;
    List useBoxes;
  Constant mConstant;
  List mDefBoxes;  

    private class LinkedBafLocalBox extends BafLocalBox
    {
        ValueBox otherBox = null;

        private LinkedBafLocalBox(Value v)
        {
            super(v);
        }

        public void setOtherBox(ValueBox otherBox) 
        { 
            this.otherBox = otherBox; 
        }

        public Value getValue()
        {
            Value toReturn = super.getValue();
            
            return toReturn;
        }
        
        public void setValue(Value v)
        {
            super.setValue(v);
            
            if(otherBox != null)
            {
                if(otherBox.getValue() != v)
                    otherBox.setValue(v);
            }
        }
    }
       
    public BIncInst(Local local, Constant constant)
    {
      mConstant = constant;
      
      localBox = new BafLocalBox(local);
      
      useBoxes = new ArrayList();
      useBoxes.add(localBox);
      useBoxes = Collections.unmodifiableList(useBoxes);

      defLocalBox = new BafLocalBox(local);
      
      //((LinkedBafLocalBox) defLocalBox).setOtherBox(localBox);
      //((LinkedBafLocalBox) localBox).setOtherBox(defLocalBox);

      mDefBoxes = new ArrayList();
      mDefBoxes.add(defLocalBox);
      mDefBoxes = Collections.unmodifiableList(mDefBoxes);
      
    }

    public int getInCount()
    {
        return 0;
    }

    public Object clone() 
    {
      return new  BIncInst( getLocal(), getConstant());
    }

  public int getInMachineCount()
  {
    return 0;
  }
    
  public int getOutCount()
  {
    return 0;
  }

    public int getOutMachineCount()
    {
        return 0;
    }
    
   

  
  public Constant getConstant() 
  {
    return mConstant;
  }
  
  public void setConstant(Constant aConstant) 
  {
    mConstant = aConstant;
  }



  final public String getName() { return "inc.i"; }
    final String getParameters()
    { return " "+ localBox.getValue().toString(); }
    protected void getParameters(UnitPrinter up ) {
        up.literal(" ");
        localBox.toString(up);
    }
    
    public void apply(Switch sw)
    {
        ((InstSwitch) sw).caseIncInst(this);
    }   
 
    public void setLocal(Local l)
    {
        localBox.setValue(l);
    }   
    
    public Local getLocal()
    {
        return (Local) localBox.getValue();
    }

    public List getUseBoxes() 
    {
        return useBoxes;
    }
    
    public List getDefBoxes() 
    {
        return mDefBoxes;
    }

  
  public String toString()
  {
    return "inc.i" + " " +getLocal() + " " + getConstant() ;
  }

  public void toString( UnitPrinter up ) {
      up.literal( "inc.i" );
      up.literal( " " );
      localBox.toString( up );
      up.literal( " " );
      up.constant( mConstant );
  }

    
}
