package soot.jimple.toolkit.callgraph;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mockito;
import soot.FastHierarchy;
import soot.RefType;
import soot.Type;
import soot.jimple.toolkits.callgraph.AssignableTypeFilter;

public class AssignableTypeFilterTest {

    @Test
    public void skipsWhenTypeIsNotAssignable() {
        RefType required = Mockito.mock(RefType.class);
        Type reaching = Mockito.mock(Type.class);
        FastHierarchy fh = Mockito.mock(FastHierarchy.class);
        Mockito.when(fh.canStoreType(reaching, required)).thenReturn(false);

        AssignableTypeFilter filter = new AssignableTypeFilter(required);
        assertTrue(filter.skipSite(null, fh, reaching));
    }

    @Test
    public void doesNotSkipWhenTypeIsAssignable() {
        RefType required = Mockito.mock(RefType.class);
        Type reaching = Mockito.mock(Type.class);
        FastHierarchy fh = Mockito.mock(FastHierarchy.class);
        Mockito.when(fh.canStoreType(reaching, required)).thenReturn(true);

        AssignableTypeFilter filter = new AssignableTypeFilter(required);
        assertFalse(filter.skipSite(null, fh, reaching));
    }
}