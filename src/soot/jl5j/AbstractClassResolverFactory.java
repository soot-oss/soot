package soot.jl5j;

import java.util.*;

public abstract class AbstractClassResolverFactory {

    protected abstract AbstractClassResolver createClassResolver(soot.SootClass sootClass, List references);
}
