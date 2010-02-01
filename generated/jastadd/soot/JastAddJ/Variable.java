
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public interface Variable {
    // Declared in VariableDeclaration.jrag at line 12

    public String name();

    // Declared in VariableDeclaration.jrag at line 13

    public TypeDecl type();

    // Declared in VariableDeclaration.jrag at line 15

    // 4.5.3
    public boolean isClassVariable();

    // Declared in VariableDeclaration.jrag at line 16

    public boolean isInstanceVariable();

    // Declared in VariableDeclaration.jrag at line 17

    public boolean isMethodParameter();

    // Declared in VariableDeclaration.jrag at line 18

    public boolean isConstructorParameter();

    // Declared in VariableDeclaration.jrag at line 19

    public boolean isExceptionHandlerParameter();

    // Declared in VariableDeclaration.jrag at line 20

    public boolean isLocalVariable();

    // Declared in VariableDeclaration.jrag at line 22

    // 4.5.4
    public boolean isFinal();

    // Declared in VariableDeclaration.jrag at line 24


    public boolean isBlank();

    // Declared in VariableDeclaration.jrag at line 25

    public boolean isStatic();

    // Declared in VariableDeclaration.jrag at line 26

    public boolean isSynthetic();

    // Declared in VariableDeclaration.jrag at line 28


    public TypeDecl hostType();

    // Declared in VariableDeclaration.jrag at line 30


    public Expr getInit();

    // Declared in VariableDeclaration.jrag at line 31

    public boolean hasInit();

    // Declared in VariableDeclaration.jrag at line 33


    public Constant constant();

    // Declared in Generics.jrag at line 1273
 @SuppressWarnings({"unchecked", "cast"})     public Variable sourceVariableDecl();
}
