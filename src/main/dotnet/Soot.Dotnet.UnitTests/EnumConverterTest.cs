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
using Soot.Dotnet.Decompiler.Models.Protobuf;
using Soot.Dotnet.Decompiler.ProtoConverter;
using Xunit;

namespace Soot.Dotnet.UnitTests
{
    public class EnumConverterTest
    {
        [Fact]
        public void ValueTypeStackTypeMapsToValueTypeProto()
        {
            Assert.Equal(IlInstructionMsg.Types.IlStackType.V, EnumConverter.ToIlStackTypeEnum(StackType.VT));
        }

        [Fact]
        public void ReferenceStackTypeMapsToObjectProto()
        {
            Assert.Equal(IlInstructionMsg.Types.IlStackType.O, EnumConverter.ToIlStackTypeEnum(StackType.Obj));
        }

        [Theory]
        [InlineData(StackType.I4, IlInstructionMsg.Types.IlStackType.I4Stack)]
        [InlineData(StackType.I8, IlInstructionMsg.Types.IlStackType.I8Stack)]
        [InlineData(StackType.I, IlInstructionMsg.Types.IlStackType.IStack)]
        [InlineData(StackType.F4, IlInstructionMsg.Types.IlStackType.F4)]
        [InlineData(StackType.F8, IlInstructionMsg.Types.IlStackType.F8)]
        [InlineData(StackType.Ref, IlInstructionMsg.Types.IlStackType.RefStack)]
        [InlineData(StackType.Void, IlInstructionMsg.Types.IlStackType.Void)]
        [InlineData(StackType.Unknown, IlInstructionMsg.Types.IlStackType.UnknownStack)]
        public void OtherStackTypesKeepTheirMapping(StackType stackType, IlInstructionMsg.Types.IlStackType expected)
        {
            Assert.Equal(expected, EnumConverter.ToIlStackTypeEnum(stackType));
        }
    }
}
