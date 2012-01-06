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

/** Generic superclass for all attributes.
 * @author Clark Verbrugge
 */
class attribute_info {
   /** String by which a SourceFile attribute is recognized.
    * @see SourceFile_attribute
    */
   public static final String SourceFile = "SourceFile";
   /** String by which a ConstantValue attribute is recognized.
    * @see ConstantValue_attribute
    */
   public static final String ConstantValue = "ConstantValue";
   /** String by which a Code attribute is recognized.
    * @see Code_attribute
    */
   public static final String Code = "Code";
   /** String by which an Exceptions attribute is recognized.
    * @see Exception_attribute
    */
   public static final String Exceptions = "Exceptions";
   /** String by which a LineNumberTable attribute is recognized.
    * @see LineNumberTable_attribute
    */
   public static final String LineNumberTable = "LineNumberTable";
   /** String by which a LocalVariableTable attribute is recognized.
    * @see LocalVariableTable_attribute
    */
   public static final String LocalVariableTable = "LocalVariableTable";
   /** String by which a InnerClasses attribute is recognized.
    * @see InnerClasses_attribute
    */
   public static final String InnerClasses = "InnerClasses";
   /** String by which a Synthetic attribute is recognized.
    * @see Synthetic_attribute
    */
   public static final String Synthetic = "Synthetic";
   /** String by which a BootstrapMethods attribute is recognized.
    * @see BootstrapMethods_attribute
    */
   public static final String BootstrapMethods = "BootstrapMethods";

   
   /**
    * the following tags are added for java1.5
    */
   
   /** String by which a Synthetic attribute is recognized.
    * @see Signature_attribute
    */
   public static final String Signature = "Signature";

   /** String by which a Deprecated attribute is recognized.
    * @see Deprecated_attribute
    */
   public static final String Deprecated = "Deprecated";

   /** String by which a EnclosingMethod attribute is recognized.
    * @see EnclosingMethod_attribute
    */
   public static final String EnclosingMethod = "EnclosingMethod";
   
   /** String by which a LocalVariableTypeTable attribute is recognized.
    * @see LocalVariableTypeTable_attribute
    */
   public static final String LocalVariableTypeTable = "LocalVariableTypeTable";
   
   /** String by which a runtime visible annotation attribute is recognized.
    * @see RuntimeVisibleAnnotations_attribute
    */
   public static final String RuntimeVisibleAnnotations = "RuntimeVisibleAnnotations";

   /** String by which a runtime invisible annotation attribute is recognized.
    * @see RuntimeInvisibleAnnotations_attribute
    */
   public static final String RuntimeInvisibleAnnotations = "RuntimeInvisibleAnnotations";

   /** String by which a runtime visible parameter annotation attribute 
    * is recognized.
    * @see RuntimeVisibleParameterAnnotations_attribute
    */
   public static final String RuntimeVisibleParameterAnnotations = "RuntimeVisibleParameterAnnotations";

   /** String by which a runtime invisible parameter annotation attribute 
    * is recognized.
    * @see RuntimeInvisibleParameterAnnotations_attribute
    */
   public static final String RuntimeInvisibleParameterAnnotations = "RuntimeInvisibleParameterAnnotations";

   /** String by which an annotation default attribute 
    * is recognized.
    * @see AnnotationDefault_attribute
    */
   public static final String AnnotationDefault = "AnnotationDefault";

   /** Constant pool index of the name of this attribute; should be a utf8 String
    * matching one of the constant Strings define here.
    * @see attribute_info#SourceFile
    * @see attribute_info#ConstantValue
    * @see attribute_info#Code
    * @see attribute_info#Exceptions
    * @see attribute_info#LineNumberTable
    * @see attribute_info#LocalVariableTable
    * @see attribute_info#InnerClasses
    * @see CONSTANT_Utf8_info
    */
   public int attribute_name;
   /** Length of attribute in bytes. */
   public long attribute_length;
}
