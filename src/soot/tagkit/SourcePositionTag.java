package soot.tagkit;

public class SourcePositionTag extends PositionTag {
    public SourcePositionTag(int i, int j){
        super(i,j);
    }
    
    public String getName()
    {
		return "SourcePositionTag";
    }
}
