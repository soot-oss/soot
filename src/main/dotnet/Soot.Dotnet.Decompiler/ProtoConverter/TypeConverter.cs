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

using System;
using System.Reflection.Metadata.Ecma335;
using ICSharpCode.Decompiler.TypeSystem;
using Soot.Dotnet.Decompiler.Helper;
using Soot.Dotnet.Decompiler.Models.Protobuf;

namespace Soot.Dotnet.Decompiler.ProtoConverter
{
    public partial class ProtoConverter
    {
        /// <summary>
        ///     Convert TypeDefinition with members (method, fields, events, etc.) to proto message
        /// </summary>
        /// <param name="typeDefinition"></param>
        /// <param name="fullCopy">if true, convert members, too.</param>
        /// <returns></returns>
        public TypeDefinition ToTypeDefinitionMessage(IType typeDefinition, bool fullCopy = false)
        {
            var tdMsg = new TypeDefinition
            {
                Fullname = DefinitionUtils.ConvertCilToJvmNaming(DefinitionUtils.GetTypeFullname(typeDefinition)),
                Namespace = typeDefinition.Namespace,
                TypeKind = EnumConverter.ToTypeKindEnum(typeDefinition.Kind)
            };

            switch (typeDefinition)
            {
                case ParameterizedType parameterizedType: // reflection name ends with [[type]]
                {
                    tdMsg.Fullname =
                        DefinitionUtils.ConvertCilToJvmNaming(
                            DefinitionUtils.GetTypeFullname(parameterizedType.GenericType));
                    foreach (var typeArgument in parameterizedType.TypeArguments)
                        tdMsg.GenericTypeArguments.Add(ToTypeDefinitionMessage(typeArgument));
                    break;
                }
                case TupleType type: // tuple name ends with [[type],[type]]
                {
                    tdMsg.Fullname =
                        DefinitionUtils.ConvertCilToJvmNaming(DefinitionUtils.GetTypeFullname(type.UnderlyingType));
                    foreach (var typeArgument in type.ElementTypes)
                        tdMsg.GenericTypeArguments.Add(ToTypeDefinitionMessage(typeArgument));
                    break;
                }
                case ArrayType arrayType: // ends with []
                    tdMsg.Fullname =
                        DefinitionUtils.ConvertCilToJvmNaming(DefinitionUtils.GetTypeFullname(arrayType.ElementType));
                    tdMsg.ArrayDimensions = arrayType.Dimensions;
                    break;
                case ByReferenceType referenceType:
                    var t = ToTypeDefinitionMessage(referenceType.ElementType, fullCopy);
                    t.TypeKind = t.TypeKind == TypeKindDef.Array ? TypeKindDef.ByRefAndArray : TypeKindDef.ByRef;
                    return t;
                case PointerType pointerType:
                    t = ToTypeDefinitionMessage(pointerType.ElementType, fullCopy);
                    t.TypeKind = TypeKindDef.Pointer;
                    return t;
                case ITypeDefinition definition:
                {
                    tdMsg.Accessibility = EnumConverter.ToAccessibilityEnum(definition.Accessibility);
                    tdMsg.IsAbstract = definition.IsAbstract;
                    tdMsg.IsSealed = definition.IsSealed;
                    tdMsg.IsStatic = definition.IsStatic;
                    tdMsg.IsSealed = definition.IsSealed;
                    tdMsg.IsReadOnly = definition.IsReadOnly;
                    tdMsg.PeToken = MetadataTokens.GetToken(definition.MetadataToken);

                    tdMsg.DeclaringOuterClass = definition.DeclaringTypeDefinition == null
                        ? ""
                        : definition.DeclaringTypeDefinition != null
                            ? DefinitionUtils.ConvertCilToJvmNaming(
                                DefinitionUtils.GetTypeFullname(definition.DeclaringTypeDefinition))
                            : "";

                    if (fullCopy)
                    {
                        // methods
                        foreach (var method in definition.Methods)
                        {
                            var m = ToMethodDefinition(method, true);
                            if (method.IsConstructor)
                            {
                                m.Name = DefinitionUtils.ConvertConstructorMethodName(m.Name);
                                m.FullName =
                                    DefinitionUtils.ConvertCilToJvmNaming(
                                        DefinitionUtils.ConvertConstructorMethodName(m.FullName));
                            }

                            tdMsg.Methods.Add(m);
                        }

                        // fields
                        foreach (var field in definition.Fields)
                        {
                            var f = ToFieldDefinition(field);
                            tdMsg.Fields.Add(f);
                        }

                        // properties
                        foreach (var property in definition.Properties)
                        {
                            var p = ToPropertyDefinition(property);
                            tdMsg.Properties.Add(p);
                        }

                        // events
                        foreach (var @event in definition.Events)
                        {
                            var e = ToEventDefinition(@event);
                            tdMsg.Events.Add(e);
                        }

                        // attributes
                        try
                        {
                            var attributes = definition.GetAttributes();
                            if (attributes != null)
                                foreach (var attribute in attributes)
                                    tdMsg.Attributes.Add(ToAttributeDefinition(attribute));
                        }
                        catch (Exception)
                        {
                            // ignore errors
                        }

                        // direct base
                        foreach (var baseType in typeDefinition.DirectBaseTypes)
                            tdMsg.DirectBaseTypes.Add(ToTypeDefinitionMessage(baseType));
                    }

                    break;
                }
            }

            return tdMsg;
        }
    }
}