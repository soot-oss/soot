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

import soot.util.*;
import java.util.*;
import soot.*;
import soot.baf.*;

public class BTableSwitchInst extends AbstractInst implements TableSwitchInst
{
    UnitBox defaultTargetBox;
    int lowIndex, highIndex;
    UnitBox[] targetBoxes;
    List unitBoxes;

    public BTableSwitchInst(Unit defaultTarget, int lowIndex,
                             int highIndex, List targets)
    {
        this.defaultTargetBox = Baf.v().newInstBox(defaultTarget); 

        this.targetBoxes = new UnitBox[targets.size()];

        for(int i = 0; i < targetBoxes.length; i++)
            this.targetBoxes[i] = Baf.v().newInstBox((Unit) targets.get(i));

        this.lowIndex = lowIndex; this.highIndex = highIndex;

        // Build up unitBoxes
        {
            unitBoxes = new ArrayList();

            for (UnitBox element : targetBoxes)
				unitBoxes.add(element);

            unitBoxes.add(defaultTargetBox);
            unitBoxes = Collections.unmodifiableList(unitBoxes);
        }
    }

    public Object clone() 
    {        
        List list = new ArrayList();
        for (UnitBox element : targetBoxes) {
            list.add(element.getUnit());
        }
    
        return new  BTableSwitchInst(defaultTargetBox.getUnit(), lowIndex, highIndex, list);                
    }
    





    public int getInCount()
    {
        return 1;
    }

    public int getInMachineCount()
    {
        return 1;
    }
    
    public int getOutCount()
    {
        return 0;
    }

    public int getOutMachineCount()
    {
        return 0;
    }
    
    public Unit getDefaultTarget()
    {
        return defaultTargetBox.getUnit();
    }

    public void setDefaultTarget(Unit defaultTarget)
    {
        defaultTargetBox.setUnit(defaultTarget);
    }

    public UnitBox getDefaultTargetBox()
    {
        return defaultTargetBox;
    }

    public void setLowIndex(int lowIndex) { this.lowIndex = lowIndex; }
    public void setHighIndex(int highIndex) { this.highIndex = highIndex; }

    public int getLowIndex() { return lowIndex; }
    public int getHighIndex() { return highIndex; }

    public int getTargetCount() { return targetBoxes.length; }
    
    public Unit getTarget(int index)
    {
        return targetBoxes[index].getUnit();
    }

    public void setTarget(int index, Unit target)
    {
        targetBoxes[index].setUnit(target);
    }

    public void setTargets(List<Unit> targets)
    {
        for(int i = 0; i < targets.size(); i++)
            targetBoxes[i].setUnit(targets.get(i));
    }

    public UnitBox getTargetBox(int index)
    {
        return targetBoxes[index];
    }

    public List getTargets()
    {
        List targets = new ArrayList();

        for (UnitBox element : targetBoxes)
			targets.add(element.getUnit());

        return targets;
    }

    public String getName() { return "tableswitch"; }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        String endOfLine = " ";
        
        buffer.append("tableswitch" + endOfLine);
            
        buffer.append("{" + endOfLine);
        
        // In this for-loop, we cannot use "<=" since 'i' would wrap around.
        // The case for "i == highIndex" is handled separately after the loop.
        for(int i = lowIndex; i < highIndex; i++)
        {
            buffer.append("    case " + i + ": goto " +
                getTarget(i - lowIndex) + ";"
                          + endOfLine);
        }
        buffer.append("    case " + highIndex + ": goto " +
                  getTarget(highIndex - lowIndex) + ";"
                            + endOfLine);

        buffer.append("    default: goto " + getDefaultTarget() + ";" + endOfLine);
        buffer.append("}");

        return buffer.toString();
    }

    public void toString(UnitPrinter up) {
        up.literal("tableswitch");
        up.newline();
        up.literal("{");
        up.newline();
        
        // In this for-loop, we cannot use "<=" since 'i' would wrap around.
        // The case for "i == highIndex" is handled separately after the loop.
        for(int i = lowIndex; i < highIndex; i++)
        {
            printCaseTarget(up, i);
        }
        printCaseTarget(up, highIndex);

        up.literal("    default: goto ");
        defaultTargetBox.toString(up);
        up.literal(";");
        up.newline();
        up.literal("}");
    }

	private void printCaseTarget(UnitPrinter up, int targetIndex) {
		up.literal("    case ");
		up.literal(new Integer(targetIndex).toString());
		up.literal(": goto ");
		targetBoxes[targetIndex-lowIndex].toString(up);
		up.literal(";");
		up.newline();
	}

    public List getUnitBoxes()
    {
        return unitBoxes;
    }

    public void apply(Switch sw)
    {
        ((InstSwitch) sw).caseTableSwitchInst(this);
    }


    public boolean fallsThrough()
    {
        return false;
    }
    public boolean branches()
    {
        return true;
    }

    

}
