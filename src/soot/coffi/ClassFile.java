/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997 Clark Verbrugge
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */







package soot.coffi;
import soot.*;

import java.io.*;

/**
 * A ClassFile object represents the contents of a <tt>.class</tt> file.
 * <p>
 * A ClassFile contains code for manipulation of its constituents.
 * @author Clark Verbrugge
 */
public class ClassFile {

   /** Magic number. */
    static final long MAGIC = 0xCAFEBABEL;

   /** Access bit flag. */
    static final short ACC_PUBLIC =    0x0001;
   /** Access bit flag. */
    static final short ACC_PRIVATE =   0x0002;
   /** Access bit flag. */
    static final short ACC_PROTECTED = 0x0004;
   /** Access bit flag. */
    static final short ACC_STATIC =    0x0008;
   /** Access bit flag. */
    static final short ACC_FINAL =     0x0010;
   /** Access bit flag. */
    static final short ACC_SUPER =     0x0020;
   /** Access bit flag. */
    static final short ACC_VOLATILE =  0x0040;
   /** Access bit flag. */
    static final short ACC_TRANSIENT = 0x0080;
   /** Access bit flag. */
    static final short ACC_INTERFACE = 0x0200;
   /** Access bit flag. */
    static final short ACC_ABSTRACT =  0x0400;
   /** Access bit flag. */
    static final short ACC_STRICT =    0x0800;
   /** Access bit flag. */
    static final short ACC_ANNOTATION = 0x2000;
   /** Access bit flag. */
    static final short ACC_ENUM = 0x4000;
   /** Remaining bits in the access bit flag. */
    static final short ACC_UNKNOWN =   0x7000;

   /** Descriptor code string. */
    static final String DESC_BYTE =    "B";
   /** Descriptor code string. */
    static final String DESC_CHAR =    "C";
   /** Descriptor code string. */
    static final String DESC_DOUBLE =  "D";
   /** Descriptor code string. */
    static final String DESC_FLOAT=    "F";
   /** Descriptor code string. */
    static final String DESC_INT =     "I";
   /** Descriptor code string. */
    static final String DESC_LONG =    "J";
   /** Descriptor code string. */
    static final String DESC_OBJECT =  "L";
   /** Descriptor code string. */
    static final String DESC_SHORT =   "S";
   /** Descriptor code string. */
    static final String DESC_BOOLEAN = "Z";
   /** Descriptor code string. */
    static final String DESC_VOID =    "V";
   /** Descriptor code string. */
    static final String DESC_ARRAY =   "[";

   /** Debugging flag. */
    boolean debug;

   /** File name of the <tt>.class</tt> this represents. */
    String fn;
    

   /* For chaining ClassFiles into a list.
      ClassFile next;*/

   /** Magic number read in.
    * @see ClassFile#MAGIC
    */
    long magic;
   /** Minor version. */
    int minor_version;
   /** Major version. */
    int major_version;
   /** Number of items in the constant pool. */
    public int constant_pool_count;
   /** Array of constant pool items.
    * @see cp_info
    */
    public cp_info constant_pool[];
   /** Access flags for this Class.
    */
    public int access_flags;
   /** Constant pool index of the Class constant describing <i>this</i>.
    * @see CONSTANT_Class_info
    */
    public int this_class;
   /** Constant pool index of the Class constant describing <i>super</i>.
    * @see CONSTANT_Class_info
    */
    public int super_class;
   /** Count of interfaces implemented. */
    public int interfaces_count;
   /** Array of constant pool indices of Class constants describing each
    * interace implemented by this class, as given in the source for this
    * class.
    * @see CONSTANT_Class_info
    */
    public int interfaces[];
   /** Count of fields this Class contains. */
    public int fields_count;
   /** Array of field_info objects describing each field.
    * @see field_info
    */
    public field_info fields[];
   /** Count of methods this Class contains. */
    public int methods_count;
   /** Array of method_info objects describing each field.
    * @see method_info
    */
    public method_info methods[];
   /** Count of attributes this class contains. */
    public int attributes_count;
   /** Array of attribute_info objects for this class.
    * @see attribute_info
    */
    public attribute_info attributes[];
    
    /** bootstrap-methods attribute (if any) */
    public BootstrapMethods_attribute bootstrap_methods_attribute;

   /** Creates a new ClassFile object given the name of the file.
    * @param nfn file name which this ClassFile will represent.
    */
    public ClassFile(String nfn) { fn = nfn; }

    /** Returns the name of this Class. */
    public String toString() {
	return (constant_pool[this_class].toString(constant_pool));
    }

    public boolean loadClassFile(InputStream is) 
    {
      InputStream f = null;
      InputStream classFileStream;
      DataInputStream d;
      boolean b;

      classFileStream = is;
     
      byte[]  data;
      
      
      Timers.v().readTimer.start();
      
      try 
      {
      	DataInputStream classDataStream = new DataInputStream(classFileStream);
        data = new byte[classDataStream.available()];
        classDataStream.readFully(data);
        f = new ByteArrayInputStream(data);
         
      } catch(IOException e)
      {
      }
      
      Timers.v().readTimer.end();
      
      d = new DataInputStream(f);
      b = readClass(d);
      
      try {
        classFileStream.close();
        d.close(); 
        f.close();
      } catch(IOException e) {
         G.v().out.println("IOException with " + fn + ": " + e.getMessage());
         return false;
      }
      
      if (!b) return false;
      //parse();        // parse all methods & builds CFGs
      //G.v().out.println("-- Read " + cf + " --");
      return true;
   }


















   /** Main entry point for writing a class file.
    * The file name is given in the constructor; this opens the
    * file and writes the internal representation.
    * @return <i>true</i> on success.
    */
    boolean saveClassFile() {
      FileOutputStream f;
      DataOutputStream d;
      boolean b;
      try {
         f = new FileOutputStream(fn);
      } catch(FileNotFoundException e) {
         if (fn.indexOf(".class")>=0) {
            G.v().out.println("Can't find " + fn);
            return false;
         }
         fn = fn + ".class";
         try {
            f = new FileOutputStream(fn);
         } catch(FileNotFoundException ee) {
            G.v().out.println("Can't find " + fn);
            return false;
         }
      }
      d = new DataOutputStream(f);
      if (d==null) {
         try {
            f.close();
         } catch(IOException e) { }
         return false;
      }
      b = writeClass(d);
      try {
         d.close();
         f.close();
      } catch(IOException e) {
         G.v().out.println("IOException with " + fn + ": " + e.getMessage());
         return false;
      }
      return b;
   }

   /** Returns a String constructed by parsing the bits in the given
    * access code (as defined by the ACC_* constants).
    * @param af access code.
    * @param separator String used to separate words used for access bits.
    * @see ClassFile#access_flags
    * @see method_info#access_flags
    * @see field_info#access_flags
    */
    static String access_string(int af,String separator) {
      boolean hasone = false;
      String s = "";
      if ((af & ACC_PUBLIC) != 0) {
         s = "public";
         hasone = true;
      }
      if ((af & ACC_PRIVATE) != 0) {
         if (hasone) s = s + separator;
         else hasone = true;
         s = s + "private";
      }
      if ((af & ACC_PROTECTED) != 0) {
         if (hasone) s = s + separator;
         else hasone = true;
         s = s + "protected";
      }
      if ((af & ACC_STATIC) != 0) {
         if (hasone) s = s + separator;
         else hasone = true;
         s = s + "static";
      }
      if ((af & ACC_FINAL) != 0) {
         if (hasone) s = s + separator;
         else hasone = true;
         s = s + "final";
      }
      if ((af & ACC_SUPER) != 0) {
         if (hasone) s = s + separator;
         else hasone = true;
         s = s + "super";
      }
      if ((af & ACC_VOLATILE) != 0) {
         if (hasone) s = s + separator;
         else hasone = true;
         s = s + "volatile";
      }
      if ((af & ACC_TRANSIENT) != 0) {
         if (hasone) s = s + separator;
         else hasone = true;
         s = s + "transient";
      }
      if ((af & ACC_INTERFACE) != 0) {
         if (hasone) s = s + separator;
         else hasone = true;
         s = s + "interface";
      }
      if ((af & ACC_ABSTRACT) != 0) {
         if (hasone) s = s + separator;
         else hasone = true;
         s = s + "abstract";
      }
      if ((af & ACC_STRICT) != 0) {
         if (hasone) s = s + separator;
         else hasone = true;
         s = s + "strict";
      }
      if ((af & ACC_ANNOTATION) != 0) {
         if (hasone) s = s + separator;
         else hasone = true;
         s = s + "annotation";
      }
      if ((af & ACC_ENUM) != 0) {
         if (hasone) s = s + separator;
         else hasone = true;
         s = s + "enum";
      }
      if ((af & ACC_UNKNOWN) != 0) {
         if (hasone) s = s + separator;
         else hasone = true;
         s = s + "unknown";
      }
      return s;
   }

