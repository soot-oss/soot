package soot.dotnet.types;

import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.Type;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2022 Fraunhofer SIT
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

/**
 * Constants with basic classes
 */
public class DotNetBasicTypes {
  public static final String SYSTEM_OBJECT = "System.Object";
  public static final String SYSTEM_VALUETYPE = "System.ValueType";
  public static final String SYSTEM_VOID = "System.Void";
  public static final String SYSTEM_STRING = "System.String";

  public static final String SYSTEM_BOOLEAN = "System.Boolean";
  public static final String SYSTEM_BYTE = "System.Byte";
  public static final String SYSTEM_SBYTE = "System.SByte";
  public static final String SYSTEM_CHAR = "System.Char";
  public static final String SYSTEM_DECIMAL = "System.Decimal";
  public static final String SYSTEM_DOUBLE = "System.Double";
  public static final String SYSTEM_SINGLE = "System.Single"; // float
  public static final String SYSTEM_INT32 = "System.Int32";
  public static final String SYSTEM_UINT32 = "System.UInt32";
  public static final String SYSTEM_INTPTR = "System.IntPtr"; // nint
  public static final String SYSTEM_UINTPTR = "System.UIntPtr"; // nuint
  public static final String SYSTEM_INT64 = "System.Int64"; // long
  public static final String SYSTEM_UINT64 = "System.UInt64"; // ulong
  public static final String SYSTEM_INT16 = "System.Int16"; // short
  public static final String SYSTEM_UINT16 = "System.UInt16"; // ushort

  public static final String SYSTEM_TYPE = "System.Type";
  public static final String SYSTEM_ENUM = "System.Enum";
  public static final String SYSTEM_ARRAY = "System.Array";

  public static final String SYSTEM_ICOMPARABLE_1 = "System.IComparable`1";
  public static final String SYSTEM_ICOMPARABLE = "System.IComparable";
  public static final String SYSTEM_ICONVERTIBLE = "System.IConvertible";
  public static final String SYSTEM_IEQUATABLE_1 = "System.IEquatable`1";
  public static final String SYSTEM_IFORMATTABLE = "System.IFormattable";
  public static final String SYSTEM_ICLONEABLE = "System.ICloneable";

