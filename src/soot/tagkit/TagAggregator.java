package soot.tagkit;
import soot.*;

/** Interface to aggregate tags of units. */

public interface TagAggregator {
    /** Adds in a new (unit, tag) pair. */
    public void aggregateTag(Tag t, Unit u);

    /** Generates the aggregated tag. */    
    public Tag produceAggregateTag();

    /** Clears old accumulated tags. */
    public void refresh();

    /** Returns true if the aggregator is active. */
    public boolean isActive();
}
