/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

package soot.javaToJimple;
import soot.*;

import java.util.*;

import polyglot.ast.ClassDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.types.ConstructorInstance;
import polyglot.util.IdentityKey;

public class InitialResolver implements IInitialResolver {

    private polyglot.ast.Node astNode;  // source node
    private polyglot.frontend.Compiler compiler; 
    private BiMap anonClassMap;   // maps New to SootClass (name)
    private HashMap<IdentityKey, String> anonTypeMap;    //maps polyglot types to soot types
    private BiMap localClassMap;  // maps LocalClassDecl to SootClass (name)
    private HashMap<IdentityKey, String> localTypeMap;   // maps polyglot types to soot types
    private int privateAccessCounter = 0; // global for whole program because
                                          // the methods created are static 
    private HashMap<IdentityKey, AnonLocalClassInfo> finalLocalInfo; // new or lcd mapped to list of final locals avail in current meth and the whether its static
    private HashMap<String, Node> sootNameToAST = null;
    private ArrayList hasOuterRefInInit; // list of sootclass types that need an outer class this param in for init
   
    private HashMap<String, String> classToSourceMap;
    private HashMap<SootClass, SootClass> specialAnonMap;
    private HashMap<IdentityKey, SootMethod> privateFieldGetAccessMap;
    private HashMap<IdentityKey, SootMethod> privateFieldSetAccessMap;
    private HashMap<IdentityKey, SootMethod> privateMethodGetAccessMap;
    private ArrayList<String> interfacesList;
    private ArrayList<Node> cCallList;
    
    private HashMap<New, ConstructorInstance> anonConstructorMap;

    public void addToAnonConstructorMap(polyglot.ast.New anonNew, polyglot.types.ConstructorInstance ci){
        if (anonConstructorMap == null){
            anonConstructorMap = new HashMap<New, ConstructorInstance>();
        }
        anonConstructorMap.put(anonNew, ci);
    }
   
    public polyglot.types.ConstructorInstance getConstructorForAnon(polyglot.ast.New anonNew){
        if (anonConstructorMap == null) return null;
        return anonConstructorMap.get(anonNew);
    }

    private FastHierarchy hierarchy;

    private AbstractJBBFactory jbbFactory = new JimpleBodyBuilderFactory();

    public void setJBBFactory(AbstractJBBFactory jbbFactory){
        this.jbbFactory = jbbFactory;
    }
    
    public AbstractJBBFactory getJBBFactory(){
        return jbbFactory;
    }
    
    /**
     * returns true if there is an AST avail for given soot class
     */
    public boolean hasASTForSootName(String name){
       if (sootNameToAST == null) return false;
       if (sootNameToAST.containsKey(name)) return true;
       return false;
    }
    
    /**
     * sets AST for given soot class if possible
     */
    public void setASTForSootName(String name){
        if (!hasASTForSootName(name)) {
            throw new RuntimeException("Can only set AST for name if it exists. You should probably not be calling this method unless you know what you're doing!");
        }
        setAst(sootNameToAST.get(name));
    }
   
    public InitialResolver(soot.Singletons.Global g){}
    public static InitialResolver v() {
        return soot.G.v().soot_javaToJimple_InitialResolver();
    }
    

    
    /**
     * Invokes polyglot and gets the AST for the source given in fullPath
     */
    public void formAst(String fullPath, List<String> locations, String className){
    
        JavaToJimple jtj = new JavaToJimple();
        polyglot.frontend.ExtensionInfo extInfo = jtj.initExtInfo(fullPath, locations);
        // only have one compiler - for memory issues
        if (compiler == null) {
            compiler = new polyglot.frontend.Compiler(extInfo);
        }
        // build ast
        astNode = jtj.compile(compiler, fullPath, extInfo);
 
        resolveAST();


    }

    /**
     * if you have a special AST set it here then call resolveFormJavaFile
     * on the soot class
     */ 
    public void setAst(polyglot.ast.Node ast) {
        astNode = ast;
    }
    
    /*
     * March 2nd, 2006 Nomair
     * Is it okkay get the ast and send it to the ASTMetrics package????
     */
    public polyglot.ast.Node getAst(){
    	return astNode;
    }

