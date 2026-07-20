package soot.jimple.toolkits.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */


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