package soot.validation;

import java.util.List;

import soot.Body;
import soot.UnitBox;

public class UnitBoxesValidator implements BodyValidator {
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
