package soot.JastAddJ;

/**
 * @ast class
 */
public class CONSTANT_MethodType_Info extends CONSTANT_Info {

    public int descriptor_index;


    public CONSTANT_MethodType_Info(BytecodeParser parser) {
      super(parser);
      descriptor_index = p.u2();
    }


    public String toString() {
        return "MethodTypeInfo: " + descriptor_index ;
    }


}
