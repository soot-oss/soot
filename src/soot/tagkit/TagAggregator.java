package soot.tagkit;
import soot.*;

public interface TagAggregator {
    /** Add in a new (unit, tag) pair. */
    public void aggregateTag(Tag t, Unit u);

    /** Generate the aggregated tag. */    
    public Tag produceAggregateTag();

    /** Clear old accumulated tags. */
    public void refresh();

    /** Return true if the aggregator is active. */
    public boolean isActive();
}