   /** Builds the internal representation of this Class by reading in the
    * given class file.
    * @param d Stream forming the <tt>.class</tt> file.
    * @return <i>true</i> if read was successful, <i>false</i> on some error.
    */
   public boolean readClass(DataInputStream d) {
      try {
         // first read in magic number
         magic = d.readInt() & 0xFFFFFFFFL;
         if (magic != MAGIC) {
            G.v().out.println("Wrong magic number in " + fn + ": " + magic);
            return false;
         }
         //G.v().out.println("Magic number ok");
         minor_version = d.readUnsignedShort();
         major_version = d.readUnsignedShort();
        // G.v().out.println("Version: " + major_version + "." + minor_version);
         constant_pool_count = d.readUnsignedShort();
         //G.v().out.println("Constant pool count: " + constant_pool_count);

         if (!readConstantPool(d))
            return false;

         access_flags = d.readUnsignedShort();
         /*if (access_flags!=0)
             G.v().out.println("Access flags: " + access_flags + " = " +
                            access_string(access_flags,", "));*/

         this_class = d.readUnsignedShort();
         super_class = d.readUnsignedShort();
         interfaces_count = d.readUnsignedShort();
         if (interfaces_count>0) {
            interfaces = new int[interfaces_count];
            int j;
            for (j=0; j<interfaces_count; j++)
               interfaces[j] = d.readUnsignedShort();
         }
         //G.v().out.println("Implements " + interfaces_count + " interface(s)");

         Timers.v().fieldTimer.start();
         
         fields_count = d.readUnsignedShort();
         //G.v().out.println("Has " + fields_count + " field(s)");
         readFields(d);
         Timers.v().fieldTimer.end();
        
         Timers.v().methodTimer.start();
         methods_count = d.readUnsignedShort();
         //G.v().out.println("Has " + methods_count + " method(s)");
         readMethods(d);
         Timers.v().methodTimer.end();
        
         Timers.v().attributeTimer.start();
         
         attributes_count = d.readUnsignedShort();
         //G.v().out.println("Has " + attributes_count + " attribute(s)");
         if (attributes_count>0) {
            attributes =  new attribute_info[attributes_count];
            readAttributes(d,attributes_count,attributes);
         }
         Timers.v().attributeTimer.end();
         
      } catch(IOException e) {
         throw new RuntimeException("IOException with " + fn + ": " + e.getMessage(), e);
      }

      /*inf.fields = fields_count;
        inf.methods = methods_count;
        inf.cp = constant_pool_count;*/

      return true;
   }

   /** Reads in the constant pool from the given stream.
    * @param d Stream forming the <tt>.class</tt> file.
    * @return <i>true</i> if read was successful, <i>false</i> on some error.
    * @exception java.io.IOException on error.
    */
   protected boolean readConstantPool(DataInputStream d) throws IOException {
      byte tag;
      cp_info cp;
      int i;
      boolean skipone;   // set if next cp entry is to be skipped

      constant_pool = new cp_info[constant_pool_count];
      //Instruction.constant_pool = constant_pool;
      skipone = false;

      for (i=1;i<constant_pool_count;i++) {
         if (skipone) {
            skipone = false;
            continue;
         }
         tag = (byte)d.readUnsignedByte();
         switch(tag) {
         case cp_info.CONSTANT_Class:
            cp = new CONSTANT_Class_info();
            ((CONSTANT_Class_info)cp).name_index = d.readUnsignedShort();
            if (debug) G.v().out.println("Constant pool[" + i + "]: Class");
            break;
         case cp_info.CONSTANT_Fieldref:
            cp = new CONSTANT_Fieldref_info();
            ((CONSTANT_Fieldref_info)cp).class_index = d.readUnsignedShort();
            ((CONSTANT_Fieldref_info)cp).name_and_type_index =
                d.readUnsignedShort();
            if (debug) G.v().out.println("Constant pool[" + i + "]: Fieldref");
            break;
         case cp_info.CONSTANT_Methodref:
            cp = new CONSTANT_Methodref_info();
            ((CONSTANT_Methodref_info)cp).class_index = d.readUnsignedShort();
            ((CONSTANT_Methodref_info)cp).name_and_type_index =
               d.readUnsignedShort();
            if (debug) G.v().out.println("Constant pool[" + i + "]: Methodref");
            break;
         case cp_info.CONSTANT_InterfaceMethodref:
            cp = new CONSTANT_InterfaceMethodref_info();
            ((CONSTANT_InterfaceMethodref_info)cp).class_index =
               d.readUnsignedShort();
            ((CONSTANT_InterfaceMethodref_info)cp).name_and_type_index =
               d.readUnsignedShort();
            if (debug)
               G.v().out.println("Constant pool[" + i + "]: MethodHandle");
            break;
         case cp_info.CONSTANT_String:
            cp = new CONSTANT_String_info();
            ((CONSTANT_String_info)cp).string_index =
                d.readUnsignedShort();
            if (debug) G.v().out.println("Constant pool[" + i + "]: String");
            break;
         case cp_info.CONSTANT_Integer:
            cp = new CONSTANT_Integer_info();
            ((CONSTANT_Integer_info)cp).bytes = d.readInt();
            if (debug) G.v().out.println("Constant pool[" + i + "]: Integer = " +
                                          ((CONSTANT_Integer_info)cp).bytes);
            break;
         case cp_info.CONSTANT_Float:
            cp = new CONSTANT_Float_info();
            ((CONSTANT_Float_info)cp).bytes = d.readInt();
            if (debug) G.v().out.println("Constant pool[" + i + "]: Float = " +
                                          ((CONSTANT_Float_info)cp).convert());
            break;
         case cp_info.CONSTANT_Long:
            cp = new CONSTANT_Long_info();
            ((CONSTANT_Long_info)cp).high = d.readInt() & 0xFFFFFFFFL;
            ((CONSTANT_Long_info)cp).low = d.readInt() & 0xFFFFFFFFL;
            
            if (debug) {
               String temp = cp.toString(constant_pool);
               G.v().out.println("Constant pool[" + i + "]: Long = " + temp);
               /*G.v().out.println("Constant pool[" + i + "]: that's " +
                 cp.printBits(((CONSTANT_Long_info)cp).high) + " <<32 + " +
                 cp.printBits(((CONSTANT_Long_info)cp).low) + " = " +
                 cp.printBits(((CONSTANT_Long_info)cp).convert()));*/
            }
            skipone = true;  // next entry needs to be skipped
            break;
         case cp_info.CONSTANT_Double:
            cp = new CONSTANT_Double_info();
            ((CONSTANT_Double_info)cp).high = d.readInt() & 0xFFFFFFFFL;
            ((CONSTANT_Double_info)cp).low = d.readInt() & 0xFFFFFFFFL;
            if (debug) G.v().out.println("Constant pool[" + i + "]: Double = " +
                                          ((CONSTANT_Double_info)cp).convert());
            skipone = true;  // next entry needs to be skipped
            break;
         case cp_info.CONSTANT_NameAndType:
            cp = new CONSTANT_NameAndType_info();
            ((CONSTANT_NameAndType_info)cp).name_index =
               d.readUnsignedShort();
            ((CONSTANT_NameAndType_info)cp).descriptor_index =
               d.readUnsignedShort();
            if (debug) G.v().out.println("Constant pool[" + i + "]: Name and Type");
            break;
         case cp_info.CONSTANT_Utf8:
            CONSTANT_Utf8_info cputf8 = new CONSTANT_Utf8_info(d);
            // If an equivalent CONSTANT_Utf8 already exists, we return
            // the pre-existing one and allow cputf8 to be GC'd.
            cp = (cp_info) CONSTANT_Utf8_collector.v().add(cputf8);
            if (debug)
               G.v().out.println("Constant pool[" + i + "]: Utf8 = \"" +
                                  cputf8.convert() + "\"");
            break;
         case cp_info.CONSTANT_MethodHandle:
             cp = new CONSTANT_MethodHandle_info();
             ((CONSTANT_MethodHandle_info)cp).kind = d.readByte();
             ((CONSTANT_MethodHandle_info)cp).target_index = d.readUnsignedShort();
             break;
         case cp_info.CONSTANT_InvokeDynamic:
             cp = new CONSTANT_InvokeDynamic_info();
             ((CONSTANT_InvokeDynamic_info)cp).bootstrap_method_index = d.readUnsignedShort();
             ((CONSTANT_InvokeDynamic_info)cp).name_and_type_index = d.readUnsignedShort();
             break;
         default:
            G.v().out.println("Unknown tag in constant pool: " +
                               tag + " at entry " + i);
            return false;
         }
         cp.tag = tag;
         constant_pool[i] = cp;
      }
      return true;
   }

