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

using ICSharpCode.Decompiler.IL;
using ICSharpCode.Decompiler.TypeSystem;
using Soot.Dotnet.Decompiler.Models.Protobuf;
using Accessibility = Soot.Dotnet.Decompiler.Models.Protobuf.Accessibility;

namespace Soot.Dotnet.Decompiler.ProtoConverter
{
    /// <summary>
    ///     Convert all enum types of ILSpy to proto enum
    /// </summary>
    public static class EnumConverter
    {
        public static Accessibility ToAccessibilityEnum(ICSharpCode.Decompiler.TypeSystem.Accessibility accessibility)
        {
            var res = accessibility switch
            {
                ICSharpCode.Decompiler.TypeSystem.Accessibility.Public => Accessibility.Public,
                ICSharpCode.Decompiler.TypeSystem.Accessibility.Private => Accessibility.Private,
                ICSharpCode.Decompiler.TypeSystem.Accessibility.Internal => Accessibility.Internal,
                ICSharpCode.Decompiler.TypeSystem.Accessibility.Protected => Accessibility.Protected,
                ICSharpCode.Decompiler.TypeSystem.Accessibility.ProtectedAndInternal => Accessibility
                    .ProtectedAndInternal,
                ICSharpCode.Decompiler.TypeSystem.Accessibility.ProtectedOrInternal =>
                    Accessibility.ProtectedOrInternal,
                _ => Accessibility.None
            };
            return res;
        }

        public static TypeKindDef ToTypeKindEnum(TypeKind tk)
        {
            var res = tk switch
            {
                TypeKind.Class => TypeKindDef.Class,
                TypeKind.Interface => TypeKindDef.Interface,
                TypeKind.Enum => TypeKindDef.Enum,
                TypeKind.Array => TypeKindDef.Array,
                TypeKind.Delegate => TypeKindDef.Delegate,
                TypeKind.Dynamic => TypeKindDef.Dynamic,
                TypeKind.Intersection => TypeKindDef.Intersection,
                TypeKind.None => TypeKindDef.NoneType,
                TypeKind.Null => TypeKindDef.Null,
                TypeKind.Other => TypeKindDef.Other,
                TypeKind.Pointer => TypeKindDef.Pointer,
                TypeKind.Struct => TypeKindDef.Struct,
                TypeKind.Tuple => TypeKindDef.Tuple,
                TypeKind.Unknown => TypeKindDef.Unknown,
                TypeKind.Void => TypeKindDef.Void,
                TypeKind.ArgList => TypeKindDef.ArgList,
                TypeKind.ByReference => TypeKindDef.ByRef,
                TypeKind.FunctionPointer => TypeKindDef.FunctionPointer,
                TypeKind.ModOpt => TypeKindDef.ModOpt,
                TypeKind.ModReq => TypeKindDef.ModReq,
                TypeKind.NInt => TypeKindDef.NInt,
                TypeKind.NUInt => TypeKindDef.NUint,
                TypeKind.UnboundTypeArgument => TypeKindDef.UnboundTypeArg,
                _ => TypeKindDef.NoType
            };
            return res;
        }

        /// <summary>
        ///     Sign of primitive type, such as signed int or unsigned int
        /// </summary>
        /// <param name="sign"></param>
        /// <returns></returns>
        public static IlInstructionMsg.Types.IlSign ToIlSignEnum(Sign sign)
        {
            var res = sign switch
            {
                Sign.None => IlInstructionMsg.Types.IlSign.NoneSign,
                Sign.Signed => IlInstructionMsg.Types.IlSign.Signed,
                Sign.Unsigned => IlInstructionMsg.Types.IlSign.Unsigned,
                _ => IlInstructionMsg.Types.IlSign.NoneSign
            };
            return res;
        }

