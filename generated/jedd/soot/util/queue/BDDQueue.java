package soot.util.queue;

public class BDDQueue {
    BDDChunk chunk = new BDDChunk();
    
    public void add(final jedd.internal.RelationContainer r) { chunk.bdd.eqUnion(r); }
    
    public BDDReader reader() { return new BDDReader(this); }
    
    BDDChunk newChunk() {
        chunk = (chunk.next = new BDDChunk());
        return chunk;
    }
    
    public BDDQueue() { super(); }
}