  public static final String SYSTEM_EXCEPTION = "System.Exception";
  public static final String SYSTEM_ARITHMETICEXCEPTION = "System.ArithmeticException";
  public static final String SYSTEM_RUNTIME_INTEROPSERVICES_MARSHAL = "System.Runtime.InteropServices.Marshal";
  public static final String SYSTEM_SYSTEMEXCEPTION = "System.SystemException";
  public static final String SYSTEM_ARRAYTYPEMISMATCHEXCEPTION = "System.ArrayTypeMismatchException";
  public static final String SYSTEM_INVALIDCASTEXCEPTION = "System.InvalidCastException";
  public static final String SYSTEM_INDEXOUTOFRANGEEXCEPTION = "System.IndexOutOfRangeException";
  public static final String SYSTEM_OVERFLOWEXCEPTION = "System.OverflowException";
  public static final String SYSTEM_NULLREFERENCEEXCEPTION = "System.NullReferenceException";
  public static final String SYSTEM_OUTOFMEMORYEXCEPTION = "System.OutOfMemoryException";
  public static final String SYSTEM_MISSINGFIELDEXCEPTION = "System.MissingFieldException";
  public static final String SYSTEM_MISSINGMETHODEXCEPTION = "System.MissingMethodException";
  public static final String SYSTEM_SECURITYEXCEPTION = "System.SecurityException";
  public static final String SYSTEM_METHODACCESSEXCEPTION = "System.MethodAccessException";
  public static final String SYSTEM_DIVIDEBYZEROEXCEPTION = "System.DivideByZeroException";
  public static final String SYSTEM_VERIFICATIONEXCEPTION = "System.VerificationException";
  public static final String SYSTEM_STACKOVERFLOWEXCEPTION = "System.StackOverflowException";
  public static final String SYSTEM_TYPELOADEXCEPTION = "System.TypeLoadException";
  public static final String SYSTEM_FIELDACCESSEXCEPTION = "System.FieldAccessException";
  public static final String SYSTEM_INVALIDOPERATIONEXCEPTION = "System.InvalidOperationException";
  public static final String SYSTEM_ACCESSVIOLATIONEXCEPTION = "System.AccessViolationException";
  public static final String SYSTEM_AGGREGATEEXCEPTION = "System.AggregateException";
  public static final String SYSTEM_APPDOMAINUNLOADEDEXCEPTION = "System.AppDomainUnloadedException";
  public static final String SYSTEM_APPLICATIONEXCEPTION = "System.ApplicationException";
  public static final String SYSTEM_ARGUMENTEXCEPTION = "System.ArgumentException";
  public static final String SYSTEM_ARGUMENTNULLEXCEPTION = "System.ArgumentNullException";
  public static final String SYSTEM_ARGUMENTOUTOFRANGEEXCEPTION = "System.ArgumentOutOfRangeException";
  public static final String SYSTEM_BADIMAGEFORMATEXCEPTION = "System.BadImageFormatException";
  public static final String SYSTEM_CANNOTUNLOADAPPDOMAINEXCEPTION = "System.CannotUnloadAppDomainException";
  public static final String SYSTEM_CONTEXTMARSHALEXCEPTION = "System.ContextMarshalException";
  public static final String SYSTEM_DATAMISALIGNEDEXCEPTION = "System.DataMisalignedException";
  public static final String SYSTEM_DLLNOTFOUNDEXCEPTION = "System.DllNotFoundException";
  public static final String SYSTEM_DUPLICATEWAITOBJECTEXCEPTION = "System.DuplicateWaitObjectException";
  public static final String SYSTEM_ENTRYPOINTNOTFOUNDEXCEPTION = "System.EntryPointNotFoundException";
  public static final String SYSTEM_EXECUTIONENGINEEXCEPTION = "System.ExecutionEngineException";
  public static final String SYSTEM_FORMATEXCEPTION = "System.FormatException";
  public static final String SYSTEM_INSUFFICIENTEXECUTIONSTACKEXCEPTION = "System.InsufficientExecutionStackException";
  public static final String SYSTEM_INSUFFICIENTMEMORYEXCEPTION = "System.InsufficientMemoryException";
  public static final String SYSTEM_INVALIDPROGRAMEXCEPTION = "System.InvalidProgramException";
  public static final String SYSTEM_INVALIDTIMEZONEEXCEPTION = "System.InvalidTimeZoneException";
  public static final String SYSTEM_TYPEACCESSEXCEPTION = "System.TypeAccessException";
  public static final String SYSTEM_TYPEINITIALIZATIONEXCEPTION = "System.TypeInitializationException";
  public static final String SYSTEM_TYPEUNLOADEDEXCEPTION = "System.TypeUnloadedException";
  public static final String SYSTEM_UNAUTHORIZEDACCESSEXCEPTION = "System.UnauthorizedAccessException";
  public static final String SYSTEM_URIFORMATEXCEPTION = "System.UriFormatException";

  public static final String SYSTEM_THREADING = "System.Threading";
  public static final String SYSTEM_SERIALIZEABLEATTRIBUTE = "System.SerializableAttribute";
  public static final String SYSTEM_CONSOLE = "System.Console";

  public static final String SYSTEM_OBSOLETEATTRIBUTE = "System.ObsoleteAttribute";
  public static final String SYSTEM_RUNTIMEMETHODHANDLE = "System.RuntimeMethodHandle";
  public static final String SYSTEM_RUNTIMEFIELDHANDLE = "System.RuntimeFieldHandle";
  public static final String SYSTEM_RUNTIMETYPEHANDLE = "System.RuntimeTypeHandle";
  public static boolean isValueType(Type t) {
    if (t instanceof PrimType) {
      return true;
    }
    if (t instanceof RefType) {
      RefType rt = (RefType) t;
      if (rt.hasSootClass()) {
        final RefType valueType = RefType.v(SYSTEM_VALUETYPE);
        return Scene.v().getOrMakeFastHierarchy().canStoreType(rt, valueType);
      }
    }
    return false;
  }

}