   private void readAllBytes(byte[] dest, DataInputStream d) throws IOException {
     int total_len = dest.length;
     int read_len = 0;
     while(read_len < total_len){
       int to_read = total_len - read_len;
       int curr_read = d.read(dest, read_len, to_read);
       read_len += curr_read;
     }
   }

   /** Reads in the given number of attributes from the given stream.
    * @param d Stream forming the <tt>.class</tt> file.
    * @param attributes_count number of attributes to read in.
    * @param ai pre-allocated array of attributes to be filled in.
    * @return <i>true</i> if read was successful, <i>false</i> on some error.
    * @exception java.io.IOException on error.
    */
   protected boolean readAttributes(DataInputStream d,int attributes_count,
                                    attribute_info[] ai) throws IOException {
      attribute_info a=null;
      int i;
      int j;
      long len;
      String s;

      for (i=0;i<attributes_count;i++) {
         
         j = d.readUnsignedShort();  // read attribute name before allocating
         len = d.readInt() & 0xFFFFFFFFL;
         s = ((CONSTANT_Utf8_info)(constant_pool[j])).convert();
         if (s.compareTo(attribute_info.SourceFile)==0) 
         {
            SourceFile_attribute sa = new SourceFile_attribute();
            sa.sourcefile_index = d.readUnsignedShort();
            a = (attribute_info)sa;
         } else if(s.compareTo(attribute_info.ConstantValue)==0) 
         {
            ConstantValue_attribute ca = new ConstantValue_attribute();
            ca.constantvalue_index = d.readUnsignedShort();
            a = (attribute_info)ca;
         } else if(s.compareTo(attribute_info.Code)==0) 
         {
            Code_attribute ca = new Code_attribute();
            ca.max_stack = d.readUnsignedShort();
            ca.max_locals = d.readUnsignedShort();
            ca.code_length = d.readInt() & 0xFFFFFFFFL;
            ca.code = new byte[(int) ca.code_length];
            readAllBytes(ca.code, d);
            ca.exception_table_length = d.readUnsignedShort();
            ca.exception_table = new exception_table_entry[ca.exception_table_length];
            int k;
            exception_table_entry e;
            for (k=0; k<ca.exception_table_length; k++) {
               e = new exception_table_entry();
               e.start_pc = d.readUnsignedShort();
               e.end_pc = d.readUnsignedShort();
               e.handler_pc = d.readUnsignedShort();
               e.catch_type = d.readUnsignedShort();
               ca.exception_table[k] = e;
            }
            ca.attributes_count = d.readUnsignedShort();
            ca.attributes = new attribute_info[ca.attributes_count];
            readAttributes(d,ca.attributes_count,ca.attributes);
            a = (attribute_info)ca;
            
            
         } else if(s.compareTo(attribute_info.Exceptions)==0) 
         {
            Exception_attribute ea = new Exception_attribute();
            ea.number_of_exceptions = d.readUnsignedShort();
            if (ea.number_of_exceptions>0) {
               int k;
               ea.exception_index_table = new int[ea.number_of_exceptions];
               for (k=0; k<ea.number_of_exceptions; k++)
                  ea.exception_index_table[k]  = d.readUnsignedShort();
            }
            a = (attribute_info)ea;
         } else if(s.compareTo(attribute_info.LineNumberTable)==0) 
         {
	     LineNumberTable_attribute la = new LineNumberTable_attribute();
	     la.line_number_table_length = d.readUnsignedShort();
	     int k;
	     line_number_table_entry e;
	     la.line_number_table = new
		 line_number_table_entry[la.line_number_table_length];
	     for (k=0; k<la.line_number_table_length; k++) {
		 e = new line_number_table_entry();
		 e.start_pc = d.readUnsignedShort();
		 e.line_number = d.readUnsignedShort();
		 la.line_number_table[k] = e;
	     }
	     a = (attribute_info)la;
         } 
         else if(s.compareTo(attribute_info.LocalVariableTable)==0) 
         {
            LocalVariableTable_attribute la = new LocalVariableTable_attribute();
            la.local_variable_table_length = d.readUnsignedShort();
            int k;
            local_variable_table_entry e;
            la.local_variable_table =
               new local_variable_table_entry[la.local_variable_table_length];
            for (k=0; k<la.local_variable_table_length; k++) {
               e = new local_variable_table_entry();
               e.start_pc = d.readUnsignedShort();
               e.length = d.readUnsignedShort();
               e.name_index = d.readUnsignedShort();
               e.descriptor_index = d.readUnsignedShort();
               e.index = d.readUnsignedShort();
               la.local_variable_table[k] = e;
            }
            a = (attribute_info)la;
	 } 
         else if(s.compareTo(attribute_info.LocalVariableTypeTable)==0) 
         {
            LocalVariableTypeTable_attribute la = new LocalVariableTypeTable_attribute();
            la.local_variable_type_table_length = d.readUnsignedShort();
            int k;
            local_variable_type_table_entry e;
            la.local_variable_type_table =
               new local_variable_type_table_entry[la.local_variable_type_table_length];
            for (k=0; k<la.local_variable_type_table_length; k++) {
               e = new local_variable_type_table_entry();
               e.start_pc = d.readUnsignedShort();
               e.length = d.readUnsignedShort();
               e.name_index = d.readUnsignedShort();
               e.signature_index = d.readUnsignedShort();
               e.index = d.readUnsignedShort();
               la.local_variable_type_table[k] = e;
            }
            a = (attribute_info)la;
	 } 
     else if (s.compareTo(attribute_info.Synthetic)==0){
        Synthetic_attribute ia = new Synthetic_attribute();
        a = (attribute_info)ia;
     }
     else if (s.compareTo(attribute_info.Signature)==0){
        Signature_attribute ia = new Signature_attribute();
        ia.signature_index = d.readUnsignedShort();
        a = (attribute_info)ia;
     }
     else if (s.compareTo(attribute_info.Deprecated)==0){
        Deprecated_attribute da = new Deprecated_attribute();
        a = (attribute_info)da;
     }
     else if (s.compareTo(attribute_info.EnclosingMethod)==0){
        EnclosingMethod_attribute ea = new EnclosingMethod_attribute();
        ea.class_index = d.readUnsignedShort();
        ea.method_index = d.readUnsignedShort();
        a = (attribute_info)ea;
     }
         else if(s.compareTo(attribute_info.InnerClasses)==0)
	 {
	    InnerClasses_attribute ia = new InnerClasses_attribute();
	    ia.inner_classes_length = d.readUnsignedShort();
	    ia.inner_classes = new inner_class_entry[ia.inner_classes_length];
	    for (int k = 0; k < ia.inner_classes_length; k++) {
	       inner_class_entry e = new inner_class_entry();
	       e.inner_class_index = d.readUnsignedShort();
	       e.outer_class_index = d.readUnsignedShort();
	       e.name_index = d.readUnsignedShort();
	       e.access_flags = d.readUnsignedShort();
	       ia.inner_classes[k] = e;
	    }
	    a = (attribute_info)ia;
        }
        else if (s.compareTo(attribute_info.RuntimeVisibleAnnotations)==0)
        {
            RuntimeVisibleAnnotations_attribute ra = new RuntimeVisibleAnnotations_attribute();
            ra.number_of_annotations = d.readUnsignedShort();
            ra.annotations = new annotation[ra.number_of_annotations];
            for (int k = 0; k < ra.number_of_annotations; k++){
                annotation annot = new annotation();
                annot.type_index = d.readUnsignedShort();
                annot.num_element_value_pairs = d.readUnsignedShort();
                annot.element_value_pairs = readElementValues(annot.num_element_value_pairs, d, true, 0);
                ra.annotations[k] = annot;
            }
        
            a = (attribute_info)ra;
         } 
        else if (s.compareTo(attribute_info.RuntimeInvisibleAnnotations)==0)
        {
            RuntimeInvisibleAnnotations_attribute ra = new RuntimeInvisibleAnnotations_attribute();
            ra.number_of_annotations = d.readUnsignedShort();
            ra.annotations = new annotation[ra.number_of_annotations];
            for (int k = 0; k < ra.number_of_annotations; k++){
                annotation annot = new annotation();
                annot.type_index = d.readUnsignedShort();
                annot.num_element_value_pairs = d.readUnsignedShort();
                annot.element_value_pairs = readElementValues(annot.num_element_value_pairs, d, true, 0);
                ra.annotations[k] = annot;
            }
            a = (attribute_info)ra;
         } 
        else if (s.compareTo(attribute_info.RuntimeVisibleParameterAnnotations)==0)
        {
            RuntimeVisibleParameterAnnotations_attribute ra = new RuntimeVisibleParameterAnnotations_attribute();
            ra.num_parameters = d.readUnsignedByte();
            ra.parameter_annotations = new parameter_annotation[ra.num_parameters];
            for (int x = 0; x < ra.num_parameters; x++){
                parameter_annotation pAnnot = new parameter_annotation();
                pAnnot.num_annotations = d.readUnsignedShort();
                pAnnot.annotations = new annotation[pAnnot.num_annotations];
                for (int k = 0; k < pAnnot.num_annotations; k++){
                    annotation annot = new annotation();
                    annot.type_index = d.readUnsignedShort();
                    annot.num_element_value_pairs = d.readUnsignedShort();
                    annot.element_value_pairs = readElementValues(annot.num_element_value_pairs, d, true, 0);
                    pAnnot.annotations[k] = annot;
                }
                ra.parameter_annotations[x] = pAnnot;
            }
            a = (attribute_info)ra;
         } 
        else if (s.compareTo(attribute_info.RuntimeInvisibleParameterAnnotations)==0)
        {
            RuntimeInvisibleParameterAnnotations_attribute ra = new RuntimeInvisibleParameterAnnotations_attribute();
            ra.num_parameters = d.readUnsignedByte();
            ra.parameter_annotations = new parameter_annotation[ra.num_parameters];
            for (int x = 0; x < ra.num_parameters; x++){
                parameter_annotation pAnnot = new parameter_annotation();
                pAnnot.num_annotations = d.readUnsignedShort();
                pAnnot.annotations = new annotation[pAnnot.num_annotations];
                for (int k = 0; k < pAnnot.num_annotations; k++){
                    annotation annot = new annotation();
                    annot.type_index = d.readUnsignedShort();
                    annot.num_element_value_pairs = d.readUnsignedShort();
                    annot.element_value_pairs = readElementValues(annot.num_element_value_pairs, d, true, 0);
                    pAnnot.annotations[k] = annot;
                }
                ra.parameter_annotations[x] = pAnnot;
            }
            a = (attribute_info)ra;
         }
         else if (s.compareTo(attribute_info.AnnotationDefault)==0){
             AnnotationDefault_attribute da = new AnnotationDefault_attribute();
             element_value [] result = readElementValues(1, d, false, 0);
             da.default_value = result[0];
             a = (attribute_info)da;
         } else if (s.equals(attribute_info.BootstrapMethods)){
            	 BootstrapMethods_attribute bsma = new BootstrapMethods_attribute();
            	 int count = d.readUnsignedShort();
            	 bsma.method_handles = new short[count];
            	 bsma.arg_indices = new short[count][];
            	 for(int num=0;num<count;num++) {
            		 short index = (short) d.readUnsignedShort();
            		 bsma.method_handles[num] = index;
            		 int argCount = d.readUnsignedShort();
            		 bsma.arg_indices[num] = new short[argCount];
            		 for(int numArg=0;numArg<argCount;numArg++) {
            			 short indexArg = (short) d.readUnsignedShort();
            			 bsma.arg_indices[num][numArg] = indexArg;
            		 }
            	 }
            	 assert bootstrap_methods_attribute==null : "More than one bootstrap methods attribute!";
            	 a = bootstrap_methods_attribute = bsma;
         } else {
            // unknown attribute
            // G.v().out.println("Generic/Unknown Attribute: " + s);
            Generic_attribute ga = new Generic_attribute();
            if (len>0) {
               ga.info = new byte[(int) len];
               readAllBytes(ga.info, d);
            }
            a = (attribute_info)ga;
         }
         a.attribute_name = j;
         a.attribute_length = len;
         ai[i] = a;
      }
      return true;
   }

