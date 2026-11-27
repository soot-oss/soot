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

using System.Collections.Generic;
using System.Linq;
using ICSharpCode.Decompiler.IL;
using ICSharpCode.Decompiler.TypeSystem;
using Soot.Dotnet.Decompiler.Models.Protobuf;

namespace Soot.Dotnet.Decompiler.ProtoConverter
{
    public partial class ProtoConverter
    {
        /// <summary>
        ///     Resolves an IL instruction and converts it to a proto message
        /// </summary>
        /// <param name="il"></param>
        /// <param name="protoMsg"></param>
        private void ResolveAstInstructionMetaData(ILInstruction il, ref IlInstructionMsg protoMsg)
        {
            BlockContainer tryBlock;
            switch (il)
            {
                case ILFunction i:
                    protoMsg.LambdaFunction = ToIlFunctionMessage(i);
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Ilfunction;
                    break;
                case Nop _:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Nop;
                    break;
                case Call i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Call;
                    protoMsg.Method = ToMethodDefinition(i.Method);
                    foreach (var argument in i.Arguments)
                        protoMsg.Arguments.Add(VisitIlAst(argument));
                    break;
                case CallVirt i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Callvirt;
                    protoMsg.Method = ToMethodDefinition(i.Method);
                    foreach (var argument in i.Arguments)
                        protoMsg.Arguments.Add(VisitIlAst(argument));
                    break;
                case Leave i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Leave;
                    protoMsg.ValueInstruction = VisitIlAst(i.Value);
                    protoMsg.IsLeavingFunction = i.IsLeavingFunction;
                    protoMsg.TargetLabel = i.TargetLabel;
                    break;
                case LdStr i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Ldstr;
                    protoMsg.ValueConstantString = i.Value;
                    break;
                case StObj i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Stobj;
                    protoMsg.Target = VisitIlAst(i.Target);
                    protoMsg.ValueInstruction = VisitIlAst(i.Value);
                    protoMsg.Type = ToTypeDefinitionMessage(i.Type);
                    break;
                case LdFlda i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Ldflda;
                    protoMsg.Field = ToFieldDefinition(i.Field);
                    protoMsg.Target = VisitIlAst(i.Target);
                    break;
                case StringToInt i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.StringToInt;
                    protoMsg.ValueInstruction = VisitIlAst(i.Argument);
                    break;
                case LdsFlda i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Ldsflda;
                    protoMsg.Field = ToFieldDefinition(i.Field);
                    break;
                case LdcDecimal i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.LdcDecimal;
                    protoMsg.ValueConstantString = i.Value.ToString(System.Globalization.CultureInfo.InvariantCulture);
                    protoMsg.ConstantType = Soot.Dotnet.Decompiler.Models.Protobuf.IlType.TypeDecimal;
                   break;
                case LdcI4 i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.LdcI4;
                    protoMsg.ValueConstantInt64 = i.Value;
                    protoMsg.ConstantType = Soot.Dotnet.Decompiler.Models.Protobuf.IlType.TypeInt32;
                   break;
                case LdcI8 i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.LdcI8;
                    protoMsg.ValueConstantInt64 = i.Value;
                    protoMsg.ConstantType = Soot.Dotnet.Decompiler.Models.Protobuf.IlType.TypeInt64;
                    break;
                case LdcF4 i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.LdcR4;
                    protoMsg.ValueConstantFloat = i.Value;
                    protoMsg.ConstantType = Soot.Dotnet.Decompiler.Models.Protobuf.IlType.TypeFloat;
                    break;
                case LdcF8 i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.LdcR8;
                    protoMsg.ValueConstantDouble = i.Value;
                    protoMsg.ConstantType = Soot.Dotnet.Decompiler.Models.Protobuf.IlType.TypeDouble;
                    break;
                case LdLoc i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Ldloc;
                    protoMsg.Variable = ToIlVariableMsg(i.Variable);
                    break;
                case LdLoca i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Ldloca;
                    protoMsg.Variable = ToIlVariableMsg(i.Variable);
                    break;
                case LdObj i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Ldobj;
                    protoMsg.Target = VisitIlAst(i.Target);
                    protoMsg.Type = ToTypeDefinitionMessage(i.Type);
                    break;
                case StLoc i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Stloc;
                    protoMsg.Variable = ToIlVariableMsg(i.Variable);
                    protoMsg.ValueInstruction = VisitIlAst(i.Value);
                    break;
                case NewObj i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Newobj;
                    protoMsg.Method = ToMethodDefinition(i.Method);
                    foreach (var argument in i.Arguments)
                        protoMsg.Arguments.Add(VisitIlAst(argument));
                    break;
                case BinaryNumericInstruction i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.BinaryNumericInstruction;
                    protoMsg.Left = VisitIlAst(i.Left);
                    protoMsg.Right = VisitIlAst(i.Right);
                    protoMsg.Operator = EnumConverter.ToIlBinaryNumericOperatorEnum(i.Operator);
                    protoMsg.CheckForOverflow = i.CheckForOverflow;
                    break;
                case Branch i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Branch;
                    protoMsg.TargetLabel = i.TargetLabel;
                    break;
                case Comp i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Comp;
                    protoMsg.Sign = EnumConverter.ToIlSignEnum(i.Sign);
                    protoMsg.Left = VisitIlAst(i.Left);
                    protoMsg.Right = VisitIlAst(i.Right);
                    protoMsg.ComparisonKind = EnumConverter.ToIlComparisonKindEnum(i.Kind);
                    break;
                case IfInstruction i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.IfInstruction;
                    protoMsg.Condition = VisitIlAst(i.Condition);
                    protoMsg.TrueInst = VisitIlAst(i.TrueInst);
                    protoMsg.FalseInst = VisitIlAst(i.FalseInst);
                    break;
                case LdNull _:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Ldnull;
                    break;
                case LdLen i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Ldlen;
                    protoMsg.ResultType = EnumConverter.ToIlStackTypeEnum(i.ResultType);
                    protoMsg.Array = VisitIlAst(i.Array);
                    break;
                case Conv i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Conv;
                    protoMsg.Argument = VisitIlAst(i.Argument);
                    protoMsg.InputType = EnumConverter.ToIlStackTypeEnum(i.InputType);
                    protoMsg.Sign = EnumConverter.ToIlSignEnum(i.InputSign);
                    protoMsg.TargetType = EnumConverter.ToIlPrimitiveTypeEnum(i.TargetType);
                    protoMsg.ResultType = EnumConverter.ToIlStackTypeEnum(i.ResultType);
                    protoMsg.ConversionKind = EnumConverter.ToIlConversionKindEnum(i.Kind);
                    protoMsg.CheckForOverflow = i.CheckForOverflow;
                    break;
                case NewArr i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Newarr;
                    protoMsg.Indices.AddRange(i.Indices.Select(VisitIlAst));
                    protoMsg.Type = ToTypeDefinitionMessage(i.Type);
                    break;
                case LdElema i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Ldelema;
                    protoMsg.Array = VisitIlAst(i.Array);
                    protoMsg.Indices.AddRange(i.Indices.Select(VisitIlAst));
                    protoMsg.Type = ToTypeDefinitionMessage(i.Type);
                    break;
                case CastClass i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Castclass;
                    protoMsg.Argument = VisitIlAst(i.Argument);
                    protoMsg.Type = ToTypeDefinitionMessage(i.Type);
                    break;
                case IsInst i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Isinst;
                    protoMsg.Argument = VisitIlAst(i.Argument);
                    protoMsg.Type = ToTypeDefinitionMessage(i.Type);
                    break;
                case Box i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Box;
                    protoMsg.Argument = VisitIlAst(i.Argument);
                    protoMsg.Type = ToTypeDefinitionMessage(i.Type);
                    protoMsg.ResultType = EnumConverter.ToIlStackTypeEnum(i.ResultType);
                    break;
                case UnboxAny i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Unboxany;
                    protoMsg.Argument = VisitIlAst(i.Argument);
                    protoMsg.Type = ToTypeDefinitionMessage(i.Type);
                    protoMsg.ResultType = EnumConverter.ToIlStackTypeEnum(i.ResultType);
                    break;
                case Unbox i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Unbox;
                    protoMsg.Argument = VisitIlAst(i.Argument);
                    protoMsg.Type = ToTypeDefinitionMessage(i.Type);
                    protoMsg.ResultType = EnumConverter.ToIlStackTypeEnum(i.ResultType);
                    break;
                case Block i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Block;
                    protoMsg.TryBlock = ToIlBlockContainerMessage(i);
                    break;
                case TryCatch i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.TryCatch;
                    tryBlock = (BlockContainer)i.TryBlock;
                    CleanUpBlockContainer(ref tryBlock);
                    protoMsg.TryBlock = ToIlBlockContainerMessage(tryBlock);
                    foreach (var handler in i.Handlers)
                        protoMsg.Handlers.Add(ToIlTryCatchHandlerMessage(handler));
                    break;
                case TryFinally i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.TryFinally;
                    var finallyBlock = (BlockContainer)i.FinallyBlock;
                    CleanUpBlockContainer(ref finallyBlock);
                    protoMsg.FinallyBlock = ToIlBlockContainerMessage(finallyBlock);
                    tryBlock = (BlockContainer)i.TryBlock;
                    CleanUpBlockContainer(ref tryBlock);
                    protoMsg.TryBlock = ToIlBlockContainerMessage(tryBlock);
                    break;
                case TryFault i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.TryFault;
                    tryBlock = (BlockContainer)i.TryBlock;
                    CleanUpBlockContainer(ref tryBlock);
                    protoMsg.TryBlock = ToIlBlockContainerMessage(tryBlock);
                    var faultBlock = (BlockContainer)i.FaultBlock;
                    CleanUpBlockContainer(ref faultBlock);
                    protoMsg.FaultBlock = ToIlBlockContainerMessage(faultBlock);
                    break;
                case BitNot i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Not;
                    protoMsg.Argument = VisitIlAst(i.Argument);
                    break;
                case DefaultValue i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.DefaultValue;
                    protoMsg.Type = ToTypeDefinitionMessage(i.Type);
                    break;
                case Rethrow i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Rethrow;
                    protoMsg.Variable = ToIlVariableMsg(((TryCatchHandler)i.Parent.Parent.Parent).Variable);
                    break;
                case Throw i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Throw;
                    protoMsg.Argument = VisitIlAst(i.Argument);
                    break;
                case DebugBreak _:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.DebugBreak;
                    break;
                case Ckfinite i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.CkFinite;
                    protoMsg.Argument = VisitIlAst(i.Argument);
                    break;
                case SwitchInstruction i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.Switch;
                    var sectionsDict = new SortedDictionary<long, ILInstruction>();
                    ILInstruction defaultTargetInstr = null;
                    foreach (var switchSection in i.Sections)
                    {
                        var targetInstruction = switchSection.Body;
                        foreach (var label in switchSection.Labels.Intervals)
                            if (label.InclusiveEnd.Equals(long.MaxValue)
                                || label.Start.Equals(long.MinValue)) // check if min in range
                                defaultTargetInstr = targetInstruction;
                            else
                                for (var l = label.Start; l < label.End; l++)
                                    sectionsDict.Add(l, targetInstruction);
                    }

                    // FILL Proto MSG
                    protoMsg.KeyInstr = VisitIlAst(i.Value);
                    if (defaultTargetInstr != null)
                        protoMsg.DefaultInst = VisitIlAst(defaultTargetInstr);
                    foreach (var protoSection in
                             sectionsDict.Select(pair =>
                                 new IlSwitchSectionMsg { Label = pair.Key, TargetInstr = VisitIlAst(pair.Value) }))
                        protoMsg.SwitchSections.Add(protoSection);
                    break;
                case LdFtn i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.LdFtn;
                    protoMsg.Method = ToMethodDefinition(i.Method);
                    break;
                case LdVirtFtn i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.LdVirtFtn;
                    protoMsg.Method = ToMethodDefinition(i.Method);
                    break;
                case LocAlloc i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.LocAlloc;
                    protoMsg.Argument = VisitIlAst(i.Argument);
                    break;
                case MakeRefAny i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.MkRefAny;
                    protoMsg.Type = ToTypeDefinitionMessage(i.Type);
                    protoMsg.Argument = VisitIlAst(i.Argument);
                    break;
                case RefAnyType i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.RefAnyType;
                    protoMsg.Argument = VisitIlAst(i.Argument);
                    break;
                case RefAnyValue i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.RefAnyVal;
                    protoMsg.Type = ToTypeDefinitionMessage(i.Type);
                    protoMsg.Argument = VisitIlAst(i.Argument);
                    break;
                case SizeOf i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.SizeOf;
                    protoMsg.Type = ToTypeDefinitionMessage(i.Type);
                    break;
                // ldtoken for fields/methods
                case LdMemberToken i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.LdMemberToken;

                    var t = i.Member.GetType();
                    if (typeof(IField).IsAssignableFrom(t))
                    {
                        var field = (IField)i.Member;
                        protoMsg.Field = ToFieldDefinition(field);
                    }
                    else if (typeof(IMethod).IsAssignableFrom(t))
                    {
                        var method = (IMethod)i.Member;
                        protoMsg.Method = ToMethodDefinition(method);
                    }
                    else
                    {
                        protoMsg.ValueConstantString = i.Member.FullName;
                    }

                    break;
                // ldtoken for types
                case LdTypeToken i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.LdTypeToken;
                    protoMsg.ValueConstantString = i.Type.ReflectionName;
                    break;
                case InvalidBranch i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.InvalidBranch;
                    protoMsg.ValueConstantString = i.Message;
                    break;
                case MatchInstruction i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.MatchInstruction;
                    protoMsg.Variable = ToIlVariableMsg(i.Variable);
                    if (i.Method != null)
                        protoMsg.Method = ToMethodDefinition(i.Method);
                    protoMsg.ValueInstruction = VisitIlAst(i.TestedOperand);
                    foreach (var argument in i.SubPatterns)
                        protoMsg.Arguments.Add(VisitIlAst(argument));
                    
                    break;
                
                case DynamicGetMemberInstruction i:
                    break;
                case DynamicConvertInstruction i:
                    break;
                
                case AddressOf i:
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.AddressOf;
                    protoMsg.ValueInstruction = VisitIlAst(i.Value);
                    protoMsg.Type = ToTypeDefinitionMessage(i.Type); 
                    break;
                    
                case CallIndirect i:
                // not implemented due to CLI/C++
                default:
                    Logger.Warn("OpCode " + il.GetType() + " is not implemented!");
                    protoMsg.OpCode = IlInstructionMsg.Types.IlOpCode.NoneOp;
                    break;
            }
        }

