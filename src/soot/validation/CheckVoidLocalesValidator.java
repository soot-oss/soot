package soot.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.Local;
import soot.VoidType;
import soot.util.Chain;

public class CheckVoidLocalesValidator implements BodyValidator {

	private static final Logger logger =LoggerFactory.getLogger(CheckVoidLocalesValidator.class);
	public static CheckVoidLocalesValidator INSTANCE;
	
	
	public static CheckVoidLocalesValidator v() {
		if (INSTANCE == null)
		{
			INSTANCE = new CheckVoidLocalesValidator();
		}
		return INSTANCE;
	}


	@Override
	public void validate(Body body, List<ValidationException> exception) {
		Chain<Local> locals=body.getLocals();

		Iterator<Local> it=locals.iterator();
		while(it.hasNext()) {
		    Local l=it.next();
		    if(l.getType() instanceof VoidType) 
			exception.add(new ValidationException(l, "Local "+l+" in "+body.getMethod()+" defined with void type"));
		}
   }

    
	@Override
	public boolean isBasicValidator() {
		return false;
	}
}
