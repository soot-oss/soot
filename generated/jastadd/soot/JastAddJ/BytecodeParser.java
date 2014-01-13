package soot.JastAddJ;

import java.util.HashSet;
import java.io.File;
import java.util.*;
import beaver.*;
import java.util.ArrayList;
import java.util.zip.*;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Collection;
import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.coffi.ClassFile;
import soot.coffi.method_info;
import soot.coffi.CONSTANT_Utf8_info;
import soot.tagkit.SourceFileTag;
import soot.coffi.CoffiMethodSource;
/**
  * @ast class
 * 
 */
public class BytecodeParser extends java.lang.Object implements Flags, BytecodeReader {

    public CompilationUnit read(InputStream is, String fullName, Program p) throws FileNotFoundException, IOException {
      return new BytecodeParser(is, fullName).parse(null, null, p);
    }



    public static final boolean VERBOSE = false;



    private DataInputStream is;


    public CONSTANT_Class_Info classInfo;


    public String outerClassName;


    public String name;



    public BytecodeParser(byte[] buffer, int size, String name) {
      //this.is = new DataInputStream(new DummyInputStream(buffer, size));
      this.is = new DataInputStream(new ByteArrayInputStream(buffer, 0, size));
      this.name = name;
    }


    public BytecodeParser(InputStream in, String name) {
      //this.is = new DataInputStream(new DummyInputStream(buffer, size));
      this.is = new DataInputStream(new DummyInputStream(in));
      this.name = name;
    }



    public BytecodeParser() {
      this("");
    }


    public BytecodeParser(String name) {
      if (!name.endsWith(".class")) {
        //name = name.replaceAll("\\.", "/") + ".class";
        name = name.replace('.', '/') + ".class";
      }
      this.name = name;
    }



    private static class DummyInputStream extends InputStream {
      byte[] bytes;
      int pos;
      int size;
      public DummyInputStream(byte[] buffer, int size) {
        bytes = buffer;
        this.size = size;
      }
      public DummyInputStream(InputStream is) {
        bytes = new byte[1024];
        int index = 0;
        size = 1024;
        try {
          int status;
          do {
            status = is.read(bytes, index, size - index);
            if(status != -1) {
              index += status;
              if(index == size) {
                byte[] newBytes = new byte[size*2];
                System.arraycopy(bytes, 0, newBytes, 0, size);
                bytes = newBytes;
                size *= 2;
              }
            }
          } while (status != -1);
        } catch (IOException e) {
          System.err.println("Something went wrong trying to read " + is);
          //System.exit(1);
        }
        size = index;
        pos = 0;
      }

      public int available() {
        return size - pos;
      }

      public void close() {
      }

      public void mark(int readlimit) {
      }

      public boolean markSupported() {
        return false;
      }

      public int read(byte[] b) {
        int actualLength = Math.min(b.length, size-pos);
        System.arraycopy(bytes, pos, b, 0, actualLength);
        pos += actualLength;
        return actualLength;
      }

      public int read(byte[] b, int offset, int length) {
        int actualLength = Math.min(length, size-pos);
        System.arraycopy(bytes, pos, b, offset, actualLength);
        pos += actualLength;
        return actualLength;
      }

      public void reset() {
      }

      public long skip(long n) {
        if(size == pos)
          return -1;
        long skipSize = Math.min(n, size-pos);
        pos += skipSize;
        return skipSize;
      }

      public int read() throws IOException {
        if(pos < size) {
          int i = bytes[pos++];
          if(i < 0)
            i = 256 + i;
          return i;
        }
        return -1;
      }
    }



    public int next() {
      try {
        return is.read();
      } catch (IOException e) {
        System.exit(1);
      }
      return -1;
    }



    public int u1() {
      try {
        return is.readUnsignedByte();
      } catch (IOException e) {
        System.exit(1);
      }
      return -1;
    }



    public int u2() {
      try {
        return is.readUnsignedShort();
      } catch (IOException e) {
        System.exit(1);
      }
      return -1;
    }



    public int u4() {
      try {
        return is.readInt();
      } catch (IOException e) {
        System.exit(1);
      }
      return -1;
    }



    public int readInt() {
      try {
        return is.readInt();
      } catch (IOException e) {
        System.exit(1);
      }
      return -1;
    }



