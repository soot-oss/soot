package ca.mcgill.sable.soot.baf;

public interface DupInst extends Inst
{
    public int getWordCount();
    public void setWordCount(int count);
    public int getOffset();    
    public void setOffset(int off);
}
