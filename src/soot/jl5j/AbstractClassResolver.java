package soot.jl5j;

import soot.javaToJimple.*;
import soot.*;
import java.util.*;

public abstract class AbstractClassResolver {

    public void ext(AbstractClassResolver ext){
        this.ext = ext;
        if (ext.ext != null){
            throw new RuntimeException("Extensions created in wrong order.");
        }
        ext.base = this.base;
    }

    public AbstractClassResolver ext(){
        if (ext == null) return this;
        return ext;
    }

    private AbstractClassResolver ext = null;

    public void base(AbstractClassResolver base){
        this.base = base;
    }

    public AbstractClassResolver base(){
        return base;
    }
    
    private AbstractClassResolver base = this;

    protected SootClass sootClass;
    protected List references;
    
    public void addSourceFileTag(soot.SootClass sc){
        ext().addSourceFileTag(sc);
    }

    public void createClassDecl(polyglot.ast.ClassDecl cDecl){
        ext().createClassDecl(cDecl);
    }

    public void findReference(polyglot.ast.Node node){
        ext().findReference(node);
    }

    public void createClassBody(polyglot.ast.ClassBody classBody){
        ext().createClassBody(classBody);
    }

    public void addOuterClassThisRefField(polyglot.types.Type outerType){
        ext().addOuterClassThisRefField(outerType);
    }

    public void addOuterClassThisRefToInit(polyglot.types.Type outerType){
        ext().addOuterClassThisRefToInit(outerType);
    }

    public void addFinals(polyglot.types.LocalInstance li, ArrayList finalFields){
        ext().addFinals(li, finalFields);
    }

    public void createAnonClassDecl(polyglot.ast.New aNew){
        ext().createAnonClassDecl(aNew);
    }

    public void addModifiers(polyglot.types.Flags flags, polyglot.ast.ClassDecl cDecl){
        ext().addModifiers(flags, cDecl);
    }

    public soot.SootClass getSpecialInterfaceAnonClass(soot.SootClass addToClass){
        return ext().getSpecialInterfaceAnonClass(addToClass);
    }

    public void handleAssert(polyglot.ast.ClassBody cBody){
        ext().handleAssert(cBody);
    }

    public void createConstructorDecl(polyglot.ast.ConstructorDecl constructor){
        ext().createConstructorDecl(constructor);
    }
    
    public void createMethodDecl(polyglot.ast.MethodDecl method){
        ext().createMethodDecl(method);
    }

    public void finishProcedure(polyglot.ast.ProcedureDecl procedure, soot.SootMethod sootMethod){
        ext().finishProcedure(procedure, sootMethod);
    }

    public void handleFieldInits(){
        ext().handleFieldInits();
    }

    public void handleClassLiteral(polyglot.ast.ClassBody cBody){
        ext().handleClassLiteral(cBody);
    }

    public String getSimpleClassName(){
        return ext().getSimpleClassName();
    }

    public void createSource(polyglot.ast.SourceFile source){
        ext().createSource(source);
    }

    public void handleInnerClassTags(polyglot.ast.ClassBody classBody){
        ext().handleInnerClassTags(classBody);
    }

    public void addQualifierRefToInit(polyglot.types.Type type){
        ext().addQualifierRefToInit(type);
    }

    public void addProcedureToClass(soot.SootMethod method){
        ext.addProcedureToClass(method);
    }

    public void addConstValTag(polyglot.ast.FieldDecl field, soot.SootField sootField){
        ext().addConstValTag(field, sootField);
    }

    public void createFieldDecl(polyglot.ast.FieldDecl field){
        ext().createFieldDecl(field);
    }

    public String createName(polyglot.ast.ProcedureDecl procedure){
        return ext().createName(procedure);
    }

    public ArrayList createParameters(polyglot.ast.ProcedureDecl procedure){
        return ext().createParameters(procedure);
    }

    public ArrayList createExceptions(polyglot.ast.ProcedureDecl procedure){
        return ext().createExceptions(procedure);
    }

    public soot.SootMethod createSootMethod(String name, polyglot.types.Flags flags, polyglot.types.Type returnType, ArrayList parameters, ArrayList exceptions){
        return ext().createSootMethod(name, flags, returnType, parameters, exceptions);
    }

    public void createInitializer(polyglot.ast.Initializer initializer){
        ext().createInitializer(initializer);
    }

    public soot.SootMethod createSootConstructor(String name, polyglot.types.Flags flags, ArrayList parameters, ArrayList exceptions){
        return ext().createSootConstructor(name, flags, parameters, exceptions);
    }

}
