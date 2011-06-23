package soot.tagkit;

public class AnnotationBooleanElem extends AnnotationElem{
    boolean value;

    public AnnotationBooleanElem(boolean v, char kind, String name){
        super(kind, name);
        this.value = v;
    }

    public String toString(){
        return super.toString()+" value: " +value;
    }

    public boolean getValue(){
        return value;
    }
}