    private void makeASTMap() {
        ClassDeclFinder finder = new ClassDeclFinder();
        astNode.visit(finder);
        Iterator<ClassDecl> it = finder.declsFound().iterator();
        while (it.hasNext()){
            polyglot.ast.ClassDecl decl = it.next();
            polyglot.types.ClassType type = decl.type();
            if (type.flags().isInterface()){
                if (interfacesList ==  null){
                    interfacesList = new ArrayList<String>();
                }
                interfacesList.add(Util.getSootType(type).toString());
            }
            addNameToAST(Util.getSootType(type).toString());
        }
    }
    
    /**
     * add name to AST to map - used mostly for inner and non public
     * top-level classes
     */
    protected void addNameToAST(String name){
        if (sootNameToAST == null){
            sootNameToAST = new HashMap<String, Node>();
        }   
        sootNameToAST.put(name, astNode);
    }
   
    public void resolveAST(){
        buildInnerClassInfo();
        if (astNode instanceof polyglot.ast.SourceFile) {
            createClassToSourceMap((polyglot.ast.SourceFile)astNode);
        }
    }
    
    // resolves all types and deals with .class literals and asserts
    public Dependencies resolveFromJavaFile(soot.SootClass sc) {
        Dependencies dependencies = new Dependencies();
        //conservatively load all to signatures
        ClassResolver cr = new ClassResolver(sc, dependencies.typesToSignature);
        
        // create class to source map first 
        // create source file
        if (astNode instanceof polyglot.ast.SourceFile) {
            cr.createSource((polyglot.ast.SourceFile)astNode);
        }
        
        cr.addSourceFileTag(sc);
        
        makeASTMap();
        
        
        return dependencies;
    }
    

    private void createClassToSourceMap(polyglot.ast.SourceFile src){
       
        String srcName = src.source().path();
        String srcFileName = null;
        if (src.package_() != null){
            String slashedPkg = soot.util.StringTools.replaceAll(src.package_().package_().fullName(), ".", System.getProperty("file.separator"));
            srcFileName = srcName.substring(srcName.lastIndexOf(slashedPkg));
        }
        else {
            srcFileName = srcName.substring(srcName.lastIndexOf(System.getProperty("file.separator"))+1);
        }

        ArrayList list = new ArrayList();
        Iterator it = src.decls().iterator();
        while (it.hasNext()){
            polyglot.ast.ClassDecl nextDecl = (polyglot.ast.ClassDecl)it.next();
            addToClassToSourceMap(Util.getSootType(nextDecl.type()).toString(), srcFileName); 
        }

    }

    private void createLocalAndAnonClassNames(ArrayList<Node> anonBodyList, ArrayList<Node> localClassDeclList){
        Iterator<Node> anonBodyIt = anonBodyList.iterator();
        while (anonBodyIt.hasNext()){
            createAnonClassName((polyglot.ast.New)anonBodyIt.next());
        }
        Iterator<Node> localClassDeclIt = localClassDeclList.iterator();
        while (localClassDeclIt.hasNext()){
            createLocalClassName((polyglot.ast.LocalClassDecl)localClassDeclIt.next());
        }
    }

    protected int getNextAnonNum(){
        if (anonTypeMap == null) return 1;
        else return anonTypeMap.size()+1;
    }
    
