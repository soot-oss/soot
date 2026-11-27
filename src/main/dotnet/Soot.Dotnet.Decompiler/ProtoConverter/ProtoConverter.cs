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

using Google.Protobuf;
using ICSharpCode.Decompiler.IL;
using NLog;
using Soot.Dotnet.Decompiler.Models.Cli;
using Soot.Dotnet.Decompiler.Models.Protobuf;

namespace Soot.Dotnet.Decompiler.ProtoConverter
{
    /// <summary>
    ///     This class converts ILSpy objects, such as IlFunction or BlockContainer or Cil Instructions to a proto message
    ///     This partial class is split into several files
    /// </summary>
    public partial class ProtoConverter
    {
        private static readonly Logger Logger = LogManager.GetCurrentClassLogger();

        private readonly bool _isTryFilter;

        public ProtoConverter()
        {
        }

        private ProtoConverter(bool isTryFilter)
        {
            _isTryFilter = isTryFilter;
        }

        public static CliByteArray ConvertMethodBody(ILFunction ilBody)
        {
            var returnValue = new CliByteArray();
            var protoConverter = new ProtoConverter();
            var protoBlockContainer = protoConverter.ToIlFunctionMessage(ilBody);
            returnValue.SetArray(protoBlockContainer.ToByteArray());
            return returnValue;
        }

        private IlBlock ToIlBlockMessage(Block block)
        {
            var b = new IlBlock { BlockName = block.Label };
            foreach (var instruction in block.Instructions)
                b.ListOfIlInstructions.Add(VisitIlAst(instruction));
            b.FinalInstruction = VisitIlAst(block.FinalInstruction);
            return b;
        }

        private IlBlockContainerMsg ToIlBlockContainerMessage(BlockContainer bc)
        {
            var msg = new IlBlockContainerMsg();
            foreach (var block in bc.Blocks)
                msg.Blocks.Add(ToIlBlockMessage(block));
            return msg;
        }
        private IlBlockContainerMsg ToIlBlockContainerMessage(Block bc)
        {
            var msg = new IlBlockContainerMsg();
            msg.Blocks.Add(ToIlBlockMessage(bc));
            return msg;
        }

        /// <summary>
        ///     Function -> BlockContainer -> Block -> Instruction
        /// </summary>
        /// <param name="function"></param>
        /// <returns></returns>
        public IlFunctionMsg ToIlFunctionMessage(ILFunction function)
        {
            var msg = new IlFunctionMsg { Body = ToIlBlockContainerMessage((BlockContainer)function.Body) };
            // Convert variable definitions to proto message record
            foreach (var variable in function.Variables)
                msg.Variables.Add(ToIlVariableMsg(variable));
            return msg;
        }

        /// <summary>
        ///     Usage in Try/Catch/Finally Instructions to remove leave block x instruction, because these BlockContainer
        ///     are part of the main BlockContainer
        /// </summary>
        /// <param name="container"></param>
        private static void CleanUpBlockContainer(ref BlockContainer container)
        {
            if (container?.Blocks == null || container.Blocks.Count == 0)
                return;

            // if void method remove "leave block x" instruction
            if (container.Blocks.Last().Instructions.Last() is Leave i &&
                i.TargetLabel.Equals(container.Blocks.Last().Label))
                container.Blocks.Last().Instructions.RemoveAt(container.Blocks.Last().Instructions.Count - 1);
        }
    }
}