    private element_value [] readElementValues(int count, DataInputStream d, boolean needName, int name_index)
        throws IOException {
        element_value [] list = new element_value[count];
        for (int x = 0; x < count; x++){
            if (needName){
                name_index = d.readUnsignedShort();
            }
            int tag = d.readUnsignedByte();
            char kind = (char)tag;
            if (kind == 'B' || kind == 'C' || kind == 'D' || kind == 'F' || kind == 'I' || kind == 'J' || kind == 'S' || kind == 'Z' || kind == 's'){
                constant_element_value elem = new constant_element_value();
                elem.name_index = name_index;
                elem.tag = kind;
                elem.constant_value_index = d.readUnsignedShort();
                list[x] = elem;
            }
            else if (kind == 'e'){
                enum_constant_element_value elem = new enum_constant_element_value();
                elem.name_index = name_index;
                elem.tag = kind;
                elem.type_name_index = d.readUnsignedShort();
                elem.constant_name_index = d.readUnsignedShort();
                list[x] = elem;
            }
            else if (kind == 'c'){
                class_element_value elem = new class_element_value();
                elem.name_index = name_index;
                elem.tag = kind;
                elem.class_info_index = d.readUnsignedShort();
                list[x] = elem;
            }
            else if (kind == '['){
                array_element_value elem = new array_element_value();
                elem.name_index = name_index;
                elem.tag = kind;
                elem.num_values = d.readUnsignedShort();
                elem.values = readElementValues(elem.num_values, d, false, name_index);
                list[x] = elem;
            }
            else if (kind == '@'){
                annotation_element_value elem = new annotation_element_value();
                elem.name_index = name_index;
                elem.tag = kind;
                annotation annot = new annotation();
                annot.type_index = d.readUnsignedShort();
                annot.num_element_value_pairs = d.readUnsignedShort();
                annot.element_value_pairs = readElementValues(annot.num_element_value_pairs, d, true, 0);
                elem.annotation_value = annot;
                list[x] = elem;
            }
            else {
                throw new RuntimeException("Unknown element value pair kind: "+kind);
            }
        }
        return list;
    }

   /** Reads in the fields from the given stream.
    * @param d Stream forming the <tt>.class</tt> file.
    * @return <i>true</i> if read was successful, <i>false</i> on some error.
    * @exception java.io.IOException on error.
    */
   protected boolean readFields(DataInputStream d) throws IOException {
      field_info fi;
      int i;

      fields = new field_info[fields_count];

      for (i=0;i<fields_count;i++) {
         fi = new field_info();
         fi.access_flags = d.readUnsignedShort();
         fi.name_index = d.readUnsignedShort();
         fi.descriptor_index = d.readUnsignedShort();
         fi.attributes_count = d.readUnsignedShort();
         if (fi.attributes_count>0) {
            fi.attributes = new attribute_info[fi.attributes_count];
            readAttributes(d,fi.attributes_count,fi.attributes);
         }
         /*CONSTANT_Utf8_info ci;
           ci = (CONSTANT_Utf8_info)(constant_pool[fi.name_index]);
           G.v().out.println("Field: " + ci.convert());*/
         fields[i] = fi;
      }

      return true;
   }