    private void createAnonClassName(polyglot.ast.New nextNew){
        // maybe this anon has already been resolved
        if (anonClassMap == null){
            anonClassMap = new BiMap();
        }
        if (anonTypeMap == null){
            anonTypeMap = new HashMap<IdentityKey, String>();
        }
        if (!anonClassMap.containsKey(nextNew)){
            int nextAvailNum = 1;
            polyglot.types.ClassType outerToMatch = nextNew.anonType().outer();
            while (outerToMatch.isNested()){
                outerToMatch = outerToMatch.outer();
            }

            if (!anonTypeMap.isEmpty()){
                Iterator<IdentityKey> matchIt = anonTypeMap.keySet().iterator();
                while (matchIt.hasNext()){
                    polyglot.types.ClassType pType = (polyglot.types.ClassType)matchIt.next().object();
                    polyglot.types.ClassType outerMatch = pType.outer();
                    while (outerMatch.isNested()){
                        outerMatch = outerMatch.outer();
                    }
                    if (outerMatch.equals(outerToMatch)){
                        int numFound = getAnonClassNum(anonTypeMap.get(new polyglot.util.IdentityKey(pType)));
                        if (numFound >= nextAvailNum){
                            nextAvailNum = numFound+1;
                        }
                    }
                }
            }
            
            String realName = outerToMatch.fullName()+"$"+nextAvailNum;
            anonClassMap.put(nextNew, realName);
            anonTypeMap.put(new polyglot.util.IdentityKey(nextNew.anonType()), realName);
            addNameToAST(realName);
            
        }
    }
    
    private void createLocalClassName(polyglot.ast.LocalClassDecl lcd){
        // maybe this localdecl has already been resolved 
        if (localClassMap == null){
            localClassMap = new BiMap();
        }
        if (localTypeMap == null){
            localTypeMap = new HashMap<IdentityKey, String>();
        }
        
        if (!localClassMap.containsKey(lcd)){
            int nextAvailNum = 1;
            polyglot.types.ClassType outerToMatch = lcd.decl().type().outer();
            while (outerToMatch.isNested()){
                outerToMatch = outerToMatch.outer();
            }

            if (!localTypeMap.isEmpty()){
                Iterator<IdentityKey> matchIt = localTypeMap.keySet().iterator();
                while (matchIt.hasNext()){
                    polyglot.types.ClassType pType = (polyglot.types.ClassType)matchIt.next().object();
                    polyglot.types.ClassType outerMatch = pType.outer();
                    while (outerMatch.isNested()){
                        outerMatch = outerMatch.outer();
                    }
                    if (outerMatch.equals(outerToMatch)){
                        int numFound = getLocalClassNum(localTypeMap.get(new polyglot.util.IdentityKey(pType)), lcd.decl().name());
                        if (numFound >= nextAvailNum){
                            nextAvailNum = numFound+1;
                        }
                    }
                }
            }

            String realName = outerToMatch.fullName()+"$"+nextAvailNum+lcd.decl().name();
            localClassMap.put(lcd, realName);
            localTypeMap.put(new polyglot.util.IdentityKey(lcd.decl().type()), realName);
            addNameToAST(realName);
        }
    }

    private static final int NO_MATCH = 0;
    
    private int getLocalClassNum(String realName, String simpleName){
        // a local inner class is named outer$NsimpleName where outer 
        // is the very outer most class
        int dIndex = realName.indexOf("$");
        int nIndex = realName.indexOf(simpleName, dIndex);
        if (nIndex == -1) return NO_MATCH;
        if (dIndex == -1) {
            throw new RuntimeException("Matching an incorrectly named local inner class: "+realName);
        }
        String numString = realName.substring(dIndex+1, nIndex);
        for (int i = 0; i < numString.length(); i++){
            if (!Character.isDigit(numString.charAt(i))) return NO_MATCH;
        }
        return (new Integer(numString)).intValue();
    }
    
    private int getAnonClassNum(String realName){
        // a anon inner class is named outer$N where outer 
        // is the very outer most class
        int dIndex = realName.indexOf("$");
        if (dIndex == -1) {
            throw new RuntimeException("Matching an incorrectly named anon inner class: "+realName);
        }
        return (new Integer(realName.substring(dIndex+1))).intValue();
    }
    

    /**
     * ClassToSourceMap is for classes whos names don't match the source file
     * name - ex: multiple top level classes in a single file
     */
    private void addToClassToSourceMap(String className, String sourceName) {
            
        if (classToSourceMap == null){
            classToSourceMap = new HashMap<String, String>();
        }
        classToSourceMap.put(className, sourceName);
    }
    

    public boolean hasClassInnerTag(soot.SootClass sc, String innerName){
        Iterator it = sc.getTags().iterator();
        while (it.hasNext()){
            soot.tagkit.Tag t = (soot.tagkit.Tag)it.next();
            if (t instanceof soot.tagkit.InnerClassTag) {
                soot.tagkit.InnerClassTag tag = (soot.tagkit.InnerClassTag)t;
                if (tag.getInnerClass().equals(innerName)) return true;
            }
        }
        return false;
    }
   
