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
using System.Runtime.CompilerServices;

namespace Soot.Dotnet.TestSnippets.Types
{
    /// <summary>
    ///     Types such as int or arrays
    /// </summary>
    public class Typ
    {
        public static int StatischesInt;

        public void DoArr(in string[] s)
        {
            var array = new[] { 1, 3, 5, 6 };
            var elem = array[3];
        }

        public void DoMultArr(ref string[,,] strings)
        {
            // ldtoken - dup only in CIL, not ILspy
            // ldtoken - stloc fldHandle(ldmembertoken 8B4B2444E57AED8C2D05A1293255DA1B048C63224317D4666230760935FA4A18)
            var array2D = new[,] { { 1, 2 }, { 3, 4 }, { 5, 6 }, { 7, 8 } };
            var elem = array2D[0, 1];

            var arr1 = new[] { 1, 2, 3, 4 };
            var s = Unsafe.SizeOf<Enumm.MyCustomAttribute>();

            var arr3 = new int[10][];
            var elem3 = arr3[1][1];
        }

        public void DoTuple()
        {
            var tup = new Tuple<string, int>("yo", 1);
            var (t1, t2) = tup;
            t1 = "y";
        }

        public void Arr()
        {
            var l = new int[20];
            var k = l.Length;
            l[11] = 34;
            k = l[13];
        }
    }
}