   /** Reads in the methods from the given stream.
    * @param d Stream forming the <tt>.class</tt> file.
    * @return <i>true</i> if read was successful, <i>false</i> on some error.
    * @exception java.io.IOException on error.
    */
   protected boolean readMethods(DataInputStream d) throws IOException {
      method_info mi;
      int i;

      methods = new method_info[methods_count];

      for (i=0;i<methods_count;i++) {
         mi = new method_info();
         mi.access_flags = d.readUnsignedShort();
         
         mi.name_index = d.readUnsignedShort();
         
         mi.descriptor_index = d.readUnsignedShort();
         
         mi.attributes_count = d.readUnsignedShort();

         CONSTANT_Utf8_info ci;
           ci = (CONSTANT_Utf8_info)(constant_pool[mi.name_index]);
          //G.v().out.println("Has " + mi.attributes_count + " attribute(s)");
         
         if (mi.attributes_count>0) {
            mi.attributes = new attribute_info[mi.attributes_count];
            readAttributes(d,mi.attributes_count,mi.attributes);

	    for (int j=0; j<mi.attributes_count; j++)
	    {
		if (mi.attributes[j] instanceof Code_attribute)
		{
		    mi.code_attr = (Code_attribute)mi.attributes[j];
		    break;
		}
	    }
         }

         /*if ("main".compareTo(ci.convert())==0) {
           decompile(mi);
           }*/

         methods[i] = mi;
      }

      return true;
   }

   /* DEPRECATED
      public void showByteCode(Code_attribute ca) {
      int i=0,j;

      G.v().out.println("Code bytes follow...");
      while(i<ca.code_length) {
      j = (int)(ca.code[i]);
      j &= 0xff;
      G.v().out.print(Integer.toString(j) + " ");
      i++;
      }
      G.v().out.println("");
      }*/

   /** Writes the current constant pool to the given stream.
    * @param dd output stream.
    * @return <i>true</i> if write was successful, <i>false</i> on some error.
    * @exception java.io.IOException on error.
    */
   protected boolean writeConstantPool(DataOutputStream dd) throws IOException {
      byte tag;
      cp_info cp;
      int i;
      boolean skipone = false;

      for (i=1;i<constant_pool_count;i++) {
         if (skipone) {
            skipone = false;
            continue;
         }
         cp = constant_pool[i];
         dd.writeByte(cp.tag);
         switch(cp.tag) {
         case cp_info.CONSTANT_Class:
            dd.writeShort(((CONSTANT_Class_info)cp).name_index);
            break;
         case cp_info.CONSTANT_Fieldref:
            dd.writeShort(((CONSTANT_Fieldref_info)cp).class_index);
            dd.writeShort(((CONSTANT_Fieldref_info)cp).name_and_type_index);
            break;
         case cp_info.CONSTANT_Methodref:
            dd.writeShort(((CONSTANT_Methodref_info)cp).class_index);
            dd.writeShort(((CONSTANT_Methodref_info)cp).name_and_type_index);
            break;
         case cp_info.CONSTANT_InterfaceMethodref:
            dd.writeShort(((CONSTANT_InterfaceMethodref_info)cp).class_index);
            dd.writeShort(((CONSTANT_InterfaceMethodref_info)cp).name_and_type_index);
            break;
         case cp_info.CONSTANT_String:
            dd.writeShort(((CONSTANT_String_info)cp).string_index);
            break;
         case cp_info.CONSTANT_Integer:
            dd.writeInt((int) ((CONSTANT_Integer_info)cp).bytes);
            break;
         case cp_info.CONSTANT_Float:
            dd.writeInt((int) ((CONSTANT_Float_info)cp).bytes);
            break;
         case cp_info.CONSTANT_Long:
            dd.writeInt((int) ((CONSTANT_Long_info)cp).high);
            dd.writeInt((int) ((CONSTANT_Long_info)cp).low);
            skipone = true;
            break;
         case cp_info.CONSTANT_Double:
            dd.writeInt((int) ((CONSTANT_Double_info)cp).high);
            dd.writeInt((int) ((CONSTANT_Double_info)cp).low);
            skipone = true;
            break;
         case cp_info.CONSTANT_NameAndType:
            dd.writeShort(((CONSTANT_NameAndType_info)cp).name_index);
            dd.writeShort(((CONSTANT_NameAndType_info)cp).descriptor_index);
            break;
         case cp_info.CONSTANT_Utf8:
            ((CONSTANT_Utf8_info)cp).writeBytes(dd);
            break;
         default:
            G.v().out.println("Unknown tag in constant pool: " + cp.tag);
            return false;
         }
      }
      return true;
   }

   /** Writes the given array of attributes to the given stream.
    * @param dd output stream.
    * @param attributes_count number of attributes to write.
    * @param ai array of attributes to write.
    * @return <i>true</i> if write was successful, <i>false</i> on some error.
    * @exception java.io.IOException on error.
    */
   protected boolean writeAttributes(DataOutputStream dd, int attributes_count,
                                     attribute_info[] ai) throws IOException {
      attribute_info a=null;
      int i,len;
      short j;
      String s;

      for (i=0;i<attributes_count;i++) {
         a = ai[i];
         dd.writeShort(a.attribute_name);
         dd.writeInt((int) a.attribute_length);
         if (a instanceof SourceFile_attribute) {
            SourceFile_attribute sa = (SourceFile_attribute)a;
            dd.writeShort(sa.sourcefile_index);
         } else if(a instanceof ConstantValue_attribute) {
            ConstantValue_attribute ca = (ConstantValue_attribute)a;
            dd.writeShort(ca.constantvalue_index);
         } else if(a instanceof Code_attribute) {
            Code_attribute ca = (Code_attribute)a;
            dd.writeShort(ca.max_stack);
            dd.writeShort(ca.max_locals);
            dd.writeInt((int) ca.code_length);
            dd.write(ca.code,0, (int) ca.code_length);
            dd.writeShort(ca.exception_table_length);
            int k;
            exception_table_entry e;
            for (k=0; k<ca.exception_table_length; k++) {
               e = ca.exception_table[k];
               dd.writeShort(e.start_pc);
               dd.writeShort(e.end_pc);
               dd.writeShort(e.handler_pc);
               dd.writeShort(e.catch_type);
            }
            dd.writeShort(ca.attributes_count);
            if (ca.attributes_count>0)
               writeAttributes(dd,ca.attributes_count,ca.attributes);
         } else if(a instanceof Exception_attribute) {
            Exception_attribute ea = (Exception_attribute)a;
            dd.writeShort(ea.number_of_exceptions);
            if (ea.number_of_exceptions>0) {
               int k;
               for (k=0; k<ea.number_of_exceptions; k++)
                  dd.writeShort(ea.exception_index_table[k]);
            }
         } else if(a instanceof LineNumberTable_attribute) {
            LineNumberTable_attribute la = (LineNumberTable_attribute)a;
            dd.writeShort(la.line_number_table_length);
            int k;
            line_number_table_entry e;
            for (k=0; k<la.line_number_table_length; k++) {
               e = la.line_number_table[k];
               dd.writeShort(e.start_pc);
               dd.writeShort(e.line_number);
            }
         } else if(a instanceof LocalVariableTable_attribute) {
            LocalVariableTable_attribute la = (LocalVariableTable_attribute)a;
            dd.writeShort(la.local_variable_table_length);
            int k;
            local_variable_table_entry e;
            for (k=0; k<la.local_variable_table_length; k++) {
               e = la.local_variable_table[k];
               dd.writeShort(e.start_pc);
               dd.writeShort(e.length);
               dd.writeShort(e.name_index);
               dd.writeShort(e.descriptor_index);
               dd.writeShort(e.index);
            }
         } else {
            // unknown attribute
            G.v().out.println("Generic/Unknown Attribute in output");
            Generic_attribute ga = (Generic_attribute)a;
            if (ga.attribute_length>0) {
               dd.write(ga.info,0,(int) ga.attribute_length);
            }
         }
      }
      return true;
   }

