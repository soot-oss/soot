package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.*;

public class BLookupSwitchInst extends AbstractInst implements LookupSwitchInst
{
    UnitBox defaultTargetBox;
    List lookupValues;
    UnitBox[] targetBoxes;
    List unitBoxes;

    public BLookupSwitchInst(Unit defaultTarget, 
                             List lookupValues, List targets)
    {
        this.defaultTargetBox = Baf.v().newInstBox(defaultTarget); 

        this.targetBoxes = new UnitBox[targets.size()];

        for(int i = 0; i < targetBoxes.length; i++)
            this.targetBoxes[i] = Baf.v().newInstBox((Unit) targets.get(i));

        this.lookupValues = new ArrayList();
        this.lookupValues.addAll(lookupValues);

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

        
        return new  BLookupSwitchInst(defaultTargetBox.getUnit(), lookupValues, list);
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

    public void setLookupValues(List lookupValues)
    {
        this.lookupValues = new ArrayList();
        this.lookupValues.addAll(lookupValues);
    }

    public void setLookupValue(int index, int value)
    {
        this.lookupValues.set(index, new Integer(value));
    }

    public int getLookupValue(int index)
    {
        return ((Integer) lookupValues.get(index)).intValue();
    }

    public  List getLookupValues()
    {
        return Collections.unmodifiableList(lookupValues);
    }

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

    public String getName() { return "lookupswitch"; }

    protected String toString(boolean isBrief, Map unitToName, String indentation)
    {
        StringBuffer buffer = new StringBuffer();
        String endOfLine = (indentation.equals("")) ? " " : StringTools.lineSeparator;
        
        buffer.append(indentation + "lookupswitch" + endOfLine);
            
        buffer.append(indentation + "{" + endOfLine);
        
        for(int i = 0; i < lookupValues.size(); i++)
        {
            buffer.append(indentation + "    case " + lookupValues.get(i) + ": goto " + 
                (String) unitToName.get(getTarget(i)) + ";" + endOfLine);
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
        ((InstSwitch) sw).caseLookupSwitchInst(this);
    }
}
