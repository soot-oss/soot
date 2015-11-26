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

package soot.jimple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.Local;
import soot.RefType;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.jimple.validation.FieldRefValidator;
import soot.jimple.validation.IdentityStatementsValidator;
import soot.jimple.validation.IdentityValidator;
import soot.jimple.validation.InvokeArgumentValidator;
import soot.jimple.validation.JimpleTrapValidator;
import soot.jimple.validation.NewValidator;
import soot.jimple.validation.ReturnStatementsValidator;
import soot.jimple.validation.TypesValidator;
import soot.options.Options;
import soot.validation.BodyValidator;
import soot.validation.ValidationException;

/** Implementation of the Body class for the Jimple IR. */
public class JimpleBody extends StmtBody
{
	private static BodyValidator[] validators;
	
	/**
	 * Returns an array containing some validators in order to validate the JimpleBody
	 * @return the array containing validators
	 */
	private synchronized static BodyValidator[] getValidators() {
		if (validators == null)
		{
			validators = new BodyValidator[] {
				IdentityStatementsValidator.v(),
				TypesValidator.v(),
				ReturnStatementsValidator.v(),
				InvokeArgumentValidator.v(),
 				FieldRefValidator.v(),
 				NewValidator.v(),
 				JimpleTrapValidator.v(),
 				IdentityValidator.v()
				//InvokeValidator.v()
			};
		}
		return validators;
	};
	
    /**
        Construct an empty JimpleBody 
     **/
    
    JimpleBody(SootMethod m)
    {
        super(m);
    }

    /**
       Construct an extremely empty JimpleBody, for parsing into.
    **/

    JimpleBody() 
    {
    }

    /** Clones the current body, making deep copies of the contents. */
    public Object clone()
    {
        Body b = new JimpleBody(getMethod());
        b.importBodyContentsFrom(this);
        return b;
    }



    /** Make sure that the JimpleBody is well formed.  If not, throw
     *  an exception.  Right now, performs only a handful of checks.  
     */
    public void validate()
    {
        final List<ValidationException> exceptionList = new ArrayList<ValidationException>();
        validate(exceptionList);
        if (!exceptionList.isEmpty())
        	throw exceptionList.get(0);
    }
    
    /**
     * Validates the jimple body and saves a list of all validation errors 
     * @param exceptionList the list of validation errors
     */
    public void validate(List<ValidationException> exceptionList) {
        super.validate(exceptionList);
        final boolean runAllValidators = Options.v().debug() || Options.v().validate();
    	for (BodyValidator validator : getValidators()) {
    		if (!validator.isBasicValidator() && !runAllValidators)
    			continue;
    		validator.validate(this, exceptionList);
    	}
    }
    
    public void validateIdentityStatements() {
    	runValidation(IdentityStatementsValidator.v());
    }
    
    
    /** Inserts usual statements for handling this & parameters into body. */
    public void insertIdentityStmts()
    {
    	Unit lastUnit = null;
    	
        //add this-ref before everything else
        if (!getMethod().isStatic())
        {
        	Local l = Jimple.v().newLocal("this", 
        			RefType.v(getMethod().getDeclaringClass()));
        	Stmt s = Jimple.v().newIdentityStmt(l, Jimple.v().newThisRef((RefType)l.getType()));
        	
        	getLocals().add(l);
        	getUnits().addFirst(s);
            lastUnit = s;
        }
        
        int i = 0;
        for (Type t : getMethod().getParameterTypes()) {
            Local l = Jimple.v().newLocal("parameter"+i, t);
            Stmt s = Jimple.v().newIdentityStmt(l, Jimple.v().newParameterRef(l.getType(), i));
            
            getLocals().add(l);
            if (lastUnit == null)
            	getUnits().addFirst(s);
            else
            	getUnits().insertAfter(s, lastUnit);
            
            lastUnit = s;
            i++;
        }
    }

    /** Returns the first non-identity stmt in this body. */
    public Stmt getFirstNonIdentityStmt()
    {
        Iterator<Unit> it = getUnits().iterator();
        Object o = null;
        while (it.hasNext())
            if (!((o = it.next()) instanceof IdentityStmt))
                break;
        if (o == null)
            throw new RuntimeException("no non-id statements!");
        return (Stmt)o;
    }
}