   /** Writes the fields to the given stream.
    * @param dd output stream.
    * @return <i>true</i> if write was successful, <i>false</i> on some error.
    * @exception java.io.IOException on error.
    */
   protected boolean writeFields(DataOutputStream dd) throws IOException {
      field_info fi;
      int i;

      for (i=0;i<fields_count;i++) {
         fi = fields[i];
         dd.writeShort(fi.access_flags);
         dd.writeShort(fi.name_index);
         dd.writeShort(fi.descriptor_index);
         dd.writeShort(fi.attributes_count);
         if (fi.attributes_count>0) {
            writeAttributes(dd,fi.attributes_count,fi.attributes);
         }
      }
      return true;
   }

   /** Writes the methods to the given stream.
    * @param dd output stream.
    * @return <i>true</i> if write was successful, <i>false</i> on some error.
    * @exception java.io.IOException on error.
    */
   protected boolean writeMethods(DataOutputStream dd) throws IOException {
      method_info mi;
      int i;

      for (i=0;i<methods_count;i++) {
         mi = methods[i];
         dd.writeShort(mi.access_flags);
         dd.writeShort(mi.name_index);
         dd.writeShort(mi.descriptor_index);
         dd.writeShort(mi.attributes_count);
         if (mi.attributes_count>0) {
            writeAttributes(dd,mi.attributes_count,mi.attributes);
         }
      }
      return true;
   }

   /** Writes this entire ClassFile object to the given stream.
    * @param dd output stream.
    * @return <i>true</i> if write was successful, <i>false</i> on some error.
    */
    boolean writeClass(DataOutputStream dd) {
      // outputs the .class file from the loaded one
      try {
         // first write magic number
         dd.writeInt((int) magic);

         dd.writeShort(minor_version);
         dd.writeShort(major_version);
         dd.writeShort(constant_pool_count);

         if (!writeConstantPool(dd))
            return false;

         dd.writeShort(access_flags);
         dd.writeShort(this_class);
         dd.writeShort(super_class);
         dd.writeShort(interfaces_count);
         if (interfaces_count>0) {
            int j;
            for (j=0; j<interfaces_count; j++)
               dd.writeShort(interfaces[j]);
         }

         dd.writeShort(fields_count);
         writeFields(dd);

         dd.writeShort(methods_count);
         writeMethods(dd);

         dd.writeShort(attributes_count);
         if (attributes_count>0) {
            writeAttributes(dd,attributes_count,attributes);
         }
      } catch(IOException e) {
         G.v().out.println("IOException with " + fn + ": " + e.getMessage());
         return false;
      }
      return true;
   }

   /** Parses the given method, converting its bytecode array into a list
    * of Instruction objects.
    * @param m method to parse.
    * @return head of a list of Instructions.
    * @see Instruction
    * @see ByteCode
    * @see ByteCode#disassemble_bytecode
    */
    public Instruction parseMethod(method_info m) {
      // first task, look through attributes for a code attribute
      int j;
      Code_attribute ca;
      ByteCode bc;
      Instruction inst,head,tail;
      exception_table_entry e;

      head = null;
      tail = null;
      bc = new ByteCode();

      ca = m.locate_code_attribute();
      if (ca==null) return null;

      j = 0;
      while(j<ca.code_length) {
         inst = bc.disassemble_bytecode(ca.code,j);
         inst.originalIndex = j;
         // G.v().out.println(inst + ": " + (((int)(inst.code))&0xff));
         // G.v().out.println(j + " : " + inst);

         if (inst instanceof Instruction_Unknown) {
            G.v().out.println("Unknown instruction in \"" + m.toName(constant_pool) +
                               "\" at offset " + j);
            G.v().out.println(" bytecode = " + (((int)(inst.code))&0xff));
         }
         // G.v().out.println("before: " + j);
         j = inst.nextOffset(j);
         // G.v().out.println("after: " + j);

         if ( head==null ) 
	     head = inst;
         else
	 { 	     
	     tail.next = inst;
	     inst.prev = tail;
	 }
         tail = inst;
      }

      // bytecode converted into instructions, now build pointers
      bc.build(head);

      // also change exception table to use pointers instead of absolute addresses
      for (j=0;j<ca.exception_table_length;j++) {
         e = ca.exception_table[j];
         e.start_inst = bc.locateInst(e.start_pc);
         if (e.end_pc == ca.code_length)
            e.end_inst = null;
         else
            e.end_inst = bc.locateInst(e.end_pc);
         e.handler_inst = bc.locateInst(e.handler_pc);
         if (e.handler_inst!=null)
            e.handler_inst.labelled = true;
      }

      m.instructions = head;

      for (attribute_info element : ca.attributes) {
	  if (element instanceof LineNumberTable_attribute)
	  {
	      LineNumberTable_attribute lntattr =
		  (LineNumberTable_attribute)element;

	      for (line_number_table_entry element0 : lntattr.line_number_table) {
		  element0.start_inst =
		      bc.locateInst(element0.start_pc);
	      }
	  }
      }

      return head;
   }

   /** For every method, this calls parseMethod, storing the list of Instructions
    * in the method_info object, and also constructs the corresponding CFG.
    * @see ClassFile#parseMethod
    * @see CFG
    */
    public void parse() {
      method_info mi;
      int i;

      for (i=0;i<methods_count;i++) {
         mi = methods[i];
         mi.instructions = parseMethod(mi);
         //new CFG(mi);
         // don't build it right away for now
      }
   }

   /** Recomputes the offset of each Instruction starting from 0;
    * used when converting references back to offsets.
    * @param i list of Instructions to process.
    * @return length of corresponding bytecode.
    * @see Instruction#nextOffset
    */
    int relabel(Instruction i) {
      int index = 0;
      while (i!=null) {
         i.label = index;
         index = i.nextOffset(index);
         i = i.next;
      }
      return index;
   }

   /** Inversive to parseMethod, this converts the list of
    * Instructions stored in a method_info object back to an
    * array of bytecode.
    * @param m method to unparse.
    * @return array of bytecode, or <i>null</i> on error.
    * @see CFG#reconstructInstructions
    * @see ClassFile#parseMethod
    * @see ClassFile#relabel
    * @see Instruction#compile
    */
    byte[] unparseMethod(method_info m) {
      int codesize;
      byte bc[];
      Instruction i;

      // Rebuild instruction sequence
      m.cfg.reconstructInstructions();

      // relabel instructions and get size of code array
      codesize = relabel(m.instructions);

      // construct a new array for the byte-code
      bc = new byte[codesize];
      if (bc==null) {
         G.v().out.println("Warning: can't allocate memory for recompile");
         return null;
      }

      // then recompile the instructions into byte-code
      i = m.instructions;
      codesize = 0;
      while (i!=null) {
         codesize = i.compile(bc,codesize);
         i = i.next;
      }
      if (codesize != bc.length)
         G.v().out.println("Warning: code size doesn't match array length!");

      return bc;
   }

   /** Inversive to parse, this method calls unparseMethod for each
    * method, storing the resulting bytecode in the method's code
    * attribute, and recomputing offsets for exception handlers.
    * @see ClassFile#unparseMethod
    */
    void unparse() {
      int i,j;
      Code_attribute ca;
      byte bc[];
      method_info mi;
      exception_table_entry e;

      for (i=0;i<methods_count;i++) {
         mi = methods[i];
         // locate code attribute
         ca = mi.locate_code_attribute();
         if (ca==null) continue;
         bc = unparseMethod(mi);
         if (bc==null) {
            G.v().out.println("Recompile of " + mi.toName(constant_pool) + " failed!");
         } else {
            ca.code_length = bc.length;
            ca.code = bc;
            // also recompile exception table
            for (j=0;j<ca.exception_table_length;j++) {
               e = ca.exception_table[j];
               e.start_pc = (e.start_inst.label);
               if (e.end_inst!=null)
                  e.end_pc = (e.end_inst.label);
               else
                  e.end_pc = (int) (ca.code_length);
               e.handler_pc = (e.handler_inst.label);
            }
         }
      }
   }

