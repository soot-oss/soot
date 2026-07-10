package soot.jimple.toolkits.callgraph;

import soot.FastHierarchy;
import soot.Type;

//Act as a  bridge interface so whichever class comes here needs to implement this interface
// Extension point for filtering out reaching types at virtual call sites that model special framework control flow
public interface VirtualCallSiteFilter {

    boolean skipSite(VirtualCallSite site,FastHierarchy fh,Type type);
}