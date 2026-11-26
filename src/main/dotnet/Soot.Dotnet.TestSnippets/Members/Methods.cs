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
using System.Collections.Generic;
using System.Linq;

namespace Soot.Dotnet.TestSnippets.Members
{
    /// <summary>
    ///     different types of methods such as anonymous functions
    /// </summary>
    public class Methods
    {
        public void Anonym()
        {
            var lst = new List<int> { 1, 23, 45432, 432, 123 };
            var lst2 = lst.Select(x => x++);
        }

        // void Finalize() not possible!
        public void finalize()
        {
        }

        public unsafe void MyUnsafeMethod()
        {
            var i = 0;
            var p = &i;
        }

        ~Methods()
        {
            Console.WriteLine("Ciao");
        }
    }
}