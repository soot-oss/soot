package soot.tagkit;
import soot.*;

public interface TagAggregator {
    
    public void aggregateTag(Tag t, Unit u);    
    public Tag produceAggregateTag();
    public boolean isActive();    
}