   /** Static utility method to parse the given method descriptor string.
    * @param s descriptor string.
    * @return return type of method.
    * @see ClassFile#parseDesc
    * @see ClassFile#parseMethodDesc_params
    */
    static String parseMethodDesc_return(String s) {
      int j;
      j = s.lastIndexOf(')');
      if (j>=0) {
         return parseDesc(s.substring(j+1),",");
      }
      return parseDesc(s,",");
   }

   /** Static utility method to parse the given method descriptor string.
    * @param s descriptor string.
    * @return comma-separated ordered list of parameter types
    * @see ClassFile#parseDesc
    * @see ClassFile#parseMethodDesc_return
    */
    static String parseMethodDesc_params(String s) {
      int i,j;
      i = s.indexOf('(');
      if (i>=0) {
         j = s.indexOf(')',i+1);
         if (j>=0) {
            return parseDesc(s.substring(i+1,j),",");
         }
      }
      return "<parse error>";
   }

   /** Static utility method to parse the given method descriptor string.
    * @param desc descriptor string.
    * @param sep String to use as a separator between types.
    * @return String of types parsed.
    * @see ClassFile#parseDesc
    * @see ClassFile#parseMethodDesc_return
    */
    static String parseDesc(String desc,String sep) {
      String params = "",param;
      char c;
      int i,len,arraylevel=0;
      boolean didone = false;

      len = desc.length();
      for (i=0;i<len;i++) {
         c = desc.charAt(i);
         if (c==DESC_BYTE.charAt(0)) {
            param = "byte";
         } else if (c==DESC_CHAR.charAt(0)) {
            param = "char";
         } else if (c==DESC_DOUBLE.charAt(0)) {
            param = "double";
         } else if (c==DESC_FLOAT.charAt(0)) {
            param = "float";
         } else if (c==DESC_INT.charAt(0)) {
            param = "int";
         } else if (c==DESC_LONG.charAt(0)) {
            param = "long";
         } else if (c==DESC_SHORT.charAt(0)) {
            param = "short";
         } else if (c==DESC_BOOLEAN.charAt(0)) {
            param = "boolean";
         } else if (c==DESC_VOID.charAt(0)) {
            param = "void";
         } else if (c==DESC_ARRAY.charAt(0)) {
            arraylevel++;
            continue;
         } else if (c==DESC_OBJECT.charAt(0)) {
            int j;
            j = desc.indexOf(';',i+1);
            if (j<0) {
               G.v().out.println("Warning: Parse error -- can't find a ; in " +
                                  desc.substring(i+1));
               param = "<error>";
            } else {
               if (j-i>10 && desc.substring(i+1,i+11).compareTo("java/lang/")==0)
                  i = i+10;
               param = desc.substring(i+1,j);
               // replace '/'s with '.'s
               param = param.replace('/','.');
               i = j;
            }
         } else    {
            param = "???";
         }
         if (didone) params = params + sep;
         params = params + param;
         while (arraylevel>0) {
            params = params + "[]";
            arraylevel--;
         }
         didone = true;
      }
      return params;
   }


   /** Locates a method by name.
    * @param s name of method.
    * @return method_info object representing method, or <i>null</i> if not found.
    * @see method_info#toName
    */
    method_info findMethod(String s) {
      method_info m;
      int i;

      for (i=0;i<methods_count;i++) {
         m = methods[i];
         if (s.equals(m.toName(constant_pool))) {
            return m;
         }
      }
      return null;
   }

   /** Displays a the prototypes for all the methods defined in this ClassFile.
    * @see ClassFile#methods
    * @see ClassFile#methods_count
    * @see method_info#prototype
    */
    void listMethods() {
      int i;

      for (i=0;i<methods_count;i++) {
         G.v().out.println(methods[i].prototype(constant_pool));
      }
   }

   /** Displays the entire constant pool.
    * @see ClassFile#constant_pool
    * @see ClassFile#constant_pool_count
    * @see cp_info#toString
    */
    void listConstantPool() {
      cp_info c;
      int i;

      // note that we start at 1 in the constant pool
      for (i=1;i<constant_pool_count;i++) {
         c = constant_pool[i];
         G.v().out.println("[" + i + "] " + c.typeName() +
                            "=" + c.toString(constant_pool));
         if ((constant_pool[i]).tag==cp_info.CONSTANT_Long ||
             (constant_pool[i]).tag==cp_info.CONSTANT_Double) {
            // must skip an entry after a long or double constant
            i++;
         }
      }
   }

   /** Displays the list of fields defined in this ClassFile, including
    * any static initializers (constants).
    * @see ClassFile#fields
    * @see ClassFile#fields_count
    * @see field_info#prototype
    * @see ConstantValue_attribute
    */
    void listFields() {
      field_info fi;
      ConstantValue_attribute cva;
      CONSTANT_Utf8_info cm;
      int i,j;

      for (i=0;i<fields_count;i++) {
         fi = fields[i];
         G.v().out.print(fi.prototype(constant_pool));
         // see if has a constant value attribute
         for (j=0;j<fi.attributes_count;j++) {
            cm = (CONSTANT_Utf8_info)(constant_pool[fi.attributes[j].attribute_name]);
            if (cm.convert().compareTo(attribute_info.ConstantValue)==0) {
               cva = (ConstantValue_attribute)(fi.attributes[j]);
               //dm = (CONSTANT_Utf8_info)(constant_pool[cva.constantvalue_index]);
               G.v().out.print(" = " +
                                constant_pool[cva.constantvalue_index].
                                toString(constant_pool));
               break;
            }
         }
         G.v().out.println(";");
      }
   }

   /** Moves a method to a different index in the methods array.
    * @param m name of method to move.
    * @param pos desired index.
    * @see ClassFile#methods
    */
    void moveMethod(String m,int pos) {
      int i,j;
      method_info mthd;
      G.v().out.println("Moving " + m + " to position " + pos +
                         " of " + methods_count);

      for (i=0;i<methods_count;i++) {
         if (m.compareTo(methods[i].toName(constant_pool))==0) {
            mthd = methods[i];
            if (i>pos) {
               for (j=i;j>pos && j>0;j--)
                  methods[j] = methods[j-1];
               methods[pos] = mthd;
            } else if (i<pos) {
               for (j=i;j<pos && j<methods_count-1;j++)
                  methods[j] = methods[j+1];
               methods[pos] = mthd;
            }
            return;
         }
      }
   }

   /** Answers whether this class is an immediate descendant (as subclass or
    * as an implementation of an interface) of the given class.
    * @param cf ClassFile of supposed parent.
    * @return <i>true</i> if it is a parent, <i>false</i> otherwise.
    * @see ClassFile#descendsFrom(String)
    */
    boolean descendsFrom(ClassFile cf) { return descendsFrom(cf.toString()); }

   /** Answers whether this class is an immediate descendant (as subclass or
    * as an implementation of an interface) of the given class.
    * @param cname name of supposed parent.
    * @return <i>true</i> if it is a parent, <i>false</i> otherwise.
    * @see ClassFile#descendsFrom(ClassFile)
    */
    boolean descendsFrom(String cname) {
      cp_info cf;
      int i;
      cf = constant_pool[super_class];
      if (cf.toString(constant_pool).compareTo(cname)==0) return true;
      for (i=0;i<interfaces_count;i++) {
         cf = constant_pool[interfaces[i]];
         if (cf.toString(constant_pool).compareTo(cname)==0) return true;
      }
      return false;
   }

   /** Answers whether this class can have subclasses outside its package.
    * @return <i>true</i> if it cannot, <i>false</i> if it might.
    */
    boolean isSterile() {
      if ((access_flags&ACC_PUBLIC)!=0 && (access_flags&ACC_FINAL)==0) return false;
      return true;
   }

   /** Given the name of a class --- possibly with <tt>.class</tt> after it,
    * this answers whether the class might refer to this ClassFile object.
    * @return <i>true</i> if it does, <i>false</i> if it doesn't.
    */
    boolean sameClass(String cfn) {
      String s = cfn;
      int i = s.lastIndexOf(".class");
      if (i>0) {  // has .class after it
         s = s.substring(0,i);  // cut off the .class
      }
      if (s.compareTo(toString())==0)
         return true;
      return false;
   }

