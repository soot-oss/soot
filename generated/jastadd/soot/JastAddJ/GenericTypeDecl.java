
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public interface GenericTypeDecl {
    // Declared in Generics.jrag at line 140

    TypeDecl original();

    // Declared in Generics.jrag at line 141

    int getNumTypeParameter();

    // Declared in Generics.jrag at line 142

    TypeVariable getTypeParameter(int index);

    // Declared in Generics.jrag at line 143

    List getTypeParameterList();

    // Declared in Generics.jrag at line 145

    public String fullName();

    // Declared in Generics.jrag at line 146

    public String typeName();

    // Declared in Generics.jrag at line 211

  public TypeDecl makeGeneric(Signatures.ClassSignature s);


    // Declared in Generics.jrag at line 456


  public SimpleSet addTypeVariables(SimpleSet c, String name);


    // Declared in Generics.jrag at line 657

  public List createArgumentList(ArrayList params);


    // Declared in Generics.jrag at line 139
 @SuppressWarnings({"unchecked", "cast"})     public boolean isGenericType();
    // Declared in Generics.jrag at line 144
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl rawType();
    // Declared in Generics.jrag at line 591
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl lookupParTypeDecl(ParTypeAccess p);
    // Declared in Generics.jrag at line 628
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl lookupParTypeDecl(ArrayList list);
}
