package soot.jimple.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Jimple;

@SuppressWarnings("serial")
abstract class AbstractTableStmt extends AbstractStmt {

    final UnitBox defaultTargetBox;
    
    final ValueBox keyBox;

    final List<UnitBox> stmtBoxes;
    
    final protected UnitBox[] targetBoxes;
    
    protected AbstractTableStmt(ValueBox keyBox, UnitBox defaultTargetBox, UnitBox ... targetBoxes) {
    	this.keyBox = keyBox;
    	this.defaultTargetBox = defaultTargetBox;
    	this.targetBoxes = targetBoxes;
    	
        // Build up stmtBoxes
        List<UnitBox> list = new ArrayList<UnitBox>();
        stmtBoxes = Collections.unmodifiableList(list);
        
        Collections.addAll(list, targetBoxes);
        list.add(defaultTargetBox);
    }

    final public Unit getDefaultTarget()
    {
        return defaultTargetBox.getUnit();
    }

    final public void setDefaultTarget(Unit defaultTarget)
    {
        defaultTargetBox.setUnit(defaultTarget);
    }

    final public UnitBox getDefaultTargetBox()
    {
        return defaultTargetBox;
    }

    final public Value getKey()
    {
        return keyBox.getValue();
    }

    final public void setKey(Value key)
    {
        keyBox.setValue(key);
    }

    final public ValueBox getKeyBox()
    {
        return keyBox;
    }    
    
    final public List<ValueBox> getUseBoxes()
    {
        List<ValueBox> list = new ArrayList<ValueBox>();

        list.addAll(keyBox.getValue().getUseBoxes());
        list.add(keyBox);

        return list;
    }
    
    final public int getTargetCount()
    {
        return targetBoxes.length;
    }
    
    final public Unit getTarget(int index)
    {
        return targetBoxes[index].getUnit();
    }

    final public UnitBox getTargetBox(int index)
    {
        return targetBoxes[index];
    }

    final public void setTarget(int index, Unit target)
    {
        targetBoxes[index].setUnit(target);
    }
    
    final public List<Unit> getTargets()
    {
        List<Unit> targets = new ArrayList<Unit>();

        for (UnitBox element : targetBoxes)
			targets.add(element.getUnit());

        return targets;
    }
    
    final public void setTargets(List<Unit> targets)
    {
        for(int i = 0; i < targets.size(); i++)
            targetBoxes[i].setUnit(targets.get(i));
    }
    
    final public void setTargets(Unit[] targets)
    {
        for(int i = 0; i < targets.length; i++)
            targetBoxes[i].setUnit(targets[i]);
    }

    final public List<UnitBox> getUnitBoxes()
    {
        return stmtBoxes;
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
