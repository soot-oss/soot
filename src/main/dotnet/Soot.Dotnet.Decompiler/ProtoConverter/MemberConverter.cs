/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2021 Thomas Schmeiduch
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

using System.Reflection.Metadata.Ecma335;
using ICSharpCode.Decompiler.TypeSystem;
using Soot.Dotnet.Decompiler.Models.Protobuf;
using System.Reflection;
using NLog;
using ICSharpCode.Decompiler;
using HandleKind = System.Reflection.Metadata.HandleKind;

namespace Soot.Dotnet.Decompiler.ProtoConverter
{

    /**
     * Members like Fields, Methods, Events
     */
    public partial class ProtoConverter
    {

        private FieldDefinition ToFieldDefinition(IField field, bool complete = true)
        {
            ICSharpCode.Decompiler.TypeSystem.IModule mod = field.ParentModule;
            object constantValue = null;

            byte[] initialValue = null;
            var metadataToken = field.MetadataToken;
            // EntityHandle and FieldDefinitionHandle are distinct structs, so `is` can never match.
            if (metadataToken.Kind == HandleKind.FieldDefinition)
            {
                System.Reflection.Metadata.FieldDefinitionHandle fh =
                    (System.Reflection.Metadata.FieldDefinitionHandle)metadataToken;
                if (complete)
                {
                    var def = mod.MetadataFile.Metadata.GetFieldDefinition(fh);
                    if ((def.Attributes & FieldAttributes.HasFieldRVA) == FieldAttributes.HasFieldRVA)
                    {
                        int rva = def.GetRelativeVirtualAddress();
                        if (mod.MetadataFile.GetContainingSectionIndex(rva) >= 0)
                        {
                            System.Reflection.Metadata.BlobReader initVal = SRMExtensions.GetInitialValue(def, mod.MetadataFile, null);
                            initialValue = initVal.ReadBytes(initVal.Length);

                        }

                    }
                }
            }

            MethodInfo dynMethod = field.GetType().GetMethod("GetConstantValue",
    BindingFlags.Public | BindingFlags.Instance);
            if (dynMethod == null) {
                Logger.Error("Method GetConstantValue not found on " + field.GetType());
            } else {
                constantValue = dynMethod.Invoke(field, new object[] { false });
            }
            var f = new FieldDefinition
            {
                Accessibility = EnumConverter.ToAccessibilityEnum(field.Accessibility),
                IsAbstract = field.IsAbstract,
                IsConst = field.IsConst,
                IsOverride = field.IsOverride,
                IsSealed = field.IsSealed,
                IsStatic = field.IsStatic,
                IsVirtual = field.IsVirtual,
                IsReadOnly = field.IsReadOnly,
                IsExplicitInterfaceImplementation = field.IsExplicitInterfaceImplementation,
                Type = ToTypeDefinitionMessage(field.Type),
                TypeKind = EnumConverter.ToTypeKindEnum(field.Type.Kind),
                Name = field.Name,
                FullName = field.FullName,
                DeclaringType = ToTypeDefinitionMessage(field.DeclaringType),
                PeToken = MetadataTokens.GetToken(field.MetadataToken)

            };
            if (initialValue != null)
                f.InitialValue = Google.Protobuf.ByteString.CopyFrom(initialValue);
            if (constantValue != null)
            {
                if (constantValue is float)
                {
                    f.ValueConstantFloat = (float)constantValue;
                    f.ConstantType = IlType.TypeFloat;
                }
                else
                if (constantValue is double)
                {
                    f.ValueConstantDouble = (double)constantValue;
                    f.ConstantType = IlType.TypeDouble;
                }
                else if (constantValue is int)
                {
                    f.ValueConstantInt64 = (int)constantValue;
                    f.ConstantType = IlType.TypeInt32;
                }
                else if (constantValue is long)
                {
                    f.ValueConstantInt64 = (long)constantValue;
                    f.ConstantType = IlType.TypeInt64;
                }
                else if (constantValue is string)
                {
                    f.ValueConstantString = (string)constantValue;
                    f.ConstantType = IlType.TypeString;
                }
                else if (constantValue is bool)
                {
                    f.ValueConstantBool = (bool)constantValue;
                    f.ConstantType = IlType.TypeBool;
                }
                else if (constantValue is byte)
                {
                    f.ValueConstantInt64 = (byte)constantValue;
                    f.ConstantType = IlType.TypeByte;
                }
                else if (constantValue is sbyte)
                {
                    f.ValueConstantInt64 = (sbyte)constantValue;
                    f.ConstantType = IlType.TypeSbyte;
                }
                else if (constantValue is uint)
                {
                    f.ValueConstantInt64 = (uint)constantValue;
                    f.ConstantType = IlType.TypeUint32;
                }
                else if (constantValue is ushort)
                {
                    f.ValueConstantInt64 = (ushort)constantValue;
                    f.ConstantType = IlType.TypeUint16;
                }
                else if (constantValue is short)
                {
                    f.ValueConstantInt64 = (short)constantValue;
                    f.ConstantType = IlType.TypeInt16;
                }
                else if (constantValue is ulong)
                {
                    f.ValueConstantUint64 = (ulong)constantValue;
                    f.ConstantType = IlType.TypeUint64;
                }
                else if (constantValue is char)
                {
                    f.ValueConstantUint64 = (char)constantValue;
                    f.ConstantType = IlType.TypeChar;
                }
                else if (constantValue is decimal)
                {
                    f.ValueConstantString = ((decimal)constantValue).ToString();
                    f.ConstantType = IlType.TypeDecimal;
                }
                else
                {
                    Logger.Warn("Unsupported constant " + constantValue + ": " + constantValue.GetType());
                }
            }



            return f;
        }

