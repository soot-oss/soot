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

using ICSharpCode.Decompiler.CSharp;
using Soot.Dotnet.Decompiler.Helper;

namespace Soot.Dotnet.Decompiler.Parser
{
    /// <summary>
    /// This class extracts definitions and CIL code with the help of decompiler/disassembler and converts with the help
    /// of ProtoConverter to a proto message
    /// </summary>
    public partial class AssemblyParser
    {
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();
        
        private string AssemblyFileAbsolutePath { get; }
        private bool DebugMode { get; }
        private CSharpDecompiler Decompiler { get; }
        private MethodBodyDisassembler Disassembler { get; }

        public AssemblyParser(string assemblyFileAbsolutePath, bool debugMode = false)
        {
            AssemblyFileAbsolutePath = assemblyFileAbsolutePath;
            DebugMode = debugMode;
            Decompiler = IlSpyUtils.GetDecompiler(AssemblyFileAbsolutePath);
            Disassembler = IlSpyUtils.GetDisassembler(AssemblyFileAbsolutePath);
        }

    }
}