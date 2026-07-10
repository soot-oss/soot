package soot.jimple.toolkits.callgraph;

import soot.FastHierarchy;
import soot.RefType;
import soot.Type;

/**
 * A {@link VirtualCallSiteFilter} that only accepts reaching types assignable
 * to a given base type. Reused for every concurrency helper (Thread, Executor,
 * AsyncTask, Handler) since they all share the same "must be assignable to X"
 * rule -- only the base type X differs.
 */
public class AssignableTypeFilter implements VirtualCallSiteFilter {

    private final RefType requiredType;

    public AssignableTypeFilter(RefType requiredType) {
        if (requiredType == null) {
            throw new IllegalArgumentException("requiredType must not be null");
        }
        this.requiredType = requiredType;
    }

    @Override
    public boolean skipSite(VirtualCallSite site, FastHierarchy fh, Type type) {
        return !fh.canStoreType(type, requiredType);
    }
}

//A reusable class .Here we can reuse this class for THREAD,EXECUTOR,ASYNCTASK,HANDLER
//Following the DRY(Dont Repeat yourself) principle