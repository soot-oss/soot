
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public class FieldInfo extends java.lang.Object {
    // Declared in BytecodeDescriptor.jrag at line 36

    private BytecodeParser p;

    // Declared in BytecodeDescriptor.jrag at line 37

    String name;

    // Declared in BytecodeDescriptor.jrag at line 38

    int flags;

    // Declared in BytecodeDescriptor.jrag at line 39

    private FieldDescriptor fieldDescriptor;

    // Declared in BytecodeDescriptor.jrag at line 40

    private Attributes.FieldAttributes attributes;

    // Declared in BytecodeDescriptor.jrag at line 42


    public FieldInfo(BytecodeParser parser) {
      p = parser;
      flags = p.u2();
      if(BytecodeParser.VERBOSE)
        p.print("Flags: " + flags);
      int name_index = p.u2();
      name = ((CONSTANT_Utf8_Info) p.constantPool[name_index]).string();

      fieldDescriptor = new FieldDescriptor(p, name);
      attributes = new Attributes.FieldAttributes(p);
    }

    // Declared in BytecodeDescriptor.jrag at line 54


    public BodyDecl bodyDecl() {
      FieldDeclaration f;
      if((flags & Flags.ACC_ENUM) != 0)
        //EnumConstant : FieldDeclaration ::= Modifiers <ID:String> Arg:Expr* BodyDecl* /TypeAccess:Access/ /[Init:Expr]/;
        f = new EnumConstant(
            BytecodeParser.modifiers(flags),
            name,
            new List(),
            new List()
            );
      else {
        Signatures.FieldSignature s = attributes.fieldSignature;
        Access type = s != null ? s.fieldTypeAccess() : fieldDescriptor.type();
        f = new FieldDeclaration(
            BytecodeParser.modifiers(flags),
            type,
            name,
            new Opt()
            );
      }
      if(attributes.constantValue() != null)
        if(fieldDescriptor.isBoolean()) {
          f.setInit(attributes.constantValue().exprAsBoolean());
        }
        else {
          f.setInit(attributes.constantValue().expr());
        }

      if(attributes.annotations != null)
        for(Iterator iter = attributes.annotations.iterator(); iter.hasNext(); )
          f.getModifiersNoTransform().addModifier((Modifier)iter.next());

      return f;
    }

    // Declared in BytecodeDescriptor.jrag at line 89


    public boolean isSynthetic() {
      return attributes.isSynthetic();
    }


}