        public static IlInstructionMsg.Types.IlComparisonKind ToIlComparisonKindEnum(ComparisonKind comp)
        {
            var res = comp switch
            {
                ComparisonKind.Equality => IlInstructionMsg.Types.IlComparisonKind.Equality,
                ComparisonKind.Inequality => IlInstructionMsg.Types.IlComparisonKind.Inequality,
                ComparisonKind.GreaterThan => IlInstructionMsg.Types.IlComparisonKind.GreaterThan,
                ComparisonKind.LessThan => IlInstructionMsg.Types.IlComparisonKind.LessThan,
                ComparisonKind.GreaterThanOrEqual => IlInstructionMsg.Types.IlComparisonKind.GreaterThanOrEqual,
                ComparisonKind.LessThanOrEqual => IlInstructionMsg.Types.IlComparisonKind.LessThanOrEqual,
                _ => IlInstructionMsg.Types.IlComparisonKind.NoneKind
            };
            return res;
        }

        public static IlInstructionMsg.Types.IlConversionKind ToIlConversionKindEnum(ConversionKind conversion)
        {
            var res = conversion switch
            {
                ConversionKind.Invalid => IlInstructionMsg.Types.IlConversionKind.Invalid,
                ConversionKind.Nop => IlInstructionMsg.Types.IlConversionKind.Nop,
                ConversionKind.Truncate => IlInstructionMsg.Types.IlConversionKind.Truncate,
                ConversionKind.ObjectInterior => IlInstructionMsg.Types.IlConversionKind.ObjectInterior,
                ConversionKind.SignExtend => IlInstructionMsg.Types.IlConversionKind.SignExtend,
                ConversionKind.ZeroExtend => IlInstructionMsg.Types.IlConversionKind.ZeroExtend,
                ConversionKind.FloatPrecisionChange => IlInstructionMsg.Types.IlConversionKind.FloatPrecisionChange,
                ConversionKind.FloatToInt => IlInstructionMsg.Types.IlConversionKind.FloatToInt,
                ConversionKind.IntToFloat => IlInstructionMsg.Types.IlConversionKind.IntToFloat,
                ConversionKind.StartGCTracking => IlInstructionMsg.Types.IlConversionKind.StartGctracking,
                ConversionKind.StopGCTracking => IlInstructionMsg.Types.IlConversionKind.StopGctracking,
                _ => IlInstructionMsg.Types.IlConversionKind.NoneConversion
            };
            return res;
        }

        public static IlInstructionMsg.Types.IlStackType ToIlStackTypeEnum(StackType type)
        {
            var res = type switch
            {
                StackType.F4 => IlInstructionMsg.Types.IlStackType.F4,
                StackType.F8 => IlInstructionMsg.Types.IlStackType.F8,
                StackType.I => IlInstructionMsg.Types.IlStackType.IStack,
                StackType.I4 => IlInstructionMsg.Types.IlStackType.I4Stack,
                StackType.I8 => IlInstructionMsg.Types.IlStackType.I8Stack,
                StackType.Obj => IlInstructionMsg.Types.IlStackType.O,
                StackType.VT => IlInstructionMsg.Types.IlStackType.V,
                StackType.Ref => IlInstructionMsg.Types.IlStackType.RefStack,
                StackType.Unknown => IlInstructionMsg.Types.IlStackType.UnknownStack,
                StackType.Void => IlInstructionMsg.Types.IlStackType.Void,
                _ => IlInstructionMsg.Types.IlStackType.NoneStackType
            };
            return res;
        }

        public static IlInstructionMsg.Types.IlPrimitiveType ToIlPrimitiveTypeEnum(PrimitiveType type)
        {
            var res = type switch
            {
                PrimitiveType.I => IlInstructionMsg.Types.IlPrimitiveType.I,
                PrimitiveType.I1 => IlInstructionMsg.Types.IlPrimitiveType.I1,
                PrimitiveType.I2 => IlInstructionMsg.Types.IlPrimitiveType.I2,
                PrimitiveType.I4 => IlInstructionMsg.Types.IlPrimitiveType.I4,
                PrimitiveType.I8 => IlInstructionMsg.Types.IlPrimitiveType.I8,
                PrimitiveType.None => IlInstructionMsg.Types.IlPrimitiveType.None,
                PrimitiveType.R => IlInstructionMsg.Types.IlPrimitiveType.R,
                PrimitiveType.R4 => IlInstructionMsg.Types.IlPrimitiveType.R4,
                PrimitiveType.R8 => IlInstructionMsg.Types.IlPrimitiveType.R8,
                PrimitiveType.Ref => IlInstructionMsg.Types.IlPrimitiveType.Ref,
                PrimitiveType.U => IlInstructionMsg.Types.IlPrimitiveType.U,
                PrimitiveType.U1 => IlInstructionMsg.Types.IlPrimitiveType.U1,
                PrimitiveType.U2 => IlInstructionMsg.Types.IlPrimitiveType.U2,
                PrimitiveType.U4 => IlInstructionMsg.Types.IlPrimitiveType.U4,
                PrimitiveType.U8 => IlInstructionMsg.Types.IlPrimitiveType.U8,
                PrimitiveType.Unknown => IlInstructionMsg.Types.IlPrimitiveType.Unknown,
                _ => IlInstructionMsg.Types.IlPrimitiveType.NonePrimitiveType
            };
            return res;
        }

