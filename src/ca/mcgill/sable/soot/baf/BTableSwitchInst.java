package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.*;

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

            for(int i = 0; i < targetBoxes.length; i++)
                unitBoxes.add(targetBoxes[i]);

            unitBoxes.add(defaultTargetBox);
            unitBoxes = Collections.unmodifiableList(unitBoxes);
        }
    }

    public Object clone() 
    {        
        List list = new ArrayList();
        for(int i =0; i< targetBoxes.length; i++) {
            list.add(targetBoxes[i].getUnit());
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

    public void setTargets(List targets)
    {
        for(int i = 0; i < targets.size(); i++)
            targetBoxes[i].setUnit((Unit) targets.get(i));
    }

    public UnitBox getTargetBox(int index)
    {
        return targetBoxes[index];
    }

    public List getTargets()
    {
        List targets = new ArrayList();

        for(int i = 0; i < targetBoxes.length; i++)
            targets.add(targetBoxes[i].getUnit());

        return targets;
    }

    public String getName() { return "tableswitch"; }

    protected String toString(boolean isBrief, Map unitToName, String indentation)
    {
        StringBuffer buffer = new StringBuffer();
        String endOfLine = (indentation.equals("")) ? " " : StringTools.lineSeparator;
        
        buffer.append(indentation + "tableswitch" + endOfLine);
            
        buffer.append(indentation + "{" + endOfLine);
        
        for(int i = lowIndex; i <= highIndex; i++)
        {
            buffer.append(indentation + "    case " + i + ": goto " + 
                (String) unitToName.get(getTarget(i - lowIndex)) + ";" 
                          + endOfLine);
        }

        buffer.append(indentation + "    default: goto " + (String) unitToName.get(getDefaultTarget()) + ";" + endOfLine);
        buffer.append(indentation + "}");

        return buffer.toString();
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
