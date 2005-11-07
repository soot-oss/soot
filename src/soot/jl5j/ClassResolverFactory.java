package soot.jl5j;

import java.util.*;
import soot.javaToJimple.*;

public class ClassResolverFactory extends AbstractClassResolverFactory {

    protected AbstractClassResolver createClassResolver(soot.SootClass sootClass, List references){
        return new ClassResolver(sootClass, references);
    }
}