   /** Returns the name of a specific field in the field array.
    * @param i index of field in field array.
    * @return name of field.
    */
    String fieldName(int i) {
      return fields[i].toName(constant_pool);
   }

   /* DEPRECATED
   // Locates the given classfile, and extracts it from the list.
   // It cannot be the first one in the list, and this returns null
   // or the classfile.
    static ClassFile removeClassFile(ClassFile cfhead,String cfn) {
      ClassFile cf,cfprev;
      cf = cfhead;
      cfprev = null;
      while (cf!=null) {
         if (cf.sameClass(cfn)) {
            if (cfprev==null) return null; // this shouldn't happen
            cfprev.next = cf.next;
            cf.next = null;
            return cf;
         }
         cfprev = cf;
         cf = cf.next;
      }
      return null;
   }

   // returns true if this class contains any references to the given
   // cuClass.cuName, which is of type cuDesc.  Searches for methods if
   // ismethod is true, fields otherwise.
   boolean refersTo(boolean ismethod,CONSTANT_Utf8_info cuClass,
                    CONSTANT_Utf8_info cuName,CONSTANT_Utf8_info cuDesc) {
      int i;
      CONSTANT_Utf8_info cu;
      // note that we start at 1 in the constant pool
      if (ismethod) {
         for (i=1;i<constant_pool_count;i++) {
            if ((constant_pool[i]).tag==cp_info.CONSTANT_Methodref) {
               CONSTANT_Methodref_info cf = (CONSTANT_Methodref_info)(constant_pool[i]);
               CONSTANT_Class_info cc = (CONSTANT_Class_info)
                  (constant_pool[cf.class_index]);
               if (cuClass.equals((CONSTANT_Utf8_info)
                                  (constant_pool[cc.name_index]))) {
                  CONSTANT_NameAndType_info cn = (CONSTANT_NameAndType_info)
                     (constant_pool[cf.name_and_type_index]);
                  if (cuName.equals((CONSTANT_Utf8_info)
                                    (constant_pool[cn.name_index])) &&
                      cuDesc.equals((CONSTANT_Utf8_info)
                                    (constant_pool[cn.descriptor_index])))
                     return true;
               }
            } else if ((constant_pool[i]).tag==
                       cp_info.CONSTANT_InterfaceMethodref) {
               CONSTANT_InterfaceMethodref_info cf =
                  (CONSTANT_InterfaceMethodref_info)(constant_pool[i]);
               CONSTANT_Class_info cc = (CONSTANT_Class_info)
                  (constant_pool[cf.class_index]);
               if (cuClass.equals((CONSTANT_Utf8_info)
                                  (constant_pool[cc.name_index]))) {
                  CONSTANT_NameAndType_info cn = (CONSTANT_NameAndType_info)
                     (constant_pool[cf.name_and_type_index]);
                  if (cuName.equals((CONSTANT_Utf8_info)
                                    (constant_pool[cn.name_index])) &&
                      cuDesc.equals((CONSTANT_Utf8_info)
                                    (constant_pool[cn.descriptor_index])))
                     return true;
               }
            } else if ((constant_pool[i]).tag==cp_info.CONSTANT_Long ||
                       (constant_pool[i]).tag==cp_info.CONSTANT_Double) {
               // must skip an entry after a long or double constant
               i++;
            }
         }
      } else {
         for (i=1;i<constant_pool_count;i++) {
            if ((constant_pool[i]).tag==cp_info.CONSTANT_Fieldref) {
               CONSTANT_Fieldref_info cf = (CONSTANT_Fieldref_info)(constant_pool[i]);
               CONSTANT_Class_info cc = (CONSTANT_Class_info)
                  (constant_pool[cf.class_index]);
               if (cuClass.equals((CONSTANT_Utf8_info)
                                  (constant_pool[cc.name_index]))) {
                  CONSTANT_NameAndType_info cn = (CONSTANT_NameAndType_info)
                     (constant_pool[cf.name_and_type_index]);
                  if (cuName.equals((CONSTANT_Utf8_info)
                                    (constant_pool[cn.name_index])) &&
                      cuDesc.equals((CONSTANT_Utf8_info)
                                    (constant_pool[cn.descriptor_index])))
                     return true;
               }
            } else if ((constant_pool[i]).tag==cp_info.CONSTANT_Long ||
                       (constant_pool[i]).tag==cp_info.CONSTANT_Double) {
               // must skip an entry after a long or double constant
               i++;
            }
         }
      }
      return false;
   }

   // produces a sorted array of constant pool indices, one for each Utf8 entry used
   // by any field
   short[] forbiddenFields() {
      short fFields[] = new short[fields_count];
      for (int i=0;i<fields_count;i++) {
         fFields[i] = fields[i].name_index;
      }
      // now to sort the array
      return sortShorts(fFields);
   }

   // sorts an array of shorts using selection sort.  It's assumed no valid
   // entry is 0.
   static short[] sortShorts(short a[]) {
      int i,largest;
      short s;
      for(largest = a.length-1;largest>=1;largest--) {
         for (i=0;i<largest;i++) {
            if (a[i]>a[largest]) {
               s = a[i];
               a[i] = a[largest];
               a[largest] = s;
            }
         }
      }
      return a;
   }

   // Given a new constant pool, and a list of redirections
   // (new index = redirect[old index]), this changes all constant
   // pool entries, and installs the new constant pool of size size
   void changeConstantPool(short redirect[],cp_info newCP[],short size) {
      Debig d = new Debig(this);
      d.redirectCPRefs(redirect);
      constant_pool = newCP;
      constant_pool_count = size;
   }

   // the constant pool is typically a few hundred entries in size, and so
   // is just a bit too big to make use of insertion/selection sort.
   // However, the variable size of the entries makes using a heapsort
   // or quicksort rather cumbersome, so since it is quite close to the
   // limits of efficient insertion/selection sort, we'll use that anyway.
    void sortConstantPool() {
      cp_info newcp[] = new cp_info[constant_pool_count];
      short redirect[] = new short[constant_pool_count];
      newcp[0] = constant_pool[0];  // the 0-entry stays put
      redirect[0] = (short)0;
      int smallest,j;
      for (int i=1;i<constant_pool_count;i++) redirect[i] = (short)0;
      for (int i=1;i<constant_pool_count;i++) {
         for (smallest = 1;smallest<constant_pool_count;smallest++)
            if (redirect[smallest]==(short)0) break;
         //G.v().out.println(" smallest = " + smallest);
         j = (constant_pool[smallest].tag==cp_info.CONSTANT_Double ||
              constant_pool[smallest].tag==cp_info.CONSTANT_Long) ? smallest+2 : smallest+1;
         for (;j<constant_pool_count;j++) {
            if ((redirect[j]==(short)0) && constant_pool[j].
                compareTo(constant_pool,constant_pool[smallest],constant_pool)<0) {
               smallest = j;
            }
            if (constant_pool[j].tag==cp_info.CONSTANT_Double ||
                constant_pool[j].tag==cp_info.CONSTANT_Long) j++;
         }
         redirect[smallest] = (short)i;
         newcp[i] = constant_pool[smallest];
         //G.v().out.println(" Smallest cp entry is [" + smallest + "] = " + constant_pool[smallest]
         //                 + " -> " + i);

         if (constant_pool[smallest].tag==cp_info.CONSTANT_Double ||
             constant_pool[smallest].tag==cp_info.CONSTANT_Long) {
            redirect[++smallest] = (short)(++i);
            newcp[i] = constant_pool[smallest];
         }
      }
      // constant pool is now sorted into newcp
      changeConstantPool(redirect,newcp,constant_pool_count);
      G.v().out.println("Finished sorting constant pool");
   }

   // just a wrapper for the debigulation, so we can elegantly allocate
   // a new debigulator, debigualte and then produce some output
    void debigulate(boolean attribs,boolean privates) {
      Debig debigulator = new Debig(this);
      debigulator.debigulate(attribs,privates);
      debigulator.setCF(null);

      inf.verboseReport(G.v().out);
   }*/


}






