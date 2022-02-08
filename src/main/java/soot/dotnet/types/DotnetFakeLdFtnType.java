package soot.dotnet.types;

import soot.*;
import soot.javaToJimple.IInitialResolver;
import soot.jimple.Jimple;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper to generate the Fake.LdFtn Sootclass and the given method
 */
public class DotnetFakeLdFtnType {

    // Define static fake method
    private static final String FAKE_LDFTN_METHOD_NAME = "FakeLdFtn";

    /**
     * If LdFtn instruction, rewrite and resolve fake Soot class
     * @param sootClass
     * @return
     */
    public static IInitialResolver.Dependencies resolve(SootClass sootClass) {
        IInitialResolver.Dependencies deps = new IInitialResolver.Dependencies();
        SootClass superClass = SootResolver.v().makeClassRef(DotnetBasicTypes.SYSTEM_OBJECT);
        deps.typesToHierarchy.add(superClass.getType());
        sootClass.setSuperclass(superClass);
        int classModifier = 0;
        classModifier |= Modifier.PUBLIC;
        classModifier |= Modifier.STATIC;
        sootClass.setModifiers(classModifier);

        // add fake method
        int modifier = 0;
        modifier |= Modifier.PUBLIC;
        modifier |= Modifier.STATIC;
        modifier |= Modifier.NATIVE;

        SootMethod m = Scene.v().makeSootMethod(FAKE_LDFTN_METHOD_NAME, new ArrayList<>(), DotnetTypeFactory.toSootType(DotnetBasicTypes.SYSTEM_INTPTR), modifier);
        sootClass.addMethod(m);

        return deps;
    }

    /**
     * Make fake method for CIL instruction LdFtn, which returns an IntPtr
     * Workaround for difference in CIL and Java bytecode
     * @return
     */
    public static Value makeMethod() {
        SootClass clazz = Scene.v().getSootClass(DotnetBasicTypes.FAKE_LDFTN);

        // arguments which are passed to this function
        List<Local> argsVariables = new ArrayList<>();
        // method-parameters (signature)
        List<Type> methodParams = new ArrayList<>();

        SootMethodRef methodRef = Scene.v().makeMethodRef(clazz, FAKE_LDFTN_METHOD_NAME, methodParams,
                DotnetTypeFactory.toSootType(DotnetBasicTypes.SYSTEM_INTPTR), true);
        return Jimple.v().newStaticInvokeExpr(methodRef, argsVariables);
    }
}
