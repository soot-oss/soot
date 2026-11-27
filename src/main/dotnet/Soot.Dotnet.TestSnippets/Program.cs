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
using Soot.Dotnet.TestSnippets.Events;
using Soot.Dotnet.TestSnippets.IL;
using Soot.Dotnet.TestSnippets.Types;

namespace Soot.Dotnet.TestSnippets
{
    /// <summary>
    ///     This project is to produce different types of IL Code parts or ILSpy AST
    ///     Can be analyzed by a compiler
    /// </summary>
    internal static class Program
    {
        private static void Main(string[] args)
        {
            Console.WriteLine("Hello World!");
            var t = new Try();
            // t.TryCatch();
            Inherit.inh();
            EventUsing.Start();
        }
    }
}