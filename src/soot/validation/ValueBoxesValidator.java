package soot.validation;

import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.Unit;
import soot.ValueBox;

public class ValueBoxesValidator implements BodyValidator {
	public static ValueBoxesValidator INSTANCE;
	
	
	public static ValueBoxesValidator v() {
		if (INSTANCE == null)
		{
			INSTANCE = new ValueBoxesValidator();
		}
		return INSTANCE;
	}


	@Override
	/** Verifies that a ValueBox is not used in more than one place. */
	public void validate(Body body, List<ValidationException> exception) {
        List<ValueBox> l = body.getUseAndDefBoxes();
        for( int i = 0; i < l.size(); i++ ) {
            for( int j = 0; j < l.size(); j++ ) {
                if( i == j ) continue;
                if( l.get(i) == l.get(j) ) {
                    System.err.println("Aliased value box : "+l.get(i)+" in "+body.getMethod());
                    for( Iterator<Unit> uIt = body.getUnits().iterator(); uIt.hasNext(); ) {
                        final Unit u = uIt.next();
                        System.err.println(""+u);
                    }
                    exception.add(new ValidationException(l.get(i), "Aliased value box : "+l.get(i)+" in "+body.getMethod()));
                }
            }
        }
    }

	@Override
	public boolean isBasicValidator() {
		return false;
	}
}