        /// <summary>
        ///     Convert IL Instruction to proto message
        ///     ILSpy Instructions are designed as AST model
        /// </summary>
        /// <param name="instruction"></param>
        /// <returns></returns>
        private IlInstructionMsg VisitIlAst(ILInstruction instruction)
        {
            if (instruction == null)
                return null;

            var inst = new IlInstructionMsg();
            ResolveAstInstructionMetaData(instruction, ref inst);

            return inst;
        }

        /// <summary>
        ///     Internal: Convert IL TryCatch Handler to proto message
        /// </summary>
        /// <param name="tch"></param>
        /// <returns></returns>
        private IlTryCatchHandlerMsg ToIlTryCatchHandlerMessage(TryCatchHandler tch)
        {
            var msg = new IlTryCatchHandlerMsg { Variable = ToIlVariableMsg(tch.Variable) };
            // if try catch handler has a filter
            if (tch.Filter is BlockContainer filter)
            {
                var protoConverter = new ProtoConverter(true);
                msg.Filter = protoConverter.ToIlBlockContainerMessage(filter);
                msg.HasFilter = true;
                if (msg.Variable.Type.Fullname.Equals("System.Object"))
                    msg.Variable.Type.Fullname = "System.Exception";
            }

            var body = (BlockContainer)tch.Body;
            CleanUpBlockContainer(ref body);
            msg.Body = ToIlBlockContainerMessage(body);
            return msg;
        }
    }
}