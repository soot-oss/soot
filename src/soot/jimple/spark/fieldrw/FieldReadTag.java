package soot.jimple.spark.fieldrw;
import soot.tagkit.*;
import java.util.*;

/** Implements a tag that holds a list of fields read by a call. */
public class FieldReadTag extends FieldRWTag {
    FieldReadTag( Set fields ) {
        super(fields);
    }
    public String getName() {
        return "FieldReadTag";
    }
}