    private void buildInnerClassInfo(){
        InnerClassInfoFinder icif = new InnerClassInfoFinder();
        astNode.visit(icif);
        createLocalAndAnonClassNames(icif.anonBodyList(), icif.localClassDeclList());
        buildFinalLocalMap(icif.memberList());
    }
    
    private void buildFinalLocalMap(ArrayList<Node> memberList){
        Iterator<Node> it = memberList.iterator();
        while (it.hasNext()){
            handleFinalLocals((polyglot.ast.ClassMember)it.next());
        }
    }
    
    private void handleFinalLocals(polyglot.ast.ClassMember member){
        MethodFinalsChecker mfc = new MethodFinalsChecker();
        member.visit(mfc);
        //System.out.println("member: "+member);
        //System.out.println("mcf final locals avail: "+mfc.finalLocals());
        //System.out.println("mcf locals used: "+mfc.typeToLocalsUsed());
        //System.out.println("mfc inners: "+mfc.inners());
        if (cCallList == null){
            cCallList = new ArrayList<Node>();
        }
        cCallList.addAll(mfc.ccallList());
        //System.out.println("cCallList: "+cCallList);
        AnonLocalClassInfo alci = new AnonLocalClassInfo();
        if (member instanceof polyglot.ast.ProcedureDecl){
            polyglot.ast.ProcedureDecl procedure = (polyglot.ast.ProcedureDecl)member;
            // not sure if this will break deep nesting
            alci.finalLocalsAvail(mfc.finalLocals());
            if (procedure.flags().isStatic()){
                alci.inStaticMethod(true);
            }
        }
        else if (member instanceof polyglot.ast.FieldDecl){
            alci.finalLocalsAvail(new ArrayList<IdentityKey>());
            if (((polyglot.ast.FieldDecl)member).flags().isStatic()){
                alci.inStaticMethod(true);
            }
        }
        else if (member instanceof polyglot.ast.Initializer){
            // for now don't make final locals avail in init blocks
            // need to test this
            alci.finalLocalsAvail(mfc.finalLocals());
            if (((polyglot.ast.Initializer)member).flags().isStatic()){
                alci.inStaticMethod(true);
            }
        }
        if (finalLocalInfo == null){
            finalLocalInfo = new HashMap<IdentityKey, AnonLocalClassInfo>();
        }
        Iterator<IdentityKey> it = mfc.inners().iterator();
        while (it.hasNext()){
            
            polyglot.types.ClassType cType = (polyglot.types.ClassType)it.next().object();
            // do the comparison about locals avail and locals used here
            HashMap<IdentityKey, ArrayList<IdentityKey>> typeToLocalUsed = mfc.typeToLocalsUsed();
            ArrayList<IdentityKey> localsUsed = new ArrayList<IdentityKey>();
            if (typeToLocalUsed.containsKey(new polyglot.util.IdentityKey(cType))){
                ArrayList localsNeeded = typeToLocalUsed.get(new polyglot.util.IdentityKey(cType));
                Iterator usesIt = localsNeeded.iterator();
                while (usesIt.hasNext()){
                    polyglot.types.LocalInstance li = (polyglot.types.LocalInstance)((polyglot.util.IdentityKey)usesIt.next()).object();
                    if (alci.finalLocalsAvail().contains(new polyglot.util.IdentityKey(li))){
                        localsUsed.add(new polyglot.util.IdentityKey(li));
                    }
                }
            }
                
            
            AnonLocalClassInfo info = new AnonLocalClassInfo();
            info.inStaticMethod(alci.inStaticMethod());
            info.finalLocalsAvail(localsUsed);
            if (!finalLocalInfo.containsKey(new polyglot.util.IdentityKey(cType))){
                finalLocalInfo.put(new polyglot.util.IdentityKey(cType), info);
            }
        }
    }
    