        public static IlInstructionMsg.Types.IlBinaryNumericOperator ToIlBinaryNumericOperatorEnum(
            BinaryNumericOperator o)
        {
            var res = o switch
            {
                BinaryNumericOperator.Add => IlInstructionMsg.Types.IlBinaryNumericOperator.Add,
                BinaryNumericOperator.Div => IlInstructionMsg.Types.IlBinaryNumericOperator.Div,
                BinaryNumericOperator.Mul => IlInstructionMsg.Types.IlBinaryNumericOperator.Mul,
                BinaryNumericOperator.None => IlInstructionMsg.Types.IlBinaryNumericOperator.NoneBinary,
                BinaryNumericOperator.Rem => IlInstructionMsg.Types.IlBinaryNumericOperator.Rem,
                BinaryNumericOperator.Sub => IlInstructionMsg.Types.IlBinaryNumericOperator.Sub,
                BinaryNumericOperator.BitAnd => IlInstructionMsg.Types.IlBinaryNumericOperator.BitAnd,
                BinaryNumericOperator.BitOr => IlInstructionMsg.Types.IlBinaryNumericOperator.BitOr,
                BinaryNumericOperator.BitXor => IlInstructionMsg.Types.IlBinaryNumericOperator.BitXor,
                BinaryNumericOperator.ShiftLeft => IlInstructionMsg.Types.IlBinaryNumericOperator.ShiftLeft,
                BinaryNumericOperator.ShiftRight => IlInstructionMsg.Types.IlBinaryNumericOperator.ShiftRight,
                _ => IlInstructionMsg.Types.IlBinaryNumericOperator.NoneBinary
            };
            return res;
        }

        public static IlVariableMsg.Types.IlVariableKind ToIlVariableKindEnum(VariableKind kind)
        {
            var res = kind switch
            {
                VariableKind.Local => IlVariableMsg.Types.IlVariableKind.Local,
                VariableKind.Parameter => IlVariableMsg.Types.IlVariableKind.Parameter,
                VariableKind.ExceptionLocal => IlVariableMsg.Types.IlVariableKind.ExceptionLocal,
                VariableKind.ForeachLocal => IlVariableMsg.Types.IlVariableKind.ForeachLocal,
                VariableKind.InitializerTarget => IlVariableMsg.Types.IlVariableKind.InitializerTarget,
                VariableKind.NamedArgument => IlVariableMsg.Types.IlVariableKind.NamedArgument,
                VariableKind.PatternLocal => IlVariableMsg.Types.IlVariableKind.PatternLocal,
                VariableKind.PinnedLocal => IlVariableMsg.Types.IlVariableKind.PinnedLocal,
                VariableKind.StackSlot => IlVariableMsg.Types.IlVariableKind.Stackslot,
                VariableKind.UsingLocal => IlVariableMsg.Types.IlVariableKind.UsingLocal,
                VariableKind.DeconstructionInitTemporary => IlVariableMsg.Types.IlVariableKind
                    .DeconstructionInitTemporary,
                VariableKind.DisplayClassLocal => IlVariableMsg.Types.IlVariableKind.DisplayClassLocal,
                VariableKind.ExceptionStackSlot => IlVariableMsg.Types.IlVariableKind.ExceptionStackSlot,
                VariableKind.PinnedRegionLocal => IlVariableMsg.Types.IlVariableKind.PinnedRegionLocal,
                _ => IlVariableMsg.Types.IlVariableKind.None
            };
            return res;
        }
    }
}