package soot.tagkit;

import soot.*;
import java.util.*;

public interface  Attribute extends Tag
{
    public byte[] getValue() throws AttributeValueException;
    public void setValue(byte[] v);
    public List getUnitBoxes();
}
