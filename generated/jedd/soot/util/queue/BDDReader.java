package soot.util.queue;

public class BDDReader implements Cloneable {
    BDDChunk chunk;
    
    BDDQueue q;
    
    BDDReader(BDDQueue q) {
        super();
        this.q = q;
        chunk = q.newChunk();
    }
    
    public jedd.internal.RelationContainer next() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new jedd.Attribute[] {  },
                                              new jedd.PhysicalDomain[] {  },
                                              ("<> ret; at /tmp/soot-trunk/src/soot/util/queue/BDDReader.jed" +
                                               "d:41,11-14"));
        do  {
            ret.eq(chunk.bdd);
            chunk = chunk.next;
            if (chunk == null) chunk = q.newChunk();
        }while(jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(ret), jedd.internal.Jedd.v().falseBDD()) &&
                 chunk.next != null); 
        return new jedd.internal.RelationContainer(new jedd.Attribute[] {  },
                                                   new jedd.PhysicalDomain[] {  },
                                                   ("return ret; at /tmp/soot-trunk/src/soot/util/queue/BDDReader" +
                                                    ".jedd:47,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        while (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(chunk.bdd),
                                             jedd.internal.Jedd.v().falseBDD()) &&
                 chunk.next != null)
            chunk = chunk.next;
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(chunk.bdd),
                                              jedd.internal.Jedd.v().falseBDD());
    }
    
    private BDDReader(BDDQueue q, BDDChunk chunk) {
        super();
        this.q = q;
        this.chunk = chunk;
    }
    
    public Object clone() { return new BDDReader(q, chunk); }
}