    public float readFloat() {
      try {
        return is.readFloat();
      } catch (IOException e) {
        System.exit(1);
      }
      return -1;
    }



    public long readLong() {
      try {
        return is.readLong();
      } catch (IOException e) {
        System.exit(1);
      }
      return -1;
    }



    public double readDouble() {
      try {
        return is.readDouble();
      } catch (IOException e) {
        System.exit(1);
      }
      return -1;
    }



    public String readUTF() {
      try {
        return is.readUTF();
      } catch (IOException e) {
        System.exit(1);
      }
      return "";
    }



    public void skip(int length) {
      try {
        is.skip(length);
      } catch (IOException e) {
        System.exit(1);
      }
    }



    public void error(String s) {
      throw new RuntimeException(s);
    }



    public void print(String s) {
      //System.out.print(s);
    }



    public void println(String s) {
      print(s + "\n");
    }



    public void println() {
      print("\n");
    }



	  public CompilationUnit parse(TypeDecl outerTypeDecl, String outerClassName, Program classPath, boolean isInner) 
        throws FileNotFoundException, IOException {
          isInnerClass = isInner;
          return parse(outerTypeDecl, outerClassName, classPath);
    }



    public CompilationUnit parse(TypeDecl outerTypeDecl, String outerClassName, Program program) 
      throws FileNotFoundException, IOException {
        //InputStream file = ClassLoader.getSystemResourceAsStream(name);

        if(is == null) {
          FileInputStream file = new FileInputStream(name);
          //System.err.println("/home/torbjorn/sandbox/jdk/" + name);

          if(file == null) {
            throw new FileNotFoundException(name);
          }

          // // Does not work without DummyInputStream. Why?
          //is = new DataInputStream(new DummyInputStream(new BufferedInputStream(file)));
          is = new DataInputStream(new BufferedInputStream(file));
        }
        if(BytecodeParser.VERBOSE) 
          println("Parsing byte codes in " + name);

        this.outerClassName = outerClassName;
        parseMagic();
        parseMinor();
        parseMajor();
        parseConstantPool();
        CompilationUnit cu = new CompilationUnit();
        TypeDecl typeDecl = parseTypeDecl();
        cu.setPackageDecl(classInfo.packageDecl());
        cu.addTypeDecl(typeDecl);
        parseFields(typeDecl);
        parseMethods(typeDecl);
        //parse attributes, and if we have an inner class, then execute the branch...
        if(new Attributes.TypeAttributes(this, typeDecl, outerTypeDecl, program).isInnerClass()) {        
            //this is a workaround for the fact that JastAdd stores inner classes as members of
            //their outer classes, even for inner classes that come from bytecode;
            //to avoid having inner classes show up as top-level classes, we remove them here
            //from the compilation unit again...
            
            //first add the cu to the program, so that getTypeDecls() won't fail 
        	program.addCompilationUnit(cu);
        	//then clear the cu
        	for(int i=0;i<cu.getTypeDecls().getNumChild();i++) {
        		cu.getTypeDecls().removeChild(i);
        	}
        	//and remove the cu from the program again
        	program.getCompilationUnits().removeChild(program.getCompilationUnits().getIndexOfChild(cu));
        }
        
        is.close();
        is = null;
        return cu;
      }



    public void parseMagic() {
      if (next() != 0xca || next() != 0xfe || next() != 0xba || next() != 0xbe)
        error("magic error");
    }



    public void parseMinor() {
      int low = u1();
      int high = u1();
      if(BytecodeParser.VERBOSE) 
        println("Minor: " + high + "." + low);
    }



    public void parseMajor() {
      int low = u1();
      int high = u1();
      if(BytecodeParser.VERBOSE) 
        println("Major: " + high + "." + low);
    }



    public boolean isInnerClass = false;