        private MethodDefinition ToMethodDefinition(IMethod method, bool fullCopy = false)
        {
            var memberDef = (IMethod)method.MemberDefinition;
            var m = new MethodDefinition
            {
                Name = method.Name,
                HasBody = method.HasBody,
                IsAbstract = method.IsAbstract,
                IsAccessor = method.IsAccessor,
                IsConstructor = method.IsConstructor,
                IsDestructor = method.IsDestructor,
                IsStatic = method.IsStatic,
                IsVirtual = method.IsVirtual,
                IsOperator = method.IsOperator,
                IsSealed = method.IsSealed,
                ReturnType = ToTypeDefinitionMessage(memberDef.ReturnType),
                IsExplicitInterfaceImplementation = method.IsExplicitInterfaceImplementation,
                Accessibility = EnumConverter.ToAccessibilityEnum(method.Accessibility),
                FullName = method.FullName,
                DeclaringType = ToTypeDefinitionMessage(method.DeclaringType),
                PeToken = MetadataTokens.GetToken(method.MetadataToken)
            };

            // rewrite IsExtern Field, because is not present in ILSpy
            if (!method.IsAbstract && !method.HasBody)
                m.IsExtern = true;

            foreach (var parameter in memberDef.Parameters)
            {
                var p = new ParameterDefinition
                {
                    Type = ToTypeDefinitionMessage(parameter.Type),
                    ParameterName = parameter.Name,
                    IsIn = parameter.ReferenceKind == ReferenceKind.In,
                    IsOut = parameter.ReferenceKind == ReferenceKind.Out,
                    IsRef = parameter.ReferenceKind == ReferenceKind.Ref || parameter.ReferenceKind == ReferenceKind.RefReadOnly,
                    IsOptional = parameter.IsOptional
                };
                m.Parameter.Add(p);
                // TODO IsUnsafe is not working fine, ILSpy has an AST transformation for this, but we need the method body for this - System.String:InternalCopy has no pointertype but is unsafe
                if (parameter.Type is PointerType)
                    m.IsUnsafe = true;
            }

            if (!fullCopy) return m;

            var attributes = method.GetAttributes();
            if (attributes == null) return m;
            foreach (var attribute in attributes)
                m.Attributes.Add(ToAttributeDefinition(attribute));

            return m;
        }

        private PropertyDefinition ToPropertyDefinition(IProperty property)
        {
            var p = new PropertyDefinition
            {
                Accessibility = EnumConverter.ToAccessibilityEnum(property.Accessibility),
                CanGet = property.CanGet,
                CanSet = property.CanSet,
                IsAbstract = property.IsAbstract,
                IsOverride = property.IsOverride,
                IsSealed = property.IsSealed,
                IsStatic = property.IsStatic,
                IsVirtual = property.IsVirtual,
                IsExplicitInterfaceImplementation = property.IsExplicitInterfaceImplementation,
                Type = ToTypeDefinitionMessage(property.ReturnType),
                TypeKind = EnumConverter.ToTypeKindEnum(property.ReturnType.Kind),
                Name = property.Name,
                PeToken = MetadataTokens.GetToken(property.MetadataToken)
            };

            var attributes = property.GetAttributes();
            foreach (var attribute in attributes)
            {
                p.Attributes.Add(ToAttributeDefinition(attribute));
                var attrString = attribute.AttributeType.ReflectionName;
                if (attrString.Contains("System.Runtime.InteropServices.DllImportAttribute") ||
                    attrString.Contains("System.Runtime.CompilerServices"))
                    p.IsExtern = true;
            }

            if (property.CanGet)
                p.Getter = ToMethodDefinition(property.Getter, true);
            if (property.CanSet)
                p.Setter = ToMethodDefinition(property.Setter, true);

            return p;
        }

        private EventDefinition ToEventDefinition(IEvent @event)
        {
            var e = new EventDefinition
            {
                Accessibility = EnumConverter.ToAccessibilityEnum(@event.Accessibility),
                Name = @event.Name,
                FullName = @event.FullName,
                PeToken = MetadataTokens.GetToken(@event.MetadataToken)
            };
            if (@event.CanAdd)
            {
                e.CanAdd = true;
                e.AddAccessorMethod = ToMethodDefinition(@event.AddAccessor);
            }

            if (@event.CanInvoke)
            {
                e.CanInvoke = true;
                e.InvokeAccessorMethod = ToMethodDefinition(@event.InvokeAccessor);
            }

            // ReSharper disable once InvertIf
            if (@event.CanRemove)
            {
                e.CanRemove = true;
                e.RemoveAccessorMethod = ToMethodDefinition(@event.RemoveAccessor);
            }

            return e;
        }
    }
}