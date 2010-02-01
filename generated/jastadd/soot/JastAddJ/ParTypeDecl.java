
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public interface ParTypeDecl extends Parameterization {
    // Declared in Generics.jrag at line 218

    //syn String name();
    int getNumArgument();

    // Declared in Generics.jrag at line 219

    Access getArgument(int index);

    // Declared in Generics.jrag at line 222

    public String typeName();

    // Declared in Generics.jrag at line 693

  public TypeDecl substitute(TypeVariable typeVariable);


    // Declared in Generics.jrag at line 706


  public int numTypeParameter();


    // Declared in Generics.jrag at line 709

  public TypeVariable typeParameter(int index);


    // Declared in Generics.jrag at line 741

  public Access substitute(Parameterization parTypeDecl);


    // Declared in GenericsParTypeDecl.jrag at line 73


  public Access createQualifiedAccess();


    // Declared in GenericsCodegen.jrag at line 406


  public void transformation();


    // Declared in Generics.jrag at line 220
 @SuppressWarnings({"unchecked", "cast"})     public boolean isParameterizedType();
    // Declared in Generics.jrag at line 221
 @SuppressWarnings({"unchecked", "cast"})     public boolean isRawType();
    // Declared in Generics.jrag at line 347
 @SuppressWarnings({"unchecked", "cast"})     public boolean sameArgument(ParTypeDecl decl);
    // Declared in Generics.jrag at line 544
 @SuppressWarnings({"unchecked", "cast"})     public boolean sameSignature(Access a);
    // Declared in Generics.jrag at line 579
 @SuppressWarnings({"unchecked", "cast"})     public boolean sameSignature(ArrayList list);
    // Declared in GenericsParTypeDecl.jrag at line 30
 @SuppressWarnings({"unchecked", "cast"})     public String nameWithArgs();
    // Declared in GenericsParTypeDecl.jrag at line 45
    public TypeDecl genericDecl();
}