    public TypeDecl parseTypeDecl() {
      int flags = u2();
      Modifiers modifiers = modifiers(flags & 0xfddf);
      if((flags & (ACC_INTERFACE | ACC_ENUM)) == ACC_ENUM) {
        // Modifiers <ID:String> /[SuperClassAccess:Access]/ Implements:Access* BodyDecl*;
        EnumDecl decl = new EnumDecl();
        decl.setModifiers(modifiers);
        decl.setID(parseThisClass());
        Access superClass = parseSuperClass();
        decl.setImplementsList(parseInterfaces(new List()));
        return decl;
      }
      else if ((flags & ACC_INTERFACE) == 0) {
        ClassDecl decl = new ClassDecl();
        decl.setModifiers(modifiers);
        decl.setID(parseThisClass());
        Access superClass = parseSuperClass();
        decl.setSuperClassAccessOpt(superClass == null ? new Opt()
            : new Opt(superClass));
        decl.setImplementsList(parseInterfaces(new List()));
        return decl;
      } else if((flags & ACC_ANNOTATION) == 0) {
        InterfaceDecl decl = new InterfaceDecl();
        decl.setModifiers(modifiers);
        decl.setID(parseThisClass());
        Access superClass = parseSuperClass();
        decl.setSuperInterfaceIdList(
            parseInterfaces(
              superClass == null ? new List()
              : new List().add(superClass)));
        return decl;
      } else {
        AnnotationDecl decl = new AnnotationDecl();
        decl.setModifiers(modifiers);
        decl.setID(parseThisClass());
        Access superClass = parseSuperClass();
        parseInterfaces(
            superClass == null ? new List()
            : new List().add(superClass));
        return decl;
      }
    }




    public String parseThisClass() {
      int index = u2();
      CONSTANT_Class_Info info = (CONSTANT_Class_Info) constantPool[index];
      classInfo = info;
      return info.simpleName();
    }



    public Access parseSuperClass() {
      int index = u2();
      if (index == 0)
        return null;
      CONSTANT_Class_Info info = (CONSTANT_Class_Info) constantPool[index];
      return info.access();
    }



    public List parseInterfaces(List list) {
      int count = u2();
      for (int i = 0; i < count; i++) {
        CONSTANT_Class_Info info = (CONSTANT_Class_Info) constantPool[u2()];
        list.add(info.access());
      }
      return list;
    }




    public Access fromClassName(String s) {
      // Sample ClassName: a/b/c$d$e
      // the package name ends at the last '/'
      // after that follows a list of type names separated by '$'
      // all except the first are nested types

      String packageName = "";
      int index = s.lastIndexOf('/');
      if(index != -1)
        packageName = s.substring(0, index).replace('/', '.');
      String typeName = s.substring(index + 1, s.length());
      if(typeName.indexOf('$') != -1)
        return new BytecodeTypeAccess(packageName, typeName);
      else
        return new TypeAccess(packageName, typeName);
    }



    public static Modifiers modifiers(int flags) {
      Modifiers m = new Modifiers();
      if ((flags & 0x0001) != 0)
        m.addModifier(new Modifier("public"));
      if ((flags & 0x0002) != 0)
        m.addModifier(new Modifier("private"));
      if ((flags & 0x0004) != 0)
        m.addModifier(new Modifier("protected"));
      if ((flags & 0x0008) != 0)
        m.addModifier(new Modifier("static"));
      if ((flags & 0x0010) != 0)
        m.addModifier(new Modifier("final"));
      if ((flags & 0x0020) != 0)
        m.addModifier(new Modifier("synchronized"));
      if ((flags & 0x0040) != 0)
        m.addModifier(new Modifier("volatile"));
      if ((flags & 0x0080) != 0)
        m.addModifier(new Modifier("transient"));
      if ((flags & 0x0100) != 0)
        m.addModifier(new Modifier("native"));
      if ((flags & 0x0400) != 0)
        m.addModifier(new Modifier("abstract"));
      if ((flags & 0x0800) != 0)
        m.addModifier(new Modifier("strictfp"));
      return m;
    }



    public void parseFields(TypeDecl typeDecl) {
      int count = u2();
      if(BytecodeParser.VERBOSE) 
        println("Fields (" + count + "):");
      for (int i = 0; i < count; i++) {
        if(BytecodeParser.VERBOSE) 
          print(" Field nbr " + i + " ");
        FieldInfo fieldInfo = new FieldInfo(this);
        if(!fieldInfo.isSynthetic())
          typeDecl.addBodyDecl(fieldInfo.bodyDecl());
      }
    }




