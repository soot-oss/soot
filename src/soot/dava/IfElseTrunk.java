package soot.dava;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.util.*;

public class IfElseTrunk extends AbstractTrunk
{
    Trunk ifTrunk;
    Trunk elseTrunk;
    ValueBox conditionBox;
    
    IfElseTrunk(ConditionExpr e, Trunk ifTrunk, Trunk elseTrunk)
    {
        conditionBox = Jimple.v().newConditionExprBox(e);
        
        this.ifTrunk = ifTrunk;
        this.elseTrunk = elseTrunk;
    }
    
    public Trunk getIf()
    {
        return ifTrunk;
    }
    
    public void setIf(Trunk t)
    {
        ifTrunk = t;
    }
    
    public Trunk getElse()
    {
        return elseTrunk;
    }
    
    public void setElse(Trunk t)
    {
        elseTrunk = t;
    }
    
    public Value getCondition()
    {
        return conditionBox.getValue();
    }
    
    public void setCondition(Value v)
    {
        conditionBox.setValue(v);
    }
    
    public List getChildren()
    {
        List l = new ArrayList();
        
        l.add(ifTrunk);
        l.add(elseTrunk);
        
        return Collections.unmodifiableList(l);
    }
    
    public Object clone()
    {
        return new IfElseTrunk((ConditionExpr) getCondition().clone(), 
            (Trunk) getIf().clone(),
            (Trunk) getElse().clone());
    }
        
    protected String toString(boolean isBrief, Map stmtToName, String indentation)
    {
        String endOfLine = (indentation.equals("")) ? " " : StringTools.lineSeparator;
        StringBuffer b = new StringBuffer();
        
        b.append(indentation + "if(" + 
            ((isBrief) ? ((ToBriefString) getCondition()).toBriefString() : getCondition().toString()) + ")" + endOfLine);
            
        b.append(indentation + "{" + endOfLine);
        b.append(((isBrief) ? getIf().toBriefString(stmtToName, indentation + "    ") : 
                           getIf().toString(stmtToName, indentation + "    ")) + endOfLine);
        b.append(indentation + "}" + endOfLine);
        b.append(indentation + "else { " + endOfLine);
        b.append(((isBrief) ? getElse().toBriefString(stmtToName, indentation + "    ") : 
                           getElse().toString(stmtToName, indentation + "    ")) + endOfLine);
        b.append(indentation + "}" + endOfLine);

        return b.toString();
    }

}




