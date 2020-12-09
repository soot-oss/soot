package soot.JastAddJ;

/**
 * @ast class
 */
public class CONSTANT_MethodHandle_Info extends CONSTANT_Info {

    public int reference_kind;


    public int reference_index;


    public CONSTANT_MethodHandle_Info(BytecodeParser parser) {
      super(parser);
      reference_kind = p.u1();
      reference_index = p.u2();
    }


    public String toString() {
        return "MethodHandleInfo: " + reference_kind + " " + reference_index;
    }


}
