package soot.jimple.toolkits.callgraph;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import soot.Kind;
import soot.RefType;

public class VirtualEdgesSummariesRequiredTypeTest {

    @Test
    public void parsesRequiredTypeAttribute() throws Exception {
        String xml = "<virtualedges>"
                + "<edge type=\"EXECUTOR\" requiredType=\"java.lang.Runnable\">"
                + "<source declaringclass=\"java.util.concurrent.Executor\" invoketype=\"instance\" "
                + "subsignature=\"void execute(java.lang.Runnable)\"/>"
                + "<targets><direct index=\"0\" subsignature=\"void run()\" target-position=\"argument\"/></targets>"
                + "</edge></virtualedges>";

        VirtualEdgesSummaries summaries = new TestableVirtualEdgesSummaries();
        summaries.loadSummaries(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        assertEquals(RefType.v("java.lang.Runnable"), summaries.getRequiredType(Kind.EXECUTOR));
        assertNull(summaries.getRequiredType(Kind.VIRTUAL));
    }

    /** Exposes the protected loadSummaries() method for testing without touching the real XML file. */
    private static class TestableVirtualEdgesSummaries extends VirtualEdgesSummaries {
        TestableVirtualEdgesSummaries() {
            super(java.util.Collections.emptyList());
        }
    }
}