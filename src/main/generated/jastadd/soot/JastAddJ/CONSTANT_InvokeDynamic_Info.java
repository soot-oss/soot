package soot.JastAddJ;

/**
 * @ast class
 */
public class CONSTANT_InvokeDynamic_Info extends CONSTANT_Info {

    public int method_attr_index;


    public int name_and_type_index;


    public CONSTANT_InvokeDynamic_Info(BytecodeParser parser) {
      super(parser);
      method_attr_index = p.u2();
      name_and_type_index = p.u2();
    }


    public String toString() {
        return "InvokeDynamicInfo: " + method_attr_index + " " + name_and_type_index;
    }


}
