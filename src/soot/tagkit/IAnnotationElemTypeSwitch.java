package soot.tagkit;

import soot.util.Switch;

public interface IAnnotationElemTypeSwitch extends Switch{
	public abstract void caseAnnotationAnnotationElem(AnnotationAnnotationElem v);
	public abstract void caseAnnotationArrayElem(AnnotationArrayElem v);
	public abstract void caseAnnotationBooleanElem(AnnotationBooleanElem v);
	public abstract void caseAnnotationClassElem(AnnotationClassElem v);
	public abstract void caseAnnotationDoubleElem(AnnotationDoubleElem v);
	public abstract void caseAnnotationEnumElem(AnnotationEnumElem v);
	public abstract void caseAnnotationFloatElem(AnnotationFloatElem v);
	public abstract void caseAnnotationIntElem(AnnotationIntElem v);
	public abstract void caseAnnotationLongElem(AnnotationLongElem v);
	public abstract void caseAnnotationStringElem(AnnotationStringElem v);
    public abstract void defaultCase(Object object);

}
