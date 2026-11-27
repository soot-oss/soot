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

using System.Linq;
using Google.Protobuf;
using Soot.Dotnet.Decompiler.Models.Cli;
using Soot.Dotnet.Decompiler.Models.Protobuf;
using Soot.Dotnet.Decompiler.Parser;
using Xunit;

namespace Soot.Dotnet.UnitTests
{
    public class RvaInitialValueTest
    {
        public static readonly string PathTestSnippetDll;

        static RvaInitialValueTest()
        {
            PathTestSnippetDll = TestClass.PathTestSnippetDll;
        }

        /// <summary>
        ///     Fields with data in the PE file (RVA fields) must carry their initial value in the proto message.
        ///     The parser used to test the handle with `is`, which can never match for two distinct struct types.
        /// </summary>
        [Fact]
        public void RvaInitialValueIsTransferred()
        {
            var parser = new AssemblyParser(PathTestSnippetDll);
            CliByteArray content = parser.GetAllTypeDefinitions();
            Assert.NotEqual(0, content.Length);

            AssemblyAllTypes allTypes = AssemblyAllTypes.Parser.ParseFrom(content.GetArray());
            byte[] expected = { 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 4, 0, 0, 0 };

            var fieldsWithInitialValue = allTypes.ListOfTypes
                .SelectMany(t => t.Fields)
                .Where(f => f.InitialValue.Length > 0)
                .ToList();

            Assert.Contains(fieldsWithInitialValue, f => f.InitialValue.ToByteArray().SequenceEqual(expected));
        }
    }
}
