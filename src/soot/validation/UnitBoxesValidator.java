package soot.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import soot.Body;
import soot.UnitBox;

public class UnitBoxesValidator implements BodyValidator {

	private static final Logger logger =LoggerFactory.getLogger(UnitBoxesValidator.class);
	public static UnitBoxesValidator INSTANCE;
	
	
	public static UnitBoxesValidator v() {
		if (INSTANCE == null)
		{
			INSTANCE = new UnitBoxesValidator();
		}
		return INSTANCE;
	}


	@Override
    /** Verifies that the UnitBoxes of this Body all point to a Unit contained within this body. */
	public void validate(Body body, List<ValidationException> exception) {
        for (UnitBox ub : body.getAllUnitBoxes())
        {
            if (!body.getUnits().contains(ub.getUnit()))
                throw new RuntimeException
                    ("Unitbox points outside unitChain! to unit : "+ub.getUnit()+" in "+body.getMethod());
        }
    }

	@Override
	public boolean isBasicValidator() {
		return true;
	}
}
