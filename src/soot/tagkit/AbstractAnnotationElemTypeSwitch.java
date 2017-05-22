package soot.tagkit;

public abstract class AbstractAnnotationElemTypeSwitch implements IAnnotationElemTypeSwitch {
	
    Object result;

	@Override
	public void caseAnnotationAnnotationElem(AnnotationAnnotationElem v) {
		defaultCase(v);

	}

	@Override
	public void caseAnnotationArrayElem(AnnotationArrayElem v) {
		defaultCase(v);

	}

	@Override
	public void caseAnnotationBooleanElem(AnnotationBooleanElem v) {
		defaultCase(v);

	}

	@Override
	public void caseAnnotationClassElem(AnnotationClassElem v) {
		defaultCase(v);

	}

	@Override
	public void caseAnnotationDoubleElem(AnnotationDoubleElem v) {
		defaultCase(v);

	}

	@Override
	public void caseAnnotationEnumElem(AnnotationEnumElem v) {
		defaultCase(v);

	}

	@Override
	public void caseAnnotationFloatElem(AnnotationFloatElem v) {
		defaultCase(v);

	}

	@Override
	public void caseAnnotationIntElem(AnnotationIntElem v) {
		defaultCase(v);

	}

	@Override
	public void caseAnnotationLongElem(AnnotationLongElem v) {
		defaultCase(v);

	}

	@Override
	public void caseAnnotationStringElem(AnnotationStringElem v) {
		defaultCase(v);

	}

	@Override
	public void defaultCase(Object object){
		
	}
	
    public Object getResult()
    {
        return result;
    }

    public void setResult(Object result)
    {
        this.result = result;
    }

}