    public boolean isAnonInCCall(polyglot.types.ClassType anonType){
        //System.out.println("checking type: "+anonType);
        Iterator<Node> it = cCallList.iterator();
        while (it.hasNext()){
            polyglot.ast.ConstructorCall cCall = (polyglot.ast.ConstructorCall)it.next();
            //System.out.println("cCall params: "+cCall.arguments());
            Iterator argsIt = cCall.arguments().iterator();
            while (argsIt.hasNext()){
                Object next = argsIt.next();
                if (next instanceof polyglot.ast.New && ((polyglot.ast.New)next).anonType() != null){
                    //System.out.println("comparing: "+((polyglot.ast.New)next).anonType());
                    if (((polyglot.ast.New)next).anonType().equals(anonType)) return true;
                }
            }
        }
        return false;
    }
    
    public BiMap getAnonClassMap(){
        return anonClassMap;
    }

    public BiMap getLocalClassMap(){
        return localClassMap;
    }
    
    public HashMap<IdentityKey, String> getAnonTypeMap(){
        return anonTypeMap;
    }

    public HashMap<IdentityKey, String> getLocalTypeMap(){
        return localTypeMap;
    }
  
    public HashMap<IdentityKey, AnonLocalClassInfo> finalLocalInfo(){
        return finalLocalInfo;
    }

    public int getNextPrivateAccessCounter(){
        int res = privateAccessCounter;
        privateAccessCounter++;
        return res;
    }

    public ArrayList getHasOuterRefInInit(){
        return hasOuterRefInInit;
    }

    public void setHasOuterRefInInit(ArrayList list){
        hasOuterRefInInit = list;
    }

    public HashMap<SootClass, SootClass> specialAnonMap(){
        return specialAnonMap;
    }

    public void setSpecialAnonMap(HashMap<SootClass, SootClass> map){
        specialAnonMap = map;
    }

    public void hierarchy(soot.FastHierarchy fh){
        hierarchy = fh;
    }
    
    public soot.FastHierarchy hierarchy(){
        return hierarchy;
    }

    private HashMap<SootClass, InnerClassInfo> innerClassInfoMap;
   
    public HashMap<SootClass, InnerClassInfo> getInnerClassInfoMap(){
        return innerClassInfoMap;
    }
 
    public void setInnerClassInfoMap(HashMap<SootClass, InnerClassInfo> map){
        innerClassInfoMap = map;
    }
 
    protected HashMap<String, String> classToSourceMap(){
        return classToSourceMap;
    }

    public void addToPrivateFieldGetAccessMap(polyglot.ast.Field field, soot.SootMethod meth){
        if (privateFieldGetAccessMap == null){
            privateFieldGetAccessMap = new HashMap<IdentityKey, SootMethod>();
        }
        privateFieldGetAccessMap.put(new polyglot.util.IdentityKey(field.fieldInstance()), meth);
    }
    
    public HashMap<IdentityKey, SootMethod> getPrivateFieldGetAccessMap(){
        return privateFieldGetAccessMap;
    }
    
    public void addToPrivateFieldSetAccessMap(polyglot.ast.Field field, soot.SootMethod meth){
        if (privateFieldSetAccessMap == null){
            privateFieldSetAccessMap = new HashMap<IdentityKey, SootMethod>();
        }
        privateFieldSetAccessMap.put(new polyglot.util.IdentityKey(field.fieldInstance()), meth);
    }
    
    public HashMap<IdentityKey, SootMethod> getPrivateFieldSetAccessMap(){
        return privateFieldSetAccessMap;
    }
    
    public void addToPrivateMethodGetAccessMap(polyglot.ast.Call call, soot.SootMethod meth){
        if (privateMethodGetAccessMap == null){
            privateMethodGetAccessMap = new HashMap<IdentityKey, SootMethod>();
        }
        privateMethodGetAccessMap.put(new polyglot.util.IdentityKey(call.methodInstance()), meth);
    }
    
    public HashMap<IdentityKey, SootMethod> getPrivateMethodGetAccessMap(){
        return privateMethodGetAccessMap;
    }

    public ArrayList<String> getInterfacesList() {
        return interfacesList;
    } 
}

