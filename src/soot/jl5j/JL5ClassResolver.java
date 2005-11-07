package soot.jl5j;

import polyglot.ext.jl5.ast.*;
import soot.tagkit.*;
import java.util.*;
import polyglot.ast.*;

public class JL5ClassResolver extends AbstractClassResolver {


    private VisibilityAnnotationTag createRuntimeVisibleAnnotationTag(List annots){
        VisibilityAnnotationTag tag = new VisibilityAnnotationTag(AnnotationConstants.RUNTIME_VISIBLE);
        addAnnotations(tag, annots);
        return tag;
    }

    private VisibilityAnnotationTag createRuntimeInvisibleAnnotationTag(List annots){
        VisibilityAnnotationTag tag = new VisibilityAnnotationTag(AnnotationConstants.RUNTIME_INVISIBLE);
        addAnnotations(tag, annots);
        return tag;
    }

    private void addAnnotations(VisibilityAnnotationTag tag, List annots){
        for (Iterator it = annots.iterator(); it.hasNext(); ){
            NormalAnnotationElem ae = (NormalAnnotationElem)it.next();
            AnnotationTag aTag = new AnnotationTag(ae.typeName().type().toClass().fullName(), ae.elements().size());
            aTag.setElems(createElemTags(ae.elements()));
            tag.addAnnotation(aTag);
        }
    }
    
    private ArrayList createElemTags(List elements){
        ArrayList list = new ArrayList();
        for (Iterator it = elements.iterator(); it.hasNext(); ){
            ElementValuePair elemValue = (ElementValuePair)it.next();
            String name = elemValue.name();
            Expr value = elemValue.value();
            if (value instanceof IntLit){
                int constVal = ((Integer)((IntLit)value).constantValue()).intValue();
                AnnotationIntElem elem = new AnnotationIntElem(constVal, 'I', name);
                list.add(elem);
            }
        }
        return list;
    }
    
    public void createClassDecl(polyglot.ast.ClassDecl cDecl){
        ext().createClassDecl(cDecl);
        List runtimeAnnots = ((polyglot.ext.jl5.ast.JL5ClassDecl)cDecl).runtimeAnnotations();
        List classAnnots = ((polyglot.ext.jl5.ast.JL5ClassDecl)cDecl).classAnnotations();
        if (!runtimeAnnots.isEmpty()){
            sootClass.addTag(createRuntimeVisibleAnnotationTag(runtimeAnnots));
        }
        if (!classAnnots.isEmpty()){
            sootClass.addTag(createRuntimeInvisibleAnnotationTag(classAnnots));
        }
    }

    public JL5ClassResolver(soot.SootClass sootClass, List refs){
        this.sootClass = sootClass;
        this.references = references;
    }
}