    public void parseMethods(TypeDecl typeDecl) {
      int count = u2();
      if(BytecodeParser.VERBOSE) 
        println("Methods (" + count + "):");
      for (int i = 0; i < count; i++) {
        if(BytecodeParser.VERBOSE) 
          print("  Method nbr " + i + " ");
        MethodInfo info = new MethodInfo(this);
        if(!info.isSynthetic() && !info.name.equals("<clinit>")) {
          typeDecl.addBodyDecl(info.bodyDecl());
        }
      }
    }




    public CONSTANT_Info[] constantPool = null;



    private void checkLengthAndNull(int index) {
      if(index >= constantPool.length) {
        throw new Error("Trying to access element " + index  + " in constant pool of length " + constantPool.length);
      }
      if(constantPool[index] == null)
        throw new Error("Unexpected null element in constant pool at index " + index);
    }


    public boolean validConstantPoolIndex(int index) {
      return index < constantPool.length && constantPool[index] != null;
    }


    public CONSTANT_Info getCONSTANT_Info(int index) {
      checkLengthAndNull(index);
      return constantPool[index];
    }


    public CONSTANT_Utf8_Info getCONSTANT_Utf8_Info(int index) {
      checkLengthAndNull(index);
      CONSTANT_Info info = constantPool[index];
      if(!(info instanceof CONSTANT_Utf8_Info))
        throw new Error("Expected CONSTANT_Utf8_info at " + index + " in constant pool but found " + info.getClass().getName());
      return (CONSTANT_Utf8_Info)info;
    }


    public CONSTANT_Class_Info getCONSTANT_Class_Info(int index) {
      checkLengthAndNull(index);
      CONSTANT_Info info = constantPool[index];
      if(!(info instanceof CONSTANT_Class_Info))
        throw new Error("Expected CONSTANT_Class_info at " + index + " in constant pool but found " + info.getClass().getName());
      return (CONSTANT_Class_Info)info;
    }



    public void parseConstantPool() {
      int count = u2();
      if(BytecodeParser.VERBOSE) 
        println("constant_pool_count: " + count);
      constantPool = new CONSTANT_Info[count + 1];
      for (int i = 1; i < count; i++) {
        parseEntry(i);
        if (constantPool[i] instanceof CONSTANT_Long_Info
            || constantPool[i] instanceof CONSTANT_Double_Info)
          i++;
      }

      //println("ConstantPool: ");
      //for(int i = 1; i < count; i++) {
      //  println(i + ", " + constantPool[i]);
      //}

    }



    private static final int CONSTANT_Class = 7;


    private static final int CONSTANT_FieldRef = 9;


    private static final int CONSTANT_MethodRef = 10;


    private static final int CONSTANT_InterfaceMethodRef = 11;


    private static final int CONSTANT_String = 8;


    private static final int CONSTANT_Integer = 3;


    private static final int CONSTANT_Float = 4;


    private static final int CONSTANT_Long = 5;


    private static final int CONSTANT_Double = 6;


    private static final int CONSTANT_NameAndType = 12;


    private static final int CONSTANT_Utf8 = 1;



    public void parseEntry(int i) {
      int tag = u1();
      switch (tag) {
        case CONSTANT_Class:
          constantPool[i] = new CONSTANT_Class_Info(this);
          break;
        case CONSTANT_FieldRef:
          constantPool[i] = new CONSTANT_Fieldref_Info(this);
          break;
        case CONSTANT_MethodRef:
          constantPool[i] = new CONSTANT_Methodref_Info(this);
          break;
        case CONSTANT_InterfaceMethodRef:
          constantPool[i] = new CONSTANT_InterfaceMethodref_Info(this);
          break;
        case CONSTANT_String:
          constantPool[i] = new CONSTANT_String_Info(this);
          break;
        case CONSTANT_Integer:
          constantPool[i] = new CONSTANT_Integer_Info(this);
          break;
        case CONSTANT_Float:
          constantPool[i] = new CONSTANT_Float_Info(this);
          break;
        case CONSTANT_Long:
          constantPool[i] = new CONSTANT_Long_Info(this);
          break;
        case CONSTANT_Double:
          constantPool[i] = new CONSTANT_Double_Info(this);
          break;
        case CONSTANT_NameAndType:
          constantPool[i] = new CONSTANT_NameAndType_Info(this);
          break;
        case CONSTANT_Utf8:
          constantPool[i] = new CONSTANT_Utf8_Info(this);
          break;
        default:
          println("Unknown entry: " + tag);
      }
    }


}
